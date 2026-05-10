package com.utc2.appreborn.ui.profile.model;

public class ProfileInfoItem {
    private String label;
    private String value;

    public ProfileInfoItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }
}