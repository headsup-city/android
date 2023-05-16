package com.krish.headsup.repositories

import com.krish.headsup.model.PushTokenSubscriptionRequest
import com.krish.headsup.model.UpdateAvatarResponse
import com.krish.headsup.model.UpdatePasswordRequest
import com.krish.headsup.model.UpdateUserRequest
import com.krish.headsup.model.User
import com.krish.headsup.model.UserSearchResponse
import com.krish.headsup.paging.SearchPagingSource
import com.krish.headsup.services.api.UserApi
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.UserResult
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApi: UserApi) {

    private var searchPagingSource: SearchPagingSource? = null

    suspend fun getSelf(token: String): Response<User> {
        return userApi.getSelf(token)
    }

    suspend fun getUser(token: String, userId: String): Result {
        return try {
            val response = userApi.getUser("Bearer $token", userId)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to fetch user data"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun changePassword(token: String, id: String, body: UpdatePasswordRequest): Response<User> {
        return userApi.changePassword(token, id, body)
    }

    suspend fun updateAvatar(token: String, id: String, body: MultipartBody.Part): Response<UpdateAvatarResponse> {
        return userApi.updateAvatar(token, id, body)
    }

    suspend fun updateUser(token: String, id: String, body: UpdateUserRequest): Response<User> {
        return userApi.updateUser(token, id, body)
    }

    suspend fun subscribePushToken(token: String, body: PushTokenSubscriptionRequest): Response<User> {
        return userApi.subscribePushToken(token, body)
    }

    suspend fun deleteUser(token: String): Response<User> {
        return userApi.deleteUser(token)
    }

    suspend fun blockUser(token: String, toBlock: String): Response<User> {
        return userApi.blockUser(token, toBlock)
    }

    suspend fun unblockUser(token: String, toUnblock: String): Response<User> {
        return userApi.unblockUser(token, toUnblock)
    }

    suspend fun searchUser(accessToken: String, query: String): Response<UserSearchResponse> {
        return userApi.searchUser(accessToken, query, 0)
    }
}
