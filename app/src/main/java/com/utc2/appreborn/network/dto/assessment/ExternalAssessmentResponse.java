package com.utc2.appreborn.network.dto.assessment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExternalAssessmentResponse {

    @SerializedName("periodId")
    public String periodId;

    @SerializedName("items")
    public List<Item> items;

    public static class Item {
        @SerializedName("criteriaId")
        public int criteriaId;

        @SerializedName("tapTheScore")
        public float tapTheScore;

        @SerializedName("khoaScore")
        public float khoaScore;

        @SerializedName("truongScore")
        public float truongScore;
    }
}