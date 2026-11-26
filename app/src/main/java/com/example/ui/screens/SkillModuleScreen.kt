package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalAppStrings
import com.example.data.local.SkillCategory
import com.example.data.local.SkillModulesContent
import com.example.data.local.VocabularyEntity
import com.example.ui.components.AudioSpeedControlsRow
import com.example.ui.components.MascotSpeechBanner
import com.example.ui.components.OwlExpression
import com.example.ui.theme.OwlEmerald

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SkillModuleScreen(
    category: SkillCategory,
    language: AppLanguage,
    vocabulary: List<VocabularyEntity>,
    onSpeak: (String, Boolean) -> Unit,
    onPlayCorrect: () -> Unit,
    onPlayIncorrect: () -> Unit,
    onBack: () -> Unit,
) {
    BackHandler { onBack() }
    val strings = LocalAppStrings.current

    val title = when (category) {
        SkillCategory.ALPHABET -> strings.skillAlphabet
        SkillCategory.VOCABULARY -> strings.skillVocabulary
        SkillCategory.GRAMMAR -> strings.skillGrammar
        SkillCategory.PRONUNCIATION -> strings.skillPronunciation
        SkillCategory.READING -> strings.skillReading
        SkillCategory.WRITING -> strings.skillWriting
        SkillCategory.LISTENING -> strings.skillListening
        SkillCategory.CONVERSATION -> strings.skillConversation
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
                            .testTag("skill_back_button"),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backButton,
                        )
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
            when (category) {
                SkillCategory.ALPHABET -> {
                    var selectedLetter by remember { mutableStateOf(SkillModulesContent.alphabet.first()) }
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                ),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = "${selectedLetter.uppercase} ${selectedLetter.lowercase}",
                                            style = MaterialTheme.typography.displaySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = "${selectedLetter.sampleWord} — ${selectedLetter.sampleMeaningFa} (${selectedLetter.phoneticHintFa})",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }
                                    AudioSpeedControlsRow(
                                        onPlayNormal = { onSpeak("${selectedLetter.uppercase}. ${selectedLetter.sampleWord}", false) },
                                        onPlaySlow = { onSpeak("${selectedLetter.uppercase}. ${selectedLetter.sampleWord}", true) },
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = strings.tapLetterHint,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        item {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    SkillModulesContent.alphabet.forEach { item ->
                                        val isSelected = item.uppercase == selectedLetter.uppercase
                                        Surface(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    selectedLetter = item
                                                    onSpeak("${item.uppercase}. ${item.sampleWord}", false)
                                                }
                                                .testTag("alphabet_${item.uppercase}"),
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.surface
                                            },
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${item.uppercase}${item.lowercase}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurface
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SkillCategory.VOCABULARY, SkillCategory.PRONUNCIATION -> {
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            MascotSpeechBanner(
                                expression = OwlExpression.HAPPY,
                                message = strings.tapWordToListen,
                            )
                        }
                        items(vocabulary, key = { it.id }) { word ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Column {
                                                Text(
                                                    text = word.word,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = MaterialTheme.colorScheme.primary,
                                                )
                                                Text(
                                                    text = word.ipa,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                        }
                                        Text(
                                            text = word.persianMeaning,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = word.exampleSentence,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                    Text(
                                        text = word.exampleTranslationFa,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    AudioSpeedControlsRow(
                                        onPlayNormal = { onSpeak("${word.word}. ${word.exampleSentence}", false) },
                                        onPlaySlow = { onSpeak("${word.word}. ${word.exampleSentence}", true) },
                                    )
                                }
                            }
                        }
                    }
                }

                SkillCategory.GRAMMAR -> {
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(SkillModulesContent.grammarCards) { card ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.PERSIAN) card.titleFa else card.titleEn,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = if (language == AppLanguage.PERSIAN) card.ruleFa else card.ruleEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                Text(
                                                    text = card.exampleEn,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = card.exampleFa,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                    AudioSpeedControlsRow(
                                        onPlayNormal = { onSpeak(card.exampleEn, false) },
                                        onPlaySlow = { onSpeak(card.exampleEn, true) },
                                    )
                                }
                            }
                        }
                    }
                }

                SkillCategory.READING -> {
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(SkillModulesContent.readingStories, key = { it.id }) { story ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.PERSIAN) story.titleFa else story.titleEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    story.lines.forEach { (en, fa) ->
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { onSpeak(en, false) },
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                    Text(
                                                        text = en,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.SemiBold,
                                                    )
                                                }
                                                Text(
                                                    text = fa,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                        }
                                    }
                                    AudioSpeedControlsRow(
                                        onPlayNormal = { onSpeak(story.lines.joinToString(" ") { it.first }, false) },
                                        onPlaySlow = { onSpeak(story.lines.joinToString(" ") { it.first }, true) },
                                    )
                                }
                            }
                        }
                    }
                }

                SkillCategory.WRITING -> {
                    val prompts = SkillModulesContent.writingPrompts
                    var index by remember { mutableIntStateOf(0) }
                    val currentPrompt = prompts[index]
                    val shuffledWords = remember(currentPrompt.id) { currentPrompt.targetWords.shuffled() }
                    val chosenIndices = remember(currentPrompt.id) { mutableStateListOf<Int>() }
                    var checkedCorrect by remember(currentPrompt.id) { mutableStateOf<Boolean?>(null) }

                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            MascotSpeechBanner(
                                expression = when (checkedCorrect) {
                                    true -> OwlExpression.CELEBRATING
                                    false -> OwlExpression.ENCOURAGING
                                    null -> OwlExpression.THINKING
                                },
                                message = if (language == AppLanguage.PERSIAN) currentPrompt.promptFa else currentPrompt.promptEn,
                            )
                        }

                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(84.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            ) {
                                Box(
                                    modifier = Modifier.padding(14.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    val built = chosenIndices.joinToString(" ") { shuffledWords[it] }
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            text = built.ifBlank { strings.arrangeWordsPrompt },
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            color = if (built.isBlank()) {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    shuffledWords.forEachIndexed { idx, word ->
                                        val isUsed = chosenIndices.contains(idx)
                                        FilledTonalButton(
                                            onClick = {
                                                if (!isUsed) {
                                                    chosenIndices.add(idx)
                                                    checkedCorrect = null
                                                }
                                            },
                                            enabled = !isUsed,
                                            modifier = Modifier.minimumInteractiveComponentSize(),
                                        ) {
                                            Text(word)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        chosenIndices.clear()
                                        checkedCorrect = null
                                    },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(Icons.Filled.Refresh, contentDescription = strings.clearSelection)
                                    Spacer(Modifier.size(6.dp))
                                    Text(strings.clearSelection)
                                }
                                Button(
                                    onClick = {
                                        val built = chosenIndices.map { shuffledWords[it] }
                                        val ok = built == currentPrompt.targetWords
                                        checkedCorrect = ok
                                        if (ok) {
                                            onPlayCorrect()
                                            onSpeak(currentPrompt.targetWords.joinToString(" "), false)
                                        } else {
                                            onPlayIncorrect()
                                        }
                                    },
                                    enabled = chosenIndices.size == shuffledWords.size,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(strings.checkAnswerButton)
                                }
                            }
                        }

                        if (checkedCorrect != null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (checkedCorrect == true) {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.errorContainer
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = if (checkedCorrect == true) strings.correctBannerTitle else strings.incorrectBannerTitle,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Text(
                                                text = currentPrompt.targetWords.joinToString(" "),
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                OutlinedButton(
                                    onClick = { if (index > 0) index-- },
                                    enabled = index > 0,
                                ) {
                                    Text(strings.previousButton)
                                }
                                Button(
                                    onClick = { if (index + 1 < prompts.size) index++ else index = 0 },
                                ) {
                                    Text(strings.flipNextCard)
                                }
                            }
                        }
                    }
                }

                SkillCategory.LISTENING -> {
                    val itemsList = vocabulary.take(10)
                    var idx by remember { mutableIntStateOf(0) }
                    val current = itemsList.getOrNull(idx)
                    var revealed by remember(idx) { mutableStateOf(false) }

                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (current != null) {
                            item {
                                MascotSpeechBanner(
                                    expression = OwlExpression.THINKING,
                                    message = strings.skillListeningDesc,
                                )
                            }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(22.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(14.dp),
                                    ) {
                                        AudioSpeedControlsRow(
                                            onPlayNormal = { onSpeak(current.exampleSentence, false) },
                                            onPlaySlow = { onSpeak(current.exampleSentence, true) },
                                        )
                                        if (revealed) {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                Text(
                                                    text = current.exampleSentence,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    textAlign = TextAlign.Center,
                                                )
                                            }
                                            Text(
                                                text = current.exampleTranslationFa,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            OutlinedButton(onClick = { revealed = !revealed }) {
                                                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                                Spacer(Modifier.size(6.dp))
                                                Text(current.word)
                                            }
                                            Button(onClick = { idx = (idx + 1) % itemsList.size }) {
                                                Text(strings.flipNextCard)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SkillCategory.CONVERSATION -> {
                    LazyColumn(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(SkillModulesContent.conversations, key = { it.id }) { conv ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.PERSIAN) conv.titleFa else conv.titleEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    conv.lines.forEach { line ->
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (line.isSpeakerA) {
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                            } else {
                                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { onSpeak(line.english, false) },
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = if (line.isSpeakerA) strings.dialogueSpeakerA else strings.dialogueSpeakerB,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold,
                                                    )
                                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                        Text(
                                                            text = line.english,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.SemiBold,
                                                        )
                                                    }
                                                    Text(
                                                        text = line.persian,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                }
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = strings.listenNormal,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
