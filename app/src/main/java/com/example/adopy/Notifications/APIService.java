package com.example.adopy.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key-AAAAQBUuBLY:APA91bH4iUZiKD8Doxw6BgF33X4aFOi7z5j0aF8udqqvKzHfnlrzcKDSw_BI1F7I4LQECqXcvhDGjtkEyVtiJu1X1rYp1XxWd4_PWfGL5ymsMEPqIPMgc9k_RVDUCslPUcWhvVsG6PTW"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
