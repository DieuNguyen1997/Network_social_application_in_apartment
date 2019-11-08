package com.example.networksocialapplication.user.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key = AAAA2g_EMaE:APA91bFxBT8WamkdCcuS_WFh2083G8y9-aA0LOWJCRIMRWP2StbhId0scxYaj_P5tPMYnoDMfTZbOyUtBBc0MD9B8X9HHDVFw1wV0yjgnIaSom0IFxY65Zo0H1zcPE0-1E95aBwJGBIX"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
