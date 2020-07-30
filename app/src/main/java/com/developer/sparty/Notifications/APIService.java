package com.developer.sparty.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Context-Type:application/json",
                    "Authorization:key=AAAAybt19gw:APA91bFX8GtkI0MBnuOcq7dWbVtTZL6unO1mKOV34du2fOBo_Xw-e12y2BGblJLOGop9n8jrfUUdq3Cvh-EYyJd8kVF6c8sPrgoEEeaF7EqNfViVGyrLTd1btBKVpDbF-nFzydn_7neh"
            }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
