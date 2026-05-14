package com.example.auraweaver;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    private static final String PREFS_NAME = "AuraPrefs";
    private static final String KEY_SPEED = "animation_speed";
    private static final String KEY_INTENSITY = "color_intensity";
    private static final String KEY_PARTICLES = "particle_count";
    private static final String KEY_HISTORY = "vibe_history";

    private final SharedPreferences prefs;
    private final Gson gson;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void savePreferences(int speed, int intensity, int particles, boolean sound) {
        prefs.edit()
            .putInt(KEY_SPEED, speed)
            .putInt(KEY_INTENSITY, intensity)
            .putInt(KEY_PARTICLES, particles)
            .apply();
    }

    public int getSpeed() { 
        return prefs.getInt(KEY_SPEED, 50); 
    }

    public int getIntensity() { 
        return prefs.getInt(KEY_INTENSITY, 70); 
    }

    public int getParticles() { 
        return prefs.getInt(KEY_PARTICLES, 50); 
    }

    public void addVibeToHistory(String vibeName) {
        List<String> history = getVibeHistory();
        history.add(0, vibeName + " - " + java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
        if (history.size() > 20) {
            history = history.subList(0, 20);
        }
        prefs.edit().putString(KEY_HISTORY, gson.toJson(history)).apply();
    }

    public List<String> getVibeHistory() {
        String json = prefs.getString(KEY_HISTORY, "[]");
        return gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
    }

    public void clearHistory() { 
        prefs.edit().remove(KEY_HISTORY).apply(); 
    }
}
