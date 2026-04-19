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

    fun saveUiScale(scale: Float) {
        prefs.edit().putFloat("ui_scale", scale).apply()
    }

    fun getUiScale(): Float {
        return prefs.getFloat("ui_scale", 1.0f)
    }

    fun saveSelectedQuizTypes(types: Set<String>) {
        prefs.edit().putStringSet("selected_quiz_types", types).apply()
    }

    fun getSelectedQuizTypes(): Set<String> {
        return prefs.getStringSet("selected_quiz_types", setOf("SPELLING", "LISTENING")) ?: setOf("SPELLING", "LISTENING")
    }

    fun saveTableColumnCount(count: Int) {
        prefs.edit().putInt("table_column_count", count).apply()
    }

    fun getTableColumnCount(): Int {
        return prefs.getInt("table_column_count", 2)
    }
}
