package com.utc2.appreborn.network;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper khớp với ApiResponse<T> của backend:
 * {
 *   "success": true,
 *   "message": "...",
 *   "data": { ... }
 * }
 */
public class ApiResponse<T> {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public boolean isSuccess() { return success; }
    public String  getMessage(){ return message; }
    public T       getData()   { return data; }
}