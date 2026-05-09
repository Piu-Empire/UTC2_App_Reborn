package com.utc2.appreborn.ui.home.model;

public class FeatureItem {
    private final String id;
    private final int    iconRes;
    private final String title;

    public FeatureItem(String id, int iconRes, String title) {
        this.id      = id;
        this.iconRes = iconRes;
        this.title   = title;
    }

    public String getId()      { return id; }
    public int    getIconRes() { return iconRes; }
    public String getTitle()   { return title; }
}
