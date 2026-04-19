package cn.eyz2021.suushi.util

import android.content.Context
import androidx.compose.runtime.*
import org.json.JSONObject

class TranslationHelper(private val context: Context) {
    // 使用 mutableStateOf 包装 translations 对象，确保其变化能触发 Compose 重绘
    var translations by mutableStateOf(JSONObject())
        private set

    private var currentLang = "zh-CN"

    fun loadLanguage(lang: String) {
        currentLang = lang
        try {
            val jsonString = context.assets.open("locales/$lang.json").bufferedReader().use { it.readText() }
            // 替换整个 JSONObject 对象，触发 Compose 状态更新
            translations = JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            if (lang != "en") loadLanguage("en")
        }
    }

    fun translate(key: String): String {
        return translations.optString(key, key)
    }
}

val LocalTranslation = compositionLocalOf<TranslationHelper> {
    error("No TranslationHelper provided")
}

@Composable
@ReadOnlyComposable
fun t(key: String): String {
    return LocalTranslation.current.translate(key)
}
