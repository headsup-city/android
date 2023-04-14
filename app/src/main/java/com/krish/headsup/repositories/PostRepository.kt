package com.krish.headsup.repositories

import com.krish.headsup.model.Post
import com.krish.headsup.services.api.PostApi
import com.krish.headsup.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApi: PostApi) {

    suspend fun getGeneralPost(): Resource<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = postApi.getGeneralPost("a", 0, 22.1, 28.23)
                if (response.isSuccessful) {
                    response.body()?.results?.let { posts ->
                        return@withContext Resource.success(posts)
                    } ?: return@withContext Resource.error("An error occurred")
                } else {
                    return@withContext Resource.error("An error occurred")
                }
            } catch (e: Exception) {
                return@withContext Resource.error("An error occurred: ${e.message}")
            }
        }
    }
}
