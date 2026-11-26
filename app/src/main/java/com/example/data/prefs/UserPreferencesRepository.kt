package com.example.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.i18n.AppLanguage
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "project_owl_preferences"
)

data class UserPreferences(
    val interfaceLanguage: AppLanguage = AppLanguage.PERSIAN,
    val learningLanguageCode: String = "en-US",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dailyXpGoal: Int = 50,
    val soundEffectsEnabled: Boolean = true,
    val slowSpeechDefault: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val username: String = "",
    val avatarId: Int = 0,
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val INTERFACE_LANGUAGE = stringPreferencesKey("interface_language")
        val LEARNING_LANGUAGE = stringPreferencesKey("learning_language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DAILY_XP_GOAL = intPreferencesKey("daily_xp_goal")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val SLOW_SPEECH_DEFAULT = booleanPreferencesKey("slow_speech_default")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USERNAME = stringPreferencesKey("username")
        val AVATAR_ID = intPreferencesKey("avatar_id")
    }

    val preferencesFlow: Flow<UserPreferences> = context.userPrefsDataStore.data.map { prefs ->
        UserPreferences(
            interfaceLanguage = AppLanguage.fromCode(prefs[Keys.INTERFACE_LANGUAGE]),
            learningLanguageCode = prefs[Keys.LEARNING_LANGUAGE] ?: "en-US",
            themeMode = ThemeMode.fromKey(prefs[Keys.THEME_MODE]),
            dailyXpGoal = prefs[Keys.DAILY_XP_GOAL] ?: 50,
            soundEffectsEnabled = prefs[Keys.SOUND_EFFECTS] ?: true,
            slowSpeechDefault = prefs[Keys.SLOW_SPEECH_DEFAULT] ?: false,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            username = prefs[Keys.USERNAME] ?: "",
            avatarId = prefs[Keys.AVATAR_ID] ?: 0,
        )
    }

    suspend fun setInterfaceLanguage(language: AppLanguage) {
        context.userPrefsDataStore.edit { it[Keys.INTERFACE_LANGUAGE] = language.code }
    }

    suspend fun setLearningLanguage(languageCode: String) {
        context.userPrefsDataStore.edit { it[Keys.LEARNING_LANGUAGE] = languageCode }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.userPrefsDataStore.edit { it[Keys.THEME_MODE] = themeMode.key }
    }

    suspend fun setDailyXpGoal(xpGoal: Int) {
        context.userPrefsDataStore.edit { it[Keys.DAILY_XP_GOAL] = xpGoal }
    }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.userPrefsDataStore.edit { it[Keys.SOUND_EFFECTS] = enabled }
    }

    suspend fun setSlowSpeechDefault(slow: Boolean) {
        context.userPrefsDataStore.edit { it[Keys.SLOW_SPEECH_DEFAULT] = slow }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.userPrefsDataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setUsername(name: String) {
        context.userPrefsDataStore.edit { it[Keys.USERNAME] = name.trim() }
    }

    suspend fun setAvatarId(avatarId: Int) {
        context.userPrefsDataStore.edit { it[Keys.AVATAR_ID] = avatarId }
    }

    suspend fun completeOnboarding(
        language: AppLanguage,
        dailyXpGoal: Int,
    ) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[Keys.INTERFACE_LANGUAGE] = language.code
            prefs[Keys.DAILY_XP_GOAL] = dailyXpGoal
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }
}
