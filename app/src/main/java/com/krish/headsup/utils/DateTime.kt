package com.krish.headsup.utils

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val zonedDateTime = ZonedDateTime.parse(this, formatter)
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
}

fun getRelativeTime(dateIso: String?): String {
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

fun getRelativeTimeForChat(dateIso: String?): String {
    if (dateIso == null) {
        return ""
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    return try {
        val date = sdf.parse(dateIso)
        val calendar: Calendar = Calendar.getInstance().apply {
            time = date
        }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        when {
            isSameDay(calendar, today) -> "Today"
            isSameDay(calendar, yesterday) -> "Yesterday"
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> SimpleDateFormat("d MMM", Locale.getDefault()).format(date)
            else -> SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(date)
        }
    } catch (e: ParseException) {
        ""
    }
}

fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
        calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}
