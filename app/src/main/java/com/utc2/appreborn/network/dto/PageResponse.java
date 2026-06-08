package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageResponse<T> {

    @SerializedName("content")
    public List<T> content;

    @SerializedName("pageable")
    public Object pageable;

    @SerializedName("last")
    public boolean last;

    @SerializedName("totalPages")
    public int totalPages;

    @SerializedName("totalElements")
    public long totalElements;

    @SerializedName("size")
    public int size;

    @SerializedName("number")
    public int number;

    @SerializedName("first")
    public boolean first;

    @SerializedName("numberOfElements")
    public int numberOfElements;

    @SerializedName("empty")
    public boolean empty;
}
