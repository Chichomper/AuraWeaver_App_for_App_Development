package com.example.auraweaver

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun savePreferences(speed: Int, intensity: Int, particles: Int, sound: Boolean) {
        prefs.edit().apply {
            putInt(KEY_SPEED, speed)
            putInt(KEY_INTENSITY, intensity)
            putInt(KEY_PARTICLES, particles)
            apply()
        }
    }

    fun getSpeed(): Int = prefs.getInt(KEY_SPEED, 50)
    fun getIntensity(): Int = prefs.getInt(KEY_INTENSITY, 70)
    fun getParticles(): Int = prefs.getInt(KEY_PARTICLES, 50)

    fun addVibeToHistory(vibeName: String) {
        val history = getVibeHistory().toMutableList()
        history.add(0, "$vibeName - ${java.text.DateFormat.getDateTimeInstance().format(java.util.Date())}")
        if (history.size > 20) {
            history.subList(20, history.size).clear()
        }
        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    fun getVibeHistory(): List<String> {
        val json = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        return gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    companion object {
        private const val PREFS_NAME = "AuraPrefs"
        private const val KEY_SPEED = "animation_speed"
        private const val KEY_INTENSITY = "color_intensity"
        private const val KEY_PARTICLES = "particle_count"
        private const val KEY_HISTORY = "vibe_history"
    }
}
