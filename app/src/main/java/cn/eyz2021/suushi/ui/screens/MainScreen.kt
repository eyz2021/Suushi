package cn.eyz2021.suushi.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.eyz2021.suushi.model.CounterGroup
import cn.eyz2021.suushi.model.sampleData
import cn.eyz2021.suushi.util.t

@Composable
fun MainScreen(onThemeChange: () -> Unit, onLanguageChange: () -> Unit) {
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
                        icon = { Icon(Icons.Default.List, contentDescription = t("tab_table")) },
                        label = { Text(t("tab_table")) }
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.Star, contentDescription = t("tab_quiz")) },
                        label = { Text(t("tab_quiz")) }
                    )
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = t("tab_settings")) },
                        label = { Text(t("tab_settings")) }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ -> 
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
                    2 -> {
                        Box(modifier = Modifier.padding(bottom = 80.dp)) {
                            SettingsScreen(
                                onThemeChange = onThemeChange,
                                onLanguageChange = onLanguageChange
                            )
                        }
                    }
                }
            }
        }
    }
}
