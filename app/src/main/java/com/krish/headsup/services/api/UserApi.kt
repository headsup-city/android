package com.krish.headsup.services.api

import com.krish.headsup.model.PushTokenSubscriptionRequest
import com.krish.headsup.model.UpdateAvatarResponse
import com.krish.headsup.model.UpdatePasswordRequest
import com.krish.headsup.model.UpdateUserRequest
import com.krish.headsup.model.User
import com.krish.headsup.model.UserSearchResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/self")
    suspend fun getSelf(@Header("Authorization") token: String): Response<User>

    @GET("users/{id}")
    suspend fun getUser(@Header("Authorization") token: String, @Path("id") id: String): Response<User>

    @POST("users/{id}/changePassword")
    suspend fun changePassword(@Header("Authorization") token: String, @Path("id") id: String, @Body body: UpdatePasswordRequest): Response<User>

    @Multipart
    @POST("users/{id}/changeAvatar")
    suspend fun updateAvatar(@Header("Authorization") token: String, @Path("id") id: String, @Part body: MultipartBody.Part): Response<UpdateAvatarResponse>

    @POST("users/{id}/updateUser")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: String, @Body body: UpdateUserRequest): Response<User>

    @POST("users/subscribePushToken")
    suspend fun subscribePushToken(@Header("Authorization") token: String, @Body body: PushTokenSubscriptionRequest): Response<User>

    @POST("users/deleteSelf")
    suspend fun deleteUser(@Header("Authorization") token: String): Response<User>

    @POST("users/{toBlock}/block")
    suspend fun blockUser(@Header("Authorization") token: String, @Path("toBlock") toBlock: String): Response<User>

    @POST("users/{toUnblock}/unblock")
    suspend fun unblockUser(@Header("Authorization") token: String, @Path("toUnblock") toUnblock: String): Response<User>

    @GET("users/search")
    suspend fun searchUser(@Header("Authorization") token: String, @Query("query") query: String, @Query("page") page: Int? = 0): Response<UserSearchResponse>
}
