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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalAppStrings
import com.example.core.i18n.formatNumber
import com.example.data.prefs.UserPreferences
import com.example.data.repository.LearningOverview
import com.example.data.repository.LessonWithState
import com.example.ui.components.MascotSpeechBanner
import com.example.ui.components.OwlExpression
import com.example.ui.components.OwlMascot
import com.example.ui.components.StatBadgePill
import com.example.ui.components.TopHeaderMetricsRow
import com.example.ui.theme.OwlAmber
import com.example.ui.theme.OwlEmerald

@Composable
fun HomeScreen(
    preferences: UserPreferences,
    overview: LearningOverview,
    onOpenLesson: (String) -> Unit,
    onOpenLearnTab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val language = preferences.interfaceLanguage
    val displayName = preferences.username.ifBlank { strings.defaultLearnerName }
    val goalProgress = (overview.todayXp.toFloat() / preferences.dailyXpGoal.coerceAtLeast(1).toFloat())
        .coerceIn(0f, 1f)
    val overallProgress = if (overview.totalStarterLessons > 0) {
        overview.completedLessonCount.toFloat() / overview.totalStarterLessons.toFloat()
    } else {
        0f
    }

    val recommendedLessons = overview.lessons
        .filter { it.isUnlocked }
        .sortedBy { it.isCompleted }
        .take(3)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Greeting Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (overview.todayXp == 0) strings.homeGreetingMorning else strings.homeGreetingDefault,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    TopHeaderMetricsRow(
                        streak = overview.currentStreak,
                        totalXp = overview.totalXp,
                        language = language,
                    )
                }
            }

            // Hero Continue Learning Card with Oli
            item {
                val nextLesson = overview.nextRecommendedLesson
                val heroTitle = nextLesson?.let {
                    if (language == AppLanguage.PERSIAN) it.lesson.titleFa else it.lesson.titleEn
                } ?: strings.appName
                val heroSubtitle = nextLesson?.let {
                    if (language == AppLanguage.PERSIAN) it.lesson.descriptionFa else it.lesson.descriptionEn
                } ?: strings.mascotTipHome

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.82f),
                                )
                            )
                        )
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f),
                                ) {
                                    Text(
                                        text = "${strings.currentLevelLabel}: ${overview.currentCefrLevel}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = heroTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = heroSubtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                )
                            }
                            OwlMascot(
                                expression = if (overview.todayXp >= preferences.dailyXpGoal) {
                                    OwlExpression.CELEBRATING
                                } else {
                                    OwlExpression.HAPPY
                                },
                                size = 92.dp,
                                avatarVariant = preferences.avatarId,
                                contentDescriptionText = strings.mascotName,
                            )
                        }

                        Button(
                            onClick = {
                                if (nextLesson != null) {
                                    onOpenLesson(nextLesson.lesson.id)
                                } else {
                                    onOpenLearnTab()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("home_continue_learning_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = strings.continueLearningCta,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = when {
                                    overview.completedLessonCount == 0 -> strings.startFirstLessonCta
                                    overview.completedLessonCount >= overview.totalStarterLessons -> strings.reviewCompletedCta
                                    else -> strings.continueLearningCta
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }

            // Daily Goal + Streak + Level Row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                progress = { goalProgress },
                                modifier = Modifier.size(72.dp),
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                color = if (goalProgress >= 1f) OwlEmerald else MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "${formatNumber((goalProgress * 100).toInt(), language)}%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.dailyGoalTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = if (overview.todayXp >= preferences.dailyXpGoal) {
                                    strings.dailyGoalCompleted
                                } else {
                                    "${formatNumber(overview.todayXp, language)} / ${formatNumber(preferences.dailyXpGoal, language)} ${strings.xpUnit}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    StatBadgePill(
                        icon = Icons.Filled.LocalFireDepartment,
                        valueText = "${formatNumber(overview.currentStreak, language)} ${strings.daysUnit}",
                        labelText = strings.dailyStreakLabel,
                        tint = OwlAmber,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                    )
                    StatBadgePill(
                        icon = Icons.Filled.School,
                        valueText = overview.currentCefrLevel,
                        labelText = strings.currentLevelLabel,
                        tint = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Overall Learning Progress Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = strings.overallProgressTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${formatNumber(overview.completedLessonCount, language)} / ${formatNumber(overview.totalStarterLessons, language)} ${strings.lessonsCompletedOf}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { overallProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50)),
                            strokeCap = StrokeCap.Round,
                        )
                    }
                }
            }

            // Mascot Tip Banner
            item {
                MascotSpeechBanner(
                    expression = OwlExpression.ENCOURAGING,
                    message = strings.mascotTipHome,
                    avatarVariant = preferences.avatarId,
                )
            }

            // Recommended Lessons
            item {
                Text(
                    text = strings.recommendedLessonsTitle,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            items(recommendedLessons, key = { it.lesson.id }) { item ->
                RecommendedLessonCard(
                    lessonState = item,
                    language = language,
                    onClick = { onOpenLesson(item.lesson.id) },
                )
            }
        }
    }
}

@Composable
private fun RecommendedLessonCard(
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
            .clickable(onClick = onClick)
            .testTag("recommended_lesson_${lessonState.lesson.id}"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (lessonState.isCompleted) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (lessonState.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.AutoAwesome,
                    contentDescription = title,
                    tint = if (lessonState.isCompleted) OwlEmerald else MaterialTheme.colorScheme.primary,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = OwlAmber,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "+${formatNumber(lessonState.lesson.xpReward, language)} ${strings.xpUnit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
