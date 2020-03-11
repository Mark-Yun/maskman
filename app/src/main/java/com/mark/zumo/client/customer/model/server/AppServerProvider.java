package com.mark.zumo.client.customer.model.server;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mark.zumo.client.customer.ContextHolder;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mark on 20. 3. 8.
 */
public enum AppServerProvider {
    INSTANCE;

    private static final String URL = "https://z8nn984j7i.execute-api.ap-northeast-2.amazonaws.com/api/";

    private static final int MAX_CACHE_SIZE = 5 * 1024 * 1024;
    public final AppServer appServer;
    private final Context context;

    AppServerProvider() {
        context = ContextHolder.getContext();
        appServer = buildAppServerInterface();
    }

    private static OkHttpClient okHttpClient(final Context context) {
        return new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), MAX_CACHE_SIZE))
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();
    }

    private AppServer buildAppServerInterface() {
        return new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(createGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient(context))
                .build()
                .create(AppServer.class);
    }

    @NonNull
    private Gson createGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

}
