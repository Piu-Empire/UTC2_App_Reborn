package com.utc2.appreborn.ui.schedule;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.ScheduleItem;
import com.utc2.appreborn.utils.ViewUtils;
import com.utc2.appreborn.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment {

    public enum ViewMode {DAY, WEEK}

    private List<ScheduleItem> scheduleList;
    private ViewMode currentMode = ViewMode.DAY;
    private ViewMode pendingMode = null;
    private boolean isUpdating = false;

    private ScheduleNavigator dateScheduleNavigator;
    private ScheduleWeekManager weekManager;
    private ScheduleDayManager dayManager;

    private ImageButton btnToggle, btnSizeToggle;
    private View layoutDay;
    private RecyclerView recyclerViewSchedule;
    private ViewStub stubWeek;
    private View viewWeek;

    private TextView tvSemester, tvCurrentWeek;

    private final android.os.Handler timeUpdateHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());

    // khởi tạo fragment hiển thị lịch học của sinh viên
    public ScheduleFragment() {
        super(R.layout.fragment_schedule);
    }

    // thiết lập giao diện và các bộ quản lý dữ liệu
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateScheduleNavigator = new ScheduleNavigator();
        btnToggle = view.findViewById(R.id.btnCycleViewMode);
        btnSizeToggle = view.findViewById(R.id.btnSizeToggle);
        tvSemester = view.findViewById(R.id.tvSemester);
        tvCurrentWeek = view.findViewById(R.id.tvCurrentWeek);
        layoutDay = view.findViewById(R.id.layoutDay);
        recyclerViewSchedule = view.findViewById(R.id.recyclerViewSchedule);
        stubWeek = view.findViewById(R.id.stubWeek);

        dayManager = new ScheduleDayManager(requireContext(), view, dateScheduleNavigator, this);
        dayManager.init();

        setupData();

        DateUtils.setSemesterHK1(9, 1, 2, 28);
        DateUtils.setSemesterHK2(3, 2, 6, 7);

        updateDateDisplay();

        if (btnSizeToggle != null) {
            ViewUtils.hide(btnSizeToggle);
            btnSizeToggle.setOnClickListener(v -> {
                if (weekManager == null || isUpdating) return;
                weekManager.setWeekMaxMode(!weekManager.isWeekMaxMode());
                updateWeekUI();
            });
        }

        if (btnToggle != null) {
            btnToggle.setOnClickListener(v -> {
                ViewMode nextMode = (currentMode == ViewMode.DAY) ? ViewMode.WEEK : ViewMode.DAY;
                if (isUpdating) {
                    pendingMode = nextMode;
                    return;
                }
                applyMode(nextMode);
            });
        }

        View layoutHeader = view.findViewById(R.id.layoutHeader);
        if (layoutHeader != null) {
            layoutHeader.setOnClickListener(v -> showCalendarPicker());
        }
    }

    // đưa lịch về ngày hiện tại trong chế độ tuần
    private void goToToday() {
        if (currentMode == ViewMode.WEEK && weekManager != null) {
            Calendar now = Calendar.getInstance();
            dateScheduleNavigator.setCurrentCalendar(now);
            updateDateDisplay();
            weekManager.getScheduleCanvasView().setViewDate(now);
            weekManager.updateWeekHeaderTexts();
            weekManager.updateWeekSize(scheduleList, null);
        }
    }

    // kích hoạt trình cập nhật thời gian khi quay lại
    @Override
    public void onResume() {
        super.onResume();
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    // tạm dừng trình cập nhật để tiết kiệm tài nguyên máy
    @Override
    public void onPause() {
        super.onPause();
        timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
    }

    // nạp dữ liệu lịch học từ kho lưu trữ hệ thống
    private void setupData() {
        scheduleList = new ArrayList<>();
        scheduleList = com.utc2.appreborn.data.repository.ScheduleRepository.getMockScheduleData();
        dayManager.updateDayView(scheduleList);
    }

    // chuyển đổi giữa xem theo ngày và xem theo tuần
    private void applyMode(ViewMode mode) {
        currentMode = mode;
        updateDateDisplay();
        updateUIForMode();
    }

    // thay đổi giao diện phù hợp với chế độ hiển thị
    private void updateUIForMode() {
        if (btnToggle == null) return;
        isUpdating = true;

        ViewUtils.hide(layoutDay);
        if (viewWeek != null) ViewUtils.hide(viewWeek);

        if (btnSizeToggle != null) {
            if (currentMode == ViewMode.WEEK) ViewUtils.show(btnSizeToggle);
            else ViewUtils.hide(btnSizeToggle);
        }

        switch (currentMode) {
            case DAY:
                btnToggle.setImageResource(R.drawable.ic_layout_list);
                ViewUtils.show(layoutDay);
                isUpdating = false;
                processPendingMode();
                break;

            case WEEK:
                btnToggle.setImageResource(R.drawable.ic_columns_3);
                if (viewWeek == null && stubWeek != null && stubWeek.getParent() != null) {
                    viewWeek = stubWeek.inflate(); // nạp file xml lịch tuần vào bộ nhớ khi cần
                    weekManager = new ScheduleWeekManager(requireContext(), viewWeek, dateScheduleNavigator);
                    weekManager.init();
                } else if (viewWeek != null) {
                    ViewUtils.show(viewWeek);
                }
                updateWeekUI();
                break;
        }
    }

    // làm mới nội dung và kích thước của lịch tuần
    private void updateWeekUI() {
        if (weekManager == null) return;
        weekManager.updateWeekHeaderTexts();
        weekManager.updateWeekSize(scheduleList, () -> {
            isUpdating = false;
            processPendingMode();
        });
    }

    // cập nhật thông tin học kỳ và tuần lên màn hình
    void updateDateDisplay() {
        Calendar selectedCal = dateScheduleNavigator.getCurrentCalendar();
        if (tvSemester != null) tvSemester.setText(DateUtils.getSemesterByDate(selectedCal));
        if (tvCurrentWeek != null) tvCurrentWeek.setText(DateUtils.getWeekOfSemester(selectedCal));
        if (dayManager != null) dayManager.updateDayView(scheduleList);
        if (currentMode == ViewMode.WEEK && weekManager != null)
            weekManager.updateWeekHeaderTexts();
    }

    // hiển thị hộp thoại chọn ngày xem lịch cụ thể
    private void showCalendarPicker() {
        com.google.android.material.datepicker.MaterialDatePicker<Long> datePicker =
                com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Chọn ngày xem lịch")
                        .setSelection(dateScheduleNavigator.getCurrentCalendar().getTimeInMillis())
                        .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selection);
            dateScheduleNavigator.setCurrentCalendar(cal);
            updateDateDisplay();
            if (currentMode == ViewMode.WEEK && weekManager != null) {
                weekManager.getScheduleCanvasView().setViewDate(cal);
                updateWeekUI();
            }
        });
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    // xử lý chuyển đổi chế độ đang chờ để tránh lỗi
    private void processPendingMode() {
        if (pendingMode != null) {
            ViewMode next = pendingMode;
            pendingMode = null;
            if (next != currentMode) applyMode(next);
        }
    }

    // tự động vẽ lại đường chỉ giờ sau mỗi phút
    private final Runnable timeUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentMode == ViewMode.WEEK && weekManager != null) {
                weekManager.syncTimeCapsule();
                if (weekManager.getScheduleCanvasView() != null) {
                    weekManager.getScheduleCanvasView().invalidate(); // ép buộc view vẽ lại đường vạch đỏ giờ hiện tại
                }
            }
            timeUpdateHandler.postDelayed(this, 60000); // lặp lại chu kỳ sau sáu mươi giây
        }
    };
}