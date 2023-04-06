package com.krish.headsup.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.krish.headsup.model.ApiImage
import com.krish.headsup.model.User
import com.krish.headsup.model.Event
import com.krish.headsup.model.Location

class Converter {
    private val gson = Gson()

    @TypeConverter
    fun fromApiImage(apiImage: ApiImage?): String? = apiImage?.let { gson.toJson(it) }

    @TypeConverter
    fun toApiImage(apiImageString: String?): ApiImage? = apiImageString?.let {
        gson.fromJson(it, object : TypeToken<ApiImage>() {}.type)
    }

    @TypeConverter
    fun fromUser(user: User?): String? = user?.let { gson.toJson(it) }

    @TypeConverter
    fun toUser(userString: String?): User? = userString?.let {
        gson.fromJson(it, object : TypeToken<User>() {}.type)
    }

    @TypeConverter
    fun fromEvent(event: Event?): String? = event?.let { gson.toJson(it) }

    @TypeConverter
    fun toEvent(eventString: String?): Event? = eventString?.let {
        gson.fromJson(it, object : TypeToken<Event>() {}.type)
    }

    @TypeConverter
    fun fromLocation(location: Location?): String? = location?.let { gson.toJson(it) }

    @TypeConverter
    fun toLocation(locationString: String?): Location? = locationString?.let {
        gson.fromJson(it, object : TypeToken<Location>() {}.type)
    }
}