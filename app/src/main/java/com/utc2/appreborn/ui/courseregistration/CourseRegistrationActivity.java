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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.courseregistration.adapter.CourseAdapter;
import com.utc2.appreborn.ui.courseregistration.exception.CourseException;
import com.utc2.appreborn.ui.courseregistration.model.Course;
import com.utc2.appreborn.ui.courseregistration.model.CourseRegistration;
import com.utc2.appreborn.ui.courseregistration.model.CourseRepository;
import com.utc2.appreborn.ui.courseregistration.model.CourseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Đăng ký học phần – Project Reborn
 */
public class CourseRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "CourseRegActivity";

    // ── Tab & Pages ──────────────────────────────────────────────────────────
    private TextView     tabDangKy, tabKetQua;
    private LinearLayout pageDangKy, pageKetQua;

    // ── Views trang Đăng ký ──────────────────────────────────────────────────
    // Đã xóa btnBack tại đây
    private TextView     btnHocKy, btnKhoaHoc, btnNganh, btnConfirm;
    private EditText     searchBox;
    private RecyclerView rvCourses;
    private LinearLayout layoutSelected, layoutSelectedItems;
    private TextView     txtTongTinChi;

    // ── Views trang Kết quả ──────────────────────────────────────────────────
    private LinearLayout layoutKetQuaItems;
    private TextView     txtKetQuaTongTinChi, txtKetQuaTrong;

    // ── Repository & Adapter ─────────────────────────────────────────────────
    private CourseRepository courseRepo;
    private CourseAdapter    courseAdapter;

    // ── Danh sách courseId đã XÁC NHẬN (lưu file) ───────────────────────────
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
        setContentView(R.layout.activity_course_registration);

        courseRepo = CourseRepository.getInstance();

        // Load dữ liệu cũ từ bộ nhớ
        confirmedIds = CourseStorage.loadConfirmedIds(this);
        Log.d(TAG, "Khởi động: đọc được " + confirmedIds.size() + " môn đã xác nhận.");

        bindViews();
        setupTabs();
        setupRecyclerView();
        setupFilterButtons();
        setupSearchBox();
        setupConfirmButton();
        // Đã xóa setupBackButton() tại đây

        showPage(true);
    }

    // ── Ánh xạ views ─────────────────────────────────────────────────────────
    private void bindViews() {
        tabDangKy           = findViewById(R.id.tabDangKy);
        tabKetQua           = findViewById(R.id.tabKetQua);
        pageDangKy          = findViewById(R.id.pageDangKy);
        pageKetQua          = findViewById(R.id.pageKetQua);

        // Đã xóa btnBack = findViewById(R.id.btnBack);
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

    // ── Tab ───────────────────────────────────────────────────────────────────
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

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG ĐĂNG KÝ
    // ══════════════════════════════════════════════════════════════════════════

    private void setupRecyclerView() {
        List<Course> courses = courseRepo.getAllCourses();
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
        try {
            courseRepo.registerCourse(course.getId());
            updateSelectedPanel();
            refreshAdapterPending();
            Toast.makeText(this, getString(R.string.course_added, course.getName()), Toast.LENGTH_SHORT).show();
        } catch (CourseException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateSelectedPanel() {
        Map<String, CourseRegistration> registrations = courseRepo.getRegistrations();
        if (registrations.isEmpty()) {
            layoutSelected.setVisibility(View.GONE);
            return;
        }
        layoutSelected.setVisibility(View.VISIBLE);
        layoutSelectedItems.removeAllViews();
        for (CourseRegistration reg : registrations.values()) {
            TextView tv = new TextView(this);
            tv.setText("• " + reg.getCourse().getName() + " (" + reg.getCourse().getCredits() + " TC)");
            tv.setTextSize(13);
            layoutSelectedItems.addView(tv);
        }
        txtTongTinChi.setText(getString(R.string.course_tong_tin_chi, courseRepo.getTotalRegisteredCredits()));
    }

    private void refreshAdapterPending() {
        List<String> allMarked = new ArrayList<>(confirmedIds);
        allMarked.addAll(courseRepo.getRegistrations().keySet());
        courseAdapter.setRegisteredIds(allMarked);
    }

    private void setupConfirmButton() {
        btnConfirm.setOnClickListener(v -> {
            Map<String, CourseRegistration> pending = courseRepo.getRegistrations();
            if (pending.isEmpty()) {
                Toast.makeText(this, getString(R.string.course_gio_trong), Toast.LENGTH_SHORT).show();
                return;
            }
            for (String id : pending.keySet()) {
                if (!confirmedIds.contains(id)) confirmedIds.add(id);
            }
            CourseStorage.saveConfirmedIds(this, confirmedIds);
            courseRepo.clearPendingRegistrations();
            layoutSelected.setVisibility(View.GONE);
            courseAdapter.setRegisteredIds(new ArrayList<>(confirmedIds));
            Toast.makeText(this, getString(R.string.course_xac_nhan_success), Toast.LENGTH_LONG).show();
        });
    }

    private void setupFilterButtons() {
        String all = getString(R.string.dorm_filter_all);
        String[] semesters = buildDisplayArray(all, SEMESTERS_CODES);
        String[] khoaHoc   = buildDisplayArray(all, KHOAHOC_CODES);
        String[] nganh     = buildDisplayArray(all, NGANH_CODES);

        btnHocKy.setOnClickListener(v -> showScrollableDialog(getString(R.string.course_chon_hoc_ky), semesters, choice -> {
            filterSemester = all.equals(choice) ? "" : choice;
            btnHocKy.setText(all.equals(choice)
                    ? getString(R.string.course_hoc_ky)
                    : getString(R.string.course_hoc_ky_prefix, choice));
            applyFilter();
        }));
        btnKhoaHoc.setOnClickListener(v -> showScrollableDialog(getString(R.string.course_chon_khoa), khoaHoc, choice -> {
            filterKhoaHoc = all.equals(choice) ? "" : choice;
            btnKhoaHoc.setText(all.equals(choice)
                    ? getString(R.string.course_khoa_hoc)
                    : getString(R.string.course_khoa_prefix, choice));
            applyFilter();
        }));
        btnNganh.setOnClickListener(v -> showScrollableDialog(getString(R.string.course_chon_nganh), nganh, choice -> {
            filterMajor = all.equals(choice) ? "" : choice;
            btnNganh.setText(all.equals(choice)
                    ? getString(R.string.course_nganh)
                    : getString(R.string.course_nganh_prefix, choice));
            applyFilter();
        }));
    }

    private void applyFilter() {
        courseAdapter.updateData(courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor));
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();
                List<Course> base = courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor);
                if (query.isEmpty()) { courseAdapter.updateData(base); return; }
                List<Course> filtered = new ArrayList<>();
                for (Course c : base) {
                    if (c.getName().toLowerCase().contains(query) || c.getCourseCode().toLowerCase().contains(query))
                        filtered.add(c);
                }
                courseAdapter.updateData(filtered);
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG KẾT QUẢ
    // ══════════════════════════════════════════════════════════════════════════

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
            Course c = courseRepo.findById(id);
            if (c == null) continue;
            tongTC += c.getCredits();

            // (Phần tạo Card UI tương tự như file cũ của bạn...)
            TextView tv = new TextView(this);
            tv.setText(c.getName() + " - " + c.getCredits() + " TC");
            layoutKetQuaItems.addView(tv);
        }
        txtKetQuaTongTinChi.setText(getString(R.string.course_tong_tc_ket_qua, tongTC));
    }

    // ── Dialog dropdown có scroll ─────────────────────────────────────────────
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

    /** Tạo mảng hiển thị với phần tử đầu tiên là nhãn "Tất cả" / "All" từ string resource. */
    private String[] buildDisplayArray(String allLabel, String[] codes) {
        String[] result = new String[codes.length];
        result[0] = allLabel;
        System.arraycopy(codes, 1, result, 1, codes.length - 1);
        return result;
    }
}