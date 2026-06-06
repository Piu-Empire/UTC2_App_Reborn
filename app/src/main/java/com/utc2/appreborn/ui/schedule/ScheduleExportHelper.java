package com.utc2.appreborn.ui.schedule;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.CustomToastHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ScheduleExportHelper {

    public static void exportWeekScheduleToImage(Activity activity, View viewWeek, String weekTitle) {
        // Tìm các thành phần cần thiết
        View weekContainer = viewWeek.findViewById(R.id.weekContainerRoot);
        View timeColumn = viewWeek.findViewById(R.id.timeColumn);
        View weekContentLayout = viewWeek.findViewById(R.id.weekContentLayout);

        if (weekContainer == null || timeColumn == null || weekContentLayout == null ||
            weekContainer.getWidth() == 0 || weekContainer.getHeight() == 0) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(activity, "Không thể tải lịch: Giao diện chưa sẵn sàng");
            return;
        }

        // Lấy kích thước thực tế (không bị cắt bởi màn hình)
        int timeColumnWidth = timeColumn.getWidth();
        int contentWidth = weekContentLayout.getWidth();
        int totalWidth = timeColumnWidth + contentWidth;
        
        int contentHeight = weekContainer.getHeight(); // Chiều cao thực của NestedScrollView

        // Cấu hình Header
        int headerHeight = 200; // chiều cao khu vực header (px)
        int totalHeight = contentHeight + headerHeight;

        try {
            // Khởi tạo Bitmap và Canvas với kích thước full
            Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Vẽ nền trắng toàn bộ
            canvas.drawColor(Color.WHITE);

            // --- VẼ HEADER ---
            // 1. Vẽ Logo Trường (logo_utc2)
            Drawable logo = ContextCompat.getDrawable(activity, R.drawable.logo_utc2);
            int logoSize = 120; // Kích thước logo
            int logoMarginTop = 40;
            int logoMarginLeft = 40;
            if (logo != null) {
                logo.setBounds(logoMarginLeft, logoMarginTop, logoMarginLeft + logoSize, logoMarginTop + logoSize);
                logo.draw(canvas);
            }

            // 2. Vẽ Tiêu đề "THỜI KHÓA BIỂU" và "Tuần XX"
            Paint paintTitle = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintTitle.setColor(Color.BLACK);
            paintTitle.setTextSize(60f);
            paintTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            String mainTitle = "THỜI KHÓA BIỂU";
            // Vẽ ngang hàng với logo, xích ra một chút
            int textStartX = logoMarginLeft + logoSize + 40;
            int textStartY = logoMarginTop + 60;
            canvas.drawText(mainTitle, textStartX, textStartY, paintTitle);

            // Vẽ chữ Tuần ở dưới
            Paint paintSub = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintSub.setColor(Color.DKGRAY);
            paintSub.setTextSize(40f);
            paintSub.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            
            canvas.drawText("Thời gian: " + weekTitle, textStartX, textStartY + 60, paintSub);

            // --- VẼ NỘI DUNG LỊCH ---
            // Dịch chuyển Canvas xuống dưới phần header
            canvas.save();
            canvas.translate(0, headerHeight);
            
            // Vẽ cột thời gian
            timeColumn.draw(canvas);
            
            // Dịch chuyển tiếp sang phải để vẽ nội dung các ngày trong tuần
            canvas.translate(timeColumnWidth, 0);
            weekContentLayout.draw(canvas);
            
            canvas.restore();

            // LƯU HÌNH ẢNH
            saveBitmapToGallery(activity, bitmap);

        } catch (Exception e) {
            e.printStackTrace();
            com.utc2.appreborn.utils.CustomToastHelper.showToast(activity, "Đã xảy ra lỗi khi tải ảnh!");
        }
    }

    private static void saveBitmapToGallery(Context context, Bitmap bitmap) {
        String fileName = "UTC2_Schedule_" + System.currentTimeMillis() + ".png";
        OutputStream fos = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    fos = context.getContentResolver().openOutputStream(uri);
                }
            } else {
                // Trên Android 9 trở xuống
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, fileName);
                fos = new FileOutputStream(file);
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                CustomToastHelper.showSuccessToast((Activity) context, "Đã lưu ảnh Lịch học thành công vào Thư viện!");
            } else {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(context, "Không thể lưu ảnh (chưa cấp quyền hoặc lỗi bộ nhớ)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.utc2.appreborn.utils.CustomToastHelper.showToast(context, "Lỗi khi lưu hình ảnh: " + e.getMessage());
        }
    }
}
