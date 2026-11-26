package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.TtsAudioController
import com.example.core.i18n.AppLanguage
import com.example.data.local.ExerciseEntity
import com.example.data.local.VocabularyEntity
import com.example.data.prefs.UserPreferences
import com.example.data.prefs.UserPreferencesRepository
import com.example.data.repository.LearningOverview
import com.example.data.repository.LearningRepository
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppUiState {
    data object Loading : AppUiState
    data class Ready(
        val preferences: UserPreferences,
        val overview: LearningOverview,
        val allVocabulary: List<VocabularyEntity>,
        val allExercises: List<ExerciseEntity>,
    ) : AppUiState
}

class MainViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val learningRepository: LearningRepository,
    private val audioController: TtsAudioController,
) : ViewModel() {

    private val _ttsWarningVisible = MutableStateFlow(false)
    val ttsWarningVisible: StateFlow<Boolean> = _ttsWarningVisible.asStateFlow()

    init {
        viewModelScope.launch {
            learningRepository.ensureSeeded()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AppUiState> = preferencesRepository.preferencesFlow
        .flatMapLatest { prefs ->
            combine(
                learningRepository.observeOverview(prefs.learningLanguageCode),
                learningRepository.observeAllVocabulary(prefs.learningLanguageCode),
                learningRepository.observeAllExercises(prefs.learningLanguageCode),
            ) { overview, vocab, exercises ->
                AppUiState.Ready(
                    preferences = prefs,
                    overview = overview,
                    allVocabulary = vocab,
                    allExercises = exercises,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppUiState.Loading,
        )

    fun speakEnglish(text: String, forceSlow: Boolean? = null) {
        val currentPrefs = (uiState.value as? AppUiState.Ready)?.preferences
        val useSlow = forceSlow ?: currentPrefs?.slowSpeechDefault ?: false
        val ok = audioController.speak(text, slow = useSlow)
        _ttsWarningVisible.value = !ok
    }

    fun dismissTtsWarning() {
        _ttsWarningVisible.value = false
    }

    fun playCorrectSound() {
        val enabled = (uiState.value as? AppUiState.Ready)?.preferences?.soundEffectsEnabled ?: true
        audioController.playCorrectChime(enabled)
    }

    fun playIncorrectSound() {
        val enabled = (uiState.value as? AppUiState.Ready)?.preferences?.soundEffectsEnabled ?: true
        audioController.playIncorrectChime(enabled)
    }

    fun completeOnboarding(language: AppLanguage, dailyXpGoal: Int) {
        viewModelScope.launch {
            preferencesRepository.completeOnboarding(language, dailyXpGoal)
        }
    }

    fun setInterfaceLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository.setInterfaceLanguage(language)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setDailyXpGoal(xpGoal: Int) {
        viewModelScope.launch {
            preferencesRepository.setDailyXpGoal(xpGoal)
        }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSoundEffectsEnabled(enabled)
        }
    }

    fun setSlowSpeechDefault(slow: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSlowSpeechDefault(slow)
        }
    }

    fun setUsername(name: String) {
        viewModelScope.launch {
            preferencesRepository.setUsername(name)
        }
    }

    fun setAvatarId(avatarId: Int) {
        viewModelScope.launch {
            preferencesRepository.setAvatarId(avatarId)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            learningRepository.resetAllProgress()
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val learningRepository: LearningRepository,
        private val audioController: TtsAudioController,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(preferencesRepository, learningRepository, audioController) as T
        }
    }
}
