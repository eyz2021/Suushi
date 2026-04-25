package cn.eyz2021.suushi.util

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.*

class TtsHelper(private val context: Context, private val onStatusChange: (Boolean) -> Unit) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    
    var isReady by mutableStateOf(false)
        private set

    // 新增：当前生效的引擎名称状态
    var currentEngineName by mutableStateOf("")
        private set

    init {
        initialize()
    }

    private fun initialize(engine: String? = null) {
        isReady = false
        onStatusChange(false)
        tts?.stop()
        tts?.shutdown()
        tts = if (engine != null) {
            TextToSpeech(context, this, engine)
        } else {
            TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 初始化成功后同步真实的引擎名称
            currentEngineName = tts?.defaultEngine ?: ""
            isReady = true
            onStatusChange(true)
        } else {
            isReady = false
            onStatusChange(false)
        }
    }

    fun getInstalledEngines(): List<TextToSpeech.EngineInfo> {
        val tempTts = TextToSpeech(context, null)
        val engines = tempTts.engines
        tempTts.shutdown()
        return engines
    }

    fun switchEngine(engineName: String) {
        if (engineName == currentEngineName) return
        currentEngineName = engineName // 立即更新 UI 显示，避免回弹感
        initialize(engineName)
    }

    fun speak(text: String, locale: Locale) {
        if (!isReady) return
        tts?.apply {
            language = locale
            speak(text, TextToSpeech.QUEUE_FLUSH, null, "lab_test_${System.currentTimeMillis()}")
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
