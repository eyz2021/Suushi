package cn.eyz2021.suushi.util

import android.content.Context
import android.content.SharedPreferences

class SettingsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("suushi_settings", Context.MODE_PRIVATE)

    fun saveSelectedCategories(categories: Set<String>) {
        prefs.edit().putStringSet("selected_categories", categories).apply()
    }

    fun getSelectedCategories(default: Set<String>): Set<String> {
        return prefs.getStringSet("selected_categories", default) ?: default
    }

    fun saveAutoPlayAudio(enabled: Boolean) {
        prefs.edit().putBoolean("auto_play_audio", enabled).apply()
    }

    fun isAutoPlayAudioEnabled(): Boolean {
        return prefs.getBoolean("auto_play_audio", true)
    }

    fun saveThemeMode(mode: Int) {
        prefs.edit().putInt("theme_mode", mode).apply()
    }

    fun getThemeMode(): Int {
        return prefs.getInt("theme_mode", 0) // 0: System, 1: Light, 2: Dark
    }

    fun saveLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
    }

    fun getLanguage(): String {
        return prefs.getString("language", "zh-CN") ?: "zh-CN"
    }
}
