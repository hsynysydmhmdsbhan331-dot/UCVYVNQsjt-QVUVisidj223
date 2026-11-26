package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalAppStrings
import com.example.core.i18n.formatNumber
import com.example.data.local.ExerciseType
import com.example.ui.components.AudioSpeedControlsRow
import com.example.ui.components.OwlExpression
import com.example.ui.components.OwlMascot
import com.example.ui.components.StatBadgePill
import com.example.ui.theme.OwlAmber
import com.example.ui.theme.OwlEmerald
import com.example.viewmodel.LessonPlayerPhase
import com.example.viewmodel.LessonPlayerViewModel

@Composable
fun LessonPlayerScreen(
    viewModel: LessonPlayerViewModel,
    language: AppLanguage,
    avatarVariant: Int,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    var showExitConfirm by remember { mutableStateOf(false) }

    val isInProgress = uiState.phase is LessonPlayerPhase.Vocabulary || uiState.phase is LessonPlayerPhase.Exercises
    BackHandler(enabled = isInProgress) {
        showExitConfirm = true
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text(strings.exitLessonTitle) },
            text = { Text(strings.exitLessonBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitConfirm = false
                        onBack()
                    },
                    modifier = Modifier.testTag("lesson_confirm_exit"),
                ) {
                    Text(strings.leaveButton, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text(strings.stayButton)
                }
            },
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (val phase = uiState.phase) {
                LessonPlayerPhase.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                LessonPlayerPhase.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OwlMascot(expression = OwlExpression.SURPRISED, size = 120.dp)
                            Button(onClick = onBack) {
                                Text(strings.backButton)
                            }
                        }
                    }
                }

                is LessonPlayerPhase.Completed -> {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 580.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        OwlMascot(
                            expression = OwlExpression.CELEBRATING,
                            size = 176.dp,
                            avatarVariant = avatarVariant,
                            contentDescriptionText = strings.mascotName,
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = strings.lessonCompleteTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = strings.lessonCompleteSubtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(22.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            StatBadgePill(
                                icon = Icons.Filled.Star,
                                valueText = "+${formatNumber(phase.xpAwarded, language)} ${strings.xpUnit}",
                                labelText = strings.xpEarnedLabel,
                                tint = OwlAmber,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                                modifier = Modifier.weight(1f),
                            )
                            StatBadgePill(
                                icon = Icons.Filled.CheckCircle,
                                valueText = "${formatNumber(phase.accuracyPercent, language)}%",
                                labelText = strings.accuracyLabel,
                                tint = OwlEmerald,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                                modifier = Modifier.weight(1f),
                            )
                        }

                        if (phase.newlyUnlockedBadges.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                                ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.EmojiEvents,
                                            contentDescription = strings.newBadgeUnlockedLabel,
                                            tint = OwlAmber,
                                        )
                                        Text(
                                            text = strings.newBadgeUnlockedLabel,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    phase.newlyUnlockedBadges.forEach { badge ->
                                        Text(
                                            text = if (language == AppLanguage.PERSIAN) badge.titleFa else badge.titleEn,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(26.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("lesson_complete_done_button"),
                            shape = RoundedCornerShape(18.dp),
                        ) {
                            Text(strings.backToHomeButton, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.replay() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(18.dp),
                        ) {
                            Text(strings.replayLessonButton)
                        }
                    }
                }

                LessonPlayerPhase.Vocabulary, LessonPlayerPhase.Exercises -> {
                    val progressFraction = if (uiState.totalSteps > 0) {
                        uiState.currentStepNumber.toFloat() / uiState.totalSteps.toFloat()
                    } else {
                        0f
                    }
                    val lessonTitle = uiState.lesson?.let {
                        if (language == AppLanguage.PERSIAN) it.titleFa else it.titleEn
                    }.orEmpty()

                    Column(
                        modifier = Modifier
                            .widthIn(max = 660.dp)
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Top Bar + Step Progress
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                IconButton(
                                    onClick = { showExitConfirm = true },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("lesson_close_button"),
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = strings.leaveButton)
                                }
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(50)),
                                    strokeCap = StrokeCap.Round,
                                )
                                Text(
                                    text = "${formatNumber(uiState.currentStepNumber, language)}/${formatNumber(uiState.totalSteps, language)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                text = lessonTitle,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        // Main Scrollable Card Area
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            if (uiState.ttsFallbackWarning) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = strings.ttsUnavailableHint,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(10.dp),
                                    )
                                }
                            }

                            if (phase == LessonPlayerPhase.Vocabulary) {
                                val vocab = uiState.currentVocab
                                if (vocab != null) {
                                    Text(
                                        text = strings.vocabPhaseTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(22.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            OwlMascot(
                                                expression = OwlExpression.HAPPY,
                                                size = 86.dp,
                                                avatarVariant = avatarVariant,
                                            )
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                Text(
                                                    text = vocab.word,
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    textAlign = TextAlign.Center,
                                                )
                                                Text(
                                                    text = vocab.ipa,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                            Text(
                                                text = vocab.persianMeaning,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                            )
                                            AudioSpeedControlsRow(
                                                onPlayNormal = { viewModel.speak(vocab.word, false) },
                                                onPlaySlow = { viewModel.speak(vocab.word, true) },
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(18.dp),
                                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(18.dp))
                                                    .clickable { viewModel.speak(vocab.exampleSentence) },
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(14.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                                ) {
                                                    Text(
                                                        text = strings.exampleSentenceLabel,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary,
                                                    )
                                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                        Text(
                                                            text = vocab.exampleSentence,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.SemiBold,
                                                        )
                                                    }
                                                    Text(
                                                        text = vocab.exampleTranslationFa,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                val exercise = uiState.currentExercise
                                if (exercise != null) {
                                    val mascotMood = when {
                                        !uiState.isAnswerChecked -> OwlExpression.THINKING
                                        uiState.isCurrentAnswerCorrect -> OwlExpression.CELEBRATING
                                        else -> OwlExpression.ENCOURAGING
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        OwlMascot(
                                            expression = mascotMood,
                                            size = 76.dp,
                                            avatarVariant = avatarVariant,
                                        )
                                        Text(
                                            text = if (language == AppLanguage.PERSIAN) exercise.promptFa else exercise.promptEn,
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }

                                    if (ExerciseType.fromKey(exercise.type) == ExerciseType.LISTENING ||
                                        exercise.targetAudioText.isNotBlank()
                                    ) {
                                        AudioSpeedControlsRow(
                                            onPlayNormal = { viewModel.speak(exercise.targetAudioText, false) },
                                            onPlaySlow = { viewModel.speak(exercise.targetAudioText, true) },
                                        )
                                    }

                                    val options = uiState.currentOptions()
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        options.forEachIndexed { idx, option ->
                                            val isSelected = uiState.selectedOptionIndex == idx
                                            val isCorrectOption = idx == exercise.correctIndex
                                            val containerColor = when {
                                                uiState.isAnswerChecked && isCorrectOption ->
                                                    MaterialTheme.colorScheme.tertiaryContainer
                                                uiState.isAnswerChecked && isSelected && !isCorrectOption ->
                                                    MaterialTheme.colorScheme.errorContainer
                                                isSelected ->
                                                    MaterialTheme.colorScheme.primaryContainer
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                            val borderColor = when {
                                                uiState.isAnswerChecked && isCorrectOption -> OwlEmerald
                                                uiState.isAnswerChecked && isSelected && !isCorrectOption ->
                                                    MaterialTheme.colorScheme.error
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                            }

                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .minimumInteractiveComponentSize()
                                                    .clip(RoundedCornerShape(18.dp))
                                                    .clickable(enabled = !uiState.isAnswerChecked) {
                                                        viewModel.selectOption(idx)
                                                    }
                                                    .testTag("exercise_option_$idx"),
                                                shape = RoundedCornerShape(18.dp),
                                                color = containerColor,
                                                border = BorderStroke(if (isSelected || (uiState.isAnswerChecked && isCorrectOption)) 2.dp else 1.dp, borderColor),
                                            ) {
                                                Text(
                                                    text = option,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                                                )
                                            }
                                        }
                                    }

                                    AnimatedVisibility(visible = uiState.isAnswerChecked) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(18.dp),
                                            color = if (uiState.isCurrentAnswerCorrect) {
                                                MaterialTheme.colorScheme.tertiaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.errorContainer
                                            },
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                            ) {
                                                Text(
                                                    text = if (uiState.isCurrentAnswerCorrect) {
                                                        strings.correctBannerTitle
                                                    } else {
                                                        strings.incorrectBannerTitle
                                                    },
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = if (language == AppLanguage.PERSIAN) {
                                                        exercise.explanationFa
                                                    } else {
                                                        exercise.explanationEn
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Action Bar
                        if (phase == LessonPlayerPhase.Vocabulary) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                if (uiState.vocabIndex > 0) {
                                    OutlinedButton(
                                        onClick = { viewModel.previousVocabCard() },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp),
                                        shape = RoundedCornerShape(16.dp),
                                    ) {
                                        Text(strings.previousButton)
                                    }
                                }
                                Button(
                                    onClick = { viewModel.nextVocabCard() },
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(52.dp)
                                        .testTag("vocab_next_button"),
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Text(strings.continueButton, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        } else {
                            val isLastExercise = uiState.exerciseIndex + 1 >= uiState.exercises.size
                            Button(
                                onClick = {
                                    if (!uiState.isAnswerChecked) {
                                        viewModel.checkAnswer()
                                    } else {
                                        viewModel.nextExerciseOrFinish()
                                    }
                                },
                                enabled = uiState.selectedOptionIndex != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .height(54.dp)
                                    .testTag("exercise_action_button"),
                                shape = RoundedCornerShape(18.dp),
                                colors = if (uiState.isAnswerChecked && uiState.isCurrentAnswerCorrect) {
                                    ButtonDefaults.buttonColors(containerColor = OwlEmerald)
                                } else {
                                    ButtonDefaults.buttonColors()
                                },
                            ) {
                                Text(
                                    text = when {
                                        !uiState.isAnswerChecked -> strings.checkAnswerButton
                                        isLastExercise -> strings.finishLessonButton
                                        else -> strings.continueButton
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
