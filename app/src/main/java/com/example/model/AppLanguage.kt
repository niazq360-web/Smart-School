package com.example.model

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val title: String, val nativeName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", "English", false),
    URDU("ur", "Urdu", "اردو", true),
    SINDHI("sd", "Sindhi", "سنڌي", true);

    val layoutDirection: LayoutDirection
        get() = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
}
