package cn.eyz2021.suushi.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class AudioHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(path: String?) {
        if (path == null) return

        try {
            // 支持从 assets/audio/ 目录下读取带文件夹结构的 mp3 文件
            val assetPath = "audio/$path.mp3"
            val afd = context.assets.openFd(assetPath)
            
            mediaPlayer?.stop()
            mediaPlayer?.release()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }
            afd.close()
        } catch (e: Exception) {
            Log.e("AudioHelper", "无法播放音频: $path (请检查 assets/audio/ 下是否存在该文件)", e)
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
