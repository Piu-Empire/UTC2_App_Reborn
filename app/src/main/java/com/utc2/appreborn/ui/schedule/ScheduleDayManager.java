package com.utc2.appreborn.ui.schedule;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.ScheduleItem;
import com.utc2.appreborn.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleDayManager {
    private final Context context;
    private final View rootView;
    private final RecyclerView recyclerViewSchedule;
    private final RecyclerView rvDateSelector;
    private final TextView tvFullDateLabel;
    private final ScheduleNavigator dateController;
    private final ScheduleFragment fragment;

    private ScheduleAdapter scheduleAdapter;
    private DateSelectorAdapter dateSelectorAdapter;

    private boolean isHorizontalLoading = false;
    private boolean isVerticalLoading = false;
    private boolean isSyncingFromVerticalScroll = false;

    private Calendar firstLoadedVerticalDate;
    private Calendar lastLoadedVerticalDate;
    private final List<ScheduleItem> continuousScheduleList = new ArrayList<>();

    private final Map<String, List<ScheduleItem>> scheduleMap = new HashMap<>();
    private final Map<String, Integer> datePositionMap = new HashMap<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Calendar today = Calendar.getInstance();

    // gán các giá trị phụ thuộc từ môi trường bên ngoài
    public ScheduleDayManager(Context context, View rootView, ScheduleNavigator dateController, ScheduleFragment fragment) {
        this.context = context;
        this.rootView = rootView;
        this.recyclerViewSchedule = rootView.findViewById(R.id.recyclerViewSchedule);
        this.rvDateSelector = rootView.findViewById(R.id.rvDateSelector);
        this.tvFullDateLabel = rootView.findViewById(R.id.tvFullDateLabel);
        this.dateController = dateController;
        this.fragment = fragment;
    }

    // thiết lập các thành phần giao diện và bộ lắng nghe
    public void init() {
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(context);
        recyclerViewSchedule.setLayoutManager(verticalLayoutManager);
        recyclerViewSchedule.setItemViewCacheSize(30);
        recyclerViewSchedule.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        if (tvFullDateLabel != null) {
            tvFullDateLabel.setTextColor(ScheduleColors.DayManager.fullDateText(context));
        }

        scheduleAdapter = new ScheduleAdapter(continuousScheduleList);
        recyclerViewSchedule.setAdapter(scheduleAdapter);

        View btnGoToday = rootView.findViewById(R.id.btnGoToday);
        if (btnGoToday != null) {
            btnGoToday.setOnClickListener(v -> {
                Calendar now = Calendar.getInstance();
                dateController.setCurrentCalendar(now);
                dateSelectorAdapter.updateSelectedDate(now);
                fadeAndRefreshVerticalList(now);
                smoothScrollToHorizontalDate(now);
                if (fragment != null) fragment.updateDateDisplay();
            });
        }

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvDateSelector.setLayoutManager(horizontalLayoutManager);

        dateSelectorAdapter = new DateSelectorAdapter(today, date -> {
            if (!DateUtils.isSameDay(date, dateController.getCurrentCalendar())) {
                isSyncingFromVerticalScroll = false;
                dateController.setCurrentCalendar(date);
                if (fragment != null) fragment.updateDateDisplay();
                fadeAndRefreshVerticalList(date);
                smoothScrollToHorizontalDate(date);
            }
        });
        dateSelectorAdapter.initAroundDate(today);
        rvDateSelector.setAdapter(dateSelectorAdapter);

        rvDateSelector.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dx == 0) return;
                int firstVisible = horizontalLayoutManager.findFirstVisibleItemPosition();
                int lastVisible = horizontalLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = horizontalLayoutManager.getItemCount();

                if (lastVisible >= totalItemCount - 3) {
                    recyclerView.post(() -> dateSelectorAdapter.addFutureWeek());
                }
                if (firstVisible <= 3 && !isHorizontalLoading) {
                    recyclerView.post(() -> loadMorePastHorizontal(horizontalLayoutManager));
                }

                int centerPos = (firstVisible + lastVisible) / 2;
                if (centerPos != RecyclerView.NO_POSITION && tvFullDateLabel != null) {
                    Calendar centerDate = dateSelectorAdapter.getDateAtPosition(centerPos);
                    if (centerDate != null) {
                        tvFullDateLabel.setText(ScheduleLanguage.Format.shortDate(centerDate));
                        tvFullDateLabel.setTextColor(ScheduleColors.DayManager.fullDateText(context));
                    }
                }
            }
        });

        recyclerViewSchedule.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy == 0 || continuousScheduleList.isEmpty()) return;

                int firstVisible = verticalLayoutManager.findFirstVisibleItemPosition();
                int lastVisible = verticalLayoutManager.findLastVisibleItemPosition();
                int total = verticalLayoutManager.getItemCount();

                if (firstVisible != RecyclerView.NO_POSITION && firstVisible < continuousScheduleList.size()) {
                    Calendar topDate = continuousScheduleList.get(firstVisible).getDisplayDate();
                    if (topDate != null && !DateUtils.isSameDay(topDate, dateController.getCurrentCalendar())) {
                        isSyncingFromVerticalScroll = true;
                        dateController.setCurrentCalendar(topDate);
                        dateSelectorAdapter.updateSelectedDate(topDate);
                        smoothScrollToHorizontalDate(topDate);
                        if (tvFullDateLabel != null) {
                            tvFullDateLabel.setText(ScheduleLanguage.Format.shortDate(topDate));
                            tvFullDateLabel.setTextColor(ScheduleColors.DayManager.fullDateText(context));
                        }
                    }
                }

                if (dy > 0 && !isVerticalLoading && lastVisible >= total - 5) {
                    loadNextDaySchedules();
                    if (total > 100) trimList(true);
                }
                if (dy < 0 && !isVerticalLoading && firstVisible <= 5) {
                    loadPastDaySchedules();
                    if (total > 100) trimList(false);
                }
            }
        });

        rvDateSelector.post(() -> smoothScrollToHorizontalDate(today));
    }

    // tạo hiệu ứng chuyển cảnh mờ dần khi đổi ngày
    private void fadeAndRefreshVerticalList(Calendar targetDate) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(recyclerViewSchedule, "alpha", 1f, 0f);
        fadeOut.setDuration(120);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                refreshVerticalList(targetDate);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(recyclerViewSchedule, "alpha", 0f, 1f);
                fadeIn.setDuration(200);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.start();
            }
        });
        fadeOut.start();
    }

    // đưa ngày được chọn vào chính giữa thanh trượt ngang
    public void smoothScrollToHorizontalDate(Calendar date) {
        int pos = dateSelectorAdapter.getPositionForDate(date);
        if (pos == -1) {
            dateSelectorAdapter.initAroundDate(date);
            pos = dateSelectorAdapter.getPositionForDate(date);
        }
        LinearLayoutManager lm = (LinearLayoutManager) rvDateSelector.getLayoutManager();
        if (lm == null || pos == -1) return;

        int firstVisible = lm.findFirstVisibleItemPosition();
        int lastVisible = lm.findLastVisibleItemPosition();
        boolean isNearby = Math.abs(pos - (firstVisible + lastVisible) / 2) <= 7;

        if (isNearby) {
            RecyclerView.SmoothScroller smoothScroller = new androidx.recyclerview.widget.LinearSmoothScroller(context) {
                @Override
                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                    return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
                }

                @Override
                protected float calculateSpeedPerPixel(android.util.DisplayMetrics displayMetrics) {
                    return 120f / displayMetrics.densityDpi;
                }
            };
            smoothScroller.setTargetPosition(pos);
            lm.startSmoothScroll(smoothScroller);
        } else {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int offset = (screenWidth / 2) - (int) (30 * context.getResources().getDisplayMetrics().density);
            lm.scrollToPositionWithOffset(pos, offset);
        }
    }

    // xử lý dữ liệu lịch học trong luồng nền hiệu quả
    private void buildScheduleMap(List<ScheduleItem> source, Runnable onComplete) {
        executorService.execute(() -> {
            Map<String, List<ScheduleItem>> tempMap = new HashMap<>();
            if (source != null) {
                for (ScheduleItem item : source) {
                    Calendar start = DateUtils.parseDate(item.getStartDate());
                    Calendar end = DateUtils.parseDate(item.getEndDate());
                    if (start == null || end == null) continue;
                    Calendar cursor = (Calendar) start.clone();
                    while (!cursor.after(end)) {
                        if (item.isHappeningOn(cursor)) {
                            String key = DateUtils.format(cursor, "yyyy-MM-dd");
                            tempMap.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
                        }
                        cursor.add(Calendar.DATE, 1);
                    }
                }
                for (List<ScheduleItem> list : tempMap.values()) {
                    Collections.sort(list, (a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                }
            }
            mainHandler.post(() -> {
                scheduleMap.clear();
                scheduleMap.putAll(tempMap);
                onComplete.run();
            });
        });
    }

    // làm mới danh sách lịch học dựa trên ngày chọn
    private void refreshVerticalList(Calendar startDate) {
        continuousScheduleList.clear();
        datePositionMap.clear();
        Calendar loadDate = DateUtils.addDays(startDate, -7);
        firstLoadedVerticalDate = DateUtils.clone(loadDate);

        for (int i = 0; i < 15; i++) {
            appendDayToList(loadDate, true);
            if (i == 14) lastLoadedVerticalDate = DateUtils.clone(loadDate);
            loadDate = DateUtils.addDays(loadDate, 1);
        }

        scheduleAdapter.resetAnimation();
        scheduleAdapter.notifyDataSetChanged();

        recyclerViewSchedule.post(() -> {
            scrollToSelectedDateInVerticalList(startDate);
            if (tvFullDateLabel != null)
                tvFullDateLabel.setText(DateUtils.format(startDate, "dd/MM/yyyy"));
        });
    }

    // thêm cấu trúc hiển thị ngày vào danh sách chính
    private int appendDayToList(Calendar dateToLoad, boolean atEnd) {
        List<ScheduleItem> dailyItems = new ArrayList<>();
        String dateKey = DateUtils.format(dateToLoad, "yyyy-MM-dd");

        if (atEnd) datePositionMap.put(dateKey, continuousScheduleList.size());

        ScheduleItem dateHeader = new ScheduleItem();
        dateHeader.setSubjectCode(DateUtils.isToday(dateToLoad) ? "HEADER_DATE_TODAY" : "HEADER_DATE");
        dateHeader.setSubjectName(ScheduleLanguage.Format.fullDateHeader(dateToLoad));
        dateHeader.setDisplayDate(DateUtils.clone(dateToLoad));
        dailyItems.add(dateHeader);

        List<ScheduleItem> items = scheduleMap.get(dateKey);
        if (items == null || items.isEmpty()) {
            ScheduleItem empty = new ScheduleItem();
            empty.setSubjectCode("EMPTY_NOTE");
            empty.setSubjectName(ScheduleLanguage.Label.noClass(context));
            empty.setDisplayDate(DateUtils.clone(dateToLoad));
            dailyItems.add(empty);
        } else {
            List<ScheduleItem> morning = new ArrayList<>(), afternoon = new ArrayList<>(), evening = new ArrayList<>();
            for (ScheduleItem item : items) {
                ScheduleItem light = lightClone(item, dateToLoad);
                int hour = parseHour(light.getStartTime());
                if (hour >= 7 && hour < 12) morning.add(light);
                else if (hour < 18) afternoon.add(light);
                else evening.add(light);
            }
            addSession(dailyItems, morning, ScheduleLanguage.Label.sessionMorning(context), dateToLoad);
            addSession(dailyItems, afternoon, ScheduleLanguage.Label.sessionAfternoon(context), dateToLoad);
            addSession(dailyItems, evening, ScheduleLanguage.Label.sessionEvening(context), dateToLoad);
        }

        if (atEnd) continuousScheduleList.addAll(dailyItems);
        else {
            continuousScheduleList.addAll(0, dailyItems);
            recalculatePositionMap();
        }
        return dailyItems.size();
    }

    // xóa dữ liệu ở xa để tiết kiệm bộ nhớ
    private void trimList(boolean fromStart) {
        if (continuousScheduleList.isEmpty()) return;
        Calendar targetDate = continuousScheduleList.get(fromStart ? 0 : continuousScheduleList.size() - 1).getDisplayDate();
        int count = 0;
        while (!continuousScheduleList.isEmpty()) {
            Calendar current = continuousScheduleList.get(fromStart ? 0 : continuousScheduleList.size() - 1).getDisplayDate();
            if (current != null && DateUtils.isSameDay(current, targetDate)) {
                continuousScheduleList.remove(fromStart ? 0 : continuousScheduleList.size() - 1);
                count++;
            } else break;
        }
        if (fromStart) {
            scheduleAdapter.notifyItemRangeRemoved(0, count);
            firstLoadedVerticalDate = DateUtils.addDays(firstLoadedVerticalDate, 1);
        } else {
            scheduleAdapter.notifyItemRangeRemoved(continuousScheduleList.size(), count);
            lastLoadedVerticalDate = DateUtils.addDays(lastLoadedVerticalDate, -1);
        }
        recalculatePositionMap();
    }

    // cập nhật lại vị trí các ngày trong danh sách
    private void recalculatePositionMap() {
        datePositionMap.clear();
        for (int i = 0; i < continuousScheduleList.size(); i++) {
            ScheduleItem item = continuousScheduleList.get(i);
            if (item.getSubjectCode().startsWith("HEADER_DATE")) {
                datePositionMap.put(DateUtils.format(item.getDisplayDate(), "yyyy-MM-dd"), i);
            }
        }
    }

    // cuộn danh sách dọc đến vị trí ngày yêu cầu
    private void scrollToSelectedDateInVerticalList(Calendar targetDate) {
        Integer pos = datePositionMap.get(DateUtils.format(targetDate, "yyyy-MM-dd"));
        if (pos != null) {
            LinearLayoutManager lm = (LinearLayoutManager) recyclerViewSchedule.getLayoutManager();
            if (lm != null) lm.scrollToPositionWithOffset(pos, 0);
        }
    }

    // tạo bản sao dữ liệu nhẹ để hiển thị lịch
    private ScheduleItem lightClone(ScheduleItem s, Calendar d) {
        ScheduleItem item = new ScheduleItem();
        item.setSubjectName(s.getSubjectName());
        item.setSubjectCode(s.getSubjectCode());
        item.setStartTime(s.getStartTime());
        item.setEndTime(s.getEndTime());
        item.setRoom(s.getRoom());
        item.setLecturer(s.getLecturer());
        item.setStartDate(s.getStartDate());
        item.setEndDate(s.getEndDate());
        item.setStartPeriod(s.getStartPeriod());
        item.setEndPeriod(s.getEndPeriod());
        item.setTotalPeriodsStudied(s.getTotalPeriodsStudied());
        item.setStudentCount(s.getStudentCount());
        item.setDisplayDate(DateUtils.clone(d));
        return item;
    }

    // cập nhật dữ liệu mới cho toàn bộ các thành phần
    public void updateDayView(List<ScheduleItem> allSchedules) {
        buildScheduleMap(allSchedules, () -> {
            Calendar selected = dateController.getCurrentCalendar();
            dateSelectorAdapter.updateSelectedDate(selected);
            smoothScrollToHorizontalDate(selected);
            if (!isSyncingFromVerticalScroll) refreshVerticalList(selected);
            isSyncingFromVerticalScroll = false;
        });
    }

    // gộp các môn học vào từng buổi học tương ứng
    private void addSession(List<ScheduleItem> m, List<ScheduleItem> s, String t, Calendar d) {
        if (!s.isEmpty()) {
            ScheduleItem h = new ScheduleItem();
            h.setSubjectCode("HEADER_SESSION");
            h.setSubjectName(t);
            h.setDisplayDate(DateUtils.clone(d));
            m.add(h);
            m.addAll(s);
        }
    }

    // trích xuất giờ từ chuỗi thời gian để phân loại
    private int parseHour(String t) {
        try {
            return Integer.parseInt(t.split(":")[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    // tự động nạp thêm lịch khi cuộn xuống dưới
    private void loadNextDaySchedules() {
        isVerticalLoading = true;
        lastLoadedVerticalDate = DateUtils.addDays(lastLoadedVerticalDate, 1);
        int added = appendDayToList(lastLoadedVerticalDate, true);
        scheduleAdapter.notifyItemRangeInserted(continuousScheduleList.size() - added, added);
        recyclerViewSchedule.postDelayed(() -> isVerticalLoading = false, 150);
    }

    // tự động nạp thêm lịch khi cuộn lên trên
    private void loadPastDaySchedules() {
        isVerticalLoading = true;
        firstLoadedVerticalDate = DateUtils.addDays(firstLoadedVerticalDate, -1);
        int added = appendDayToList(firstLoadedVerticalDate, false);
        scheduleAdapter.notifyItemRangeInserted(0, added);
        recyclerViewSchedule.postDelayed(() -> isVerticalLoading = false, 150);
    }

    // mở rộng danh sách ngày ngang về quá khứ
    private void loadMorePastHorizontal(LinearLayoutManager lm) {
        isHorizontalLoading = true;
        int firstPos = lm.findFirstVisibleItemPosition();
        View v = lm.findViewByPosition(firstPos);
        int offset = (v == null) ? 0 : v.getLeft();
        dateSelectorAdapter.addPastWeek();
        lm.scrollToPositionWithOffset(firstPos + 7, offset);
        rvDateSelector.postDelayed(() -> isHorizontalLoading = false, 100);
    }
}