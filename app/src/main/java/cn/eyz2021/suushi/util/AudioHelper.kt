package cn.eyz2021.suushi.util

import android.content.Context
import android.media.MediaPlayer

class AudioHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(audioResName: String?) {
        if (audioResName == null) return

        val resId = context.resources.getIdentifier(audioResName, "raw", context.packageName)
        if (resId != 0) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
