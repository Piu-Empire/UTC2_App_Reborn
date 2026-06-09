package com.utc2.appreborn.ui.notification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentNotificationBinding;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.NotificationApiService;
import com.utc2.appreborn.network.dto.GmailMessageResponse;
import com.utc2.appreborn.network.dto.NotificationResponse;
import com.utc2.appreborn.network.dto.PageResponse;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    public static final String TAG = "NotificationFragment";

    private FragmentNotificationBinding binding;
    private NotificationAdapter adapter;
    private SessionManager sessionManager;
    private NotificationApiService apiService;

    // Polling 60s
    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            loadNotifications();
            pollingHandler.postDelayed(this, 60000); // 60s
        }
    };

    public NotificationFragment() {
        super(R.layout.fragment_notification);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        sessionManager = SessionManager.getInstance(requireContext());
        if (sessionManager.isLoggedIn()) {
            apiService = ApiClient.getInstance(sessionManager.getAuthToken()).create(NotificationApiService.class);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyStatusBarInset();
        setupRecyclerView();
        setupClickListeners();
        
        if (sessionManager.isLoggedIn()) {
            binding.tvGmailAddress.setText(sessionManager.getEmail());
        } else {
            binding.tvGmailAddress.setText("Chưa đăng nhập");
            showEmptyState("Vui lòng đăng nhập để xem thông báo");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            pollingHandler.post(pollingRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pollingHandler.removeCallbacks(pollingRunnable);
        binding = null;
    }

    private void applyStatusBarInset() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            ViewGroup.LayoutParams lp = binding.statusBarSpacer.getLayoutParams();
            lp.height = statusBars.top;
            binding.statusBarSpacer.setLayoutParams(lp);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this::handleNotificationItemClick);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setNestedScrollingEnabled(false);
        binding.rvNotifications.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnOpenGmail.setOnClickListener(v -> openGmailApp());
        binding.btnViewInGmail.setOnClickListener(v -> openGmailApp());
    }

    private void openGmailApp() {
        try {
            Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            if (intent != null) {
                startActivity(intent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Cannot open Gmail: " + e.getMessage());
        }
    }

    private void handleNotificationItemClick(NotificationItem item) {
        if (item == null) return;

        if ("SYSTEM".equals(item.getSource())) {
            // Đánh dấu đã đọc
            if (!item.isRead()) {
                markAsRead(item.getNotifId());
                item.setRead(true);
                adapter.notifyDataSetChanged();
            }
            
            // Hiển thị chi tiết thông báo
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(item.getSubject())
                    .setMessage(item.getTimeLabel() + "\n\n" + item.getPreview())
                    .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                    .show();
                    
        } else if ("GMAIL".equals(item.getSource())) {
            openGmailApp();
        }
    }

    private void markAsRead(long notifId) {
        if (apiService == null) return;
        apiService.markAsRead(notifId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void loadNotifications() {
        if (apiService == null) return;
        
        // Gọi API lấy thông báo hệ thống (trang 0, size 20)
        apiService.getNotifications(0, 20).enqueue(new Callback<ApiResponse<PageResponse<NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<NotificationResponse>>> call, 
                                   Response<ApiResponse<PageResponse<NotificationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<NotificationResponse> content = response.body().getData().content;
                    List<NotificationItem> items = new ArrayList<>();
                    
                    if (content != null) {
                        for (NotificationResponse n : content) {
                            items.add(new NotificationItem(
                                    n.notificationId,
                                    n.source != null ? n.source : "SYSTEM",
                                    "Hệ thống",
                                    n.title,
                                    n.body,
                                    n.sentAt,
                                    n.isRead
                            ));
                        }
                    }
                    
                    // TODO: gọi thêm fetchGmailInbox() rồi trộn vào `items`, nhưng hiện tại để đơn giản
                    // mình cập nhật danh sách system notif trước.
                    updateList(items);
                    fetchGmailInbox(items);
                    
                } else if (response.code() == 401) {
                    handleUnauthorized();
                } else {
                    showToast("Lỗi tải thông báo: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<NotificationResponse>>> call, Throwable t) {
                showToast("Lỗi kết nối");
            }
        });
    }

    private void fetchGmailInbox(List<NotificationItem> currentItems) {
        if (apiService == null) return;
        apiService.getGmailInbox().enqueue(new Callback<ApiResponse<List<GmailMessageResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GmailMessageResponse>>> call, 
                                   Response<ApiResponse<List<GmailMessageResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<GmailMessageResponse> gmailMsgs = response.body().getData();
                    if (gmailMsgs != null) {
                        for (GmailMessageResponse m : gmailMsgs) {
                            currentItems.add(new NotificationItem(
                                    0, // không có ID server
                                    "GMAIL",
                                    m.from != null ? m.from : "Gmail",
                                    m.subject != null ? m.subject : "(Không tiêu đề)",
                                    m.snippet,
                                    m.receivedAt,
                                    !m.isUnread
                            ));
                        }
                    }
                    // Sort items lại theo thời gian hoặc cứ để Gmail ở dưới
                    updateList(currentItems);
                } else if (response.code() == 400 && response.body() != null && response.body().getMessage().contains("Token Gmail đã hết hạn")) {
                    Log.w(TAG, "Gmail token expired");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GmailMessageResponse>>> call, Throwable t) {
                Log.e(TAG, "Lỗi lấy Gmail: " + t.getMessage());
            }
        });
    }

    private void updateList(List<NotificationItem> items) {
        if (binding == null) return;
        if (items.isEmpty()) {
            showEmptyState("Không có thông báo nào");
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            adapter.submitList(items);
        }
    }

    private void showEmptyState(String message) {
        if (binding != null) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyMessage.setText(message);
        }
    }

    private void handleUnauthorized() {
        sessionManager.logout();
        showToast("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }

    private void showToast(String msg) {
        if (getContext() != null) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(getContext(), msg);
        }
    }
}