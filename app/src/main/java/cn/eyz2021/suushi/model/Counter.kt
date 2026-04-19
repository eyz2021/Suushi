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
            CounterItem("1", "いちがつ", "month/m01"),
            CounterItem("2", "にがつ", "month/m02"),
            CounterItem("3", "さんがつ", "month/m03"),
            CounterItem("4", "しがつ", "month/m04"),
            CounterItem("5", "ごがつ", "month/m05"),
            CounterItem("6", "ろくがつ", "month/m06"),
            CounterItem("7", "しちがつ", "month/m07"),
            CounterItem("8", "はちがつ", "month/m08"),
            CounterItem("9", "くがつ", "month/m09"),
            CounterItem("10", "じゅうがつ", "month/m10"),
            CounterItem("11", "じゅういちがつ", "month/m11"),
            CounterItem("12", "じゅうにがつ", "month/m12"),
            CounterItem("何", "なんがつ", "month/m_nan")
        )
    ),
    CounterGroup(
        title = "時",
        items = listOf(
            CounterItem("1", "いちじ", "hour/h01"),
            CounterItem("2", "にじ", "hour/h02"),
            CounterItem("3", "さんじ", "hour/h03"),
            CounterItem("4", "よじ", "hour/h04"),
            CounterItem("5", "ごじ", "hour/h05"),
            CounterItem("6", "ろくじ", "hour/h06"),
            CounterItem("7", "しちじ", "hour/h07"),
            CounterItem("8", "はちじ", "hour/h08"),
            CounterItem("9", "くじ", "hour/h09"),
            CounterItem("10", "じゅうじ", "hour/h10"),
            CounterItem("11", "じゅういちじ", "hour/h11"),
            CounterItem("12", "じゅうにじ", "hour/h12"),
            CounterItem("何", "なんじ", "hour/h_nan")
        )
    ),
    CounterGroup(
        title = "人",
        items = listOf(
            CounterItem("1", "ひとり", "person/p01"),
            CounterItem("2", "ふたり", "person/p02"),
            CounterItem("3", "さんにん", "person/p03"),
            CounterItem("4", "よにん", "person/p04"),
            CounterItem("5", "ごにん", "person/p05"),
            CounterItem("6", "ろくにん", "person/p06"),
            CounterItem("7", "しちにん/ななにん", "person/p07"),
            CounterItem("8", "はちにん", "person/p08"),
            CounterItem("9", "きゅうにん", "person/p09"),
            CounterItem("10", "じゅうにん", "person/p10"),
            CounterItem("11", "じゅういちにん", "person/p11"),
            CounterItem("12", "じゅうににん", "person/p12"),
            CounterItem("13", "じゅうさんにん", "person/p13"),
            CounterItem("14", "じゅうよにん", "person/p14"),
            CounterItem("15", "じゅうごにん", "person/p15"),
            CounterItem("16", "じゅうろくにん", "person/p16"),
            CounterItem("17", "じゅうしちにん/じゅうななにん", "person/p17"),
            CounterItem("18", "じゅうはちにん", "person/p18"),
            CounterItem("19", "じゅうきゅうにん", "person/p19"),
            CounterItem("20", "にじゅうにん", "person/p20"),
            CounterItem("何", "なんにん", "person/p_nan")
        )
    ),
    CounterGroup(
        title = "階",
        items = listOf(
            CounterItem("1", "いっかい", "floor/k01"),
            CounterItem("2", "にかい", "floor/k02"),
            CounterItem("3", "さんがい", "floor/k03"),
            CounterItem("4", "よんかい", "floor/k04"),
            CounterItem("5", "ごかい", "floor/k05"),
            CounterItem("6", "ろっかい", "floor/k06"),
            CounterItem("7", "ななかい", "floor/k07"),
            CounterItem("8", "はちかい/はっかい", "floor/k08"),
            CounterItem("9", "きゅうかい", "floor/k09"),
            CounterItem("10", "じゅっかい", "floor/k10"),
            CounterItem("11", "じゅういっかい", "floor/k11"),
            CounterItem("12", "じゅうにかい", "floor/k12"),
            CounterItem("13", "じゅうさんかい", "floor/k13"),
            CounterItem("14", "じゅうよんかい", "floor/k14"),
            CounterItem("15", "じゅうごかい", "floor/k15"),
            CounterItem("16", "じゅうろっかい", "floor/k16"),
            CounterItem("17", "じゅうななかい", "floor/k17"),
            CounterItem("18", "じゅうはちかい/じゅうはっかい", "floor/k18"),
            CounterItem("19", "じゅうきゅうかい", "floor/k19"),
            CounterItem("20", "にじゅっかい", "floor/k20"),
            CounterItem("何", "なんかい", "floor/k_nan")
        )
    ),
    CounterGroup(
        title = "つ",
        items = listOf(
            CounterItem("1", "ひとつ", "tsu/t01"),
            CounterItem("2", "ふたつ", "tsu/t02"),
            CounterItem("3", "みっつ", "tsu/t03"),
            CounterItem("4", "よっつ", "tsu/t04"),
            CounterItem("5", "いつつ", "tsu/t05"),
            CounterItem("6", "むっつ", "tsu/t06"),
            CounterItem("7", "ななつ", "tsu/t07"),
            CounterItem("8", "やっつ", "tsu/t08"),
            CounterItem("9", "ここのつ", "tsu/t09"),
            CounterItem("10", "とお", "tsu/t10"),
            CounterItem("11", "じゅういち", "tsu/t11"),
            CounterItem("12", "じゅうに", "tsu/t12"),
            CounterItem("13", "じゅうさん", "tsu/t13"),
            CounterItem("14", "じゅうよん/じゅうし", "tsu/t14"),
            CounterItem("15", "じゅうご", "tsu/t15"),
            CounterItem("16", "じゅうろく", "tsu/t16"),
            CounterItem("17", "じゅうしち/じゅうなな", "tsu/t17"),
            CounterItem("18", "じゅうはち", "tsu/t18"),
            CounterItem("19", "じゅうく/じゅうきゅう", "tsu/t19"),
            CounterItem("20", "にじゅう", "tsu/t20"),
            CounterItem("何", "いくつ", "tsu/t_nan")
        )
    ),
    CounterGroup(title = "週間", items = emptyList()),
    CounterGroup(title = "分", items = emptyList())
)
