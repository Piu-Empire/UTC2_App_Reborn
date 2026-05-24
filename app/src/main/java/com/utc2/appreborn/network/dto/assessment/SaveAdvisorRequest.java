package com.utc2.appreborn.network.dto.assessment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaveAdvisorRequest {

    @SerializedName("periodId")
    public String periodId;

    @SerializedName("items")
    public List<Item> items;

    @SerializedName("studentOpinion")
    public String studentOpinion;

    public static class Item {
        @SerializedName("criteriaId")
        public int criteriaId;

        @SerializedName("score")
        public float score;

        public Item(int criteriaId, float score) {
            this.criteriaId = criteriaId;
            this.score      = score;
        }
    }
}