package cn.eyz2021.suushi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.eyz2021.suushi.model.CounterGroup
import cn.eyz2021.suushi.model.CounterItem
import cn.eyz2021.suushi.util.AudioHelper
import cn.eyz2021.suushi.util.NumberConverter
import cn.eyz2021.suushi.util.SettingsHelper
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterTableScreen(
    group: CounterGroup,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsHelper = remember { SettingsHelper(context) }
    var columnCount by remember { mutableIntStateOf(settingsHelper.getTableColumnCount()) }
    val audioHelper = remember { AudioHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            audioHelper.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        columnCount = if (columnCount == 1) 2 else 1
                        settingsHelper.saveTableColumnCount(columnCount)
                    }) {
                        Icon(
                            imageVector = if (columnCount == 1) Icons.Default.GridView else Icons.Default.ViewStream,
                            contentDescription = "切换布局"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (group.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("内容制作中...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(group.items.chunked(columnCount)) { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                CounterCell(
                                    item = item,
                                    onClick = { audioHelper.playAudio(item.audioResName, item.reading) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size < columnCount) {
                                repeat(columnCount - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    if (group.title == "数字") {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))
                            NumberConverterTool(audioHelper)
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberConverterTool(audioHelper: AudioHelper) {
    var input by remember { mutableStateOf("") }
    val result = remember(input) { 
        if (input.isBlank()) "" else NumberConverter.convert(input)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "数字转换工具",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() || char == '.' || char == '/' } && it.length <= 15) {
                        input = it 
                    }
                },
                placeholder = { Text("输入数字、小数(.)或分数(/)", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                trailingIcon = {
                    if (input.isNotEmpty()) {
                        IconButton(onClick = { input = "" }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack, // 借用一下图标做清除，或直接写Text
                                contentDescription = "清除",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            )
            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    onClick = { audioHelper.playAudio(null, result) },
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = result,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "点击播放读音",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CounterCell(
    item: CounterItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.padding(4.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = item.number,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = item.reading,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}
