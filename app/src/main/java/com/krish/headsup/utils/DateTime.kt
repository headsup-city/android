package com.krish.headsup.utils

import android.content.Context
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun getRelativeTime(dateIso: String?, context: Context): String {
    if (dateIso == null) {
        return ""
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val date = sdf.parse(dateIso)
        val now = System.currentTimeMillis()
        if (date != null) {
            DateUtils.getRelativeTimeSpanString(date.time, now, DateUtils.MINUTE_IN_MILLIS)
                .toString()
        } else {
            ""
        }
    } catch (e: ParseException) {
        ""
    }
}
