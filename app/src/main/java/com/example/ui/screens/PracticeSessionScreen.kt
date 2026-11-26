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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
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
import com.example.data.local.PracticeMode
import com.example.ui.components.AudioSpeedControlsRow
import com.example.ui.components.OwlExpression
import com.example.ui.components.OwlMascot
import com.example.ui.components.StatBadgePill
import com.example.ui.theme.OwlAmber
import com.example.ui.theme.OwlEmerald
import com.example.viewmodel.PracticePhase
import com.example.viewmodel.PracticeSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSessionScreen(
    viewModel: PracticeSessionViewModel,
    language: AppLanguage,
    avatarVariant: Int,
    onBack: () -> Unit,
) {
    BackHandler { onBack() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current

    val title = when (uiState.mode) {
        PracticeMode.VOCAB_REVIEW -> strings.practiceVocabReview
        PracticeMode.MULTIPLE_CHOICE -> strings.practiceMultipleChoice
        PracticeMode.SENTENCE_COMPLETION -> strings.practiceSentenceCompletion
        PracticeMode.TRANSLATION -> strings.practiceTranslation
        PracticeMode.LISTENING -> strings.practiceListening
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("practice_back_button"),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.backButton)
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (val phase = uiState.phase) {
                PracticePhase.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                PracticePhase.Empty -> {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 520.dp)
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        OwlMascot(expression = OwlExpression.ENCOURAGING, size = 140.dp, avatarVariant = avatarVariant)
                        Spacer(Modifier.height(14.dp))
                        Text(strings.practiceEmptyTitle, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            strings.practiceEmptyBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = onBack) { Text(strings.backButton) }
                    }
                }

                PracticePhase.Flashcards -> {
                    val card = uiState.currentCard
                    val progress = (uiState.cardIndex + 1).toFloat() / uiState.vocabDeck.size.coerceAtLeast(1)
                    Column(
                        modifier = Modifier
                            .widthIn(max = 620.dp)
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50)),
                            strokeCap = StrokeCap.Round,
                        )

                        if (card != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(26.dp))
                                    .clickable { viewModel.flipCard() }
                                    .testTag("practice_flashcard"),
                                shape = RoundedCornerShape(26.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    OwlMascot(
                                        expression = if (uiState.isCardFlipped) OwlExpression.HAPPY else OwlExpression.THINKING,
                                        size = 90.dp,
                                        avatarVariant = avatarVariant,
                                    )
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = card.word,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = card.ipa,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    AudioSpeedControlsRow(
                                        onPlayNormal = { viewModel.speak(card.word, false) },
                                        onPlaySlow = { viewModel.speak(card.word, true) },
                                    )
                                    if (uiState.isCardFlipped) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = card.persianMeaning,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Text(
                                                text = card.exampleSentence,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                        Text(
                                            text = card.exampleTranslationFa,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                        )
                                    } else {
                                        Text(
                                            text = strings.flipCardHint,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.markCard(known = false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text(strings.needReviewButton)
                            }
                            Button(
                                onClick = { viewModel.markCard(known = true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("flashcard_know_it_button"),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text(strings.knowItButton)
                            }
                        }
                    }
                }

                PracticePhase.Quiz -> {
                    val exercise = uiState.currentExercise
                    val progress = (uiState.exerciseIndex + 1).toFloat() / uiState.exercises.size.coerceAtLeast(1)
                    Column(
                        modifier = Modifier
                            .widthIn(max = 620.dp)
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50)),
                            strokeCap = StrokeCap.Round,
                        )

                        if (exercise != null) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    OwlMascot(
                                        expression = when {
                                            !uiState.isAnswerChecked -> OwlExpression.THINKING
                                            uiState.isCurrentAnswerCorrect -> OwlExpression.CELEBRATING
                                            else -> OwlExpression.ENCOURAGING
                                        },
                                        size = 74.dp,
                                        avatarVariant = avatarVariant,
                                    )
                                    Text(
                                        text = if (language == AppLanguage.PERSIAN) exercise.promptFa else exercise.promptEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.weight(1f),
                                    )
                                }

                                if (exercise.targetAudioText.isNotBlank()) {
                                    AudioSpeedControlsRow(
                                        onPlayNormal = { viewModel.speak(exercise.targetAudioText, false) },
                                        onPlaySlow = { viewModel.speak(exercise.targetAudioText, true) },
                                    )
                                }

                                uiState.currentOptions().forEachIndexed { idx, opt ->
                                    val selected = uiState.selectedOptionIndex == idx
                                    val correctOpt = idx == exercise.correctIndex
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize()
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable(enabled = !uiState.isAnswerChecked) {
                                                viewModel.selectOption(idx)
                                            },
                                        shape = RoundedCornerShape(18.dp),
                                        color = when {
                                            uiState.isAnswerChecked && correctOpt -> MaterialTheme.colorScheme.tertiaryContainer
                                            uiState.isAnswerChecked && selected && !correctOpt -> MaterialTheme.colorScheme.errorContainer
                                            selected -> MaterialTheme.colorScheme.primaryContainer
                                            else -> MaterialTheme.colorScheme.surface
                                        },
                                        border = BorderStroke(
                                            width = if (selected) 2.dp else 1.dp,
                                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        ),
                                    ) {
                                        Text(
                                            text = opt,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(16.dp),
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = uiState.isAnswerChecked) {
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = if (uiState.isCurrentAnswerCorrect) {
                                            MaterialTheme.colorScheme.tertiaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.errorContainer
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.PERSIAN) exercise.explanationFa else exercise.explanationEn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(14.dp),
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (!uiState.isAnswerChecked) viewModel.checkAnswer() else viewModel.nextQuestion()
                            },
                            enabled = uiState.selectedOptionIndex != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(18.dp),
                        ) {
                            Text(
                                text = if (!uiState.isAnswerChecked) strings.checkAnswerButton else strings.continueButton,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }

                is PracticePhase.Completed -> {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 560.dp)
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        OwlMascot(
                            expression = OwlExpression.CELEBRATING,
                            size = 160.dp,
                            avatarVariant = avatarVariant,
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(strings.practiceCompleteTitle, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            strings.practiceCompleteSubtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            StatBadgePill(
                                icon = Icons.Filled.Star,
                                valueText = "+${formatNumber(phase.xpEarned, language)} ${strings.xpUnit}",
                                labelText = strings.xpEarnedLabel,
                                tint = OwlAmber,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                                modifier = Modifier.weight(1f),
                            )
                            StatBadgePill(
                                icon = Icons.Filled.CheckCircle,
                                valueText = "${formatNumber(phase.correctCount, language)}/${formatNumber(phase.totalCount, language)}",
                                labelText = strings.cardsReviewedLabel,
                                tint = OwlEmerald,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(18.dp),
                        ) {
                            Text(strings.backButton)
                        }
                    }
                }
            }
        }
    }
}
