package cn.eyz2021.suushi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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

enum class QuizType {
    SPELLING, // 拼写题
    LISTENING // 听力选择题
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen() {
    val context = LocalContext.current
    val settingsHelper = remember { SettingsHelper(context) }
    val audioHelper = remember { AudioHelper(context) }
    val translationHelper = LocalTranslation.current

    DisposableEffect(Unit) {
        onDispose { audioHelper.release() }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: 基础量词, 1: 随机数字

    var selectedCategories by remember {
        mutableStateOf(settingsHelper.getSelectedCategories(sampleData.map { it.title }.toSet()))
    }
    var selectedQuizTypes by remember {
        mutableStateOf(settingsHelper.getSelectedQuizTypes())
    }
    var autoPlayAudio by remember { mutableStateOf(settingsHelper.isAutoPlayAudioEnabled()) }
    var showSettings by remember { mutableStateOf(false) }

    // 随机数字设置
    var randomRangeStart by remember { mutableStateOf(1) }
    var randomRangeEnd by remember { mutableStateOf(100) }
    var includeDecimals by remember { mutableStateOf(false) }
    var includeFractions by remember { mutableStateOf(false) }
    val enableRandomNumber = selectedTab == 1

    fun generateRandomItem(): cn.eyz2021.suushi.model.CounterItem {
        val type = mutableListOf("int")
        if (includeDecimals) type.add("decimal")
        if (includeFractions) type.add("fraction")
        
        return when (type.random()) {
            "decimal" -> {
                val integerPart = (randomRangeStart..randomRangeEnd).random()
                val decimalPart = (1..99).random()
                val text = "$integerPart.$decimalPart"
                cn.eyz2021.suushi.model.CounterItem(text, cn.eyz2021.suushi.util.NumberConverter.convert(text))
            }
            "fraction" -> {
                val denominator = (2..10).random()
                val numerator = (1 until denominator).random()
                val text = "$numerator/$denominator"
                cn.eyz2021.suushi.model.CounterItem(text, cn.eyz2021.suushi.util.NumberConverter.convert(text))
            }
            else -> {
                val rand = (randomRangeStart..randomRangeEnd).random()
                cn.eyz2021.suushi.model.CounterItem(rand.toString(), cn.eyz2021.suushi.util.NumberConverter.convert(rand.toString()))
            }
        }
    }

    LaunchedEffect(selectedCategories, autoPlayAudio, selectedQuizTypes) {
        settingsHelper.saveSelectedCategories(selectedCategories)
        settingsHelper.saveAutoPlayAudio(autoPlayAudio)
        settingsHelper.saveSelectedQuizTypes(selectedQuizTypes)
    }

    val quizPool = remember(selectedCategories, enableRandomNumber) {
        if (enableRandomNumber) {
            // 在随机数字 Tab，我们不需要从静态池中过滤数据，返回一个包含虚拟项的池以通过空检查
            listOf(cn.eyz2021.suushi.model.CounterItem("", "") to "随机模式")
        } else {
            sampleData
                .filter { it.title in selectedCategories }
                .flatMap { group ->
                    group.items.map { item -> item to group.title }
                }
                .filter { it.first.reading.isNotEmpty() }
        }
    }

    // 状态管理
    var currentPair by remember { mutableStateOf<Pair<cn.eyz2021.suushi.model.CounterItem, String>?>(null) }
    var currentQuizType by remember { mutableStateOf(QuizType.SPELLING) }
    var options by remember { mutableStateOf<List<cn.eyz2021.suushi.model.CounterItem>>(emptyList()) }

    var userInput by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf<cn.eyz2021.suushi.model.CounterItem?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var isCorrect by remember { mutableStateOf(false) }

    val historyQueue = remember { mutableStateListOf<Pair<cn.eyz2021.suushi.model.CounterItem, String>>() }

    fun checkResult(correct: Boolean, answerText: String) {
        if (correct) {
            feedback = translationHelper.translate("quiz_correct")
            isCorrect = true
        } else {
            feedback = "${translationHelper.translate("quiz_error")}: $answerText"
            isCorrect = false
        }
        if (autoPlayAudio && currentQuizType == QuizType.SPELLING) {
            // 拼写题作答后发音，支持 TTS
            audioHelper.playAudio(currentPair?.first?.audioResName, currentPair?.first?.reading)
        }
    }

    fun pickNextQuestion() {
        // 决定是否生成随机数字题目
        val shouldGenerateRandom = enableRandomNumber

        val next: Pair<cn.eyz2021.suushi.model.CounterItem, String>? = if (shouldGenerateRandom) {
            generateRandomItem() to "数字"
        } else {
            if (quizPool.isEmpty()) null else {
                val availablePool = quizPool.filter { item ->
                    historyQueue.none { it.first == item.first && it.second == item.second }
                }.ifEmpty { quizPool }
                availablePool.random()
            }
        }

        if (next == null) return

        historyQueue.add(next)
        val maxHistory = if (enableRandomNumber) 20 else ceil(quizPool.size * 0.4).toInt().coerceAtLeast(1)
        if (historyQueue.size > maxHistory) historyQueue.removeAt(0)

        currentPair = next
        userInput = ""
        selectedOption = null
        feedback = null

        // 判定题型：由于有 TTS 兜底，听力题不再受限于 audioResName 是否存在
        val canDoListening = "LISTENING" in selectedQuizTypes
        val canDoSpelling = "SPELLING" in selectedQuizTypes

        currentQuizType = when {
            canDoListening && canDoSpelling -> if (Math.random() > 0.5) QuizType.LISTENING else QuizType.SPELLING
            canDoListening -> QuizType.LISTENING
            else -> QuizType.SPELLING
        }

        if (currentQuizType == QuizType.LISTENING) {
            if (next.second == "数字" && enableRandomNumber) {
                // 随机数字模式下，所有选项都随机生成
                val dists = mutableSetOf<cn.eyz2021.suushi.model.CounterItem>()
                while (dists.size < 3) {
                    val r = generateRandomItem()
                    if (r.number != next.first.number) dists.add(r)
                }
                options = (dists.toList() + next.first).shuffled()
            } else {
                val sameCategoryItems = quizPool.filter { it.second == next.second }.map { it.first }
                val otherOptions = sameCategoryItems.filter { it != next.first }.shuffled().take(3)
                options = (otherOptions + next.first).shuffled()
            }
            // 听力开局发音，支持 TTS
            audioHelper.playAudio(next.first.audioResName, next.first.reading)
        }
    }

    // 监听范围或题型变化，立即刷新当前题目
    LaunchedEffect(quizPool, selectedQuizTypes) {
        if (quizPool.isNotEmpty()) {
            historyQueue.clear()
            pickNextQuestion()
        } else {
            currentPair = null
            historyQueue.clear()
        }
    }

    fun nextQuestion() {
        pickNextQuestion()
    }

    fun checkSpellingAnswer() {
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

        checkResult(input in possibleAnswers, possibleAnswers.joinToString(" 或 "))
    }

    fun checkOptionAnswer(item: cn.eyz2021.suushi.model.CounterItem) {
        if (feedback != null) return
        selectedOption = item
        val correctItem = currentPair!!.first
        val category = currentPair!!.second
        val answerText = if (category == "つ" && correctItem.number == "何") {
            "いくつ"
        } else if (category == "つ" && (correctItem.number.toIntOrNull() ?: 0) >= 10) {
            correctItem.number
        } else if (category == "数字") {
            correctItem.number
        } else {
            correctItem.number + category
        }
        checkResult(item == correctItem, answerText)
    }

    Scaffold(
        topBar = {
            Column {
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
                                    t("quiz_settings_types"),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                val quizTypes = listOf(
                                    "SPELLING" to t("quiz_type_spelling"),
                                    "LISTENING" to t("quiz_type_listening")
                                )
                                quizTypes.forEach { (type, label) ->
                                    val isSelected = type in selectedQuizTypes
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(checked = isSelected, onCheckedChange = null)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(label)
                                            }
                                        },
                                        onClick = {
                                            val newTypes = selectedQuizTypes.toMutableSet()
                                            if (isSelected) {
                                                if (newTypes.size > 1) newTypes.remove(type)
                                            } else {
                                                newTypes.add(type)
                                            }
                                            selectedQuizTypes = newTypes
                                        }
                                    )
                                }

                                if (selectedTab == 0) {
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
                                } else {
                                    HorizontalDivider()
                                    Text(
                                        "数字范围 (1-999,999)",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = randomRangeStart.toString(),
                                            onValueChange = { randomRangeStart = it.toIntOrNull() ?: 1 },
                                            label = { Text("从") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = randomRangeEnd.toString(),
                                            onValueChange = { randomRangeEnd = it.toIntOrNull() ?: 100 },
                                            label = { Text("到") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                                        )
                                    }
                                    HorizontalDivider()
                                    Text(
                                        "额外类型",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(checked = includeDecimals, onCheckedChange = null)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("包含小数")
                                            }
                                        },
                                        onClick = { includeDecimals = !includeDecimals }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(checked = includeFractions, onCheckedChange = null)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("包含分数")
                                            }
                                        },
                                        onClick = { includeFractions = !includeFractions }
                                    )
                                }
                            }
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("基础量词") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("随机数字") }
                    )
                }
            }
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
                    text = if (currentQuizType == QuizType.SPELLING) t("quiz_prompt") else t("quiz_listening_prompt"),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (currentQuizType == QuizType.SPELLING) {
                    val displayText = remember(currentPair) {
                        val item = currentPair!!.first
                        val category = currentPair!!.second
                        if (category == "つ") {
                            if (item.number == "何") {
                                "いくつ"
                            } else {
                                val num = item.number.toIntOrNull()
                                if (num != null && num >= 10) {
                                    item.number
                                } else {
                                    "${item.number}$category"
                                }
                            }
                        } else if (category == "数字") {
                            item.number
                        } else {
                            "${item.number}$category"
                        }
                    }
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Surface(
                        onClick = { audioHelper.playAudio(currentPair!!.first.audioResName, currentPair!!.first.reading) },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (currentQuizType == QuizType.SPELLING) {
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
                            onDone = { checkSpellingAnswer() },
                            onNext = { if (feedback != null) nextQuestion() }
                        )
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        options.forEach { option ->
                            val isCorrectOption = option == currentPair!!.first
                            val isSelected = selectedOption == option

                            val backgroundColor = when {
                                feedback != null && isCorrectOption -> Color(0xFFE8F5E9)
                                feedback != null && isSelected && !isCorrectOption -> Color(0xFFFFEBEE)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            val contentColor = when {
                                feedback != null && isCorrectOption -> Color(0xFF2E7D32)
                                feedback != null && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            val optionDisplayText = remember(option, currentPair) {
                                val category = currentPair!!.second
                                if (category == "つ") {
                                    if (option.number == "何") {
                                        "いくつ"
                                    } else {
                                        val num = option.number.toIntOrNull()
                                        if (num != null && num >= 10) {
                                            option.number
                                        } else {
                                            "${option.number}$category"
                                        }
                                    }
                                } else if (category == "数字") {
                                    option.number
                                } else {
                                    "${option.number}$category"
                                }
                            }

                            Surface(
                                onClick = {
                                    if (feedback == null) {
                                        checkOptionAnswer(option)
                                    } else {
                                        // 选项发音，支持 TTS
                                        audioHelper.playAudio(option.audioResName, option.reading)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = backgroundColor,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = optionDisplayText,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = contentColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(visible = feedback != null || currentQuizType == QuizType.SPELLING) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (feedback == null) {
                            Button(
                                onClick = { checkSpellingAnswer() },
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
                }
            }

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}
