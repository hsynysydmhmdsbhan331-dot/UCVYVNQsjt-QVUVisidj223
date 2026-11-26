package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.TtsAudioController
import com.example.data.local.AchievementEntity
import com.example.data.local.ExerciseEntity
import com.example.data.local.LessonEntity
import com.example.data.local.VocabularyEntity
import com.example.data.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface LessonPlayerPhase {
    data object Loading : LessonPlayerPhase
    data object Vocabulary : LessonPlayerPhase
    data object Exercises : LessonPlayerPhase
    data class Completed(
        val xpAwarded: Int,
        val accuracyPercent: Int,
        val newlyUnlockedBadges: List<AchievementEntity>,
    ) : LessonPlayerPhase
    data object Error : LessonPlayerPhase
}

data class LessonPlayerUiState(
    val phase: LessonPlayerPhase = LessonPlayerPhase.Loading,
    val lesson: LessonEntity? = null,
    val vocabulary: List<VocabularyEntity> = emptyList(),
    val vocabIndex: Int = 0,
    val exercises: List<ExerciseEntity> = emptyList(),
    val exerciseIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerChecked: Boolean = false,
    val isCurrentAnswerCorrect: Boolean = false,
    val correctAnswersCount: Int = 0,
    val ttsFallbackWarning: Boolean = false,
) {
    val currentVocab: VocabularyEntity? get() = vocabulary.getOrNull(vocabIndex)
    val currentExercise: ExerciseEntity? get() = exercises.getOrNull(exerciseIndex)
    val totalSteps: Int get() = vocabulary.size + exercises.size
    val currentStepNumber: Int
        get() = when (phase) {
            LessonPlayerPhase.Vocabulary -> vocabIndex + 1
            LessonPlayerPhase.Exercises -> vocabulary.size + exerciseIndex + 1
            is LessonPlayerPhase.Completed -> totalSteps
            else -> 0
        }

    fun currentOptions(): List<String> {
        val raw = currentExercise?.optionsJson ?: return emptyList()
        return runCatching { Json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
    }
}

class LessonPlayerViewModel(
    private val lessonId: String,
    private val learningRepository: LearningRepository,
    private val audioController: TtsAudioController,
    private val soundEffectsEnabled: Boolean,
    private val slowSpeechDefault: Boolean,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonPlayerUiState())
    val uiState: StateFlow<LessonPlayerUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    fun loadLesson() {
        viewModelScope.launch {
            _uiState.value = LessonPlayerUiState(phase = LessonPlayerPhase.Loading)
            val (lesson, vocab, exercises) = learningRepository.getLessonDetail(lessonId)
            if (lesson == null || (vocab.isEmpty() && exercises.isEmpty())) {
                _uiState.value = LessonPlayerUiState(phase = LessonPlayerPhase.Error)
                return@launch
            }
            _uiState.value = LessonPlayerUiState(
                phase = if (vocab.isNotEmpty()) LessonPlayerPhase.Vocabulary else LessonPlayerPhase.Exercises,
                lesson = lesson,
                vocabulary = vocab,
                exercises = exercises,
            )
        }
    }

    fun speak(text: String, forceSlow: Boolean? = null) {
        val slow = forceSlow ?: slowSpeechDefault
        val ok = audioController.speak(text, slow = slow)
        if (!ok) {
            _uiState.update { it.copy(ttsFallbackWarning = true) }
        }
    }

    fun clearTtsWarning() {
        _uiState.update { it.copy(ttsFallbackWarning = false) }
    }

    fun nextVocabCard() {
        val state = _uiState.value
        if (state.vocabIndex + 1 < state.vocabulary.size) {
            _uiState.update { it.copy(vocabIndex = it.vocabIndex + 1) }
        } else if (state.exercises.isNotEmpty()) {
            _uiState.update { it.copy(phase = LessonPlayerPhase.Exercises, exerciseIndex = 0) }
        } else {
            finishLesson()
        }
    }

    fun previousVocabCard() {
        val state = _uiState.value
        if (state.vocabIndex > 0) {
            _uiState.update { it.copy(vocabIndex = it.vocabIndex - 1) }
        }
    }

    fun selectOption(index: Int) {
        val state = _uiState.value
        if (state.isAnswerChecked) return
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    fun checkAnswer() {
        val state = _uiState.value
        val selected = state.selectedOptionIndex ?: return
        val exercise = state.currentExercise ?: return
        val isCorrect = selected == exercise.correctIndex

        if (isCorrect) {
            audioController.playCorrectChime(soundEffectsEnabled)
        } else {
            audioController.playIncorrectChime(soundEffectsEnabled)
        }

        _uiState.update {
            it.copy(
                isAnswerChecked = true,
                isCurrentAnswerCorrect = isCorrect,
                correctAnswersCount = if (isCorrect) it.correctAnswersCount + 1 else it.correctAnswersCount,
            )
        }
    }

    fun nextExerciseOrFinish() {
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
            finishLesson()
        }
    }

    private fun finishLesson() {
        val state = _uiState.value
        viewModelScope.launch {
            val result = learningRepository.recordLessonCompletion(
                lessonId = lessonId,
                correctCount = state.correctAnswersCount,
                totalQuestions = state.exercises.size,
            )
            audioController.playCelebrationChime(soundEffectsEnabled)
            _uiState.update {
                it.copy(
                    phase = LessonPlayerPhase.Completed(
                        xpAwarded = result.xpAwarded,
                        accuracyPercent = result.accuracyPercent,
                        newlyUnlockedBadges = result.newlyUnlockedAchievements,
                    )
                )
            }
        }
    }

    fun replay() {
        loadLesson()
    }

    class Factory(
        private val lessonId: String,
        private val learningRepository: LearningRepository,
        private val audioController: TtsAudioController,
        private val soundEffectsEnabled: Boolean,
        private val slowSpeechDefault: Boolean,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LessonPlayerViewModel(
                lessonId = lessonId,
                learningRepository = learningRepository,
                audioController = audioController,
                soundEffectsEnabled = soundEffectsEnabled,
                slowSpeechDefault = slowSpeechDefault,
            ) as T
        }
    }
}
