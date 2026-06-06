package com.utc2.appreborn.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.utc2.appreborn.utils.LocaleHelper;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

// THÊM MỚI
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.AuthApiService;
import com.utc2.appreborn.network.dto.AuthResponse;
import com.utc2.appreborn.network.dto.LoginRequest;
import com.utc2.appreborn.network.dto.GoogleAuthRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "AppReborn_Login";
    private EditText editMssv, editPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private SessionManager sessionManager;
    private Dialog loadingDialog;
    private AuthApiService authApi; // THÊM MỚI

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                setLoading(false);
                Log.d(TAG, "ActivityResult Code: " + result.getResultCode());
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    Log.e(TAG, "Dang nhap bi huy hoac loi Intent. Code: " + result.getResultCode());
                    showToast("Yeu cau dang nhap bi tu choi hoac huy.");
                }
            }
    );

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = SessionManager.getInstance(this);
        authApi = ApiClient.getPublicInstance().create(AuthApiService.class); // THÊM MỚI

        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupGoogleSignIn();
        createLoadingDialog();
    }

    private void initViews() {
        editMssv = findViewById(R.id.editMssv);
        editPassword = findViewById(R.id.editPassword);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button googleLoginBtn = findViewById(R.id.googleLoginBtn);
        Button skipBtn = findViewById(R.id.skipBtn);
        TextView txtForgot = findViewById(R.id.txtForgot);
        TextView txtTerms = findViewById(R.id.txtTerms);

        loginBtn.setOnClickListener(v -> performManualLogin());
        googleLoginBtn.setOnClickListener(v -> performGoogleSignIn());
        skipBtn.setOnClickListener(v -> performSkipLogin());

        if (txtForgot != null) {
            txtForgot.setOnClickListener(v ->
                    startActivity(new Intent(this, ForgotPasswordActivity.class)));
        }

        setupTermsAndPrivacy(txtTerms);
    }

    private void setupTermsAndPrivacy(TextView textView) {
        if (textView == null) return;

        String fullText = "Bang cach dang nhap, ban dong y voi Dieu khoan dich vu va Chinh sach bao mat cua chung toi.";
        SpannableString ss = new SpannableString(fullText);

        ClickableSpan termsClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, TermsActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.light_blue));
                ds.setUnderlineText(true);
                ds.setFakeBoldText(true);
            }
        };

        ClickableSpan privacyClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, PrivacyPolicyActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.light_blue));
                ds.setUnderlineText(true);
                ds.setFakeBoldText(true);
            }
        };

        ss.setSpan(termsClick, 36, 54, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(privacyClick, 58, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // ĐÃ SỬA
    private void performManualLogin() {
        String inputMssv = editMssv.getText().toString().trim();
        String inputPass = editPassword.getText().toString().trim();

        if (inputMssv.isEmpty() || inputPass.isEmpty()) {
            showToast(getString(R.string.error_fill_all));
            return;
        }

        setLoading(true);

        authApi.login(new LoginRequest(inputMssv, inputPass))
                .enqueue(new Callback<ApiResponse<AuthResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                           Response<ApiResponse<AuthResponse>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            AuthResponse auth = response.body().getData();
                            sessionManager.createLoginSession(
                                    auth.accessToken,
                                    "EMAIL",
                                    auth.email,
                                    -1L,
                                    auth.studentCode
                            );
                            navigateToMain();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : getString(R.string.wrong_email_or_pass);
                            showToast(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                        setLoading(false);
                        showToast("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    private void performSkipLogin() {
        navigateToMain();
    }

    private void performGoogleSignIn() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast(getString(R.string.error_connect_network));
            return;
        }
        setLoading(true);
        googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent());
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account == null) return;

            // Validate: chỉ cho phép email @st.utc2.edu.vn
            String email = account.getEmail();
            if (email == null || !email.endsWith("@st.utc2.edu.vn")) {
                mGoogleSignInClient.signOut();
                showToast("Chỉ tài khoản @st.utc2.edu.vn mới được đăng nhập");
                return;
            }

            setLoading(true);

            authApi.googleLogin(new GoogleAuthRequest(account.getIdToken()))
                    .enqueue(new Callback<ApiResponse<AuthResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                               Response<ApiResponse<AuthResponse>> response) {
                            setLoading(false);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().isSuccess()) {
                                AuthResponse auth = response.body().getData();
                                sessionManager.createLoginSession(
                                        auth.accessToken,
                                        "GOOGLE",
                                        auth.email,
                                        -1L,
                                        auth.studentCode
                                );
                                navigateToMain();
                            } else {
                                String msg = response.body() != null
                                        ? response.body().getMessage()
                                        : "Google login thất bại";
                                showToast(msg);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                            setLoading(false);
                            showToast("Lỗi kết nối: " + t.getMessage());
                        }
                    });

        } catch (ApiException e) {
            Log.e(TAG, "Ma loi Google Sign-In: " + e.getStatusCode());
            showToast("Loi xac thuc Google (Ma: " + e.getStatusCode() + ")");
        }
    }

    private void createLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custom_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void setLoading(boolean isLoading) {
        if (isLoading && loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        } else if (!isLoading && loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showToast(String msg) {
        com.utc2.appreborn.utils.CustomToastHelper.showToast(this, msg);
    }
}