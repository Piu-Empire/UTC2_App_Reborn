package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class TranscriptRequest {
    @SerializedName("academicYear")
    public String academicYear;
    @SerializedName("semester")
    public String semester;
    @SerializedName("quantity")
    public int quantity;
    @SerializedName("note")
    public String note;

    public TranscriptRequest(String academicYear, String semester, int quantity, String note) {
        this.academicYear = academicYear;
        this.semester = semester;
        this.quantity = quantity;
        this.note = note;
    }
}
