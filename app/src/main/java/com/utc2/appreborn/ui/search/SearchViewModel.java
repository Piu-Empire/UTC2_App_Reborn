package com.utc2.appreborn.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.R;
import com.utc2.appreborn.data.repository.NewsRepository;
import com.utc2.appreborn.ui.home.model.NewsItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SearchViewModel
 * ──────────────────────────────────────────────────────────────
 * Xử lý logic tìm kiếm:
 *   1. Tìm trong danh sách tính năng (FEATURE) — so khớp tên
 *   2. Tìm trong danh sách tin tức (NEWS) — so khớp tiêu đề + nội dung
 *
 * Kết quả: FEATURE luôn hiển thị trước NEWS.
 * Tìm kiếm không phân biệt hoa/thường, có bỏ dấu cơ bản.
 *
 * Package: com.utc2.appreborn.ui.search
 */
public class SearchViewModel extends AndroidViewModel {

    private final MutableLiveData<List<SearchResult>> searchResults =
            new MutableLiveData<>(new ArrayList<>());

    private final NewsRepository newsRepository;

    // ── Danh sách tất cả tính năng có thể tìm ─────────────────
    // Cấu trúc: { featureId, displayName, subtitle }
    private static final String[][] ALL_FEATURES = {
            { "hoc_phi",    "Học phí",             "Xem và tra cứu học phí"           },
            { "dich_vu_cong","Dịch vụ công",        "Xác nhận sinh viên, học bổng, …"  },
            { "danh_gia",   "Đánh giá rèn luyện",  "Điểm rèn luyện từng học kỳ"       },
            { "ki_tuc_xa",  "Ký túc xá",           "Đăng ký và quản lý phòng KTX"     },
            { "ho_tro",     "Hỗ trợ sinh viên",    "Liên hệ phòng ban & hỗ trợ"       },
            { "lich_hoc",   "Lịch học",            "Thời khóa biểu tuần / tháng"      },
            { "ket_qua",    "Kết quả học tập",     "Điểm số các học phần"             },
    };

    // Icon tương ứng theo featureId
    private int getIconForFeature(String featureId) {
        switch (featureId) {
            case "hoc_phi":     return R.drawable.ic_hoc_phi;
            case "dich_vu_cong":return R.drawable.ic_dich_vu_cong;
            case "danh_gia":    return R.drawable.ic_danh_gia;
            case "ki_tuc_xa":   return R.drawable.ic_ki_tuc_xa;
            case "ho_tro":      return R.drawable.ic_ho_tro;
            default:            return R.drawable.ic_search;  // fallback
        }
    }

    public SearchViewModel(@NonNull Application application) {
        super(application);
        newsRepository = NewsRepository.getInstance(application);
    }

    // ── Public API ─────────────────────────────────────────────

    public LiveData<List<SearchResult>> getSearchResults() {
        return searchResults;
    }

    /**
     * Chạy tìm kiếm với từ khóa query.
     * Gọi mỗi khi người dùng thay đổi nội dung ô tìm kiếm.
     *
     * @param query Chuỗi tìm kiếm (có thể rỗng)
     */
    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            return;
        }

        String normalizedQuery = normalize(query.trim());
        List<SearchResult> results = new ArrayList<>();

        // 1. Tìm trong tính năng
        for (String[] feature : ALL_FEATURES) {
            String featureId   = feature[0];
            String displayName = feature[1];
            String description = feature[2];

            if (normalize(displayName).contains(normalizedQuery)
                    || normalize(description).contains(normalizedQuery)) {
                results.add(SearchResult.ofFeature(
                        displayName,
                        description,
                        getIconForFeature(featureId),
                        featureId));
            }
        }

        // 2. Tìm trong tin tức (từ cache LiveData của NewsRepository)
        List<NewsItem> newsList = newsRepository.getNewsLiveData().getValue();
        if (newsList != null) {
            for (NewsItem news : newsList) {
                boolean titleMatch   = normalize(news.getTitle()).contains(normalizedQuery);
                boolean contentMatch = normalize(news.getContent()).contains(normalizedQuery);

                if (titleMatch || contentMatch) {
                    results.add(SearchResult.ofNews(
                            news.getTitle(),
                            news.getDate(),
                            news.getContent()));
                }
            }
        }

        searchResults.setValue(results);
    }

    // ── Helpers ────────────────────────────────────────────────

    /**
     * Chuẩn hóa chuỗi để tìm kiếm không phân biệt hoa/thường.
     * Sử dụng Locale.ROOT để tránh lỗi với ký tự đặc biệt tiếng Anh.
     *
     * Lưu ý: bỏ dấu tiếng Việt đầy đủ cần thư viện ICU hoặc custom map.
     * Ở đây chỉ normalize lowercase — đủ dùng cho từ khóa thông thường.
     * Nâng cấp: thêm Normalizer.normalize(s, Normalizer.Form.NFD) + replaceAll("[^\\p{ASCII}]", "")
     * nếu muốn hỗ trợ tìm không dấu.
     */
    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase(Locale.ROOT);
    }
}