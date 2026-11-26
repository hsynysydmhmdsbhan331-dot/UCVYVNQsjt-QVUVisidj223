package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Learn : AppRoute

    @Serializable
    data object Practice : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data class LessonPlayer(val lessonId: String) : AppRoute

    @Serializable
    data class SkillModule(val skillKey: String) : AppRoute

    @Serializable
    data class PracticeSession(val modeKey: String) : AppRoute

    @Serializable
    data object Settings : AppRoute
}

enum class MainTab {
    HOME,
    LEARN,
    PRACTICE,
    PROFILE,
}
