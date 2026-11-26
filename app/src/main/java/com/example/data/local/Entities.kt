package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExerciseType(val key: String) {
    MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    SENTENCE_COMPLETION("SENTENCE_COMPLETION"),
    TRANSLATION("TRANSLATION"),
    LISTENING("LISTENING");

    companion object {
        fun fromKey(key: String): ExerciseType =
            entries.firstOrNull { it.key == key } ?: MULTIPLE_CHOICE
    }
}

enum class SkillCategory(val key: String) {
    ALPHABET("ALPHABET"),
    VOCABULARY("VOCABULARY"),
    GRAMMAR("GRAMMAR"),
    PRONUNCIATION("PRONUNCIATION"),
    READING("READING"),
    WRITING("WRITING"),
    LISTENING("LISTENING"),
    CONVERSATION("CONVERSATION");

    companion object {
        fun fromKey(key: String): SkillCategory =
            entries.firstOrNull { it.key == key } ?: VOCABULARY
    }
}

enum class PracticeMode(val key: String) {
    VOCAB_REVIEW("VOCAB_REVIEW"),
    MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    SENTENCE_COMPLETION("SENTENCE_COMPLETION"),
    TRANSLATION("TRANSLATION"),
    LISTENING("LISTENING");

    companion object {
        fun fromKey(key: String): PracticeMode =
            entries.firstOrNull { it.key == key } ?: VOCAB_REVIEW
    }
}

@Entity(tableName = "learning_languages")
data class LearningLanguageEntity(
    @PrimaryKey val code: String,
    val nativeName: String,
    val englishName: String,
    val persianName: String,
    val isAvailable: Boolean,
)

@Entity(tableName = "cefr_levels")
data class CefrLevelEntity(
    @PrimaryKey val code: String,
    val orderIndex: Int,
    val titleEn: String,
    val titleFa: String,
    val subtitleEn: String,
    val subtitleFa: String,
    val isAvailable: Boolean,
)

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val cefrCode: String,
    val orderIndex: Int,
    val titleEn: String,
    val titleFa: String,
    val subtitleEn: String,
    val subtitleFa: String,
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val unitId: String,
    val languageCode: String,
    val cefrCode: String,
    val orderIndex: Int,
    val titleEn: String,
    val titleFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val xpReward: Int,
    val focusSkill: String,
)

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val languageCode: String,
    val word: String,
    val ipa: String,
    val persianMeaning: String,
    val exampleSentence: String,
    val exampleTranslationFa: String,
    val category: String,
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val languageCode: String,
    val orderIndex: Int,
    val type: String,
    val promptEn: String,
    val promptFa: String,
    val targetAudioText: String,
    val optionsJson: String,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationFa: String,
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val isCompleted: Boolean,
    val bestScorePercent: Int,
    val attemptsCount: Int,
    val lastCompletedAt: Long,
)

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val dateKey: String,
    val xpEarned: Int,
    val lessonsCompleted: Int,
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val iconKey: String,
    val xpThreshold: Int,
    val lessonsThreshold: Int,
    val streakThreshold: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
)
