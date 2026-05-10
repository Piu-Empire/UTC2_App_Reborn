package com.utc2.appreborn.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ApiService — FINAL
 * ──────────────────────────────────────────────────────────────
 * Quay lại dùng NewsResponse (đã fix đúng cấu trúc JSON).
 * Không cần ResponseBody nữa.
 *
 * Package: com.utc2.appreborn.data.remote
 */
public interface ApiService {

    String FILTER_STUDENT_NEWS   = "type==STUDENT_ANNOUNCEMENT,display==true";
    String SORT_FIELD_CREATED_AT = "created_at";
    String SORT_ORDER_DESC       = "DESC";

    @GET("post")
    Call<NewsResponse> getPosts(
            @Query("currentPage")  int    currentPage,
            @Query("pageSize")     int    pageSize,
            @Query("sortField")    String sortField,
            @Query("sortOrder")    String sortOrder,
            @Query("filters")      String filters,
            @Query("subCategorys") String subCategorys
    );
}