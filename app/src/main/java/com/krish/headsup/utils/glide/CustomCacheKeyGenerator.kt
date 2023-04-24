package com.krish.headsup.utils.glide

import com.bumptech.glide.load.Key
import java.nio.ByteBuffer
import java.security.MessageDigest

class CustomCacheKeyGenerator(private val url: String) : Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val cacheKey = generateCacheKeyFromUrl(url)
        val cacheKeyBytes = ByteBuffer.allocate(4).putInt(cacheKey.length).array()
        messageDigest.update(cacheKeyBytes)
        messageDigest.update(cacheKey.toByteArray(Key.CHARSET))
    }

    private fun generateCacheKeyFromUrl(url: String): String {
        val urlWithoutQueryParams = url.substringBefore("?")
        return urlWithoutQueryParams
    }

    override fun equals(other: Any?): Boolean {
        if (other is CustomCacheKeyGenerator) {
            return url == other.url
        }
        return false
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}
