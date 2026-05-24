package com.utc2.appreborn.network.dto.assessment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StudentAssessmentResponse {

    @SerializedName("periodId")
    public String periodId;

    @SerializedName("items")
    public List<Item> items;

    public static class Item {
        @SerializedName("criteriaId")
        public int criteriaId;

        @SerializedName("score")
        public float score;

        @SerializedName("evidenceUris")
        public List<String> evidenceUris;
    }
}