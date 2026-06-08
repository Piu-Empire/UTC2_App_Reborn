package com.utc2.appreborn.network.dto.assessment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExternalAssessmentResponse {

    @SerializedName("periodId")
    public String periodId;

    @SerializedName("items")
    public List<Item> items;

    // Trạng thái duyệt — dùng để hiển thị/ẩn tab tương ứng trên App
    @SerializedName("advisorApproved")
    public boolean advisorApproved;

    @SerializedName("khoaApproved")
    public boolean khoaApproved;

    @SerializedName("truongApproved")
    public boolean truongApproved;

    public static class Item {
        @SerializedName("criteriaId")
        public int criteriaId;

        @SerializedName("tapTheScore")
        public float tapTheScore;

        @SerializedName("boMonScore")
        public float boMonScore;

        @SerializedName("khoaScore")
        public float khoaScore;

        @SerializedName("truongScore")
        public float truongScore;
    }
}