package com.aaron.base.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.aaron.base.callback.HttpCallback;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class HttpUtil {

    public static Retrofit getRetrofit(String baseUrl, @Nullable OkHttpClient client) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        if (client != null) builder.client(client);
        return builder.build();
    }

    public static <T> T createService(String baseUrl, Class<T> service) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(service);
    }

    public static <T> T createService(String baseUrl, OkHttpClient client, Class<T> service) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(service);
    }

    public static ICall get(String url, HttpCallback<String> callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        ICall httpCall = new HttpCall(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onSuccess(response.body() != null ? response.body().string() : null);
            }
        });
        return httpCall;
    }

    public static ICall post(String url, Map<String, String> map, HttpCallback<String> callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = client.newCall(request);
        ICall iCall = new HttpCall(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onSuccess(response.body() != null ? response.body().string() : null);
            }
        });
        return iCall;
    }

    /**
     * 为了不将 OkHttp 的 API 暴露出去(对象适配器模式)
     */
    private static class HttpCall implements ICall {
        private Call call;

        HttpCall(Call call) {
            this.call = call;
        }

        @Override
        public void cancel() {
            if (!call.isCanceled()) call.cancel();
        }
    }

    public interface ICall {
        void cancel();
    }

    private HttpUtil() {

    }
}
