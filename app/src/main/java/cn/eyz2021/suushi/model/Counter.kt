package cn.eyz2021.suushi.model

data class CounterItem(
    val number: String,
    val reading: String,
    val audioResName: String? = null
)

data class CounterGroup(
    val title: String,
    val items: List<CounterItem>
)

val sampleData = listOf(
    CounterGroup(
        title = "月",
        items = listOf(
            CounterItem("1", "いちがつ", "m01"),
            CounterItem("2", "にがつ", "m02"),
            CounterItem("3", "さんがつ", "m03"),
            CounterItem("4", "しがつ", "m04"),
            CounterItem("5", "ごがつ", "m05"),
            CounterItem("6", "ろくがつ", "m06"),
            CounterItem("7", "しちがつ", "m07"),
            CounterItem("8", "はちがつ", "m08"),
            CounterItem("9", "くがつ", "m09"),
            CounterItem("10", "じゅうがつ", "m10"),
            CounterItem("11", "じゅういちがつ", "m11"),
            CounterItem("12", "じゅうにがつ", "m12"),
            CounterItem("何", "なんがつ", "m_nan")
        )
    ),
    CounterGroup(
        title = "時",
        items = listOf(
            CounterItem("1", "いちじ", "h01"),
            CounterItem("2", "にじ", "h02"),
            CounterItem("3", "さんじ", "h03"),
            CounterItem("4", "よじ", "h04"),
            CounterItem("5", "ごじ", "h05"),
            CounterItem("6", "ろくじ", "h06"),
            CounterItem("7", "しちじ", "h07"),
            CounterItem("8", "はちじ", "h08"),
            CounterItem("9", "くじ", "h09"),
            CounterItem("10", "じゅうじ", "h10"),
            CounterItem("11", "じゅういちじ", "h11"),
            CounterItem("12", "じゅうにじ", "h12"),
            CounterItem("何", "なんじ", "h_nan")
        )
    ),
    CounterGroup(title = "人", items = emptyList()),
    CounterGroup(title = "階", items = emptyList()),
    CounterGroup(title = "週間", items = emptyList()),
    CounterGroup(title = "分", items = emptyList())
)
