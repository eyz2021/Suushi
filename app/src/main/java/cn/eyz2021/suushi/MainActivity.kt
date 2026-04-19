package cn.eyz2021.suushi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import cn.eyz2021.suushi.ui.screens.MainScreen
import cn.eyz2021.suushi.ui.theme.数詞Theme
import cn.eyz2021.suushi.util.LocalTranslation
import cn.eyz2021.suushi.util.SettingsHelper
import cn.eyz2021.suushi.util.TranslationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settingsHelper = remember { SettingsHelper(context) }
            val translationHelper = remember { TranslationHelper(context) }
            
            var themeTick by remember { mutableIntStateOf(0) }
            var langTick by remember { mutableIntStateOf(0) }
            var uiScaleTick by remember { mutableIntStateOf(0) }
            
            // 加载语言
            LaunchedEffect(langTick) {
                translationHelper.loadLanguage(settingsHelper.getLanguage())
            }

            val themeMode = remember(themeTick) { settingsHelper.getThemeMode() }
            val uiScale = remember(uiScaleTick) { settingsHelper.getUiScale() }
            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            // 提供全局翻译支持
            CompositionLocalProvider(
                LocalTranslation provides translationHelper,
                androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(
                    density = androidx.compose.ui.platform.LocalDensity.current.density * uiScale,
                    fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale * uiScale
                )
            ) {
                // 当语言加载完成后才渲染 UI
                if (langTick >= 0) { 
                    数詞Theme(darkTheme = darkTheme) {
                        MainScreen(
                            onThemeChange = { themeTick++ },
                            onLanguageChange = { langTick++ },
                            onUiScaleChange = { uiScaleTick++ }
                        )
                    }
                }
            }
        }
    }
}
