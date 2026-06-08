package com.utc2.appreborn.ui.courseregistration;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class CourseRegistrationFragment extends Fragment {

    private static final String TAG = "CourseRegFragment";

    // ── Tab & Pages ──────────────────────────────────────────────────────────
    private TextView     tabDangKy, tabKetQua;
    private LinearLayout pageDangKy, pageKetQua;

    // ── Views trang Đăng ký ──────────────────────────────────────────────────
    private TextView     btnBack, btnHocKy, btnKhoaHoc, btnNganh, btnConfirm;
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

    private static final String[] SEMESTERS = {"Tất cả", "HK1", "HK2", "HK3"};
    private static final String[] KHOAHOC   = {
            "Tất cả","K51","K52","K53","K54","K55","K56","K57","K58","K59","K60",
            "K61","K62","K63","K64","K65","K66","K67","K68","K69","K70"
    };
    private static final String[] NGANH = {
            "Tất cả","CNTT","KTPM","HTTT","MMT","CK","XD","KT","MT","DTVT"
    };

    // ─────────────────────────────────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_course_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        courseRepo = CourseRepository.getInstance();

        confirmedIds = CourseStorage.loadConfirmedIds(requireContext());
        Log.d(TAG, "Khởi động: đọc được " + confirmedIds.size() + " môn đã xác nhận từ file.");

        bindViews(view);
        setupTabs();
        setupRecyclerView();
        setupFilterButtons();
        setupSearchBox();
        setupConfirmButton();

        showPage(true);
    }

    // ── Ánh xạ views ─────────────────────────────────────────────────────────
    private void bindViews(View view) {
        tabDangKy           = view.findViewById(R.id.tabDangKy);
        tabKetQua           = view.findViewById(R.id.tabKetQua);
        pageDangKy          = view.findViewById(R.id.pageDangKy);
        pageKetQua          = view.findViewById(R.id.pageKetQua);

        btnBack             = view.findViewById(R.id.btnBack);
        btnHocKy            = view.findViewById(R.id.btnHocKy);
        btnKhoaHoc          = view.findViewById(R.id.btnKhoa);
        btnNganh            = view.findViewById(R.id.btnNganh);
        btnConfirm          = view.findViewById(R.id.btnConfirm);

        searchBox           = view.findViewById(R.id.searchBox);
        rvCourses           = view.findViewById(R.id.rvCourses);
        layoutSelected      = view.findViewById(R.id.layoutSelected);
        layoutSelectedItems = view.findViewById(R.id.layoutSelectedItems);
        txtTongTinChi       = view.findViewById(R.id.txtTongTinChi);

        layoutKetQuaItems    = view.findViewById(R.id.layoutKetQuaItems);
        txtKetQuaTongTinChi  = view.findViewById(R.id.txtKetQuaTongTinChi);
        txtKetQuaTrong       = view.findViewById(R.id.txtKetQuaTrong);

        btnHocKy.setText("Học kỳ ▾");
        btnKhoaHoc.setText("Khóa học ▾");
        btnNganh.setText("Ngành ▾");
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
        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCourses.setNestedScrollingEnabled(false);
        rvCourses.setAdapter(courseAdapter);
    }

    private void handleRegisterClick(Course course) {
        if (confirmedIds.contains(course.getId())) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Môn này đã được xác nhận rồi!");
            return;
        }
        try {
            courseRepo.registerCourse(course.getId());
            updateSelectedPanel();
            refreshAdapterPending();
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Đã thêm \"" + course.getName() + "\" vào giỏ đăng ký.");

        } catch (CourseException e) {
            Log.w(TAG, "CourseException: " + e.getMessage());
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), e.getMessage());

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error", e);
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Lỗi không xác định, vui lòng thử lại.");

        } finally {
            Log.d(TAG, "handleRegisterClick() done – courseId: " + course.getId());
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
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 6, 0, 6);

            TextView tvName = new TextView(requireContext());
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvName.setText("• " + reg.getCourse().getName());
            tvName.setTextColor(Color.parseColor("#333333"));
            tvName.setTextSize(13);

            TextView tvCredits = new TextView(requireContext());
            tvCredits.setText(reg.getCourse().getCredits() + " TC");
            tvCredits.setTextColor(Color.parseColor("#555555"));
            tvCredits.setTextSize(13);

            row.addView(tvName);
            row.addView(tvCredits);
            layoutSelectedItems.addView(row);
        }
        txtTongTinChi.setText("Tổng số tín chỉ: " + courseRepo.getTotalRegisteredCredits());
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
                com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Chưa có môn học nào trong giỏ đăng ký!");
                return;
            }

            for (String id : pending.keySet()) {
                if (!confirmedIds.contains(id)) confirmedIds.add(id);
            }

            CourseStorage.saveConfirmedIds(requireContext(), confirmedIds);

            // Lưu credits: đọc map hiện tại rồi cập nhật thêm các môn vừa xác nhận
            java.util.Map<String, Integer> creditsMap = CourseStorage.loadCreditsMap(requireContext());
            for (String id : confirmedIds) {
                Course c = courseRepo.findById(id);
                if (c != null) creditsMap.put(id, c.getCredits());
            }
            CourseStorage.saveCreditsMap(requireContext(), creditsMap);

            courseRepo.clearPendingRegistrations();
            layoutSelected.setVisibility(View.GONE);
            layoutSelectedItems.removeAllViews();

            courseAdapter.setRegisteredIds(new ArrayList<>(confirmedIds));

            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Xác nhận đăng ký thành công! Đã lưu dữ liệu.");
        });
    }

    private void setupFilterButtons() {
        btnHocKy.setOnClickListener(v ->
                showScrollableDialog("Chọn học kỳ", SEMESTERS, choice -> {
                    filterSemester = "Tất cả".equals(choice) ? "" : choice;
                    btnHocKy.setText("Tất cả".equals(choice) ? "Học kỳ ▾" : "Học kỳ: " + choice + " ▾");
                    applyFilter();
                }));
        btnKhoaHoc.setOnClickListener(v ->
                showScrollableDialog("Chọn khóa học", KHOAHOC, choice -> {
                    filterKhoaHoc = "Tất cả".equals(choice) ? "" : choice;
                    btnKhoaHoc.setText("Tất cả".equals(choice) ? "Khóa học ▾" : choice + " ▾");
                    applyFilter();
                }));
        btnNganh.setOnClickListener(v ->
                showScrollableDialog("Chọn ngành", NGANH, choice -> {
                    filterMajor = "Tất cả".equals(choice) ? "" : choice;
                    btnNganh.setText("Tất cả".equals(choice) ? "Ngành ▾" : "Ngành: " + choice + " ▾");
                    applyFilter();
                }));
    }

    private void applyFilter() {
        try {
            courseAdapter.updateData(courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor));
        } finally {
            Log.d(TAG, "applyFilter() done.");
        }
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String query = s.toString().trim().toLowerCase();
                    List<Course> base = courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor);
                    if (query.isEmpty()) { courseAdapter.updateData(base); return; }
                    List<Course> filtered = new ArrayList<>();
                    for (Course c : base) {
                        if (c.getName().toLowerCase().contains(query)
                                || c.getCourseCode().toLowerCase().contains(query))
                            filtered.add(c);
                    }
                    courseAdapter.updateData(filtered);
                } catch (Exception e) {
                    Log.e(TAG, "Search error", e);
                }
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
        int stt    = 1;

        List<String> idsCopy = new ArrayList<>(confirmedIds);
        for (String id : idsCopy) {
            Course c = courseRepo.findById(id);
            if (c == null) continue;
            tongTC += c.getCredits();

            android.widget.FrameLayout card = new android.widget.FrameLayout(requireContext());
            card.setBackgroundResource(R.drawable.bg_search);
            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardLp.setMargins(0, 0, 0, 12);
            card.setLayoutParams(cardLp);

            LinearLayout contentLayout = new LinearLayout(requireContext());
            contentLayout.setOrientation(LinearLayout.VERTICAL);
            contentLayout.setPadding(dp(12), dp(12), dp(90), dp(12));
            contentLayout.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout rowTitle = new LinearLayout(requireContext());
            rowTitle.setOrientation(LinearLayout.HORIZONTAL);
            rowTitle.setGravity(android.view.Gravity.CENTER_VERTICAL);

            TextView tvStt = new TextView(requireContext());
            tvStt.setText(stt + ". ");
            tvStt.setTextSize(14);
            tvStt.setTextColor(Color.parseColor("#E53935"));
            tvStt.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvName = new TextView(requireContext());
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvName.setText(c.getName());
            tvName.setTextSize(14);
            tvName.setTextColor(Color.BLACK);
            tvName.setTypeface(null, android.graphics.Typeface.BOLD);

            rowTitle.addView(tvStt);
            rowTitle.addView(tvName);
            contentLayout.addView(rowTitle);

            View div = new View(requireContext());
            LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            divLp.setMargins(0, dp(6), 0, dp(6));
            div.setLayoutParams(divLp);
            div.setBackgroundColor(Color.parseColor("#EEEEEE"));
            contentLayout.addView(div);

            contentLayout.addView(makeInfoRow("Mã môn: ",      c.getCourseCode()));
            contentLayout.addView(makeInfoRow("Số tín chỉ: ",  String.valueOf(c.getCredits())));
            contentLayout.addView(makeInfoRow("Giảng viên: ",  c.getLecturer()));
            contentLayout.addView(makeInfoRow("Thời gian: ",   c.getSchedule()));
            contentLayout.addView(makeInfoRow("Phòng: ",       c.getRoom()));
            contentLayout.addView(makeInfoRow("Bắt đầu: ",     c.getStartDate()));
            contentLayout.addView(makeInfoRow("Kết thúc: ",    c.getEndDate()));
            contentLayout.addView(makeInfoRow("Số tiết: ",     String.valueOf(c.getTotalPeriods())));

            card.addView(contentLayout);

            TextView btnHuy = new TextView(requireContext());
            android.widget.FrameLayout.LayoutParams huyLp = new android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, dp(40));
            huyLp.gravity = android.view.Gravity.END | android.view.Gravity.CENTER_VERTICAL;
            huyLp.setMargins(0, 0, dp(12), 0);
            btnHuy.setLayoutParams(huyLp);
            btnHuy.setText("Hủy đăng ký");
            btnHuy.setTextSize(12);
            btnHuy.setTypeface(null, android.graphics.Typeface.BOLD);
            btnHuy.setTextColor(Color.WHITE);
            btnHuy.setBackgroundResource(R.drawable.bg_button_black);
            btnHuy.setGravity(android.view.Gravity.CENTER);
            btnHuy.setPadding(dp(14), 0, dp(14), 0);
            btnHuy.setClickable(true);
            btnHuy.setFocusable(true);

            final String courseId = id;
            btnHuy.setOnClickListener(v -> huyDangKy(courseId));
            card.addView(btnHuy);

            layoutKetQuaItems.addView(card);
            stt++;
        }

        txtKetQuaTongTinChi.setText("Tổng số tín chỉ đã đăng ký: " + tongTC);
    }

    private void huyDangKy(String courseId) {
        Course c = courseRepo.findById(courseId);
        String tenMon = (c != null) ? c.getName() : courseId;

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận hủy đăng ký")
                .setMessage("Bạn có chắc muốn hủy đăng ký môn\n\"" + tenMon + "\"?")
                .setPositiveButton("Hủy đăng ký", (dialog, which) -> {
                    confirmedIds.remove(courseId);
                    CourseStorage.saveConfirmedIds(requireContext(), confirmedIds);
                    courseAdapter.setRegisteredIds(new ArrayList<>(confirmedIds));
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Đã hủy đăng ký môn \"" + tenMon + "\"");
                    buildKetQuaPage();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private TextView makeInfoRow(String label, String value) {
        TextView tv = new TextView(requireContext());
        android.text.SpannableString ss = new android.text.SpannableString(label + value);
        ss.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#E53935")),
                0, label.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new android.text.style.ForegroundColorSpan(Color.BLACK),
                label.length(), ss.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
        tv.setTextSize(13);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(2), 0, 0);
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    // ── Dialog dropdown có scroll ─────────────────────────────────────────────
    private interface MenuCallback { void onSelected(String choice); }

    private void showScrollableDialog(String title, String[] options, MenuCallback cb) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(0, 0, 0, 0);

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTitle.setPadding(48, 40, 48, 32);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(tvTitle);

        View div = new View(requireContext());
        LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        div.setLayoutParams(divLp);
        div.setBackgroundColor(Color.parseColor("#EEEEEE"));
        root.addView(div);

        ScrollView scrollView = new ScrollView(requireContext());
        int maxH = (int) (320 * getResources().getDisplayMetrics().density);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, maxH));

        LinearLayout listLayout = new LinearLayout(requireContext());
        listLayout.setOrientation(LinearLayout.VERTICAL);

        for (String opt : options) {
            TextView item = new TextView(requireContext());
            item.setText(opt);
            item.setTextSize(15);
            item.setTextColor(Color.parseColor("#222222"));
            item.setPadding(48, 36, 48, 36);
            item.setClickable(true);
            item.setFocusable(true);
            item.setOnClickListener(v -> { cb.onSelected(opt); dialog.dismiss(); });
            listLayout.addView(item);

            View sep = new View(requireContext());
            LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            sepLp.setMargins(48, 0, 48, 0);
            sep.setLayoutParams(sepLp);
            sep.setBackgroundColor(Color.parseColor("#F0F0F0"));
            listLayout.addView(sep);
        }

        scrollView.addView(listLayout);
        root.addView(scrollView);
        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (300 * getResources().getDisplayMetrics().density),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame);
        }
        dialog.show();
    }
}