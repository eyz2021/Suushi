package cn.eyz2021.suushi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.eyz2021.suushi.model.CounterGroup
import cn.eyz2021.suushi.model.CounterItem
import cn.eyz2021.suushi.util.AudioHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterTableScreen(
    group: CounterGroup,
    onBack: () -> Unit
) {
    val context = LocalContext.current
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
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (group.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("内容制作中...")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(group.items.chunked(2)) { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                CounterCell(
                                    item = item,
                                    onClick = { audioHelper.playAudio(item.audioResName) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
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
