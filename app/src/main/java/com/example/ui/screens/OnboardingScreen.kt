package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalAppStrings
import com.example.core.i18n.formatNumber
import com.example.ui.components.OwlExpression
import com.example.ui.components.OwlMascot

@Composable
fun OnboardingScreen(
    currentLanguage: AppLanguage,
    currentDailyGoal: Int,
    onSelectLanguage: (AppLanguage) -> Unit,
    onCompleteOnboarding: (AppLanguage, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalAppStrings.current
    var step by rememberSaveable { mutableIntStateOf(0) }
    var selectedGoal by rememberSaveable { mutableIntStateOf(currentDailyGoal) }

    BackHandler(enabled = step > 0) {
        step -= 1
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        MaterialTheme.colorScheme.background,
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 580.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top progress & back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (step > 0) {
                    IconButton(
                        onClick = { step -= 1 },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("onboarding_back_button"),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backButton,
                        )
                    }
                } else {
                    Spacer(Modifier.height(48.dp))
                }
                LinearProgressIndicator(
                    progress = { (step + 1) / 4f },
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(50)),
                )
            }

            // Step content
            AnimatedContent(
                targetState = step,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                label = "onboarding_step",
            ) { currentStep ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    when (currentStep) {
                        0 -> {
                            OwlMascot(
                                expression = OwlExpression.HAPPY,
                                size = 180.dp,
                                contentDescriptionText = strings.mascotName,
                            )
                            Text(
                                text = strings.appName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = strings.onboardingWelcomeTitle,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = strings.onboardingWelcomeSubtitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }

                        1 -> {
                            OwlMascot(
                                expression = OwlExpression.ENCOURAGING,
                                size = 160.dp,
                                contentDescriptionText = strings.mascotName,
                            )
                            Text(
                                text = strings.onboardingIntroTitle,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                            )
                            Card(
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                            ) {
                                Text(
                                    text = strings.onboardingIntroBody,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(20.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        2 -> {
                            OwlMascot(
                                expression = OwlExpression.THINKING,
                                size = 136.dp,
                                contentDescriptionText = strings.mascotName,
                            )
                            Text(
                                text = strings.onboardingLanguageTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = strings.onboardingLanguageSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(4.dp))
                            OnboardingSelectionCard(
                                title = strings.languagePersianLabel,
                                subtitle = strings.languagePersianDesc,
                                selected = currentLanguage == AppLanguage.PERSIAN,
                                onClick = { onSelectLanguage(AppLanguage.PERSIAN) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Language,
                                        contentDescription = strings.languagePersianLabel,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                },
                                modifier = Modifier.testTag("onboarding_lang_fa"),
                            )
                            OnboardingSelectionCard(
                                title = strings.languageEnglishLabel,
                                subtitle = strings.languageEnglishDesc,
                                selected = currentLanguage == AppLanguage.ENGLISH,
                                onClick = { onSelectLanguage(AppLanguage.ENGLISH) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Language,
                                        contentDescription = strings.languageEnglishLabel,
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                },
                                modifier = Modifier.testTag("onboarding_lang_en"),
                            )
                        }

                        else -> {
                            OwlMascot(
                                expression = OwlExpression.CELEBRATING,
                                size = 136.dp,
                                contentDescriptionText = strings.mascotName,
                            )
                            Text(
                                text = strings.onboardingGoalTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = strings.onboardingGoalSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            val goalOptions = listOf(
                                20 to strings.goalCasualLabel,
                                50 to strings.goalSteadyLabel,
                                100 to strings.goalFocusedLabel,
                            )
                            goalOptions.forEach { (xp, title) ->
                                OnboardingSelectionCard(
                                    title = "$title — ${formatNumber(xp, currentLanguage)} ${strings.xpPerDaySuffix}",
                                    subtitle = strings.activeLanguageBadge,
                                    selected = selectedGoal == xp,
                                    onClick = { selectedGoal = xp },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Flag,
                                            contentDescription = title,
                                            tint = MaterialTheme.colorScheme.secondary,
                                        )
                                    },
                                    modifier = Modifier.testTag("onboarding_goal_$xp"),
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (step < 3) {
                        step += 1
                    } else {
                        onCompleteOnboarding(currentLanguage, selectedGoal)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag(if (step < 3) "onboarding_next_button" else "onboarding_start_button"),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    text = if (step < 3) strings.nextButton else strings.startLearningButton,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun OnboardingSelectionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            icon()
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
