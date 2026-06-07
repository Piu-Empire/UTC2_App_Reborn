package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.AiChatRequest;
import com.utc2.appreborn.network.dto.AiChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AiChatApiService {
    @POST("api/aichat/message")
    Call<AiChatResponse> processMessage(@Body AiChatRequest request);
}
