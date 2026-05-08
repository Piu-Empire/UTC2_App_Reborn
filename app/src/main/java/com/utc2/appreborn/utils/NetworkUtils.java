package com.utc2.appreborn.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

/**
 * Lớp tiện ích quản lý kết nối mạng cho AppReborn.
 * Hỗ trợ kiểm tra nhanh và đăng ký lắng nghe sự kiện thay đổi mạng.
 */
public class NetworkUtils {

    // Interface để các Activity (như InvoiceActivity) có thể nhận thông báo khi mạng thay đổi.
    public interface NetworkStatusListener {
        void onNetworkAvailable(); // Gọi khi có mạng trở lại
        void onNetworkLost();      // Gọi khi mất kết nối mạng
    }

    private final Context context;
    private final NetworkStatusListener listener;
    private ConnectivityManager.NetworkCallback networkCallback;

    /**
     * Constructor dùng khi muốn khởi tạo đối tượng để lắng nghe sự kiện mạng[cite: 13].
     * @param context Ngữ cảnh ứng dụng
     * @param listener Interface xử lý sự kiện
     */
    public NetworkUtils(Context context, NetworkStatusListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Hàm static kiểm tra nhanh có kết nối mạng hay không[cite: 9, 11, 12].
     * Thường dùng trước khi nhấn nút "Thanh toán" hoặc "Gửi yêu cầu".
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;

        // Lấy dịch vụ quản lý kết nối của hệ thống Android
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        // Xử lý cho các phiên bản Android mới (từ Android 6.0 Marshmallow trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            // Kiểm tra khả năng kết nối (Wifi, 4G/5G, Ethernet)
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        } else {
            // Hỗ trợ các máy Android đời cũ (Legacy)
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * Đăng ký lắng nghe sự kiện thay đổi trạng thái mạng từ hệ thống[cite: 13].
     * Giải quyết lỗi: Cannot resolve method 'register'.
     */
    public void register() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return;

        // Yêu cầu hệ thống theo dõi các mạng có khả năng truy cập Internet
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        // Định nghĩa các hành động khi trạng thái mạng thay đổi
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // Sử dụng Handler để đẩy sự kiện về luồng chính (Main Thread)
                // giúp cập nhật giao diện hoặc hiển thị Toast an toàn[cite: 13].
                new Handler(Looper.getMainLooper()).post(listener::onNetworkAvailable);
            }

            @Override
            public void onLost(@NonNull Network network) {
                // Tương tự, đẩy thông báo mất mạng về Main Thread
                new Handler(Looper.getMainLooper()).post(listener::onNetworkLost);
            }
        };

        // Bắt đầu lắng nghe
        cm.registerNetworkCallback(request, networkCallback);
    }

    /**
     * Hủy đăng ký lắng nghe để giải phóng bộ nhớ khi Activity bị đóng[cite: 13].
     * Giải quyết lỗi: Cannot resolve method 'unregister'.
     */
    public void unregister() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && networkCallback != null) {
            try {
                // Ngắt kết nối lắng nghe để tránh Memory Leak (Rò rỉ bộ nhớ)
                cm.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}