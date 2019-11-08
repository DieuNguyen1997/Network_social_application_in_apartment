package com.example.networksocialapplication.user.notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit sRetrofit = null;

    public static  Retrofit getRetrofit(String url){
        if (sRetrofit == null){
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}
