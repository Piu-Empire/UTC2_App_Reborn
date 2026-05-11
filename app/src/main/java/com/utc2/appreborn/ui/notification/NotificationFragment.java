package com.utc2.appreborn.ui.notification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.databinding.FragmentNotificationBinding;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.utils.MockHelper;

import java.util.List;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


public class NotificationFragment extends Fragment {

    public static final String TAG = "NotificationFragment";
    private static final String STUDENT_EMAIL_DOMAIN = "@st.utc2.edu.vn";
    private static final String GMAIL_SEARCH_URL_TEMPLATE =
            "https://mail.google.com/mail/u/0/#search/to%%3A%s";
    private static final String GMAIL_INBOX_URL =
            "https://mail.google.com/mail/u/0/#inbox";

    private FragmentNotificationBinding binding;
    private NotificationAdapter adapter;
    private String studentEmail = null;

    public NotificationFragment() {
        super(R.layout.fragment_notification);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // khởi tạo giao diện bằng view binding cho fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        // thiết lập logic khởi tạo sau khi view sẵn sàng
        super.onViewCreated(view, savedInstanceState);

        applyStatusBarInset();
        loadStudentEmail();
        setupRecyclerView();
        setupClickListeners();
        loadNotifications();
    }

    @Override
    public void onDestroyView() {
        // hủy binding để tránh rò rỉ bộ nhớ fragment
        super.onDestroyView();
        binding = null;
    }

    private void applyStatusBarInset() {
        // điều chỉnh lề tránh bị thanh trạng thái đè giao diện
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.LayoutParams lp = binding.statusBarSpacer.getLayoutParams();
            lp.height = statusBars.top;
            binding.statusBarSpacer.setLayoutParams(lp);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void loadStudentEmail() {
        // tự động tạo địa chỉ email dựa trên mã sinh viên
        String mssv = null;

        try {
            mssv = MockHelper.getMockStudentCode();
        } catch (Exception e) {
            Log.w(TAG, "MockHelper not available: " + e.getMessage());
        }

        if (mssv != null && !mssv.isEmpty()) {
            studentEmail = mssv + STUDENT_EMAIL_DOMAIN;
        }

        if (binding != null) {
            String displayEmail = (studentEmail != null)
                    ? studentEmail
                    : "mssv" + STUDENT_EMAIL_DOMAIN;
            binding.tvGmailAddress.setText(displayEmail);
        }
    }

    private void setupRecyclerView() {
        // cấu hình hiển thị danh sách thông báo hiệu quả nhất
        adapter = new NotificationAdapter(this::handleNotificationItemClick);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setNestedScrollingEnabled(false);
        binding.rvNotifications.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // gắn các sự kiện click cho nút chức năng chính
        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnOpenGmail.setOnClickListener(v -> openGmailForStudent());
        binding.btnViewInGmail.setOnClickListener(v -> openGmailForStudent());
    }

    private void openGmailForStudent() {
        // mở ứng dụng gmail theo bộ lọc email sinh viên
        String searchUrl;
        if (studentEmail != null && !studentEmail.isEmpty()) {
            String encodedEmail = studentEmail.replace("@", "%40");
            searchUrl = String.format(GMAIL_SEARCH_URL_TEMPLATE, encodedEmail);
        } else {
            searchUrl = GMAIL_INBOX_URL;
        }

        Log.d(TAG, "Opening Gmail URL: " + searchUrl);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
            intent.setPackage("com.google.android.gm");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            }

        } catch (Exception e) {
            Log.e(TAG, "Cannot open Gmail: " + e.getMessage());
            android.widget.Toast.makeText(
                    requireContext(),
                    "Không thể mở Gmail. Vui lòng kiểm tra lại.",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void handleNotificationItemClick(NotificationItem item) {
        // tìm kiếm gmail dựa theo tiêu đề thông báo cụ thể
        if (item == null) return;

        String subject = item.getSubject();
        if (subject != null && !subject.isEmpty()) {
            String encodedSubject = Uri.encode(subject);
            String searchUrl = "https://mail.google.com/mail/u/0/#search/" + encodedSubject;

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                intent.setPackage("com.google.android.gm");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)));
                }
            } catch (Exception e) {
                Log.e(TAG, "Cannot open Gmail for item: " + e.getMessage());
            }
        } else {
            openGmailForStudent();
        }
    }

    private void loadNotifications() {
        // kiểm tra đăng nhập để chọn nguồn dữ liệu phù hợp
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());

        if (account != null && account.getAccount() != null) {
            fetchGmailData(account);
        } else {
            loadMockNotifications();
        }
    }

    private void loadMockNotifications() {
        // Lấy mock data từ MockHelper — tập trung, dễ bảo trì
        List<NotificationItem> mockList = MockHelper.getMockNotificationList();

        if (mockList.isEmpty()) {
            showEmptyState(getString(R.string.notification_empty));
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            adapter.submitList(mockList);
        }
    }

    private void fetchGmailData(GoogleSignInAccount account) {
        // chuẩn bị thực hiện lấy dữ liệu thư từ gmail api
        Log.d(TAG, "Sẵn sàng gọi Gmail API cho: " + account.getEmail());
    }


    private void showEmptyState(String message) {
        // cập nhật trạng thái hiển thị khi danh sách dữ liệu trống
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
        binding.tvEmptyMessage.setText(message);
    }
}