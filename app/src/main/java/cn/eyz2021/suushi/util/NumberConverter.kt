package cn.eyz2021.suushi.util

object NumberConverter {
    private val digits = arrayOf("れい", "いち", "に", "さん", "よん", "ご", "ろく", "なな", "はち", "きゅう")
    private val units = arrayOf("", "じゅう", "ひゃく", "せん")
    private val myriads = arrayOf("", "まん", "おく", "ちょう")

    fun convert(numberStr: String): String {
        if (numberStr.isBlank()) return ""
        
        // 处理分数
        if (numberStr.contains("/")) {
            val parts = numberStr.split("/")
            if (parts.size == 2) {
                val denominator = parts[1]
                val numerator = parts[0]
                val dText = convertLong(denominator.toLongOrNull() ?: return "输入错误")
                val nText = convertLong(numerator.toLongOrNull() ?: return "输入错误")
                if (dText == "输入错误" || nText == "输入错误") return "输入错误"
                return "${dText}ぶんの${nText}"
            }
            return "输入错误"
        }

        // 处理小数
        if (numberStr.contains(".")) {
            val parts = numberStr.split(".")
            if (parts.size == 2) {
                val integerPart = parts[0]
                val decimalPart = parts[1]
                val iText = if (integerPart.isEmpty()) "れい" else convertLong(integerPart.toLongOrNull() ?: return "输入错误")
                if (iText == "输入错误") return "输入错误"
                
                val dText = decimalPart.map { char ->
                    val digit = char.toString().toIntOrNull()
                    if (digit != null) digits[digit] else ""
                }.joinToString("")
                
                return "${iText}てん${dText}"
            }
            return "输入错误"
        }

        // 处理整数
        val number = numberStr.toLongOrNull() ?: return "输入错误"
        return convertLong(number)
    }

    private fun convertLong(number: Long): String {
        if (number == 0L) return "れい"
        if (number < 0) return "まいなす " + convertLong(Math.abs(number))
        
        var res = ""
        var n = number
        var myriadIdx = 0
        
        while (n > 0) {
            val part = (n % 10000).toInt()
            if (part > 0) {
                val partStr = convertUnder10000(part)
                res = partStr + myriads[myriadIdx] + res
            }
            n /= 10000
            myriadIdx++
        }
        
        return res
    }

    private fun convertUnder10000(n: Int): String {
        var res = ""
        var temp = n
        for (i in 0..3) {
            val digit = temp % 10
            if (digit != 0) {
                val s = when {
                    i == 0 -> digits[digit]
                    i == 1 && digit == 1 -> "じゅう"
                    i == 2 && digit == 1 -> "ひゃく"
                    i == 2 && digit == 3 -> "さんびゃく"
                    i == 2 && digit == 6 -> "ろっぴゃく"
                    i == 2 && digit == 8 -> "はっぴゃく"
                    i == 3 && digit == 1 -> "せん"
                    i == 3 && digit == 3 -> "さんぜん"
                    i == 3 && digit == 8 -> "はっせん"
                    else -> digits[digit] + units[i]
                }
                res = s + res
            }
            temp /= 10
        }
        return res
    }
}
