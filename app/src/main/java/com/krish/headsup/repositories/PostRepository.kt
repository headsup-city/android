package com.krish.headsup.repositories

import android.util.Log
import com.krish.headsup.model.Post
import com.krish.headsup.services.api.PostApi
import com.krish.headsup.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApi: PostApi) {

    suspend fun getGeneralPost(accessToken: String): Resource<List<Post>> {
        return withContext(Dispatchers.IO) {
            Log.d("PostRepository15", "Starting api call")
            try {
                val response = postApi.getGeneralPost(accessToken, 0, 22.1, 28.23)
                Log.d("PostRepository18", "Got response")
                if (response.isSuccessful) {
                    Log.d("PostRepository20", "Api Success")
                    response.body()?.results?.let { posts ->
                        return@withContext Resource.success(posts)
                    } ?: return@withContext Resource.error("An error occurred")
                } else {
                    Log.d("PostRepository25", "Api Error")
                    return@withContext Resource.error("An error occurred")
                }
            } catch (e: Exception) {
                Log.d("PostRepository29", "${e.message}")
                return@withContext Resource.error("An error occurred: ${e.message}")
            }
        }
    }
}
