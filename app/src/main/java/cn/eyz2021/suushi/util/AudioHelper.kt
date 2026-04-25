package cn.eyz2021.suushi.util

import android.content.Context
import android.media.MediaPlayer
import java.util.Locale

class AudioHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val settingsHelper = SettingsHelper(context)
    private var ttsHelper: TtsHelper? = null

    private fun getTtsHelper(): TtsHelper {
        val helper = ttsHelper ?: TtsHelper(context) { _ -> }
        ttsHelper = helper
        
        val targetEngine = settingsHelper.getTtsEngine()
        if (targetEngine != null && helper.currentEngineName != targetEngine) {
            helper.switchEngine(targetEngine)
        }
        return helper
    }

    fun playAudio(audioResName: String?, textToSpeak: String? = null) {
        val currentSource = settingsHelper.getVoiceSource()

        // --- 模式 1: TTS 发音 ---
        if (currentSource == "TTS") {
            stopBuiltinAudio()
            if (!textToSpeak.isNullOrBlank()) {
                getTtsHelper().speak(textToSpeak, Locale.JAPANESE)
            }
            return
        }

        // --- 模式 2: 内置发音 (从 Assets 加载) ---
        if (audioResName.isNullOrBlank()) {
            if (!textToSpeak.isNullOrBlank()) {
                getTtsHelper().speak(textToSpeak, Locale.JAPANESE)
            }
            return 
        }

        // 拼接 Assets 路径: audio/ + month/m01 + .mp3
        val assetPath = "audio/${audioResName.removeSuffix(".mp3")}.mp3"
        
        try {
            // 检查文件是否存在并播放
            val afd = context.assets.openFd(assetPath)
            stopBuiltinAudio()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                start()
                setOnCompletionListener { 
                    it.release()
                    if (mediaPlayer == it) mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            // 如果 Assets 中找不到文件，降级到 TTS
            if (!textToSpeak.isNullOrBlank()) {
                getTtsHelper().speak(textToSpeak, Locale.JAPANESE)
            }
        }
    }

    private fun stopBuiltinAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {}
        mediaPlayer = null
    }

    fun release() {
        stopBuiltinAudio()
        ttsHelper?.release()
        ttsHelper = null
    }
}
