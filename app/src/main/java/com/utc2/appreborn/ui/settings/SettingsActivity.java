package com.utc2.appreborn.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.utils.LocaleHelper;
import com.utc2.appreborn.utils.SessionManager;

/**
 * SettingsActivity
 * ──────────────────────────────────────────────────────────────
 * Màn hình cài đặt — mở từ icon ⚙ trên ProfileFragment.
 *
 * Chức năng hiện tại:
 *   • Đổi ngôn ngữ: Tiếng Việt ↔ English
 *
 * Thêm vào AndroidManifest.xml:
 *   <activity android:name=".ui.settings.SettingsActivity" android:exported="false" />
 *
 * Gọi LocaleHelper.applyLocale(base) trong attachBaseContext của
 * mọi Activity để locale được áp dụng khi restart.
 */
public class SettingsActivity extends AppCompatActivity {

    private TextView tvLangVi, tvLangEn;
    private View dotVi, dotEn;
    private CardView cardLangVi, cardLangEn;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        updateLanguageUI();
        setupEvents();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        cardLangVi  = findViewById(R.id.cardLangVi);
        cardLangEn  = findViewById(R.id.cardLangEn);
        tvLangVi    = findViewById(R.id.tvLangVi);
        tvLangEn    = findViewById(R.id.tvLangEn);
        dotVi       = findViewById(R.id.dotVi);
        dotEn       = findViewById(R.id.dotEn);
    }

    private void setupEvents() {
        cardLangVi.setOnClickListener(v -> applyLanguage("vi"));
        cardLangEn.setOnClickListener(v -> applyLanguage("en"));
    }

    private void applyLanguage(String lang) {
        String current = LocaleHelper.getSavedLanguage(this);
        if (current.equals(lang)) return; // không làm gì nếu đang dùng rồi

        // Lưu + áp locale mới
        LocaleHelper.setLocale(this, lang);

        // Restart toàn bộ app từ MainActivity để locale áp dụng ngay
        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }

    private void updateLanguageUI() {
        boolean isEn = LocaleHelper.isEnglish(this);

        // Card đang chọn → nền vàng, chữ đen, dot hiển thị
        // Card không chọn → nền xám tối, chữ xám, dot ẩn
        if (isEn) {
            // English selected
            cardLangEn.setCardBackgroundColor(getColor(R.color.accent_yellow));
            tvLangEn.setTextColor(getColor(R.color.black));
            dotEn.setVisibility(View.VISIBLE);

            cardLangVi.setCardBackgroundColor(getColor(R.color.feature_card_bg));
            tvLangVi.setTextColor(getColor(R.color.text_muted));
            dotVi.setVisibility(View.GONE);
        } else {
            // Vietnamese selected
            cardLangVi.setCardBackgroundColor(getColor(R.color.accent_yellow));
            tvLangVi.setTextColor(getColor(R.color.black));
            dotVi.setVisibility(View.VISIBLE);

            cardLangEn.setCardBackgroundColor(getColor(R.color.feature_card_bg));
            tvLangEn.setTextColor(getColor(R.color.text_muted));
            dotEn.setVisibility(View.GONE);
        }
    }
}