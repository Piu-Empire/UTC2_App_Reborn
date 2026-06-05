package com.utc2.appreborn.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.model.ScheduleItem;

/**
 * Bottom Sheet hiện chi tiết một buổi học / thi khi người dùng bấm vào item.
 * Màu badge thay đổi theo scheduleType:
 *   1 = Lịch học   → xanh dương
 *   2 = Lịch thi   → cam vàng
 *   3 = Lịch thi lại → đỏ
 */
public class ScheduleDetailSheet extends BottomSheetDialogFragment {

    private static final String ARG_SUBJECT_NAME   = "subjectName";
    private static final String ARG_SUBJECT_CODE   = "subjectCode";
    private static final String ARG_START_TIME     = "startTime";
    private static final String ARG_END_TIME       = "endTime";
    private static final String ARG_START_PERIOD   = "startPeriod";
    private static final String ARG_END_PERIOD     = "endPeriod";
    private static final String ARG_ROOM           = "room";
    private static final String ARG_LECTURER       = "lecturer";
    private static final String ARG_START_DATE     = "startDate";
    private static final String ARG_END_DATE       = "endDate";
    private static final String ARG_SEMESTER       = "semester";
    private static final String ARG_STUDENT_COUNT  = "studentCount";
    private static final String ARG_REMAINING      = "remaining";
    private static final String ARG_SCHEDULE_TYPE  = "scheduleType";

    // tạo instance với dữ liệu từ ScheduleItem
    public static ScheduleDetailSheet newInstance(ScheduleItem item) {
        ScheduleDetailSheet sheet = new ScheduleDetailSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SUBJECT_NAME,  item.getSubjectName() != null  ? item.getSubjectName()  : "");
        args.putString(ARG_SUBJECT_CODE,  item.getSubjectCode() != null  ? item.getSubjectCode()  : "");
        args.putString(ARG_START_TIME,    item.getStartTime()   != null  ? item.getStartTime()    : "--:--");
        args.putString(ARG_END_TIME,      item.getEndTime()     != null  ? item.getEndTime()      : "--:--");
        args.putInt   (ARG_START_PERIOD,  item.getStartPeriod());
        args.putInt   (ARG_END_PERIOD,    item.getEndPeriod());
        args.putString(ARG_ROOM,          item.getRoom()        != null  ? item.getRoom()         : "—");
        args.putString(ARG_LECTURER,      item.getLecturer()    != null  ? item.getLecturer()     : "—");
        args.putString(ARG_START_DATE,    item.getStartDate()   != null  ? item.getStartDate()    : "—");
        args.putString(ARG_END_DATE,      item.getEndDate()     != null  ? item.getEndDate()      : "—");
        args.putString(ARG_SEMESTER,      item.getSemester()    != null  ? item.getSemester()     : "—");
        args.putInt   (ARG_STUDENT_COUNT, item.getStudentCount());
        int remaining = (item.getDisplayDate() != null)
                ? item.getRemainingPeriods(item.getDisplayDate())
                : 0;
        args.putInt(ARG_REMAINING, remaining);
        args.putInt(ARG_SCHEDULE_TYPE, item.getScheduleType());
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_schedule_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle a = getArguments();
        if (a == null) return;

        int scheduleType = a.getInt(ARG_SCHEDULE_TYPE, 1);

        // Gán text
        setText(view, R.id.tvSheetSubjectName,  a.getString(ARG_SUBJECT_NAME));
        setText(view, R.id.tvSheetSubjectCode,  a.getString(ARG_SUBJECT_CODE));
        setText(view, R.id.tvSheetRoom,         a.getString(ARG_ROOM));
        setText(view, R.id.tvSheetLecturer,     a.getString(ARG_LECTURER));
        setText(view, R.id.tvSheetSemester,     a.getString(ARG_SEMESTER));

        // Thời gian
        String startTime = a.getString(ARG_START_TIME, "--:--");
        String endTime   = a.getString(ARG_END_TIME,   "--:--");
        setText(view, R.id.tvSheetTime, startTime + " – " + endTime);

        // Tiết
        int sp = a.getInt(ARG_START_PERIOD);
        int ep = a.getInt(ARG_END_PERIOD);
        setText(view, R.id.tvSheetPeriod, sp == ep ? "Tiết " + sp : "Tiết " + sp + "→" + ep);

        // Ngày
        String sd = a.getString(ARG_START_DATE, "—");
        String ed = a.getString(ARG_END_DATE,   "—");
        setText(view, R.id.tvSheetDateRange, sd.equals(ed) ? sd : sd + " — " + ed);

        // Sĩ số
        int studentCount = a.getInt(ARG_STUDENT_COUNT, 0);
        setText(view, R.id.tvSheetStudentCount, studentCount > 0 ? studentCount + " SV" : "—");

        // Còn lại
        int remaining = a.getInt(ARG_REMAINING, 0);
        TextView tvRemaining = view.findViewById(R.id.tvSheetRemaining);
        if (tvRemaining != null) {
            if (scheduleType == 2 || scheduleType == 3) {
                // Lịch thi: không có tiết còn lại
                tvRemaining.setText("—");
            } else {
                tvRemaining.setText(remaining > 0 ? remaining + " tiết" : "Đã xong");
            }
        }

        // Ẩn giảng viên nếu rỗng
        View layoutLecturer = view.findViewById(R.id.layoutSheetLecturer);
        String lecturer = a.getString(ARG_LECTURER, "");
        if (layoutLecturer != null && (lecturer.isEmpty() || "—".equals(lecturer))) {
            layoutLecturer.setVisibility(View.GONE);
        }

        // Áp màu theo loại lịch
        applyTypeStyle(view, scheduleType);
    }

    // đổi màu badge và đường kẻ header theo loại lịch
    private void applyTypeStyle(View view, int scheduleType) {
        TextView tvBadge     = view.findViewById(R.id.tvTypeBadge);
        MaterialCardView cardBadge = view.findViewById(R.id.cardTypeBadge);
        View layoutHeader    = view.findViewById(R.id.layoutSheetHeader);
        View layoutRemaining = view.findViewById(R.id.layoutSheetRemaining);

        int badgeBg, badgeText, headerAccent, remainingBg;
        String badgeLabel;

        switch (scheduleType) {
            case 2: // Lịch thi
                badgeBg      = R.color.schedule_exam_badge_bg;
                badgeText    = R.color.schedule_exam_badge_text;
                headerAccent = R.color.schedule_exam_stroke;
                remainingBg  = R.color.schedule_exam;
                badgeLabel   = "LỊCH THI";
                break;
            case 3: // Lịch thi lại
                badgeBg      = R.color.schedule_reexam_badge_bg;
                badgeText    = R.color.schedule_reexam_badge_text;
                headerAccent = R.color.schedule_reexam_stroke;
                remainingBg  = R.color.schedule_reexam;
                badgeLabel   = "THI LẠI";
                break;
            default: // Lịch học
                badgeBg      = R.color.schedule_study_badge_bg;
                badgeText    = R.color.schedule_study_badge_text;
                headerAccent = R.color.schedule_study_stroke;
                remainingBg  = R.color.text_header_dark;
                badgeLabel   = "LỊCH HỌC";
                break;
        }

        if (cardBadge != null)
            cardBadge.setCardBackgroundColor(ContextCompat.getColor(requireContext(), badgeBg));
        if (tvBadge != null) {
            tvBadge.setTextColor(ContextCompat.getColor(requireContext(), badgeText));
            tvBadge.setText(badgeLabel);
        }
        if (layoutRemaining != null)
            layoutRemaining.setBackgroundColor(ContextCompat.getColor(requireContext(), remainingBg));
    }

    private void setText(View root, int viewId, String text) {
        TextView tv = root.findViewById(viewId);
        if (tv != null) tv.setText(text != null ? text : "—");
    }
}
