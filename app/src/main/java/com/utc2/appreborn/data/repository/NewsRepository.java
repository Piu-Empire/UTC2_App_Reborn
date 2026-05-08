package com.utc2.appreborn.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.utc2.appreborn.data.remote.ApiService;
import com.utc2.appreborn.data.remote.NewsResponse;
import com.utc2.appreborn.data.remote.RetrofitClient;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * NewsRepository v4 — FINAL FIX
 * ──────────────────────────────────────────────────────────────
 * Thay đổi:
 *  • Dùng lại getPosts() trả về NewsResponse (đã fix DTO)
 *  • Đổi sang PREFS_NAME mới để xóa cache cũ sai
 *  • NewsItem.content = URL trang chi tiết (sẽ load bằng WebView)
 *
 * Package: com.utc2.appreborn.data.repository
 */
public class NewsRepository {

    private static final String TAG        = "UTC2_REPO";
    // Đổi tên để tự xóa cache cũ có cấu trúc sai
    private static final String PREFS_NAME = "utc2_news_v4";
    private static final String KEY_JSON   = "news_json";
    private static final String KEY_LAST   = "last_fetch_ms";
    private static final long   CACHE_TTL  = TimeUnit.HOURS.toMillis(24);

    // ── Singleton ─────────────────────────────────────────────
    private static NewsRepository instance;

    public static NewsRepository getInstance(Context context) {
        if (instance == null)
            instance = new NewsRepository(context.getApplicationContext());
        return instance;
    }

    public static NewsRepository getInstance() {
        if (instance == null)
            throw new IllegalStateException("Call getInstance(context) first.");
        return instance;
    }

    // ── State ─────────────────────────────────────────────────
    private final SharedPreferences               prefs;
    private final Gson                            gson = new Gson();
    private final MutableLiveData<List<NewsItem>> newsLiveData;
    private final MutableLiveData<Boolean>        isLoadingLiveData =
            new MutableLiveData<>(false);

    private Call<NewsResponse> activeCall;

    private NewsRepository(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<NewsItem> cached = loadFromCache();
        newsLiveData = new MutableLiveData<>(
                cached != null ? cached : MockHelper.getMockNewsList());
    }

    // ── Public ────────────────────────────────────────────────
    public LiveData<List<NewsItem>> getNewsLiveData()      { return newsLiveData;      }
    public LiveData<Boolean>        getIsLoadingLiveData() { return isLoadingLiveData; }

    public void fetchNewsIfNeeded() {
        if (isCacheValid()) { Log.d(TAG, "Cache OK"); return; }
        fetchFromApi();
    }

    public void forceRefresh() { fetchFromApi(); }

    public void cancelActiveCall() {
        if (activeCall != null) { activeCall.cancel(); activeCall = null; }
    }

    // ── Network ───────────────────────────────────────────────
    private void fetchFromApi() {
        if (activeCall != null && !activeCall.isCanceled()) activeCall.cancel();

        isLoadingLiveData.postValue(true);
        Log.d(TAG, "Calling API...");

        activeCall = RetrofitClient.api().getPosts(
                1, 10,
                ApiService.SORT_FIELD_CREATED_AT,
                ApiService.SORT_ORDER_DESC,
                ApiService.FILTER_STUDENT_NEWS,
                "");

        activeCall.enqueue(new Callback<NewsResponse>() {

            @Override
            public void onResponse(@NonNull Call<NewsResponse> call,
                                   @NonNull Response<NewsResponse> response) {
                isLoadingLiveData.postValue(false);
                Log.d(TAG, "HTTP " + response.code());

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Bad response: " + response.code());
                    return;
                }

                List<NewsResponse.PostDto> rows = response.body().getData();
                Log.d(TAG, "rows size = " + (rows == null ? "null" : rows.size()));

                if (rows == null || rows.isEmpty()) {
                    Log.e(TAG, "rows is empty");
                    return;
                }

                List<NewsItem> items = map(rows);
                newsLiveData.postValue(items);
                saveToCache(items);
                updateLastFetchTime();
                Log.d(TAG, "✓ Loaded " + items.size() + " items");
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call,
                                  @NonNull Throwable t) {
                if (call.isCanceled()) return;
                isLoadingLiveData.postValue(false);
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }

    // ── Cache ─────────────────────────────────────────────────
    private boolean isCacheValid() {
        long last = prefs.getLong(KEY_LAST, 0);
        return last != 0 && (System.currentTimeMillis() - last) < CACHE_TTL;
    }

    private void saveToCache(List<NewsItem> items) {
        prefs.edit().putString(KEY_JSON, gson.toJson(items)).apply();
    }

    private List<NewsItem> loadFromCache() {
        String json = prefs.getString(KEY_JSON, null);
        if (json == null) return null;
        try {
            Type type = new TypeToken<List<NewsItem>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            prefs.edit().remove(KEY_JSON).remove(KEY_LAST).apply();
            return null;
        }
    }

    private void updateLastFetchTime() {
        prefs.edit().putLong(KEY_LAST, System.currentTimeMillis()).apply();
    }

    // ── Mapping ───────────────────────────────────────────────
    private List<NewsItem> map(List<NewsResponse.PostDto> posts) {
        List<NewsItem> result = new ArrayList<>(posts.size());
        for (NewsResponse.PostDto dto : posts) {
            result.add(new NewsItem(
                    dto.getId(),
                    dto.getTitle(),
                    dto.getDisplayDate(),
                    dto.getSummary(),
                    dto.getDetailUrl()   // URL trang chi tiết → WebView load
            ));
        }
        return result;
    }
}