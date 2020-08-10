package com.something.chatapp.services

import com.something.chatapp.dataclass.Constants.Companion.CONTENT_TYPE
import com.something.chatapp.dataclass.Constants.Companion.SERVER_KEY
import com.something.chatapp.dataclass.PushNotification
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotifications(
        @Body notification : PushNotification
    ): Response<ResponseBody>
}