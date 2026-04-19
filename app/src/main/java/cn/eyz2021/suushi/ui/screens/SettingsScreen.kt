package cn.eyz2021.suushi.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import cn.eyz2021.suushi.R
import cn.eyz2021.suushi.util.SettingsHelper
import cn.eyz2021.suushi.util.t

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onThemeChange: () -> Unit, onLanguageChange: () -> Unit, onUiScaleChange: () -> Unit) {
    val context = LocalContext.current
    val settingsHelper = remember { SettingsHelper(context) }
    var themeMode by remember { mutableIntStateOf(settingsHelper.getThemeMode()) }
    var currentLang by remember { mutableStateOf(settingsHelper.getLanguage()) }
    var uiScale by remember { mutableFloatStateOf(settingsHelper.getUiScale()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(t("settings_title")) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 主题设置
            Text(
                text = t("theme_title"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val themes = listOf(
                        0 to t("theme_system"),
                        1 to t("theme_light"),
                        2 to t("theme_dark")
                    )
                    themes.forEach { (mode, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    themeMode = mode
                                    settingsHelper.saveThemeMode(mode)
                                    onThemeChange()
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = themeMode == mode, onClick = null)
                            Text(label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 语言设置
            Text(
                text = t("lang_title"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    val langs = listOf(
                        "zh-CN" to "简体中文",
                        "zh-TW" to "繁體中文",
                        "en" to "English"
                    )
                    langs.forEach { (code, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentLang = code
                                    settingsHelper.saveLanguage(code)
                                    onLanguageChange()
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = currentLang == code, onClick = null)
                            Text(label, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // UI 缩放设置
            Text(
                text = t("ui_scale_title"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(t("ui_scale_label"))
                        Text("${"%.1f".format(uiScale)}x", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = uiScale,
                        onValueChange = { 
                            uiScale = it
                        },
                        onValueChangeFinished = {
                            settingsHelper.saveUiScale(uiScale)
                            onUiScaleChange()
                        },
                        valueRange = 0.8f..1.2f,
                        steps = 3 // 0.8, 0.9, 1.0, 1.1, 1.2
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 底部信息
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    // 渲染应用图标
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "App Icon",
                            modifier = Modifier.size(48.dp) // Foreground 图标通常有内边距，所以这里稍微放大一点
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "数詞 | Suushi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = t("about_desc"),
                    fontSize = 14.sp
                )
                Text(
                    text = "${t("version")}: 1.0.0",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW,
                        "https://github.com/eyz2021/Suushi".toUri())
                    context.startActivity(intent)
                }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "GitHub",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "© 2026 eyz2021",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
