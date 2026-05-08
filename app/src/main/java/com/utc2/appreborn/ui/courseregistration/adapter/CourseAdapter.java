package com.utc2.appreborn.ui.courseregistration.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.courseregistration.model.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách học phần đăng ký.
 *
 * [Chương 3 - OOP]
 *  - Bao đóng: private fields + interface callback
 *  - Overload: 2 constructor
 *  - Inner class ViewHolder
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    public interface OnCourseClickListener {
        void onRegisterClick(Course course);
    }

    private final List<Course>          items;
    private final OnCourseClickListener listener;
    private       List<String>          registeredIds = new ArrayList<>();

    public CourseAdapter(List<Course> items, OnCourseClickListener listener) {
        this.items    = new ArrayList<>(items);
        this.listener = listener;
    }

    public CourseAdapter(List<Course> items) {
        this(items, null);
    }

    public void updateData(List<Course> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setRegisteredIds(List<String> ids) {
        this.registeredIds = ids;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = items.get(position);

        holder.txtTitle.setText(course.getName());

        // Label màu đỏ, value màu đen
        setSpannedText(holder.txtMaMonHoc,    "Mã môn: ",          course.getCourseCode());
        setSpannedText(holder.txtSoTinChi,    "Số tín chỉ: ",      String.valueOf(course.getCredits()));
        setSpannedText(holder.txtGiangVien,   "Giảng viên: ",      course.getLecturer());
        setSpannedText(holder.txtThoiGian,    "Thời gian: ",       course.getSchedule());
        setSpannedText(holder.txtPhong,       "Phòng: ",           course.getRoom());
        setSpannedText(holder.txtNgayHoc,     "Bắt đầu: ",  course.getStartDate() + " → " + course.getEndDate());
        setSpannedText(holder.txtSoTiet,      "Số tiết: ",  String.valueOf(course.getTotalPeriods()));

        // ── Thanh sĩ số ──────────────────────────────────────────────────────
        int current = course.getCurrentStudents();
        int max     = course.getMaxStudents();
        boolean full = !course.isAvailable();

        holder.progressSiSo.setMax(max);
        holder.progressSiSo.setProgress(current);

        // Text "Hiện tại : X    X / Y"
        holder.txtSiSoCurrent.setText("Hiện tại : " + current);
        holder.txtSiSoRatio.setText(current + " / " + max);

        // Trạng thái – dùng ic_status_check / ic_status_x
        if (full) {
            holder.icTrangThai.setImageResource(R.drawable.ic_status_x);
            holder.txtTrangThai.setText("Trạng thái: Hết chỗ");
            holder.txtTrangThai.setTextColor(Color.parseColor("#E53935"));
        } else {
            holder.icTrangThai.setImageResource(R.drawable.ic_status_check);
            holder.txtTrangThai.setText("Trạng thái: Còn chỗ");
            holder.txtTrangThai.setTextColor(Color.parseColor("#2E7D32"));
        }

        // ── Nút đăng ký ──────────────────────────────────────────────────────
        boolean isRegistered = registeredIds.contains(course.getId());

        if (isRegistered) {
            holder.btnRegister.setBackgroundResource(R.drawable.bg_button_disabled);
            holder.btnRegister.setText("Đã đăng ký");
            holder.btnRegister.setTextColor(Color.parseColor("#888888"));
            holder.btnRegister.setAlpha(0.6f);
            holder.btnRegister.setClickable(false);
            holder.btnRegister.setFocusable(false);
        } else if (full) {
            // Hết chỗ – vô hiệu hóa nút
            holder.btnRegister.setBackgroundResource(R.drawable.bg_button_disabled);
            holder.btnRegister.setText("Hết chỗ");
            holder.btnRegister.setTextColor(Color.parseColor("#888888"));
            holder.btnRegister.setAlpha(0.6f);
            holder.btnRegister.setClickable(false);
            holder.btnRegister.setFocusable(false);
        } else {
            holder.btnRegister.setBackgroundResource(R.drawable.bg_button_black);
            holder.btnRegister.setText("Đăng ký");
            holder.btnRegister.setTextColor(Color.WHITE);
            holder.btnRegister.setAlpha(1.0f);
            holder.btnRegister.setClickable(true);
            holder.btnRegister.setFocusable(true);
            holder.btnRegister.setOnClickListener(v -> {
                if (listener != null) listener.onRegisterClick(course);
            });
        }
    }

    /**
     * Tạo SpannableString: label màu đỏ (#E53935), value màu đen.
     */
    private void setSpannedText(TextView tv, String label, String value) {
        android.text.SpannableString ss =
                new android.text.SpannableString(label + value);
        // Label đỏ
        ss.setSpan(new android.text.style.ForegroundColorSpan(Color.parseColor("#E53935")),
                0, label.length(),
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Value đen
        ss.setSpan(new android.text.style.ForegroundColorSpan(Color.BLACK),
                label.length(), ss.length(),
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView    txtTitle;
        final TextView    txtMaMonHoc;
        final TextView    txtSoTinChi;
        final TextView    txtGiangVien;
        final TextView    txtThoiGian;
        final TextView    txtPhong;
        final TextView    txtNgayHoc;
        final TextView    txtSoTiet;
        final TextView    btnRegister;
        final ProgressBar progressSiSo;
        final TextView    txtSiSoCurrent;
        final TextView    txtSiSoRatio;
        final android.widget.ImageView icTrangThai;
        final TextView    txtTrangThai;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle       = itemView.findViewById(R.id.txtCourseTitle);
            txtMaMonHoc    = itemView.findViewById(R.id.txtMaMonHoc);
            txtSoTinChi    = itemView.findViewById(R.id.txtSoTinChi);
            txtGiangVien   = itemView.findViewById(R.id.txtGiangVien);
            txtThoiGian    = itemView.findViewById(R.id.txtThoiGian);
            txtPhong       = itemView.findViewById(R.id.txtPhong);
            txtNgayHoc     = itemView.findViewById(R.id.txtNgayHoc);
            txtSoTiet      = itemView.findViewById(R.id.txtSoTiet);
            btnRegister    = itemView.findViewById(R.id.btnRegisterCourse);
            progressSiSo   = itemView.findViewById(R.id.progressSiSo);
            txtSiSoCurrent = itemView.findViewById(R.id.txtSiSoCurrent);
            txtSiSoRatio   = itemView.findViewById(R.id.txtSiSoRatio);
            icTrangThai    = itemView.findViewById(R.id.icTrangThai);
            txtTrangThai   = itemView.findViewById(R.id.txtTrangThai);
        }
    }
}