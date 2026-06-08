package com.utc2.appreborn.model;

import java.util.ArrayList;
import java.util.List;

public class AssessmentCriteria {

    // ─── View Types ───────────────────────────────────────────────────────────
    public static final int TYPE_SECTION_HEADER = 0;
    public static final int TYPE_CRITERIA       = 1;
    public static final int TYPE_DEDUCTION      = 2;
    public static final int TYPE_FOOTER_RLSV    = 3; // Footer KQRL (Sinh viên)
    public static final int TYPE_FOOTER_CVHT    = 4; // Footer Ý kiến SV (CVHT)

    // ─── Fields ───────────────────────────────────────────────────────────────
    private final int    viewType;
    private final int    id;
    private final String title;
    private final String description;
    private final float  maxScore;
    private       float  currentScore;
    private final boolean requiresEvidence;
    private final List<String> evidenceUris;
    private final List<Float>  scoreOptions;

    // Điểm readonly các cột khác (đọc từ DB)
    private float tapTheScore;
    private float boMonScore;
    private float khoaScore;
    private float truongScore;

    // ─── Constructor đầy đủ ──────────────────────────────────────────────────

    public AssessmentCriteria(int viewType, int id, String title, String description,
                              float maxScore, boolean requiresEvidence,
                              List<Float> scoreOptions,
                              float tapTheScore, float khoaScore, float truongScore) {
        this.viewType         = viewType;
        this.id               = id;
        this.title            = title;
        this.description      = description;
        this.maxScore         = maxScore;
        this.currentScore     = maxScore;
        this.requiresEvidence = requiresEvidence;
        this.scoreOptions     = scoreOptions;
        this.evidenceUris     = new ArrayList<>();
        this.tapTheScore      = tapTheScore;
        this.khoaScore        = khoaScore;
        this.truongScore      = truongScore;
    }

    /** Constructor ngắn (không có điểm readonly) */
    public AssessmentCriteria(int viewType, int id, String title, String description,
                              float maxScore, boolean requiresEvidence,
                              List<Float> scoreOptions) {
        this(viewType, id, title, description, maxScore, requiresEvidence, scoreOptions,
                maxScore, maxScore, maxScore);
    }

    /** Constructor cho footer (chỉ cần viewType, không cần fields khác) */
    public AssessmentCriteria(int viewType) {
        this(viewType, -viewType, null, null, 0f, false, null, 0f, 0f, 0f);
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public int     getViewType()          { return viewType; }
    public int     getId()                { return id; }
    public String  getTitle()             { return title; }
    public String  getDescription()       { return description; }
    public float   getMaxScore()          { return maxScore; }
    public float   getCurrentScore()      { return currentScore; }
    public boolean isRequiresEvidence()   { return requiresEvidence; }
    public List<Float>  getScoreOptions() { return scoreOptions; }
    public List<String> getEvidenceUris() { return evidenceUris; }
    public boolean hasEvidence()          { return !evidenceUris.isEmpty(); }
    public int     evidenceCount()        { return evidenceUris.size(); }
    public float   getTapTheScore()       { return tapTheScore; }
    public float   getBoMonScore()        { return boMonScore; }
    public float   getKhoaScore()         { return khoaScore; }
    public float   getTruongScore()       { return truongScore; }

    // ─── Setters ──────────────────────────────────────────────────────────────

    public void setCurrentScore(float v) { this.currentScore = v; }
    public void setTapTheScore(float v)  { this.tapTheScore = v; }
    public void setBoMonScore(float v)   { this.boMonScore = v; }
    public void setKhoaScore(float v)    { this.khoaScore = v; }
    public void setTruongScore(float v)  { this.truongScore = v; }

    public void addEvidenceUri(String uri) {
        if (uri != null && !uri.isEmpty() && !evidenceUris.contains(uri))
            evidenceUris.add(uri);
    }

    public void removeEvidenceAt(int index) {
        if (index >= 0 && index < evidenceUris.size())
            evidenceUris.remove(index);
    }
}