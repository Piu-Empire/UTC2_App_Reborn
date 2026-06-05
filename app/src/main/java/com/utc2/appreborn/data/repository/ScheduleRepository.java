package com.utc2.appreborn.data.repository;

import android.content.Context;

import com.utc2.appreborn.data.local.ScheduleFileManager;
import com.utc2.appreborn.model.ScheduleItem;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.ScheduleApiService;
import com.utc2.appreborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Luồng hoạt động mỗi lần mở màn hình lịch:
 * <p>
 * 1. Đọc file local → hiển thị ngay (nếu có)
 * 2. Gọi GET /meta?studentCode=MSSV (public, không cần token)
 * - Server mới hơn → GET /file → lưu → refresh UI
 * - File đã mới   → không làm gì
 * - Lỗi mạng     → dùng file local (offline mode)
 */
public class ScheduleRepository {

    @FunctionalInterface
    public interface Callback {
        void onResult(List<ScheduleItem> items);
    }

    private final ScheduleFileManager fileManager;
    private final ScheduleApiService apiService;
    private final String studentCode;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ScheduleRepository(Context context) {
        this.fileManager = new ScheduleFileManager(context);
        this.studentCode = SessionManager.getInstance(context).getStudentCode();
        // Public endpoint — dùng publicInstance (không cần token)
        this.apiService = ApiClient.getPublicInstance().create(ScheduleApiService.class);
    }

    /**
     * Gọi từ UI thread — trả kết quả qua callback (cũng trên UI thread).
     *
     * @param onImmediate Gọi ngay với dữ liệu local (rỗng nếu lần đầu chưa có file)
     * @param onSynced    Gọi sau khi sync xong từ server (chỉ khi có version mới hơn)
     */
    public void loadSchedule(Callback onImmediate, Callback onSynced) {
        // Bước 1: Hiển thị file local ngay lập tức
        List<ScheduleItem> localItems = parseLocalFile();
        if (onImmediate != null) {
            onImmediate.onResult(localItems);
        }

        // Bước 2: Sync từ server trong nền
        executor.execute(() -> syncFromServer(onSynced));
    }

    private List<ScheduleItem> parseLocalFile() {
        ScheduleApiService.ScheduleFileResponse file = fileManager.readLocalFile(studentCode);
        if (file == null || file.schedules == null) return new ArrayList<>();
        return mapToScheduleItems(file.schedules);
    }

    private void syncFromServer(Callback onSynced) {
        try {
            retrofit2.Response<ApiResponse<ScheduleApiService.ScheduleMetaResponse>> metaResp =
                    apiService.getMeta(studentCode).execute();

            if (!metaResp.isSuccessful() || metaResp.body() == null || !metaResp.body().isSuccess()) {
                return; // server lỗi → giữ file local
            }

            String serverTs = metaResp.body().getData().lastUpdated;
            String localTs = fileManager.getLocalLastUpdated(studentCode);

            if (!fileManager.isServerNewer(serverTs, localTs)) {
                return; // file local đã mới nhất
            }

            // Tải file mới
            retrofit2.Response<ApiResponse<ScheduleApiService.ScheduleFileResponse>> fileResp =
                    apiService.getScheduleFile(studentCode).execute();

            if (!fileResp.isSuccessful() || fileResp.body() == null || !fileResp.body().isSuccess()) {
                return;
            }

            ScheduleApiService.ScheduleFileResponse newFile = fileResp.body().getData();
            fileManager.saveFile(studentCode, newFile);

            List<ScheduleItem> updated = mapToScheduleItems(newFile.schedules);
            if (onSynced != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(
                        () -> onSynced.onResult(updated));
            }

        } catch (Exception e) {
            // Không có mạng → im lặng, dùng file local
        }
    }

    private List<ScheduleItem> mapToScheduleItems(
            List<ScheduleApiService.ScheduleItemResponse> raw) {
        List<ScheduleItem> result = new ArrayList<>();
        if (raw == null) return result;
        for (ScheduleApiService.ScheduleItemResponse r : raw) {
            ScheduleItem item = new ScheduleItem(
                    r.subjectCode,
                    r.subjectName,
                    r.type,
                    r.lecturer,
                    r.dayOfWeek,
                    r.startPeriod,
                    r.endPeriod,
                    0,
                    r.startTime,
                    r.endTime,
                    r.startDate,
                    r.endDate,
                    r.room,
                    r.building
            );
            item.setScheduleType(r.scheduleType > 0 ? r.scheduleType : 1);
            result.add(item);
        }
        return result;
    }
}