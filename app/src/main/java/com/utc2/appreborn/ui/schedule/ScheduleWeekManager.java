package com.utc2.appreborn.ui.schedule;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.ScheduleItem;
import com.utc2.appreborn.utils.DateUtils;
import com.utc2.appreborn.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleWeekManager {

    private final Context context;
    private final View viewWeek;
    private final ScheduleNavigator dateController;

    private final NestedScrollView nestedScroll;
    private final HorizontalScrollView headerScroll, floatingHeaderScroll;
    private final ViewGroup weekHeaderContainer, floatingHeaderContainer;
    private final View floatingHeaderRoot;
    private final ScheduleCanvasView scheduleCanvasView;

    private boolean isWeekMaxMode = true;
    private boolean isUpdating = false;

    // khởi tạo và gán các thành phần giao diện tuần
    public ScheduleWeekManager(Context context, View viewWeek, ScheduleNavigator dateController) {
        this.context = context;
        this.viewWeek = viewWeek;
        this.dateController = dateController;
        this.weekHeaderContainer = viewWeek.findViewById(R.id.weekHeaderContainer);
        this.floatingHeaderContainer = viewWeek.findViewById(R.id.floatingHeaderContainer);
        this.floatingHeaderRoot = viewWeek.findViewById(R.id.floatingHeaderRoot);
        this.headerScroll = viewWeek.findViewById(R.id.headerScroll);
        this.floatingHeaderScroll = viewWeek.findViewById(R.id.floatingHeaderScroll);
        this.nestedScroll = viewWeek.findViewById(R.id.nestedScroll);
        this.scheduleCanvasView = viewWeek.findViewById(R.id.scheduleCanvasView);
    }

    // thiết lập các thành phần giao diện cơ bản ban đầu
    public void init() {
        floatingHeaderRoot.setVisibility(View.INVISIBLE);
        setupHeaderContainers();
        setupStickyLogic();
        setupTimeColumnMargins();
        syncTimeCapsule();
        if (scheduleCanvasView != null) {
            scheduleCanvasView.invalidate();
        }
    }

    // tự động tạo bảy cột tiêu đề ngày trong tuần
    private void setupHeaderContainers() {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (weekHeaderContainer != null) weekHeaderContainer.removeAllViews();
        if (floatingHeaderContainer != null) floatingHeaderContainer.removeAllViews();

        for (int i = 0; i < 7; i++) {
            if (weekHeaderContainer != null) {
                inflater.inflate(R.layout.item_week_day_header, weekHeaderContainer, true);
            }
            if (floatingHeaderContainer != null) {
                inflater.inflate(R.layout.item_week_day_header, floatingHeaderContainer, true);
            }
        }
    }

    // điều chỉnh kích thước bảng lịch và lọc dữ liệu tuần
    public void updateWeekSize(List<ScheduleItem> allSchedules, Runnable onComplete) {
        if (viewWeek == null || scheduleCanvasView == null) {
            if (onComplete != null) onComplete.run();
            return;
        }
        isUpdating = true;
        Calendar currentWeekStart = DateUtils.getFirstDayOfWeek(dateController.getCurrentCalendar());
        List<ScheduleItem> filteredWeekList = new ArrayList<>();
        for (ScheduleItem item : allSchedules) {
            if (item.isVisibleInWeek(currentWeekStart)) {
                filteredWeekList.add(item);
            }
        }

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int timeColumnWidth = ViewUtils.dpToPx(context, 60);
        // quyết định chiều ngang bảng dựa trên chế độ xem rộng hay hẹp
        int newWidth = isWeekMaxMode ? ViewUtils.dpToPx(context, 1000) : (screenWidth - timeColumnWidth);

        ViewUtils.setWidth(weekHeaderContainer, newWidth);
        ViewUtils.setWidth(floatingHeaderContainer, newWidth);
        ViewUtils.setWidth(scheduleCanvasView, newWidth);

        int columnWidth = newWidth / 7;
        if (weekHeaderContainer != null) updateHeaderItemsWidth(weekHeaderContainer, columnWidth);
        if (floatingHeaderContainer != null)
            updateHeaderItemsWidth(floatingHeaderContainer, columnWidth);

        updateScheduleHeight();
        scheduleCanvasView.setMaxMode(isWeekMaxMode);
        scheduleCanvasView.setViewDate(dateController.getCurrentCalendar());
        scheduleCanvasView.setData(filteredWeekList);

        if (isViewingCurrentWeek()) {
            viewWeek.post(this::scrollToCurrentTime);
        }
        isUpdating = false;
        if (onComplete != null) onComplete.run();
    }

    // thay đổi độ rộng của các ô tiêu đề ngày học
    private void updateHeaderItemsWidth(ViewGroup container, int width) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View dayHeader = container.getChildAt(i);
            ViewUtils.setWidth(dayHeader, width);
        }
    }

    // cập nhật dữ liệu chữ hiển thị cho thanh tiêu đề
    public void updateWeekHeaderTexts() {
        List<ScheduleNavigator.DateModel> days = dateController.getDaysInWeek();
        if (weekHeaderContainer != null) renderHeaderData(weekHeaderContainer, days);
        if (floatingHeaderContainer != null) renderHeaderData(floatingHeaderContainer, days);
    }

    // đổ dữ liệu và định dạng màu cho ngày hiện tại
    private void renderHeaderData(ViewGroup container, List<ScheduleNavigator.DateModel> days) {
        for (int i = 0; i < container.getChildCount() && i < days.size(); i++) {
            View dayHeader = container.getChildAt(i);
            TextView tvDayName = dayHeader.findViewById(R.id.tvDayName);
            TextView tvDayNumber = dayHeader.findViewById(R.id.tvDayNumber);

            ScheduleNavigator.DateModel model = days.get(i);
            if (tvDayNumber != null) tvDayNumber.setText(model.dayMonth);
            if (tvDayName != null) tvDayName.setText(model.dayName);

            if (model.isToday) {
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(ScheduleColors.WeekHeader.todayBackground(context));
                bg.setCornerRadius(ViewUtils.dpToPx(context, 8));
                bg.setStroke(ViewUtils.dpToPx(context, 1.5f), ScheduleColors.WeekHeader.todayStroke(context));
                dayHeader.setBackground(bg);

                if (tvDayNumber != null) {
                    tvDayNumber.setTextColor(ScheduleColors.WeekHeader.todayDayNumber(context));
                    tvDayNumber.setTypeface(null, Typeface.BOLD);
                }
                if (tvDayName != null) {
                    tvDayName.setTextColor(ScheduleColors.WeekHeader.todayDayName(context));
                    tvDayName.setTypeface(null, Typeface.BOLD);
                }
                animatePulse(dayHeader);
            } else {
                dayHeader.setBackground(null);
                if (tvDayNumber != null) {
                    tvDayNumber.setTextColor(ScheduleColors.WeekHeader.normalDayNumber(context));
                    tvDayNumber.setTypeface(null, Typeface.NORMAL);
                }
                if (tvDayName != null) {
                    tvDayName.setTextColor(ScheduleColors.WeekHeader.normalDayName(context));
                    tvDayName.setTypeface(null, Typeface.NORMAL);
                }
            }
        }
    }

    // tạo hiệu ứng thu phóng nhẹ để thu hút chú ý
    private void animatePulse(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.06f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.06f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);
        scaleX.start();
        scaleY.start();
    }

    // tính toán tổng chiều cao bảng theo mốc thời gian
    public void updateScheduleHeight() {
        if (scheduleCanvasView == null) return;
        int hourHeightPx = context.getResources().getDimensionPixelSize(R.dimen.distance_time_schedule);
        int totalHours = 18;
        int totalHeightPx = hourHeightPx * totalHours;
        ViewGroup.LayoutParams params = scheduleCanvasView.getLayoutParams();
        params.height = totalHeightPx;
        scheduleCanvasView.setLayoutParams(params);
    }

    // đồng bộ hóa cuộn ngang và cuộn dọc tiêu đề
    public void setupStickyLogic() {
        View.OnScrollChangeListener stickyAndSyncListener = (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            updateStickyHeaderVisibility();
            if (v == headerScroll) {
                floatingHeaderScroll.setScrollX(scrollX);
            } else if (v == floatingHeaderScroll) {
                headerScroll.setScrollX(scrollX);
            }
        };

        nestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> updateStickyHeaderVisibility());

        headerScroll.setOnScrollChangeListener(stickyAndSyncListener);
        floatingHeaderScroll.setOnScrollChangeListener(stickyAndSyncListener);

        viewWeek.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewWeek.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateStickyHeaderVisibility();
                floatingHeaderScroll.setScrollX(headerScroll.getScrollX());
            }
        });
    }

    // căn chỉnh lề cho các mốc giờ cột bên trái
    public void setupTimeColumnMargins() {
        int[] timeViewIds = {
                R.id.tv07, R.id.tv08, R.id.tv09, R.id.tv10, R.id.tv11,
                R.id.tv12, R.id.tv13, R.id.tv14, R.id.tv15, R.id.tv16,
                R.id.tv17, R.id.tv18, R.id.tv19, R.id.tv20, R.id.tv21,
                R.id.tv22, R.id.tv23
        };
        int distanceTimePx = context.getResources().getDimensionPixelSize(R.dimen.distance_time_schedule);
        int baseMarginPx = ViewUtils.dpToPx(context, 100);

        for (int i = 0; i < timeViewIds.length; i++) {
            TextView tvHour = viewWeek.findViewById(timeViewIds[i]);
            if (tvHour != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvHour.getLayoutParams();
                params.topMargin = baseMarginPx + (i * distanceTimePx);
                tvHour.setLayoutParams(params);
            }
        }
    }

    // tự động cuộn màn hình đến vị trí giờ hiện thực
    public void scrollToCurrentTime() {
        if (viewWeek == null || nestedScroll == null || headerScroll == null) return;
        viewWeek.post(() -> {
            Calendar now = Calendar.getInstance();
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int todayIndex = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - 2;

            int targetX = 0;
            if (isWeekMaxMode && scheduleCanvasView != null) {
                int totalWidth = scheduleCanvasView.getWidth();
                if (totalWidth > 0) {
                    int colWidth = totalWidth / 7;
                    targetX = todayIndex * colWidth;
                }
            }

            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            int startHour = 6;

            if (hour >= startHour && hour < 24) {
                int hourHeightPx = context.getResources().getDimensionPixelSize(R.dimen.distance_time_schedule);
                int baseMarginPx = ViewUtils.dpToPx(context, 100);
                float hoursPassed = (hour - startHour) + (minute / 60f);
                // tính toán tọa độ Y dựa trên số giờ đã trôi qua
                int targetY = baseMarginPx + (int) (hoursPassed * hourHeightPx);
                int finalY = Math.max(0, targetY - 100);
                nestedScroll.smoothScrollTo(0, finalY);
            }

            final int finalTargetX = targetX;
            headerScroll.smoothScrollTo(finalTargetX, 0);
            new Handler(Looper.getMainLooper()).postDelayed(this::updateStickyHeaderVisibility, 200);

            if (scheduleCanvasView != null) {
                scheduleCanvasView.postDelayed(() -> scheduleCanvasView.invalidate(), 100);
            }
        });
    }

    // kiểm soát sự ẩn hiện của thanh tiêu đề nổi
    public void updateStickyHeaderVisibility() {
        if (nestedScroll == null || weekHeaderContainer == null || floatingHeaderRoot == null)
            return;

        int scrollY = nestedScroll.getScrollY();
        int headerBottom = weekHeaderContainer.getBottom();
        if (headerBottom <= 0) {
            weekHeaderContainer.post(this::updateStickyHeaderVisibility);
            return;
        }

        if (scrollY > headerBottom) {
            if (floatingHeaderRoot.getVisibility() != View.VISIBLE) {
                // đồng bộ vị trí trượt ngang của tiêu đề cố định và nổi
                floatingHeaderScroll.setScrollX(headerScroll.getScrollX());
                floatingHeaderRoot.setVisibility(View.VISIBLE);
            }
        } else {
            floatingHeaderRoot.setVisibility(View.GONE);
        }
    }

    // định vị nhãn thời gian hiện tại theo trục đứng
    public void syncTimeCapsule() {
        TextView tvCapsule = viewWeek.findViewById(R.id.tvCurrentTimeLabel);
        if (tvCapsule == null || scheduleCanvasView == null) return;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        if (hour >= 6 && hour < 24) {
            tvCapsule.setVisibility(View.VISIBLE);
            tvCapsule.setText(String.format("%02d:%02d", hour, minute));

            int hourHeightPx = context.getResources().getDimensionPixelSize(R.dimen.distance_time_schedule);
            float hoursFromStart = (hour - 6 + 1) + (minute / 60f);
            float y = hoursFromStart * hourHeightPx;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvCapsule.getLayoutParams();
            int pillHeightPx = tvCapsule.getHeight();
            params.topMargin = (int) y - (pillHeightPx / 2);
            tvCapsule.setLayoutParams(params);
        } else {
            tvCapsule.setVisibility(View.GONE);
        }
    }

    // xác định xem có đang xem lịch tuần hiện tại không
    public boolean isViewingCurrentWeek() {
        Calendar now = Calendar.getInstance();
        Calendar currentView = dateController.getCurrentCalendar();
        return now.get(Calendar.YEAR) == currentView.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == currentView.get(Calendar.WEEK_OF_YEAR);
    }

    // thiết lập trạng thái mở rộng hoặc thu gọn tuần
    public void setWeekMaxMode(boolean weekMaxMode) {
        this.isWeekMaxMode = weekMaxMode;
    }

    // kiểm tra chế độ hiển thị tuần hiện tại là gì
    public boolean isWeekMaxMode() {
        return isWeekMaxMode;
    }

    // cung cấp trạng thái đang cập nhật của bộ quản lý
    public boolean isUpdating() {
        return isUpdating;
    }

    // trích xuất thành phần vẽ lịch tuần từ giao diện
    public ScheduleCanvasView getScheduleCanvasView() {
        return scheduleCanvasView;
    }
}