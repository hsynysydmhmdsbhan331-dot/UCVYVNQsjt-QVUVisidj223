package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.LocalAppStrings
import com.example.data.local.PracticeMode
import com.example.ui.components.MascotSpeechBanner
import com.example.ui.components.OwlExpression

private data class PracticeCardItem(
    val mode: PracticeMode,
    val title: String,
    val description: String,
    val icon: ImageVector,
)

@Composable
fun PracticeScreen(
    avatarVariant: Int,
    onStartPractice: (PracticeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val items = listOf(
        PracticeCardItem(
            PracticeMode.VOCAB_REVIEW,
            strings.practiceVocabReview,
            strings.practiceVocabReviewDesc,
            Icons.Filled.Style,
        ),
        PracticeCardItem(
            PracticeMode.MULTIPLE_CHOICE,
            strings.practiceMultipleChoice,
            strings.practiceMultipleChoiceDesc,
            Icons.Filled.Quiz,
        ),
        PracticeCardItem(
            PracticeMode.SENTENCE_COMPLETION,
            strings.practiceSentenceCompletion,
            strings.practiceSentenceCompletionDesc,
            Icons.Filled.EditNote,
        ),
        PracticeCardItem(
            PracticeMode.TRANSLATION,
            strings.practiceTranslation,
            strings.practiceTranslationDesc,
            Icons.Filled.Translate,
        ),
        PracticeCardItem(
            PracticeMode.LISTENING,
            strings.practiceListening,
            strings.practiceListeningDesc,
            Icons.Filled.Hearing,
        ),
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.practiceHeaderTitle,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = strings.practiceHeaderSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                MascotSpeechBanner(
                    expression = OwlExpression.ENCOURAGING,
                    message = strings.practiceEmptyBody,
                    avatarVariant = avatarVariant,
                )
            }

            items(items, key = { it.mode.key }) { card ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { onStartPractice(card.mode) }
                        .testTag("practice_mode_${card.mode.key}"),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = card.icon,
                                contentDescription = card.title,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = card.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = card.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = card.title,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
