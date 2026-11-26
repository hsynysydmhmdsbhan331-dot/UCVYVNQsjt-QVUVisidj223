package com.example.core.i18n

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(
    val code: String,
    val layoutDirection: LayoutDirection,
) {
    PERSIAN(code = "fa", layoutDirection = LayoutDirection.Rtl),
    ENGLISH(code = "en", layoutDirection = LayoutDirection.Ltr);

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: PERSIAN
    }
}

fun formatNumber(value: Int, language: AppLanguage): String {
    val raw = value.toString()
    if (language == AppLanguage.ENGLISH) return raw
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder(raw.length)
    for (ch in raw) {
        if (ch in '0'..'9') {
            sb.append(persianDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
