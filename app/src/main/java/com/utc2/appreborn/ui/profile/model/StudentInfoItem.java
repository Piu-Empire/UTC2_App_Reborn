package com.utc2.appreborn.ui.profile.model;

public class StudentInfoItem {
    private String label;
    private String value;

    public StudentInfoItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }
}