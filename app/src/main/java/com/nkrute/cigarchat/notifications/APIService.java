package com.nkrute.cigarchat.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAOM1g9jA:APA91bF0ONeWnP0F7fQTGNLvgQm6raoRZk7B5_0VcutCefqh9tckfgw2I5z2V7F2M9vHYOlR9LmuQXyNR9oyYq13UggFS2HzGwsjoGdtunkKY6E6RSHDuTDpX-XGyzk2cvBrE8F_ouki"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
