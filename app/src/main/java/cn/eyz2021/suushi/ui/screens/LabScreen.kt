package cn.eyz2021.suushi.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.eyz2021.suushi.util.TtsHelper
import cn.eyz2021.suushi.util.t
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabScreen() {
    val context = LocalContext.current
    
    // 引擎列表状态
    var engines by remember { mutableStateOf<List<TextToSpeech.EngineInfo>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // 初始化 TtsHelper
    val ttsHelper = remember { 
        TtsHelper(context) { _ -> }
    }

    // 仅在首次进入时获取已安装引擎列表
    LaunchedEffect(Unit) {
        engines = ttsHelper.getInstalledEngines()
    }

    var textToSpeak by remember { mutableStateOf("一、二、三、四、五、六、七、八、九、十") }

    DisposableEffect(Unit) {
        onDispose { ttsHelper.release() }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(t("tab_lab")) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TTS 引擎与发音测试",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 引擎切换区域
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "选择 TTS 引擎",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        OutlinedCard(
                            onClick = { isDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = ttsHelper.isReady
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 直接从 ttsHelper.currentEngineName 获取显示文字，实现单一事实来源
                                val currentLabel = engines.find { it.name == ttsHelper.currentEngineName }?.label 
                                    ?: if (ttsHelper.isReady) "正在确定引擎..." else "正在初始化..."
                                
                                Text(
                                    text = currentLabel,
                                    color = if (ttsHelper.isReady) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                                )
                                
                                if (!ttsHelper.isReady) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        }
                        
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            engines.forEach { engine ->
                                DropdownMenuItem(
                                    text = { Text(engine.label) },
                                    onClick = {
                                        // 调用 switchEngine，内部会立即更新 currentEngineName 避免回弹
                                        ttsHelper.switchEngine(engine.name)
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = textToSpeak,
                onValueChange = { textToSpeak = it },
                label = { Text("输入要朗读的文字") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 发音控制
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { ttsHelper.speak(textToSpeak, Locale.JAPANESE) },
                    modifier = Modifier.weight(1f),
                    enabled = ttsHelper.isReady
                ) {
                    Text("日语")
                }
                Button(
                    onClick = { ttsHelper.speak(textToSpeak, Locale.CHINESE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = ttsHelper.isReady
                ) {
                    Text("中文")
                }
                Button(
                    onClick = { ttsHelper.speak(textToSpeak, Locale.ENGLISH) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    enabled = ttsHelper.isReady
                ) {
                    Text("英语")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "提示：切换引擎后，系统需要重新加载语音包。单一事实来源（SSOT）已应用，解决了选择回弹问题。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
