package com.utc2.appreborn.ui.courseregistration.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp xử lý lưu/đọc dữ liệu đăng ký học phần vào file JSON nội bộ.
 *
 * [Chương 7 – Vào/Ra dữ liệu với file]
 *  - FileOutputStream / FileInputStream  : ghi và đọc file nhị phân / text
 *  - BufferedReader                       : đọc text hiệu quả từng dòng
 *  - JSONObject / JSONArray               : định dạng JSON để lưu trữ
 *  - try-catch-finally                    : đảm bảo đóng stream mọi trường hợp
 *
 * File được lưu ở bộ nhớ trong ứng dụng (Context.MODE_PRIVATE),
 * không cần quyền đọc/ghi bộ nhớ ngoài.
 */
public class CourseStorage {

    private static final String TAG       = "CourseStorage";
    private static final String FILE_NAME = "course_registrations.json";

    // ── Lưu danh sách courseId đã XÁC NHẬN vào file JSON ────────────────────
    /**
     * Ghi danh sách courseId đã xác nhận ra file JSON.
     * Format: ["c1","c3","c5"]
     *
     * [Chương 7] FileOutputStream + try-catch-finally
     */
    public static void saveConfirmedIds(Context context, List<String> confirmedIds) {
        FileOutputStream fos = null;
        try {
            JSONArray array = new JSONArray();
            for (String id : confirmedIds) {
                array.put(id);
            }
            String json = array.toString();

            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(json.getBytes("UTF-8"));
            Log.d(TAG, "Đã lưu " + confirmedIds.size() + " môn đã xác nhận vào " + FILE_NAME);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lưu file JSON: " + e.getMessage(), e);
        } finally {
            if (fos != null) {
                try { fos.close(); } catch (Exception ignore) {}
            }
        }
    }

    // ── Đọc danh sách courseId đã xác nhận từ file JSON ─────────────────────
    /**
     * Đọc file JSON trả về danh sách courseId đã xác nhận.
     * Nếu file chưa tồn tại hoặc lỗi → trả về danh sách rỗng.
     *
     * [Chương 7] FileInputStream + BufferedReader + try-catch-finally
     */
    public static List<String> loadConfirmedIds(Context context) {
        List<String>    result = new ArrayList<>();
        FileInputStream fis    = null;
        BufferedReader  reader = null;
        try {
            fis    = context.openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString().trim();
            if (!json.isEmpty()) {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    result.add(array.getString(i));
                }
            }
            Log.d(TAG, "Đã đọc " + result.size() + " môn đã xác nhận từ " + FILE_NAME);

        } catch (java.io.FileNotFoundException e) {
            // File chưa có – lần đầu mở app, bình thường
            Log.d(TAG, FILE_NAME + " chưa tồn tại, trả về danh sách rỗng.");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đọc file JSON: " + e.getMessage(), e);
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignore) {}
            try { if (fis    != null) fis.close();    } catch (Exception ignore) {}
        }
        return result;
    }

    // ── Lưu map courseId → credits ───────────────────────────────────────────
    private static final String CREDITS_FILE = "course_credits.json";

    public static void saveCreditsMap(Context context, java.util.Map<String, Integer> creditsMap) {
        FileOutputStream fos = null;
        try {
            JSONObject obj = new JSONObject();
            for (java.util.Map.Entry<String, Integer> e : creditsMap.entrySet()) {
                obj.put(e.getKey(), e.getValue());
            }
            fos = context.openFileOutput(CREDITS_FILE, Context.MODE_PRIVATE);
            fos.write(obj.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, "Lỗi saveCreditsMap: " + e.getMessage());
        } finally {
            try { if (fos != null) fos.close(); } catch (Exception ignore) {}
        }
    }

    public static java.util.Map<String, Integer> loadCreditsMap(Context context) {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = context.openFileInput(CREDITS_FILE);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONObject obj = new JSONObject(sb.toString().trim());
            java.util.Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                result.put(key, obj.getInt(key));
            }
        } catch (java.io.FileNotFoundException e) {
            // chưa có file — bình thường
        } catch (Exception e) {
            Log.e(TAG, "Lỗi loadCreditsMap: " + e.getMessage());
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignore) {}
            try { if (fis != null) fis.close(); } catch (Exception ignore) {}
        }
        return result;
    }

    // ── Lưu / đọc hóa đơn local (thanh toán offline) ────────────────────────
    private static final String INVOICE_FILE = "local_invoices.json";

    public static void saveLocalInvoice(Context context, String invoiceCode,
                                        String label, double amount, String paidAt) {
        FileOutputStream fos = null;
        try {
            JSONArray array = loadLocalInvoicesRaw(context);
            JSONObject obj = new JSONObject();
            obj.put("invoiceCode", invoiceCode);
            obj.put("label", label);
            obj.put("amount", amount);
            obj.put("paidAt", paidAt);
            array.put(obj);
            fos = context.openFileOutput(INVOICE_FILE, Context.MODE_PRIVATE);
            fos.write(array.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, "Lỗi saveLocalInvoice: " + e.getMessage());
        } finally {
            try { if (fos != null) fos.close(); } catch (Exception ignore) {}
        }
    }

    private static JSONArray loadLocalInvoicesRaw(Context context) {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = context.openFileInput(INVOICE_FILE);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return new JSONArray(sb.toString().trim());
        } catch (java.io.FileNotFoundException e) {
            return new JSONArray();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi loadLocalInvoicesRaw: " + e.getMessage());
            return new JSONArray();
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignore) {}
            try { if (fis != null) fis.close(); } catch (Exception ignore) {}
        }
    }

    /** Trả về list {invoiceCode, label, amount, paidAt} */
    public static java.util.List<String[]> loadLocalInvoices(Context context) {
        java.util.List<String[]> result = new java.util.ArrayList<>();
        try {
            JSONArray array = loadLocalInvoicesRaw(context);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                result.add(new String[]{
                        obj.optString("invoiceCode", ""),
                        obj.optString("label", ""),
                        String.valueOf(obj.optDouble("amount", 0)),
                        obj.optString("paidAt", "")
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi loadLocalInvoices: " + e.getMessage());
        }
        return result;
    }

    // ── Xóa đăng ký (KHÔNG xóa invoice) ─────────────────────────────────────
    public static void clearStorage(Context context) {
        context.deleteFile(FILE_NAME);
        context.deleteFile(CREDITS_FILE);
        Log.d(TAG, "Đa xoa enrollment + credits, giu lai invoice");
    }
}