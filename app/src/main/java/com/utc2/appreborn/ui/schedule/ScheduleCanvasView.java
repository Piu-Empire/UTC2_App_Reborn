package com.utc2.appreborn.ui.schedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.ScheduleItem;
import com.utc2.appreborn.utils.TextHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleCanvasView extends ViewGroup {
    private List<ScheduleItem> data = new ArrayList<>();
    private Paint gridPaint;
    private final int columnCount = 7;
    private final int startHour = 6;
    private final int endHour = 24;
    private final int totalHours = endHour - startHour;
    private Paint nowLinePaint;
    private Calendar viewDate;
    private boolean isMaxMode = false;

    // khởi tạo các công cụ vẽ lưới và đường thời gian
    public ScheduleCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // thiết lập thuộc tính cho bút vẽ và màu sắc nền
    private void init() {
        setWillNotDraw(false);
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(ScheduleColors.Canvas.gridLine(getContext()));
        gridPaint.setStrokeWidth(2f);
        nowLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nowLinePaint.setColor(ScheduleColors.Canvas.nowLine(getContext()));
        nowLinePaint.setStrokeWidth(4f);
    }

    // chuyển đổi trạng thái hiển thị chi tiết hoặc thu gọn
    public void setMaxMode(boolean maxMode) {
        this.isMaxMode = maxMode;
        requestLayout();
        invalidate();
    }

    // nạp dữ liệu và tạo các thẻ môn học vào giao diện
    public void setData(List<ScheduleItem> list) {
        this.data = (list != null) ? list : new ArrayList<>();
        removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (ScheduleItem item : data) {
            View childView = inflater.inflate(R.layout.item_week_class_card, this, false);
            TextView tvClassName = childView.findViewById(R.id.week_tvClassName);
            TextView tvSemester = childView.findViewById(R.id.week_tvSemester);
            TextView tvSubjectName = childView.findViewById(R.id.week_tvSubjectName);
            TextView tvLecturer = childView.findViewById(R.id.week_tvLecturer);
            TextView tvLocation = childView.findViewById(R.id.week_tvLocation);
            TextView tvDateRange = childView.findViewById(R.id.week_tvDateRange);
            TextView tvStudentCount = childView.findViewById(R.id.week_tvStudentCount);
            TextView tvRemainingPeriods = childView.findViewById(R.id.week_tvRemainingPeriods);

            if (tvClassName != null)
                tvClassName.setText(item.getSubjectCode() != null ? item.getSubjectCode() : "N/A");
            if (tvSemester != null)
                tvSemester.setText(item.getSemester() != null ? item.getSemester() : "Kỳ 2");
            if (tvSubjectName != null) tvSubjectName.setText(item.getSubjectName());
            if (tvLecturer != null)
                tvLecturer.setText(ScheduleLanguage.Format.lecturer(getContext(), item.getLecturer()));
            if (tvLocation != null)
                tvLocation.setText(item.getRoom() != null ? item.getRoom() : "P104C2");
            if (tvDateRange != null)
                tvDateRange.setText(ScheduleLanguage.Format.dateRangeShort(item.getStartDate(), item.getEndDate()));
            if (tvStudentCount != null)
                tvStudentCount.setText(String.valueOf(item.getStudentCount() > 0 ? item.getStudentCount() : 89));
            if (tvRemainingPeriods != null) {
                Calendar date = (viewDate != null) ? viewDate : Calendar.getInstance();
                tvRemainingPeriods.setText(ScheduleLanguage.Format.remainingPeriodsShort(getContext(), item.getRemainingPeriods(date)));
            }
            childView.setTag(item);
            addView(childView);
        }
        requestLayout();
        invalidate();
    }

    // xác định thời gian hiện tại để đồng bộ lịch tuần
    public void setViewDate(Calendar cal) {
        this.viewDate = cal;
        invalidate();
    }

    // tính toán kích thước và ẩn hiện các thành phần thẻ
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        float colWidth = width / 7f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ScheduleItem item = (ScheduleItem) child.getTag();
            if (item != null) {
                float top = timeToY(item.getStartTime(), height);
                float bottom = timeToY(item.getEndTime(), height);
                int cLeft = (int) (item.getDayOfWeek() * colWidth) + 4;
                int cRight = (int) ((item.getDayOfWeek() + 1) * colWidth) - 4;
                int cTop = (int) top + 2;
                int cBottom = (int) bottom - 2;
                int cWidth = Math.max(0, cRight - cLeft);
                int cHeight = Math.max(0, cBottom - cTop);

                View layoutTopRow = child.findViewById(R.id.week_layoutTopRow);
                View tvLecturer = child.findViewById(R.id.week_tvLecturer);
                View layoutLocation = child.findViewById(R.id.week_layoutLocation);
                View layoutDateRange = child.findViewById(R.id.week_layoutDateRange);
                View viewDivider = child.findViewById(R.id.week_viewDivider);
                View layoutBottomRow = child.findViewById(R.id.week_layoutBottomRow);
                TextView tvSubjectName = child.findViewById(R.id.week_tvSubjectName);

                if (!isMaxMode) {
                    if (tvSubjectName != null) {
                        tvSubjectName.setMaxLines(5);
                        tvSubjectName.setTextSize(11f);
                        String abbr = TextHelper.abbreviate(item.getSubjectName());
                        if (!tvSubjectName.getText().toString().equals(abbr))
                            tvSubjectName.setText(abbr);
                        tvSubjectName.setGravity(android.view.Gravity.CENTER);
                    }
                    if (layoutTopRow != null) layoutTopRow.setVisibility(View.GONE);
                    if (tvLecturer != null) tvLecturer.setVisibility(View.GONE);
                    if (layoutLocation != null) layoutLocation.setVisibility(View.GONE);
                    if (layoutDateRange != null) layoutDateRange.setVisibility(View.GONE);
                    if (viewDivider != null) viewDivider.setVisibility(View.GONE);
                    if (layoutBottomRow != null) layoutBottomRow.setVisibility(View.GONE);
                } else {
                    if (tvSubjectName != null) {
                        tvSubjectName.setMaxLines(3);
                        tvSubjectName.setTextSize(13f);
                    }
                    if (layoutLocation != null) layoutLocation.setVisibility(View.VISIBLE);
                    if (cHeight < 300) {
                        if (layoutBottomRow != null) layoutBottomRow.setVisibility(View.GONE);
                        if (viewDivider != null) viewDivider.setVisibility(View.GONE);
                        if (layoutDateRange != null) layoutDateRange.setVisibility(View.GONE);
                    } else {
                        if (layoutBottomRow != null) layoutBottomRow.setVisibility(View.VISIBLE);
                        if (viewDivider != null) viewDivider.setVisibility(View.VISIBLE);
                        if (layoutDateRange != null) layoutDateRange.setVisibility(View.VISIBLE);
                    }
                    if (cHeight < 180) {
                        if (layoutTopRow != null) layoutTopRow.setVisibility(View.GONE);
                        if (tvLecturer != null) tvLecturer.setVisibility(View.GONE);
                    } else {
                        if (layoutTopRow != null) layoutTopRow.setVisibility(View.VISIBLE);
                        if (tvLecturer != null) tvLecturer.setVisibility(View.VISIBLE);
                    }
                }
                int childWidthSpec = MeasureSpec.makeMeasureSpec(cWidth, MeasureSpec.EXACTLY);
                int childHeightSpec = MeasureSpec.makeMeasureSpec(cHeight, MeasureSpec.EXACTLY);
                child.measure(childWidthSpec, childHeightSpec);
            }
        }
    }

    // sắp xếp các thẻ môn học vào đúng vị trí lưới
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        float colWidth = width / 7f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ScheduleItem item = (ScheduleItem) child.getTag();
            if (item != null) {
                int day = item.getDayOfWeek();
                int cLeft = (int) (day * colWidth) + 4;
                int cTop = (int) timeToY(item.getStartTime(), height) + 2;
                int cRight = (int) ((day + 1) * colWidth) - 4;
                int cBottom = (int) timeToY(item.getEndTime(), height) - 2;
                child.layout(cLeft, cTop, cRight, cBottom);
            }
        }
    }

    // thực hiện vẽ các đường lưới ngang dọc làm nền
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float colWidth = width / 7f;
        float rowHeight = height / totalHours;
        for (int i = 0; i <= columnCount; i++) {
            float x = i * colWidth;
            canvas.drawLine(x, 0, x, height, gridPaint);
        }
        for (int i = 0; i <= totalHours; i++) {
            float y = i * rowHeight;
            canvas.drawLine(0, y, width, y, gridPaint);
        }
    }

    // điều phối việc vẽ đường chỉ giờ sau các mục
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawCurrentTimeIndicator(canvas, getWidth() / 7f);
    }

    // xác định vị trí và vẽ vạch đỏ báo giờ thực
    private void drawCurrentTimeIndicator(Canvas canvas, float colWidth) {
        if (viewDate == null) return;
        Calendar now = Calendar.getInstance();
        boolean isSameWeek = (now.get(Calendar.YEAR) == viewDate.get(Calendar.YEAR)) &&
                (now.get(Calendar.WEEK_OF_YEAR) == viewDate.get(Calendar.WEEK_OF_YEAR));
        if (!isSameWeek) return;
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        if (hour < startHour || hour >= endHour) return;
        float y = timeToY(String.format("%02d:%02d", hour, minute), getHeight());
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int todayIndex = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - 2;
        canvas.drawLine(todayIndex * colWidth, y, (todayIndex + 1) * colWidth, y, nowLinePaint);
    }

    // chuyển đổi giá trị giờ phút sang tọa độ trục tung
    private float timeToY(String time, float availableHeight) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (hour == 0 && minute == 0) hour = 24;
            float totalMinutesFromStart = (hour - startHour) * 60 + minute;
            float totalMinutesInView = totalHours * 60;
            return (totalMinutesFromStart / totalMinutesInView) * availableHeight;
        } catch (Exception e) {
            return 0;
        }
    }
}