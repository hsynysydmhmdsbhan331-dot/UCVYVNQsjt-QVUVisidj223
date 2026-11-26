package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalAppStrings
import com.example.core.i18n.formatNumber
import com.example.data.local.CefrLevelEntity
import com.example.data.local.SkillCategory
import com.example.data.prefs.UserPreferences
import com.example.data.repository.LearningOverview
import com.example.data.repository.LessonWithState
import com.example.ui.theme.OwlAmber
import com.example.ui.theme.OwlEmerald

private data class SkillCardSpec(
    val category: SkillCategory,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
)

@Composable
fun LearnScreen(
    preferences: UserPreferences,
    overview: LearningOverview,
    onOpenLesson: (String) -> Unit,
    onOpenSkillModule: (SkillCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val language = preferences.interfaceLanguage
    var selectedSubTab by rememberSaveable { mutableIntStateOf(0) }

    val skillSpecs = listOf(
        SkillCardSpec(SkillCategory.ALPHABET, strings.skillAlphabet, strings.skillAlphabetDesc, Icons.Filled.Abc),
        SkillCardSpec(SkillCategory.VOCABULARY, strings.skillVocabulary, strings.skillVocabularyDesc, Icons.Filled.Translate),
        SkillCardSpec(SkillCategory.GRAMMAR, strings.skillGrammar, strings.skillGrammarDesc, Icons.Filled.Spellcheck),
        SkillCardSpec(SkillCategory.PRONUNCIATION, strings.skillPronunciation, strings.skillPronunciationDesc, Icons.Filled.RecordVoiceOver),
        SkillCardSpec(SkillCategory.READING, strings.skillReading, strings.skillReadingDesc, Icons.AutoMirrored.Filled.MenuBook),
        SkillCardSpec(SkillCategory.WRITING, strings.skillWriting, strings.skillWritingDesc, Icons.Filled.EditNote),
        SkillCardSpec(SkillCategory.LISTENING, strings.skillListening, strings.skillListeningDesc, Icons.Filled.Hearing),
        SkillCardSpec(SkillCategory.CONVERSATION, strings.skillConversation, strings.skillConversationDesc, Icons.Filled.Forum),
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.learnHeaderTitle,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = strings.activeLanguageBadge,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SegmentedButton(
                            selected = selectedSubTab == 0,
                            onClick = { selectedSubTab = 0 },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("learn_tab_path"),
                        ) {
                            Text(strings.learnPathTab)
                        }
                        SegmentedButton(
                            selected = selectedSubTab == 1,
                            onClick = { selectedSubTab = 1 },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("learn_tab_skills"),
                        ) {
                            Text(strings.learnSkillsTab)
                        }
                    }
                }
            }

            if (selectedSubTab == 0) {
                // Units & 6 Progressive A1 Lessons
                overview.units.forEach { unit ->
                    val unitLessons = overview.lessons.filter { it.lesson.unitId == unit.id }
                    item(key = "unit_${unit.id}") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            ),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (language == AppLanguage.PERSIAN) unit.titleFa else unit.titleEn,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (language == AppLanguage.PERSIAN) unit.subtitleFa else unit.subtitleEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    items(unitLessons, key = { it.lesson.id }) { lessonState ->
                        PathLessonRow(
                            lessonState = lessonState,
                            language = language,
                            onClick = {
                                if (lessonState.isUnlocked) {
                                    onOpenLesson(lessonState.lesson.id)
                                }
                            },
                        )
                    }
                }

                // CEFR Levels A1 - C2 Roadmap
                item {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = strings.cefrLevelsTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = strings.comingSoonTierBody,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                items(overview.cefrLevels, key = { it.code }) { level ->
                    CefrTierCard(level = level, language = language)
                }
            } else {
                // 8 Core Skill Modules (2-column pairs)
                val rows = skillSpecs.chunked(2)
                items(rows.size) { rowIdx ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rows[rowIdx].forEach { spec ->
                            SkillModuleCard(
                                spec = spec,
                                onClick = { onOpenSkillModule(spec.category) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rows[rowIdx].size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PathLessonRow(
    lessonState: LessonWithState,
    language: AppLanguage,
    onClick: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val title = if (language == AppLanguage.PERSIAN) lessonState.lesson.titleFa else lessonState.lesson.titleEn
    val desc = if (language == AppLanguage.PERSIAN) lessonState.lesson.descriptionFa else lessonState.lesson.descriptionEn

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = lessonState.isUnlocked, onClick = onClick)
            .testTag("lesson_card_${lessonState.lesson.id}"),
        shape = RoundedCornerShape(20.dp),
        color = if (lessonState.isUnlocked) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        },
        border = BorderStroke(
            width = if (lessonState.isUnlocked && !lessonState.isCompleted) 1.5.dp else 1.dp,
            color = when {
                lessonState.isCompleted -> OwlEmerald.copy(alpha = 0.6f)
                lessonState.isUnlocked -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            lessonState.isCompleted -> OwlEmerald.copy(alpha = 0.16f)
                            lessonState.isUnlocked -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when {
                        lessonState.isCompleted -> Icons.Filled.CheckCircle
                        lessonState.isUnlocked -> Icons.Filled.PlayArrow
                        else -> Icons.Filled.Lock
                    },
                    contentDescription = title,
                    tint = when {
                        lessonState.isCompleted -> OwlEmerald
                        lessonState.isUnlocked -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (lessonState.isUnlocked) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Text(
                    text = if (lessonState.isUnlocked) desc else strings.completePreviousHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                    ) {
                        Text(
                            text = "+${formatNumber(lessonState.lesson.xpReward, language)} ${strings.xpUnit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                    if (lessonState.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                        ) {
                            Text(
                                text = "${strings.completedLabel} • ${formatNumber(lessonState.bestScorePercent, language)}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CefrTierCard(
    level: CefrLevelEntity,
    language: AppLanguage,
) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (level.isAvailable) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (level.isAvailable) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = level.code,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (level.isAvailable) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (language == AppLanguage.PERSIAN) level.titleFa else level.titleEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (language == AppLanguage.PERSIAN) level.subtitleFa else level.subtitleEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = if (level.isAvailable) strings.unlockedLabel else strings.comingSoonTierLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (level.isAvailable) OwlAmber else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SkillModuleCard(
    spec: SkillCardSpec,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("skill_card_${spec.category.key}"),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = spec.icon,
                    contentDescription = spec.title,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = spec.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = spec.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
            )
        }
    }
}
