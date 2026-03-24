package com.example.prm_mo.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "https://flood-rescue.onrender.com/";
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    // Cách 1: Cho các file KHÔNG dùng Context (như TimelineDetailActivity)
    public static ApiService getApiService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    // Cách 2: Cho các file CÓ dùng Context (như CreateRequestActivity)
    public static ApiService getApiService(Context context) {
        return getApiService(); // Tái sử dụng hàm trên cho gọn
    }

    public static void reset() {
        retrofit = null;
        apiService = null;
    }
}