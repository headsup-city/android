package com.krish.headsup.utils

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import okhttp3.OkHttpClient
import java.io.File

object ExoPlayerUtil {

    private const val CACHE_SIZE: Long = 100 * 1024 * 1024 // 100 MB

    private fun createCache(context: Context): Cache {
        val cacheDirectory = File(context.cacheDir, "videoCache")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
        val databaseProvider = ExoDatabaseProvider(context)
        return SimpleCache(cacheDirectory, cacheEvictor, databaseProvider)
    }

    private fun createDataSourceFactory(context: Context, cache: Cache): DataSource.Factory {
        val okHttpClient = OkHttpClient.Builder().build()
        val okHttpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient).setUserAgent("ExoPlayer")
        val defaultDataSourceFactory = DefaultDataSourceFactory(context, okHttpDataSourceFactory)

        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(defaultDataSourceFactory)
            .setCacheKeyFactory(CustomCacheKeyFactory())
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private class CustomCacheKeyFactory : CacheKeyFactory {
        override fun buildCacheKey(dataSpec: DataSpec): String {
            return dataSpec.uri.toString().substringBefore("?")
        }
    }

    fun createExoPlayer(context: Context): ExoPlayer {
        val cache = CacheSingleton.getInstance(context)
        val dataSourceFactory = createDataSourceFactory(context, cache)
        val mediaSourceFactory: MediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    private object CacheSingleton : SingletonHolder<Cache, Context>(::createCache)
}

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val synchronizedInstance = instance
            if (synchronizedInstance != null) {
                synchronizedInstance
            } else {
                val createdInstance = creator!!(arg)
                instance = createdInstance
                creator = null
                createdInstance
            }
        }
    }
}
