package com.example.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("MMMM d, EEEE", Locale.getDefault())
    private val standardDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val shortMonthFormat = SimpleDateFormat("MMM", Locale.getDefault())

    fun todayIso(): String {
        return isoDateFormat.format(Date())
    }

    fun tomorrowIso(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return isoDateFormat.format(cal.time)
    }

    fun formatHeaderGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> "Good morning,"
            in 12..16 -> "Good afternoon,"
            else -> "Good evening,"
        }
    }

    fun formatTodaySubtitle(isoDate: String = todayIso()): String {
        return try {
            val date = isoDateFormat.parse(isoDate) ?: Date()
            fullDateFormat.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }

    fun formatStandardDate(isoDate: String): String {
        return try {
            val date = isoDateFormat.parse(isoDate) ?: return isoDate
            standardDateFormat.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }

    fun formatMonthYear(cal: Calendar): String {
        return monthYearFormat.format(cal.time)
    }

    fun formatMonthYear(isoDate: String): String {
        return try {
            val date = isoDateFormat.parse(isoDate) ?: Date()
            monthYearFormat.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }

    fun getMonthShort(isoDate: String): String {
        return try {
            val date = isoDateFormat.parse(isoDate) ?: return "DAY"
            shortMonthFormat.format(date).uppercase(Locale.getDefault())
        } catch (_: Exception) {
            "DAY"
        }
    }

    fun getDayOfMonth(isoDate: String): String {
        return try {
            val parts = isoDate.split("-")
            parts[2].toInt().toString()
        } catch (_: Exception) {
            "1"
        }
    }

    fun formatRelativeOrTime(dueDate: String, dueTime: String?): String {
        val today = todayIso()
        val tomorrow = tomorrowIso()

        return when (dueDate) {
            today -> formatTime12h(dueTime) ?: "Today"
            tomorrow -> "Tomorrow"
            else -> {
                formatTime12h(dueTime) ?: formatShortDate(dueDate)
            }
        }
    }

    fun formatShortDate(isoDate: String): String {
        return try {
            val date = isoDateFormat.parse(isoDate) ?: return isoDate
            val format = SimpleDateFormat("MMM d", Locale.getDefault())
            format.format(date)
        } catch (_: Exception) {
            isoDate
        }
    }

    fun formatTime12h(timeStr: String?): String? {
        if (timeStr.isNullOrBlank()) return null
        return try {
            val parts = timeStr.split(":")
            var hour = parts[0].toInt()
            val min = parts[1].toInt()
            val amPm = if (hour >= 12) "PM" else "AM"
            if (hour == 0) {
                hour = 12
            } else if (hour > 12) {
                hour -= 12
            }
            String.format(Locale.getDefault(), "%d:%02d %s", hour, min, amPm)
        } catch (_: Exception) {
            timeStr
        }
    }

    fun parseIsoCalendar(isoDate: String): Calendar {
        val cal = Calendar.getInstance()
        try {
            val date = isoDateFormat.parse(isoDate)
            if (date != null) {
                cal.time = date
            }
        } catch (_: Exception) {
        }
        return cal
    }

    data class DayItem(
        val dayOfWeekLabel: String, // "SUN", "MON"
        val dayNumber: Int,
        val isoDate: String,
        val isToday: Boolean
    )

    fun getDaysForWeek(centerDate: String, startOfWeekSunday: Boolean = true): List<DayItem> {
        val cal = parseIsoCalendar(centerDate)
        // Find start of week
        val firstDay = if (startOfWeekSunday) Calendar.SUNDAY else Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, firstDay)

        val days = mutableListOf<DayItem>()
        val today = todayIso()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        for (i in 0 until 7) {
            val currentIso = isoDateFormat.format(cal.time)
            val label = dayFormat.format(cal.time).uppercase(Locale.getDefault())
            val number = cal.get(Calendar.DAY_OF_MONTH)
            days.add(
                DayItem(
                    dayOfWeekLabel = label,
                    dayNumber = number,
                    isoDate = currentIso,
                    isToday = currentIso == today
                )
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
}
