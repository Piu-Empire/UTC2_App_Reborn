package com.utc2.appreborn.ui.search;

/**
 * SearchResult
 * ──────────────────────────────────────────────────────────────
 * Model đại diện cho một kết quả tìm kiếm.
 *
 * Hai loại kết quả:
 *   • FEATURE — tính năng trong app (Học phí, Lịch học, …)
 *   • NEWS    — tin tức / thông báo
 *
 * Package: com.utc2.appreborn.ui.search
 */
public class SearchResult {

    // ── Loại kết quả ─────────────────────────────────────────
    public enum Type { FEATURE, NEWS }

    // ── Fields ────────────────────────────────────────────────
    private final Type   type;
    private final String title;       // Tiêu đề hiển thị
    private final String subtitle;    // Mô tả phụ (ngày tin, loại tính năng…)
    private final String content;     // Nội dung đầy đủ (chỉ dùng cho NEWS)
    private final int    iconRes;     // Icon drawable (0 nếu không có)
    private final String featureId;   // ID tính năng (chỉ dùng cho FEATURE)

    // ── Constructors ──────────────────────────────────────────

    /** Dành cho kết quả loại FEATURE */
    public static SearchResult ofFeature(String title, String subtitle,
                                         int iconRes, String featureId) {
        return new SearchResult(Type.FEATURE, title, subtitle, "", iconRes, featureId);
    }

    /** Dành cho kết quả loại NEWS */
    public static SearchResult ofNews(String title, String date, String content) {
        return new SearchResult(Type.NEWS, title, date, content, 0, null);
    }

    private SearchResult(Type type, String title, String subtitle, String content,
                         int iconRes, String featureId) {
        this.type      = type;
        this.title     = title;
        this.subtitle  = subtitle;
        this.content   = content;
        this.iconRes   = iconRes;
        this.featureId = featureId;
    }

    // ── Getters ───────────────────────────────────────────────
    public Type   getType()      { return type;      }
    public String getTitle()     { return title;     }
    public String getSubtitle()  { return subtitle;  }
    public String getContent()   { return content;   }
    public int    getIconRes()   { return iconRes;   }
    public String getFeatureId() { return featureId; }
}