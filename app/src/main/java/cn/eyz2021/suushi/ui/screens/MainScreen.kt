package cn.eyz2021.suushi.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.eyz2021.suushi.model.CounterGroup
import cn.eyz2021.suushi.model.sampleData

@Composable
fun MainScreen() {
    var currentTab by remember { mutableIntStateOf(0) }
    var selectedGroup by remember { mutableStateOf<CounterGroup?>(null) }

    // 进入具体表格后隐藏底部栏
    val showBottomBar = selectedGroup == null

    val animationSpec = tween<IntOffset>(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.List, contentDescription = "对照表") },
                        label = { Text("对照表") }
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.Star, contentDescription = "测试") },
                        label = { Text("测试") }
                    )
                }
            }
        },
        // 关键 1：强制 Scaffold 不自动处理 WindowInsets，防止底部栏出现/消失时内容瞬间位移
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ -> 
        // 关键 2：完全忽略 Scaffold 提供的 innerPadding，手动控制布局稳定性
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(animationSpec = animationSpec) { it } togetherWith
                                slideOutHorizontally(animationSpec = animationSpec) { -it }
                    } else {
                        slideInHorizontally(animationSpec = animationSpec) { -it } togetherWith
                                slideOutHorizontally(animationSpec = animationSpec) { it }
                    }
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> {
                        AnimatedContent(
                            targetState = selectedGroup,
                            transitionSpec = {
                                if (targetState != null) {
                                    slideInHorizontally(animationSpec = animationSpec) { it } togetherWith
                                            slideOutHorizontally(animationSpec = animationSpec) { -it }
                                } else {
                                    slideInHorizontally(animationSpec = animationSpec) { -it } togetherWith
                                            slideOutHorizontally(animationSpec = animationSpec) { it }
                                }
                            },
                            label = "DrillDownTransition"
                        ) { group ->
                            // 关键 3：通过固定的 padding 预留底部导航栏空间，确保即使导航栏消失，内容容器的基准线也不动
                            Box(modifier = Modifier.padding(bottom = if (showBottomBar) 80.dp else 0.dp)) {
                                if (group == null) {
                                    CategoryListScreen(
                                        categories = sampleData,
                                        onCategoryClick = { selectedGroup = it }
                                    )
                                } else {
                                    BackHandler { selectedGroup = null }
                                    CounterTableScreen(
                                        group = group,
                                        onBack = { selectedGroup = null }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        Box(modifier = Modifier.padding(bottom = 80.dp)) {
                            QuizScreen()
                        }
                    }
                }
            }
        }
    }
}
