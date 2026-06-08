package com.utc2.appreborn.ui.courseregistration;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import com.utc2.appreborn.utils.LocaleHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.EnrollmentApiService;
import com.utc2.appreborn.network.dto.CourseItemResponse;
import com.utc2.appreborn.network.dto.EnrollmentResponse;
import com.utc2.appreborn.network.dto.SemesterResponse;
import com.utc2.appreborn.ui.courseregistration.adapter.CourseAdapter;
import com.utc2.appreborn.ui.courseregistration.model.Course;
import com.utc2.appreborn.ui.courseregistration.model.CourseRepository;
import com.utc2.appreborn.ui.courseregistration.model.CourseStorage;
import com.utc2.appreborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình Đăng ký học phần – load danh sách môn từ server thay vì hardcode local.
 *
 * Root cause của bug "thanh toán 0đ":
 *  - CourseRepository dùng ID local "c1","c2"... → Long.parseLong("c1") fail →
 *    enroll() không bao giờ gọi API → server không tạo fee → thanh toán 0đ.
 *
 * Fix: load courses từ GET /api/v1/enrollment/courses → dùng courseId thật (Long).
 */
public class CourseRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "CourseRegActivity";

    // ── Tab & Pages ──────────────────────────────────────────────────────────
    private TextView     tabDangKy, tabKetQua;
    private LinearLayout pageDangKy, pageKetQua;

    // ── Views trang Đăng ký ──────────────────────────────────────────────────
    private TextView     btnHocKy, btnKhoaHoc, btnNganh, btnConfirm;
    private EditText     searchBox;
    private RecyclerView rvCourses;
    private LinearLayout layoutSelected, layoutSelectedItems;
    private TextView     txtTongTinChi;

    // ── Views trang Kết quả ──────────────────────────────────────────────────
    private LinearLayout layoutKetQuaItems;
    private TextView     txtKetQuaTongTinChi, txtKetQuaTrong;

    // ── Repository, Adapter, API ─────────────────────────────────────────────
    private CourseRepository    courseRepo;
    private CourseAdapter       courseAdapter;
    private EnrollmentApiService enrollApi;

    // Danh sách môn từ server (có courseId thật)
    private final List<CourseItemResponse> serverCourses = new ArrayList<>();

    // semesterId lấy từ server (học kỳ hiện tại)
    private Long currentSemesterId = null;

    // Map courseId(Long) → pending enroll (chờ xác nhận)
    private final Map<Long, CourseItemResponse> pendingEnroll = new LinkedHashMap<>();

    // Danh sách courseId đã XÁC NHẬN thành công với server
    private List<String> confirmedIds = new ArrayList<>();

    // ── Filter ───────────────────────────────────────────────────────────────
    private String filterSemester = "";
    private String filterKhoaHoc  = "";
    private String filterMajor    = "";

    private static final String[] SEMESTERS_CODES = {"", "HK1", "HK2", "HK3"};
    private static final String[] KHOAHOC_CODES   = {
            "","K51","K52","K53","K54","K55","K56","K57","K58","K59","K60",
            "K61","K62","K63","K64","K65","K66","K67","K68","K69","K70"
    };
    private static final String[] NGANH_CODES = {
            "","CNTT","KTPM","HTTT","MMT","CK","XD","KT","MT","DTVT"
    };

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_course_registration);

        courseRepo = CourseRepository.getInstance();
        confirmedIds = CourseStorage.loadConfirmedIds(this);

        String token = SessionManager.getInstance(this).getAuthToken();
        enrollApi = ApiClient.getInstance(token).create(EnrollmentApiService.class);

        bindViews();
        applyWindowInsets();
        setupTabs();
        setupFilterButtons();
        setupSearchBox();
        setupConfirmButton();
        showPage(true);

        // Load semester + courses từ server
        loadCurrentSemester();
        loadCoursesFromServer();
    }

    // ── Load học kỳ hiện tại từ server ───────────────────────────────────────

    private void loadCurrentSemester() {
        enrollApi.getSemesters().enqueue(new Callback<ApiResponse<List<SemesterResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SemesterResponse>>> call,
                                   Response<ApiResponse<List<SemesterResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<SemesterResponse> semesters = response.body().getData();
                    if (semesters != null && !semesters.isEmpty()) {
                        // Lấy học kỳ đầu tiên (hoặc có thể chọn theo ngày hiện tại)
                        // lấy semester MỚI NHẤT (cuối list vì sort Asc)
                        currentSemesterId = semesters.get(semesters.size() - 1).semesterId;
                        Log.d(TAG, "Current semesterId = " + currentSemesterId);
                    }
                } else {
                    Log.w(TAG, "loadCurrentSemester failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SemesterResponse>>> call, Throwable t) {
                Log.e(TAG, "loadCurrentSemester error", t);
            }
        });
    }

    // ── Confirm: gọi API POST cho từng môn trong pendingEnroll ───────────────

    private void setupConfirmButton() {
        btnConfirm.setOnClickListener(v -> {
            if (pendingEnroll.isEmpty()) {
                Toast.makeText(this, getString(R.string.course_gio_trong), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentSemesterId == null) {
                Toast.makeText(this, "Đang tải thông tin học kỳ, vui lòng thử lại...",
                        Toast.LENGTH_SHORT).show();
                loadCurrentSemester();
                return;
            }
            List<CourseItemResponse> pending = new ArrayList<>(pendingEnroll.values());
            enrollCoursesSequentially(pending, 0, new ArrayList<>(), new ArrayList<>());
        });
    }

    private void enrollCoursesSequentially(List<CourseItemResponse> courses, int index,
                                           List<String> succeeded, List<String> failed) {
        if (index >= courses.size()) {
            onEnrollComplete(succeeded, failed);
            return;
        }
        CourseItemResponse course = courses.get(index);

        Map<String, Long> body = new HashMap<>();
        body.put("courseId",   course.courseId);
        body.put("semesterId", currentSemesterId);

        enrollApi.enroll(body).enqueue(new Callback<ApiResponse<EnrollmentResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<EnrollmentResponse>> call,
                                   Response<ApiResponse<EnrollmentResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    succeeded.add(String.valueOf(course.courseId));
                    Log.d(TAG, "Enrolled: " + course.courseName);
                } else {
                    String msg = response.body() != null ? response.body().getMessage()
                            : "HTTP " + response.code();
                    failed.add(course.courseName + " (" + msg + ")");
                    Log.w(TAG, "Enroll failed for " + course.courseName + ": " + msg);
                }
                enrollCoursesSequentially(courses, index + 1, succeeded, failed);
            }
            @Override
            public void onFailure(Call<ApiResponse<EnrollmentResponse>> call, Throwable t) {
                failed.add(course.courseName + " (lỗi mạng)");
                Log.e(TAG, "Enroll error for " + course.courseName, t);
                enrollCoursesSequentially(courses, index + 1, succeeded, failed);
            }
        });
    }

    private void onEnrollComplete(List<String> succeeded, List<String> failed) {
        for (String id : succeeded) {
            if (!confirmedIds.contains(id)) confirmedIds.add(id);
        }
        CourseStorage.saveConfirmedIds(this, confirmedIds);

        // Lưu map courseId → credits để SubjectTuitionActivity tính học phí local
        java.util.Map<String, Integer> creditsMap = CourseStorage.loadCreditsMap(this);
        for (CourseItemResponse dto : serverCourses) {
            creditsMap.put(String.valueOf(dto.courseId), dto.credits != null ? dto.credits : 0);
        }
        CourseStorage.saveCreditsMap(this, creditsMap);
        pendingEnroll.clear();
        layoutSelected.setVisibility(View.GONE);
        courseAdapter.setRegisteredIds(new ArrayList<>(confirmedIds));

        if (failed.isEmpty()) {
            Toast.makeText(this, getString(R.string.course_xac_nhan_success), Toast.LENGTH_LONG).show();
        } else if (succeeded.isEmpty()) {
            Toast.makeText(this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Đăng ký thành công " + succeeded.size() + " môn. "
                            + failed.size() + " môn thất bại.", Toast.LENGTH_LONG).show();
        }
    }

    // ── Các method còn lại giữ nguyên ────────────────────────────────────────

    private void applyWindowInsets() {
        View statusBarSpacer = findViewById(R.id.statusBarSpacer);
        View navBarSpacer = findViewById(R.id.navBarSpacer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ViewGroup.LayoutParams lp = statusBarSpacer.getLayoutParams();
            lp.height = statusH;
            statusBarSpacer.setLayoutParams(lp);
            int navH = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            ViewGroup.LayoutParams lpNav = navBarSpacer.getLayoutParams();
            lpNav.height = navH;
            navBarSpacer.setLayoutParams(lpNav);
            return insets;
        });
    }

    private void bindViews() {
        tabDangKy           = findViewById(R.id.tabDangKy);
        tabKetQua           = findViewById(R.id.tabKetQua);
        pageDangKy          = findViewById(R.id.pageDangKy);
        pageKetQua          = findViewById(R.id.pageKetQua);
        btnHocKy            = findViewById(R.id.btnHocKy);
        btnKhoaHoc          = findViewById(R.id.btnKhoa);
        btnNganh            = findViewById(R.id.btnNganh);
        btnConfirm          = findViewById(R.id.btnConfirm);
        searchBox           = findViewById(R.id.searchBox);
        rvCourses           = findViewById(R.id.rvCourses);
        layoutSelected      = findViewById(R.id.layoutSelected);
        layoutSelectedItems = findViewById(R.id.layoutSelectedItems);
        txtTongTinChi       = findViewById(R.id.txtTongTinChi);
        layoutKetQuaItems    = findViewById(R.id.layoutKetQuaItems);
        txtKetQuaTongTinChi  = findViewById(R.id.txtKetQuaTongTinChi);
        txtKetQuaTrong       = findViewById(R.id.txtKetQuaTrong);
        btnHocKy.setText(getString(R.string.course_hoc_ky));
        btnKhoaHoc.setText(getString(R.string.course_khoa_hoc));
        btnNganh.setText(getString(R.string.course_nganh));
    }

    private void showPage(boolean showDangKy) {
        if (showDangKy) {
            pageDangKy.setVisibility(View.VISIBLE);
            pageKetQua.setVisibility(View.GONE);
            tabDangKy.setBackgroundResource(R.drawable.bg_tab_selected);
            tabDangKy.setTextColor(Color.WHITE);
            tabKetQua.setBackground(null);
            tabKetQua.setTextColor(Color.BLACK);
        } else {
            pageDangKy.setVisibility(View.GONE);
            pageKetQua.setVisibility(View.VISIBLE);
            tabKetQua.setBackgroundResource(R.drawable.bg_tab_selected);
            tabKetQua.setTextColor(Color.WHITE);
            tabDangKy.setBackground(null);
            tabDangKy.setTextColor(Color.BLACK);
            buildKetQuaPage();
        }
    }

    private void setupTabs() {
        tabDangKy.setOnClickListener(v -> showPage(true));
        tabKetQua.setOnClickListener(v -> showPage(false));
    }

    // ── Load danh sách môn từ server ─────────────────────────────────────────

    private void loadCoursesFromServer() {
        enrollApi.getAvailableCourses().enqueue(new Callback<ApiResponse<List<CourseItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CourseItemResponse>>> call,
                                   Response<ApiResponse<List<CourseItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<CourseItemResponse> data = response.body().getData();
                    serverCourses.clear();
                    if (data != null) serverCourses.addAll(data);
                    setupRecyclerView();
                } else {
                    Log.w(TAG, "loadCourses failed HTTP " + response.code() + " – fallback local");
                    setupRecyclerView(); // fallback: hiển thị local data
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CourseItemResponse>>> call, Throwable t) {
                Log.e(TAG, "loadCourses error", t);
                setupRecyclerView(); // fallback: hiển thị local data
            }
        });
    }

    private void setupRecyclerView() {
        // Ưu tiên dùng danh sách từ server; fallback về local nếu server chưa trả về
        List<Course> courses;
        if (!serverCourses.isEmpty()) {
            courses = new ArrayList<>();
            for (CourseItemResponse dto : serverCourses) {
                courses.add(new Course(
                        String.valueOf(dto.courseId),   // id = courseId thật (Long)
                        dto.courseName  != null ? dto.courseName  : "",
                        dto.courseCode  != null ? dto.courseCode  : "",
                        dto.credits     != null ? dto.credits     : 0,
                        "", "", "",                     // lecturer, schedule, room (không có từ API)
                        0, 0,                           // maxStudents, currentStudents
                        "", "", "", "",                 // semester, faculty, major, khoaHoc
                        "", "", 0                       // startDate, endDate, totalPeriods
                ));
            }
        } else {
            courses = courseRepo.getAllCourses();        // fallback local
        }

        courseAdapter = new CourseAdapter(courses, this::handleRegisterClick);
        courseAdapter.setRegisteredIds(new ArrayList<>(confirmedIds));
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setNestedScrollingEnabled(false);
        rvCourses.setAdapter(courseAdapter);
    }

    private void handleRegisterClick(Course course) {
        if (confirmedIds.contains(course.getId())) {
            Toast.makeText(this, getString(R.string.course_confirmed), Toast.LENGTH_SHORT).show();
            return;
        }
        Long cId;
        try {
            cId = Long.parseLong(course.getId());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Không thể đăng ký môn này (ID không hợp lệ)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pendingEnroll.containsKey(cId)) {
            Toast.makeText(this, getString(R.string.course_confirmed), Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra giới hạn tín chỉ
        int total = 0;
        for (CourseItemResponse r : pendingEnroll.values())
            total += (r.credits != null ? r.credits : 0);
        if (total + (course.getCredits()) > 24) {
            Toast.makeText(this, "Vượt quá số tín chỉ tối đa (24 tín chỉ)!", Toast.LENGTH_LONG).show();
            return;
        }
        // Tìm CourseItemResponse tương ứng
        CourseItemResponse dto = null;
        for (CourseItemResponse r : serverCourses) {
            if (String.valueOf(r.courseId).equals(course.getId())) { dto = r; break; }
        }
        if (dto == null) {
            // fallback: tạo minimal dto từ Course (local data)
            dto = new CourseItemResponse();
            dto.courseId  = cId;
            dto.courseName = course.getName();
            dto.courseCode = course.getCourseCode();
            dto.credits    = course.getCredits();
        }
        pendingEnroll.put(cId, dto);
        updateSelectedPanel();
        refreshAdapterPending();
        Toast.makeText(this, getString(R.string.course_added, course.getName()), Toast.LENGTH_SHORT).show();
    }

    private void updateSelectedPanel() {
        if (pendingEnroll.isEmpty()) {
            layoutSelected.setVisibility(View.GONE);
            return;
        }
        layoutSelected.setVisibility(View.VISIBLE);
        layoutSelectedItems.removeAllViews();
        int total = 0;
        for (CourseItemResponse dto : pendingEnroll.values()) {
            int tc = dto.credits != null ? dto.credits : 0;
            total += tc;
            TextView tv = new TextView(this);
            tv.setText("• " + dto.courseName + " (" + tc + " TC)");
            tv.setTextSize(13);
            layoutSelectedItems.addView(tv);
        }
        txtTongTinChi.setText(getString(R.string.course_tong_tin_chi, total));
    }

    private void refreshAdapterPending() {
        List<String> allMarked = new ArrayList<>(confirmedIds);
        for (Long id : pendingEnroll.keySet()) allMarked.add(String.valueOf(id));
        courseAdapter.setRegisteredIds(allMarked);
    }

    private void setupFilterButtons() {
        String all = getString(R.string.dorm_filter_all);
        String[] semesters = buildDisplayArray(all, SEMESTERS_CODES);
        String[] khoaHoc   = buildDisplayArray(all, KHOAHOC_CODES);
        String[] nganh     = buildDisplayArray(all, NGANH_CODES);

        btnHocKy.setOnClickListener(v -> showScrollableDialog(getString(R.string.course_chon_hoc_ky), semesters, choice -> {
            filterSemester = all.equals(choice) ? "" : choice;
            btnHocKy.setText(all.equals(choice) ? getString(R.string.course_hoc_ky)
                    : getString(R.string.course_hoc_ky_prefix, choice));
            applyFilter();
        }));
        btnKhoaHoc.setOnClickListener(v -> showScrollableDialog(getString(R.string.course_chon_khoa), khoaHoc, choice -> {
            filterKhoaHoc = all.equals(choice) ? "" : choice;
            btnKhoaHoc.setText(all.equals(choice) ? getString(R.string.course_khoa_hoc)
                    : getString(R.string.course_khoa_prefix, choice));
            applyFilter();
        }));
        btnNganh.setOnClickListener(v -> showScrollableDialog(getString(R.string.course_chon_nganh), nganh, choice -> {
            filterMajor = all.equals(choice) ? "" : choice;
            btnNganh.setText(all.equals(choice) ? getString(R.string.course_nganh)
                    : getString(R.string.course_nganh_prefix, choice));
            applyFilter();
        }));
    }

    private void applyFilter() {
        courseAdapter.updateData(getFilteredCourses(""));
    }

    private List<Course> getFilteredCourses(String query) {
        List<Course> base = courseAdapter != null
                ? new ArrayList<>() : new ArrayList<>();
        // Dùng data nguồn: serverCourses nếu có, fallback courseRepo
        List<Course> all;
        if (!serverCourses.isEmpty()) {
            all = new ArrayList<>();
            for (CourseItemResponse dto : serverCourses) {
                all.add(new Course(String.valueOf(dto.courseId),
                        dto.courseName != null ? dto.courseName : "",
                        dto.courseCode != null ? dto.courseCode : "",
                        dto.credits    != null ? dto.credits    : 0,
                        "", "", "", 0, 0, "", "", "", "",  "", "", 0));
            }
        } else {
            all = courseRepo.getAllCourses();
        }
        List<Course> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (Course c : all) {
            boolean okSem  = filterSemester.isEmpty() || c.getSemester().equalsIgnoreCase(filterSemester);
            boolean okKhoa = filterKhoaHoc.isEmpty()  || c.getKhoaHoc().equalsIgnoreCase(filterKhoaHoc);
            boolean okMaj  = filterMajor.isEmpty()    || c.getMajor().equalsIgnoreCase(filterMajor);
            boolean okQ    = q.isEmpty()
                    || c.getName().toLowerCase().contains(q)
                    || c.getCourseCode().toLowerCase().contains(q);
            if (okSem && okKhoa && okMaj && okQ) result.add(c);
        }
        return result;
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                courseAdapter.updateData(getFilteredCourses(s.toString().trim()));
            }
        });
    }

    private void buildKetQuaPage() {
        layoutKetQuaItems.removeAllViews();
        if (confirmedIds.isEmpty()) {
            txtKetQuaTrong.setVisibility(View.VISIBLE);
            txtKetQuaTongTinChi.setVisibility(View.GONE);
            return;
        }
        txtKetQuaTrong.setVisibility(View.GONE);
        txtKetQuaTongTinChi.setVisibility(View.VISIBLE);
        int tongTC = 0;
        for (String id : confirmedIds) {
            // Tìm trong serverCourses trước, fallback courseRepo
            String name = null;
            int tc = 0;
            for (CourseItemResponse dto : serverCourses) {
                if (String.valueOf(dto.courseId).equals(id)) {
                    name = dto.courseName;
                    tc   = dto.credits != null ? dto.credits : 0;
                    break;
                }
            }
            if (name == null) {
                Course c = courseRepo.findById(id);
                if (c != null) { name = c.getName(); tc = c.getCredits(); }
            }
            if (name == null) continue;
            tongTC += tc;
            TextView tv = new TextView(this);
            tv.setText(name + " - " + tc + " TC");
            layoutKetQuaItems.addView(tv);
        }
        txtKetQuaTongTinChi.setText(getString(R.string.course_tong_tc_ket_qua, tongTC));
    }

    private interface MenuCallback { void onSelected(String choice); }

    private void showScrollableDialog(String title, String[] options, MenuCallback cb) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setPadding(48, 40, 48, 32);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(tvTitle);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        for (String opt : options) {
            TextView item = new TextView(this);
            item.setText(opt);
            item.setPadding(48, 36, 48, 36);
            item.setOnClickListener(v -> { cb.onSelected(opt); dialog.dismiss(); });
            listLayout.addView(item);
        }
        scrollView.addView(listLayout);
        root.addView(scrollView);
        dialog.setContentView(root);
        dialog.show();
    }

    private String[] buildDisplayArray(String allLabel, String[] codes) {
        String[] result = new String[codes.length];
        result[0] = allLabel;
        System.arraycopy(codes, 1, result, 1, codes.length - 1);
        return result;
    }
}