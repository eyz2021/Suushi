package cn.eyz2021.suushi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.eyz2021.suushi.model.sampleData
import cn.eyz2021.suushi.util.AudioHelper
import cn.eyz2021.suushi.util.LocalTranslation
import cn.eyz2021.suushi.util.SettingsHelper
import cn.eyz2021.suushi.util.t
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen() {
    val context = LocalContext.current
    val settingsHelper = remember { SettingsHelper(context) }
    val audioHelper = remember { AudioHelper(context) }
    val translationHelper = LocalTranslation.current
    
    // 释放音频资源
    DisposableEffect(Unit) {
        onDispose {
            audioHelper.release()
        }
    }
    
    // 记忆用户选择的分类范围
    var selectedCategories by remember { 
        mutableStateOf(settingsHelper.getSelectedCategories(sampleData.map { it.title }.toSet())) 
    }
    
    // 自动播放音频设置
    var autoPlayAudio by remember { mutableStateOf(settingsHelper.isAutoPlayAudioEnabled()) }
    
    var showSettings by remember { mutableStateOf(false) }

    // 保存设置
    LaunchedEffect(selectedCategories, autoPlayAudio) {
        settingsHelper.saveSelectedCategories(selectedCategories)
        settingsHelper.saveAutoPlayAudio(autoPlayAudio)
    }

    // 根据选择的范围过滤出所有题目
    val quizPool = remember(selectedCategories) {
        sampleData
            .filter { it.title in selectedCategories }
            .flatMap { group ->
                group.items.map { item -> item to group.title }
            }
            .filter { it.first.reading.isNotEmpty() }
    }

    // 状态管理
    var currentPair by remember { mutableStateOf<Pair<cn.eyz2021.suushi.model.CounterItem, String>?>(null) }
    var userInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf(false) }
    
    // 历史记录队列，用于防止短期内重复出题
    val historyQueue = remember { mutableStateListOf<Pair<cn.eyz2021.suushi.model.CounterItem, String>>() }

    // 抽题逻辑
    fun pickNextQuestion() {
        if (quizPool.isEmpty()) return
        
        val coolingSize = ceil(quizPool.size * 0.4).toInt()
        val availablePool = quizPool.filter { item ->
            historyQueue.none { it.first == item.first && it.second == item.second }
        }.ifEmpty { quizPool }
        
        val next = availablePool.random()
        
        historyQueue.add(next)
        if (historyQueue.size > coolingSize) {
            historyQueue.removeAt(0)
        }
        
        currentPair = next
        userInput = ""
        feedback = null
    }

    // 题库监控与自动刷新
    LaunchedEffect(quizPool) {
        if (quizPool.isNotEmpty()) {
            if (currentPair == null || quizPool.none { it.first == currentPair?.first && it.second == currentPair?.second }) {
                historyQueue.clear()
                pickNextQuestion()
            }
        } else {
            currentPair = null
            historyQueue.clear()
        }
    }

    fun nextQuestion() {
        pickNextQuestion()
    }

    // 修复：不能在普通函数里直接调用 @Composable 的 t() 函数
    // 应该使用 translationHelper.translate()
    fun checkAnswer() {
        if (userInput.isBlank() || currentPair == null) return
        
        val input = userInput.trim()
        val item = currentPair!!.first
        val category = currentPair!!.second
        
        val possibleAnswers = when {
            category == "人" && item.number == "7" -> listOf("しちにん", "ななにん")
            category == "人" && item.number == "17" -> listOf("じゅうしちにん", "じゅうななにん")
            item.reading.contains("/") -> item.reading.split("/")
            else -> listOf(item.reading)
        }
        
        val isAnswerCorrect = input in possibleAnswers
        
        if (isAnswerCorrect) {
            feedback = translationHelper.translate("quiz_correct")
            isCorrect = true
        } else {
            feedback = "${translationHelper.translate("quiz_error")}: ${possibleAnswers.joinToString(" 或 ")}"
            isCorrect = false
        }

        if (autoPlayAudio) {
            audioHelper.playAudio(item.audioResName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t("tab_quiz")) },
                actions = {
                    Box {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Default.Settings, contentDescription = t("tab_settings"))
                        }
                        DropdownMenu(
                            expanded = showSettings,
                            onDismissRequest = { showSettings = false }
                        ) {
                            Text(
                                t("quiz_settings_general"),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = autoPlayAudio, onCheckedChange = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(t("quiz_auto_play"))
                                    }
                                },
                                onClick = { autoPlayAudio = !autoPlayAudio }
                            )
                            HorizontalDivider()
                            Text(
                                t("quiz_settings_range"),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            sampleData.forEach { group ->
                                val isSelected = group.title in selectedCategories
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = isSelected, onCheckedChange = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(group.title)
                                        }
                                    },
                                    onClick = {
                                        val newSelection = selectedCategories.toMutableSet()
                                        if (isSelected) {
                                            if (newSelection.size > 1) newSelection.remove(group.title)
                                        } else {
                                            newSelection.add(group.title)
                                        }
                                        selectedCategories = newSelection
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (quizPool.isEmpty()) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(t("quiz_empty_pool"))
            } else if (currentPair != null) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = t("quiz_prompt"),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "${currentPair!!.first.number}${currentPair!!.second}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text(t("quiz_placeholder")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = feedback == null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (feedback == null) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { checkAnswer() },
                        onNext = { if (feedback != null) nextQuestion() }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (feedback == null) {
                    Button(
                        onClick = { checkAnswer() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = userInput.isNotBlank()
                    ) {
                        Text(t("quiz_check"))
                    }
                } else {
                    Text(
                        text = feedback!!,
                        color = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { nextQuestion() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t("quiz_next"))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}
