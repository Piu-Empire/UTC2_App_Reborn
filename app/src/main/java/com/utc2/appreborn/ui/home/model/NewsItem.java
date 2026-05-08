package com.utc2.appreborn.ui.home.model;

/**
 * NewsItem
 * ──────────────────────────────────────────────────────────────
 * UI-layer model for one news post.
 * Updated: added  `content`  field (HTML string) so
 * NewsDetailActivity can render the full article body.
 *
 * Package: com.utc2.appreborn.ui.home.model
 */
public class NewsItem {

    private final String id;
    private final String title;
    private final String date;
    private final String summary;
    private final String content; // ← HTML body for detail screen

    // ── Full constructor (used when mapping from PostDto) ─────
    public NewsItem(String id, String title, String date,
                    String summary, String content) {
        this.id      = id;
        this.title   = title;
        this.date    = date;
        this.summary = summary != null ? summary : "";
        this.content = content != null ? content : "";
    }

    // ── Legacy constructor (mock data — content left empty) ───
    public NewsItem(String id, String title, String date, String summary) {
        this(id, title, date, summary, "");
    }

    // ── Getters ───────────────────────────────────────────────
    public String getId()      { return id;      }
    public String getTitle()   { return title;   }
    public String getDate()    { return date;    }
    public String getSummary() { return summary; }
    public String getContent() { return content; }
}