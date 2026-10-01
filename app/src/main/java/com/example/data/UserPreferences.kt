package com.example.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("todo_reminder_prefs", Context.MODE_PRIVATE)

    var userName: String
        get() = prefs.getString("user_name", "Alex") ?: "Alex"
        set(value) = prefs.edit().putString("user_name", value).apply()

    var defaultReminderMinutes: Int
        get() = prefs.getInt("default_reminder_minutes", 15)
        set(value) = prefs.edit().putInt("default_reminder_minutes", value).apply()

    var startOfWeek: String
        get() = prefs.getString("start_of_week", "SUNDAY") ?: "SUNDAY"
        set(value) = prefs.edit().putString("start_of_week", value).apply()

    var soundAndVibrate: Boolean
        get() = prefs.getBoolean("sound_and_vibrate", true)
        set(value) = prefs.edit().putBoolean("sound_and_vibrate", value).apply()

    var categories: Set<String>
        get() {
            val saved = prefs.getStringSet("categories", null)
            val filtered = saved?.filter {
                !it.equals("Web Redesign", ignoreCase = true)
            }?.toSet() ?: setOf("Work", "Personal", "Shopping")
            val result = if (filtered.isEmpty()) setOf("Work", "Personal", "Shopping") else filtered
            // Ensure Web Redesign is purged from storage if it was there
            if (saved?.any { it.equals("Web Redesign", ignoreCase = true) } == true) {
                prefs.edit().putStringSet("categories", result).apply()
            }
            return result
        }
        set(value) {
            val sanitized = value.filter { !it.equals("Web Redesign", ignoreCase = true) }.toSet()
            prefs.edit().putStringSet("categories", sanitized).apply()
        }

    fun addCategory(category: String) {
        val current = categories.toMutableSet()
        current.add(category)
        categories = current
    }

    fun removeCategory(category: String) {
        val current = categories.toMutableSet()
        current.remove(category)
        categories = current
    }
}
