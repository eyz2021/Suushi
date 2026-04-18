package cn.eyz2021.suushi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.eyz2021.suushi.ui.screens.MainScreen
import cn.eyz2021.suushi.ui.theme.数詞Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            数詞Theme {
                MainScreen()
            }
        }
    }
}
