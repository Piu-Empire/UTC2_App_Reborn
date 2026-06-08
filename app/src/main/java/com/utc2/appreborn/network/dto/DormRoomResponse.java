package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Mapping với backend DormRoomDto.
 * Endpoint: GET /api/v1/dormitory/rooms
 */
public class DormRoomResponse {

    @SerializedName("roomId")
    public Long roomId;

    @SerializedName("roomCode")
    public String roomCode;

    @SerializedName("building")
    public String building;

    @SerializedName("floor")
    public Integer floor;

    @SerializedName("capacity")
    public Integer capacity;

    @SerializedName("currentOccupancy")
    public Integer currentOccupancy;

    /** "NAM" hoặc "NU" */
    @SerializedName("roomType")
    public String roomType;

    @SerializedName("pricePerMonth")
    public Double pricePerMonth;

    /** "AVAILABLE" hoặc "FULL" */
    @SerializedName("status")
    public String status;

    @SerializedName("amenities")
    public String amenities;

    @SerializedName("available")
    public Boolean available;

    public double getPricePerMonthSafe() {
        return pricePerMonth != null ? pricePerMonth : 0.0;
    }

    public boolean isAvailable() {
        return Boolean.TRUE.equals(available) || "còn chỗ".equals(status);
    }
}