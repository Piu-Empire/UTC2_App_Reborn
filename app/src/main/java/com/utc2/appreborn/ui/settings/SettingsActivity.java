package com.utc2.appreborn.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.NotificationApiService;
import com.utc2.appreborn.network.dto.GmailLinkRequest;
import com.utc2.appreborn.network.dto.NotificationSettingRequest;
import com.utc2.appreborn.network.dto.NotificationSettingResponse;
import com.utc2.appreborn.utils.LocaleHelper;
import com.utc2.appreborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private TextView tvLangVi, tvLangEn;
    private View dotVi, dotEn;
    private CardView cardLangVi, cardLangEn;

    private SwitchMaterial switchSystemNotif;
    private SwitchMaterial switchGmailNotif;
    private TextView tvGmailStatus;

    private NotificationApiService apiService;
    private SessionManager sessionManager;
    private GoogleSignInClient mGoogleSignInClient;

    private boolean isUpdatingSetting = false;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null && account.getServerAuthCode() != null) {
                        Log.d(TAG, "Google Auth Code: " + account.getServerAuthCode());
                        // Gửi auth code lên server để lưu refresh token
                        linkGmailAccount(account.getServerAuthCode());
                    } else if (account != null && account.getIdToken() != null) {
                        Log.d(TAG, "Dùng tạm IdToken do không lấy được auth code (cần Web Client ID)");
                        linkGmailAccount(account.getIdToken());
                    } else {
                        com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Không lấy được token từ Google");
                        switchGmailNotif.setChecked(false);
                    }
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign in failed", e);
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Đăng nhập Google thất bại");
                    switchGmailNotif.setChecked(false);
                }
            });

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            apiService = ApiClient.getInstance(sessionManager.getAuthToken()).create(NotificationApiService.class);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("319562674855-3aga5buil99dcfdpjtgtt44n1opfb2br.apps.googleusercontent.com")
                .requestServerAuthCode("319562674855-3aga5buil99dcfdpjtgtt44n1opfb2br.apps.googleusercontent.com") 
                // Scope cần thiết để đọc Gmail
                .requestScopes(new com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/gmail.readonly"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        updateLanguageUI();
        setupEvents();

        if (sessionManager.isLoggedIn()) {
            loadNotificationSettings();
        } else {
            switchSystemNotif.setEnabled(false);
            switchGmailNotif.setEnabled(false);
        }
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

        switchSystemNotif = findViewById(R.id.switchSystemNotif);
        switchGmailNotif  = findViewById(R.id.switchGmailNotif);
        tvGmailStatus     = findViewById(R.id.tvGmailStatus);
    }

    private void setupEvents() {
        cardLangVi.setOnClickListener(v -> applyLanguage("vi"));
        cardLangEn.setOnClickListener(v -> applyLanguage("en"));

        switchSystemNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingSetting) return;
            updateSettings(isChecked, switchGmailNotif.isChecked());
        });

        switchGmailNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingSetting) return;
            if (isChecked) {
                // Người dùng muốn bật Gmail Notif -> Yêu cầu đăng nhập Google để link
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            } else {
                // Tắt Gmail Notif -> Unlink trên server
                unlinkGmailAccount();
                updateSettings(switchSystemNotif.isChecked(), false);
            }
        });
    }

    private void loadNotificationSettings() {
        if (apiService == null) return;
        isUpdatingSetting = true;
        apiService.getSettings().enqueue(new Callback<ApiResponse<NotificationSettingResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<NotificationSettingResponse>> call, Response<ApiResponse<NotificationSettingResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    NotificationSettingResponse settings = response.body().getData();
                    if (settings != null) {
                        switchSystemNotif.setChecked(settings.systemNotifEnabled);
                        switchGmailNotif.setChecked(settings.gmailNotifEnabled);

                        if (settings.gmailLinked) {
                            tvGmailStatus.setVisibility(View.VISIBLE);
                            tvGmailStatus.setText("Đã liên kết" + (settings.gmailTokenExpiry != null ? " (Hết hạn: " + settings.gmailTokenExpiry + ")" : ""));
                        } else {
                            tvGmailStatus.setVisibility(View.GONE);
                        }
                    }
                }
                isUpdatingSetting = false;
            }

            @Override
            public void onFailure(Call<ApiResponse<NotificationSettingResponse>> call, Throwable t) {
                isUpdatingSetting = false;
                com.utc2.appreborn.utils.CustomToastHelper.showToast(SettingsActivity.this, "Lỗi tải cài đặt");
            }
        });
    }

    private void updateSettings(boolean systemEnabled, boolean gmailEnabled) {
        if (apiService == null) return;
        apiService.updateSettings(new NotificationSettingRequest(systemEnabled, gmailEnabled))
                .enqueue(new Callback<ApiResponse<NotificationSettingResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<NotificationSettingResponse>> call, Response<ApiResponse<NotificationSettingResponse>> response) {
                        if (!response.isSuccessful()) {
                            com.utc2.appreborn.utils.CustomToastHelper.showToast(SettingsActivity.this, "Cập nhật cài đặt thất bại");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<NotificationSettingResponse>> call, Throwable t) {
                        com.utc2.appreborn.utils.CustomToastHelper.showToast(SettingsActivity.this, "Lỗi kết nối");
                    }
                });
    }

    private void linkGmailAccount(String googleToken) {
        if (apiService == null) return;
        apiService.linkGmail(new GmailLinkRequest(googleToken)).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(SettingsActivity.this, "Liên kết Gmail thành công");
                    updateSettings(switchSystemNotif.isChecked(), true);
                    tvGmailStatus.setVisibility(View.VISIBLE);
                    tvGmailStatus.setText("Đã liên kết");
                } else {
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(SettingsActivity.this, "Lỗi liên kết Gmail: " + response.message());
                    isUpdatingSetting = true;
                    switchGmailNotif.setChecked(false);
                    isUpdatingSetting = false;
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(SettingsActivity.this, "Lỗi kết nối");
                isUpdatingSetting = true;
                switchGmailNotif.setChecked(false);
                isUpdatingSetting = false;
            }
        });
    }

    private void unlinkGmailAccount() {
        if (apiService == null) return;
        apiService.unlinkGmail().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    tvGmailStatus.setVisibility(View.GONE);
                }
                mGoogleSignInClient.signOut();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });
    }

    private void applyLanguage(String lang) {
        String current = LocaleHelper.getSavedLanguage(this);
        if (current.equals(lang)) return;

        LocaleHelper.setLocale(this, lang);

        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        finish();
    }

    private void updateLanguageUI() {
        boolean isEn = LocaleHelper.isEnglish(this);
        if (isEn) {
            cardLangEn.setCardBackgroundColor(getColor(R.color.accent_yellow));
            tvLangEn.setTextColor(getColor(R.color.black));
            dotEn.setVisibility(View.VISIBLE);

            cardLangVi.setCardBackgroundColor(getColor(R.color.feature_card_bg));
            tvLangVi.setTextColor(getColor(R.color.text_muted));
            dotVi.setVisibility(View.GONE);
        } else {
            cardLangVi.setCardBackgroundColor(getColor(R.color.accent_yellow));
            tvLangVi.setTextColor(getColor(R.color.black));
            dotVi.setVisibility(View.VISIBLE);

            cardLangEn.setCardBackgroundColor(getColor(R.color.feature_card_bg));
            tvLangEn.setTextColor(getColor(R.color.text_muted));
            dotEn.setVisibility(View.GONE);
        }
    }
}