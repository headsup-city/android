package com.krish.headsup.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import com.krish.headsup.model.Post

@Database(entities = [Post::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}