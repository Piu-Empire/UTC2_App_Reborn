package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class TuitionResponse {
    @SerializedName("id")              public Long   id;
    @SerializedName("studentId")       public String studentId;
    @SerializedName("fullName")        public String fullName;
    @SerializedName("semesterId")      public Long   semesterId;
    @SerializedName("totalAmount")     public Double totalAmount;
    @SerializedName("paidAmount")      public Double paidAmount;
    @SerializedName("remainingAmount") public Double remainingAmount;
    @SerializedName("dueDate")         public String dueDate;
    @SerializedName("paidAt")          public String paidAt;
    @SerializedName("status")          public String status;
    @SerializedName("paymentMethod")   public String paymentMethod;
}