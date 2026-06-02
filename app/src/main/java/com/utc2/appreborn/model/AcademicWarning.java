package com.utc2.appreborn.model;

/**
 * AcademicWarning - Model cho một cảnh báo học vụ.
 *
 * status: ACTIVE (chưa giải quyết) | RESOLVED (đã giải quyết)
 */
public class AcademicWarning {

    public static final String STATUS_ACTIVE   = "ACTIVE";
    public static final String STATUS_RESOLVED = "RESOLVED";

    public static final int ICON_BOOK  = 1;
    public static final int ICON_CLOCK = 2;

    private int    id;
    private String title;
    private String subTitle;
    private String date;
    private String status;   // "ACTIVE" | "RESOLVED"
    private int    iconType;

    public AcademicWarning(int id, String title, String subTitle,
                           String date, String status, int iconType) {
        this.id       = id;
        this.title    = title;
        this.subTitle = subTitle;
        this.date     = date;
        this.status   = status;
        this.iconType = iconType;
    }

    public int    getId()       { return id; }
    public String getTitle()    { return title; }
    public String getSubTitle() { return subTitle; }
    public String getDate()     { return date; }
    public String getStatus()   { return status; }
    public int    getIconType() { return iconType; }

    public boolean isActive()   { return STATUS_ACTIVE.equals(status); }
    public boolean isResolved() { return STATUS_RESOLVED.equals(status); }
}