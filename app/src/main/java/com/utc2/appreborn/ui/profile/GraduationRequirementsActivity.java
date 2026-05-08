package com.utc2.appreborn.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class GraduationRequirementsActivity extends AppCompatActivity {

    private static final String TAG = "GraduationReq";
    private TextView tvRequirementsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graduation_requirements);

        ImageButton btnBack = findViewById(R.id.btnBack);
        tvRequirementsContent = findViewById(R.id.tvRequirementsContent);

        btnBack.setOnClickListener(v -> finish());

        displayGraduationData();
    }

    private void displayGraduationData() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("graduation_note.txt")))) {

            String line;
            while ((line = reader.readLine()) != null) {
                while ((line = reader.readLine()) != null) {
                    // Tạo chuỗi pattern để tìm các thẻ
                    String patternStr = Pattern.quote("");

                    // Xóa các thẻ đó khỏi dòng text
                    String cleanLine = line.replaceAll(patternStr, "").trim();

                    // Dọn dẹp các ký tự đặc biệt có trong file graduation_note.txt
                    cleanLine = cleanLine.replace("&amp;", "&");
                    cleanLine = cleanLine.replace("\\n", "");

                    if (!cleanLine.isEmpty()) {
                        content.append(cleanLine).append("\n\n");
                    }
                }
            }

            tvRequirementsContent.setText(content.toString().trim());

        } catch (Exception e) {
            Log.e(TAG, "Lỗi đọc file assets", e);
            tvRequirementsContent.setText(R.string.error_loading_file);
        }
    }
}