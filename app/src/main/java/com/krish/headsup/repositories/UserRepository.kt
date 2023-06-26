package com.krish.headsup.repositories

import com.krish.headsup.model.PushTokenSubscriptionRequest
import com.krish.headsup.model.UpdatePasswordRequest
import com.krish.headsup.model.UpdateUserRequest
import com.krish.headsup.services.api.UserApi
import com.krish.headsup.utils.AvatarResult
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.UnitResult
import com.krish.headsup.utils.UserResult
import com.krish.headsup.utils.UserSearchResult
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApi: UserApi) {
    suspend fun getSelf(token: String): Result {
        return try {
            val response = userApi.getSelf(token)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to fetch self data"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
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

    suspend fun changePassword(token: String, id: String, body: UpdatePasswordRequest): Result {
        return try {
            val response = userApi.changePassword(token, id, body)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to change password"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateAvatar(token: String, id: String, body: MultipartBody.Part): Result {
        return try {
            val response = userApi.updateAvatar(token, id, body)
            if (response.isSuccessful && response.body() != null) {
                AvatarResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update avatar"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun updateUser(token: String, id: String, body: UpdateUserRequest): Result {
        return try {
            val response = userApi.updateUser(token, id, body)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to update user"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun subscribePushToken(token: String, body: PushTokenSubscriptionRequest): Result {
        return try {
            val response = userApi.subscribePushToken(token, body)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to subscribe push token"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteUser(token: String): Result {
        return try {
            val response = userApi.deleteUser(token)
            if (response.isSuccessful && response.body() != null) {
                UnitResult(data = Unit)
            } else {
                Result.Error(Exception("Failed to delete user"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun blockUser(token: String, toBlock: String): Result {
        return try {
            val response = userApi.blockUser(token, toBlock)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to block user"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun unblockUser(token: String, toUnblock: String): Result {
        return try {
            val response = userApi.unblockUser(token, toUnblock)
            if (response.isSuccessful && response.body() != null) {
                UserResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to unblock user"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun searchUser(accessToken: String, query: String): Result {
        return try {
            val response = userApi.searchUser(accessToken, query, 0)
            if (response.isSuccessful && response.body() != null) {
                UserSearchResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to search user"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
