package com.utc2.appreborn.data.local;

import android.content.Context;

import com.google.gson.Gson;
import com.utc2.appreborn.network.ScheduleApiService;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Quản lý file JSON lịch học lưu trên bộ nhớ thiết bị.
 *
 * File lưu tại: filesDir/schedule_{studentCode}.json
 */
public class ScheduleFileManager {

    private static final String FILE_PREFIX = "schedule_";
    private static final String FILE_EXT    = ".json";

    private final Context context;
    private final Gson gson = new Gson();

    public ScheduleFileManager(Context context) {
        this.context = context.getApplicationContext();
    }

    // Đọc file local — trả null nếu chưa có hoặc đọc lỗi
    public ScheduleApiService.ScheduleFileResponse readLocalFile(String studentCode) {
        File file = getFile(studentCode);
        if (!file.exists()) return null;
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, ScheduleApiService.ScheduleFileResponse.class);
        } catch (IOException e) {
            return null;
        }
    }

    // Ghi file mới từ server xuống — ghi đè nếu đã có
    public void saveFile(String studentCode, ScheduleApiService.ScheduleFileResponse data) {
        File file = getFile(studentCode);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            // ghi thất bại — lần sau sẽ thử lại
        }
    }

    // Lấy lastUpdated trong file local — trả null nếu chưa có file
    public String getLocalLastUpdated(String studentCode) {
        ScheduleApiService.ScheduleFileResponse local = readLocalFile(studentCode);
        return (local != null) ? local.lastUpdated : null;
    }

    // So sánh timestamp: true = server mới hơn (cần download)
    public boolean isServerNewer(String serverTimestamp, String localTimestamp) {
        if (localTimestamp == null) return true;
        return serverTimestamp != null && serverTimestamp.compareTo(localTimestamp) > 0;
    }

    private File getFile(String studentCode) {
        return new File(context.getFilesDir(), FILE_PREFIX + studentCode + FILE_EXT);
    }
}
