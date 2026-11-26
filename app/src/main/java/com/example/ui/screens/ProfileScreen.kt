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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.local.AchievementEntity
import com.example.data.prefs.UserPreferences
import com.example.data.repository.LearningOverview
import com.example.ui.components.MascotSpeechBanner
import com.example.ui.components.OwlExpression
import com.example.ui.components.OwlMascot
import com.example.ui.components.StatBadgePill
import com.example.ui.theme.OwlAmber
import com.example.ui.theme.OwlEmerald

@Composable
fun ProfileScreen(
    preferences: UserPreferences,
    overview: LearningOverview,
    onUpdateUsername: (String) -> Unit,
    onUpdateAvatar: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    val language = preferences.interfaceLanguage
    val displayName = preferences.username.ifBlank { strings.defaultLearnerName }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var draftName by remember(preferences.username) { mutableStateOf(preferences.username) }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(strings.editNameTitle) },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    placeholder = { Text(strings.namePlaceholder) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input"),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateUsername(draftName)
                        showEditNameDialog = false
                    },
                    modifier = Modifier.testTag("profile_name_save"),
                ) {
                    Text(strings.saveButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(strings.cancelButton)
                }
            },
        )
    }

    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            title = { Text(strings.chooseAvatarTitle) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    (0..3).forEach { variant ->
                        val selected = preferences.avatarId == variant
                        Surface(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onUpdateAvatar(variant)
                                    showAvatarPicker = false
                                }
                                .testTag("avatar_option_$variant"),
                            shape = CircleShape,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            border = BorderStroke(
                                width = if (selected) 2.5.dp else 1.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            ),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                OwlMascot(
                                    expression = OwlExpression.HAPPY,
                                    size = 54.dp,
                                    avatarVariant = variant,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPicker = false }) {
                    Text(strings.cancelButton)
                }
            },
        )
    }

    val unlockedCount = overview.achievements.count { it.isUnlocked }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.profileTitle,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    OutlinedButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("profile_settings_button"),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = strings.settingsButton,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(strings.settingsButton)
                    }
                }
            }

            // Profile Identity Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape)
                                .clickable { showAvatarPicker = true }
                                .testTag("profile_avatar_button"),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                OwlMascot(
                                    expression = OwlExpression.HAPPY,
                                    size = 70.dp,
                                    avatarVariant = preferences.avatarId,
                                    contentDescriptionText = strings.chooseAvatarTitle,
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                FilledTonalIconButton(
                                    onClick = {
                                        draftName = preferences.username
                                        showEditNameDialog = true
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("profile_edit_name_button"),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = strings.editNameTitle,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                            Text(
                                text = strings.learningLanguageEnglishUs,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            // 4 Core Profile Stats
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        StatBadgePill(
                            icon = Icons.Filled.School,
                            valueText = overview.currentCefrLevel,
                            labelText = strings.currentLevelLabel,
                            tint = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f),
                        )
                        StatBadgePill(
                            icon = Icons.Filled.Star,
                            valueText = "${formatNumber(overview.totalXp, language)} ${strings.xpUnit}",
                            labelText = strings.totalXpLabel,
                            tint = OwlAmber,
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f),
                        )
                    }
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
                            icon = Icons.Filled.CheckCircle,
                            valueText = "${formatNumber(overview.completedLessonCount, language)}/${formatNumber(overview.totalStarterLessons, language)}",
                            labelText = strings.completedLessonsStat,
                            tint = OwlEmerald,
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Achievements Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.achievementsTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "${formatNumber(unlockedCount, language)}/${formatNumber(overview.achievements.size, language)} ${strings.unlockedBadgesCount}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            if (unlockedCount == 0) {
                item {
                    MascotSpeechBanner(
                        expression = OwlExpression.ENCOURAGING,
                        message = "${strings.achievementsEmptyTitle} — ${strings.achievementsEmptyBody}",
                        avatarVariant = preferences.avatarId,
                    )
                }
            }

            items(overview.achievements, key = { it.id }) { badge ->
                AchievementRowCard(badge = badge, language = language)
            }
        }
    }
}

@Composable
private fun AchievementRowCard(
    badge: AchievementEntity,
    language: AppLanguage,
) {
    val icon: ImageVector = when (badge.iconKey) {
        "star" -> Icons.Filled.Star
        "bolt" -> Icons.Filled.Bolt
        "medal" -> Icons.Filled.MilitaryTech
        "flame" -> Icons.Filled.LocalFireDepartment
        "trophy" -> Icons.Filled.EmojiEvents
        else -> Icons.Filled.WorkspacePremium
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (badge.isUnlocked) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (badge.isUnlocked) OwlAmber.copy(alpha = 0.65f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) {
                            OwlAmber.copy(alpha = 0.18f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (badge.isUnlocked) icon else Icons.Filled.Lock,
                    contentDescription = if (language == AppLanguage.PERSIAN) badge.titleFa else badge.titleEn,
                    tint = if (badge.isUnlocked) OwlAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (language == AppLanguage.PERSIAN) badge.titleFa else badge.titleEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (language == AppLanguage.PERSIAN) badge.descriptionFa else badge.descriptionEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
