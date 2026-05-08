package com.utc2.appreborn.ui.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.databinding.ActivityNewsDetailBinding;

/**
 * NewsDetailActivity — UPDATED
 * ──────────────────────────────────────────────────────────────
 * Thay đổi: List API không trả về content HTML.
 * Thay vào đó, EXTRA_CONTENT giờ chứa URL trang chi tiết
 * (https://utc2.edu.vn/sinh-vien/thong-bao/{seo_text}).
 *
 * Logic:
 *   • Nếu content bắt đầu bằng "http" → loadUrl() (trang web thật)
 *   • Nếu content là HTML string      → loadDataWithBaseURL() (fallback)
 *   • Nếu rỗng                        → hiển thị thông báo không có nội dung
 *
 * Package: com.utc2.appreborn.ui.news
 */
public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE   = "extra_title";
    public static final String EXTRA_DATE    = "extra_date";
    public static final String EXTRA_CONTENT = "extra_content"; // URL hoặc HTML

    private ActivityNewsDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title   = getIntent().getStringExtra(EXTRA_TITLE);
        String date    = getIntent().getStringExtra(EXTRA_DATE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);

        bindHeader(title, date);
        setupWebView();
        loadContent(content);

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void bindHeader(String title, String date) {
        if (title != null) binding.tvDetailTitle.setText(title);
        if (date  != null) binding.tvDetailDate.setText(date);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings s = binding.webViewContent.getSettings();
        s.setJavaScriptEnabled(true);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setDomStorageEnabled(true);

        binding.webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                // Mở tất cả link trong WebView, không ra browser ngoài
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Ẩn header title/date khi đã load URL (trang web tự có header)
                if (url != null && url.startsWith("http")) {
                    binding.tvDetailTitle.setVisibility(View.GONE);
                    binding.tvDetailDate.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadContent(String content) {
        if (content == null || content.isEmpty()) {
            // Không có gì → show thông báo
            loadHtml("<p style='padding:16px;color:#757575;'>Không có nội dung.</p>");
            return;
        }

        if (content.startsWith("http")) {
            // ✅ Đây là URL trang chi tiết → load thẳng trang web UTC2
            binding.webViewContent.loadUrl(content);
        } else {
            // Fallback: content là raw HTML string
            loadHtml(content);
        }
    }

    /** Bọc HTML string trong shell CSS chuẩn */
    private void loadHtml(String htmlBody) {
        String full = "<!DOCTYPE html><html><head>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<style>"
                + "body{font-family:Roboto,sans-serif;font-size:16px;"
                + "line-height:1.7;color:#1A1A1A;padding:0 16px 32px;margin:0}"
                + "img,video,table{max-width:100%!important;height:auto!important}"
                + "a{color:#6B47DC}"
                + "</style></head><body>" + htmlBody + "</body></html>";

        binding.webViewContent.loadDataWithBaseURL(
                "https://utc2.edu.vn/", full, "text/html", "UTF-8", null);
    }

    @Override
    protected void onDestroy() {
        if (binding != null) {
            binding.webViewContent.stopLoading();
            binding.webViewContent.destroy();
        }
        super.onDestroy();
    }
}