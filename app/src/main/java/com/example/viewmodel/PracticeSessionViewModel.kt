package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.TtsAudioController
import com.example.data.local.AchievementEntity
import com.example.data.local.ExerciseEntity
import com.example.data.local.PracticeMode
import com.example.data.local.VocabularyEntity
import com.example.data.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface PracticePhase {
    data object Loading : PracticePhase
    data object Flashcards : PracticePhase
    data object Quiz : PracticePhase
    data class Completed(
        val xpEarned: Int,
        val correctCount: Int,
        val totalCount: Int,
        val newlyUnlockedBadges: List<AchievementEntity>,
    ) : PracticePhase
    data object Empty : PracticePhase
}

data class PracticeSessionUiState(
    val mode: PracticeMode,
    val phase: PracticePhase = PracticePhase.Loading,
    val vocabDeck: List<VocabularyEntity> = emptyList(),
    val cardIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val knownCardsCount: Int = 0,
    val exercises: List<ExerciseEntity> = emptyList(),
    val exerciseIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerChecked: Boolean = false,
    val isCurrentAnswerCorrect: Boolean = false,
    val correctCount: Int = 0,
) {
    val currentCard: VocabularyEntity? get() = vocabDeck.getOrNull(cardIndex)
    val currentExercise: ExerciseEntity? get() = exercises.getOrNull(exerciseIndex)

    fun currentOptions(): List<String> {
        val raw = currentExercise?.optionsJson ?: return emptyList()
        return runCatching { Json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
    }
}

class PracticeSessionViewModel(
    private val mode: PracticeMode,
    private val languageCode: String,
    private val learningRepository: LearningRepository,
    private val audioController: TtsAudioController,
    private val soundEffectsEnabled: Boolean,
    private val slowSpeechDefault: Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeSessionUiState(mode = mode))
    val uiState: StateFlow<PracticeSessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    fun loadSession() {
        viewModelScope.launch {
            learningRepository.ensureSeeded()
            if (mode == PracticeMode.VOCAB_REVIEW) {
                val deck = learningRepository.observeAllVocabulary(languageCode).first().shuffled().take(8)
                _uiState.value = PracticeSessionUiState(
                    mode = mode,
                    phase = if (deck.isEmpty()) PracticePhase.Empty else PracticePhase.Flashcards,
                    vocabDeck = deck,
                )
            } else {
                val exercises = learningRepository.getPracticeExercises(languageCode, mode.key)
                    .shuffled()
                    .take(6)
                _uiState.value = PracticeSessionUiState(
                    mode = mode,
                    phase = if (exercises.isEmpty()) PracticePhase.Empty else PracticePhase.Quiz,
                    exercises = exercises,
                )
            }
        }
    }

    fun speak(text: String, forceSlow: Boolean? = null) {
        audioController.speak(text, slow = forceSlow ?: slowSpeechDefault)
    }

    fun flipCard() {
        _uiState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun markCard(known: Boolean) {
        val state = _uiState.value
        val updatedKnown = if (known) state.knownCardsCount + 1 else state.knownCardsCount
        if (known) audioController.playCorrectChime(soundEffectsEnabled)

        if (state.cardIndex + 1 < state.vocabDeck.size) {
            _uiState.update {
                it.copy(
                    cardIndex = it.cardIndex + 1,
                    isCardFlipped = false,
                    knownCardsCount = updatedKnown,
                )
            }
        } else {
            val xp = (updatedKnown * 3).coerceAtLeast(10)
            finishPractice(xp = xp, correct = updatedKnown, total = state.vocabDeck.size)
        }
    }

    fun selectOption(index: Int) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    fun checkAnswer() {
        val state = _uiState.value
        val selected = state.selectedOptionIndex ?: return
        val exercise = state.currentExercise ?: return
        val correct = selected == exercise.correctIndex

        if (correct) {
            audioController.playCorrectChime(soundEffectsEnabled)
        } else {
            audioController.playIncorrectChime(soundEffectsEnabled)
        }

        _uiState.update {
            it.copy(
                isAnswerChecked = true,
                isCurrentAnswerCorrect = correct,
                correctCount = if (correct) it.correctCount + 1 else it.correctCount,
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.exerciseIndex + 1 < state.exercises.size) {
            _uiState.update {
                it.copy(
                    exerciseIndex = it.exerciseIndex + 1,
                    selectedOptionIndex = null,
                    isAnswerChecked = false,
                    isCurrentAnswerCorrect = false,
                )
            }
        } else {
            val xp = (state.correctCount * 4 + 5).coerceAtLeast(10)
            finishPractice(xp = xp, correct = state.correctCount, total = state.exercises.size)
        }
    }

    private fun finishPractice(xp: Int, correct: Int, total: Int) {
        viewModelScope.launch {
            val badges = learningRepository.recordPracticeXp(xp)
            audioController.playCelebrationChime(soundEffectsEnabled)
            _uiState.update {
                it.copy(
                    phase = PracticePhase.Completed(
                        xpEarned = xp,
                        correctCount = correct,
                        totalCount = total,
                        newlyUnlockedBadges = badges,
                    )
                )
            }
        }
    }

    class Factory(
        private val mode: PracticeMode,
        private val languageCode: String,
        private val learningRepository: LearningRepository,
        private val audioController: TtsAudioController,
        private val soundEffectsEnabled: Boolean,
        private val slowSpeechDefault: Boolean,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PracticeSessionViewModel(
                mode = mode,
                languageCode = languageCode,
                learningRepository = learningRepository,
                audioController = audioController,
                soundEffectsEnabled = soundEffectsEnabled,
                slowSpeechDefault = slowSpeechDefault,
            ) as T
        }
    }
}
