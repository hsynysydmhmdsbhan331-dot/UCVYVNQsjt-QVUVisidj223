package com.example.data.repository

import com.example.data.local.AchievementEntity
import com.example.data.local.CefrLevelEntity
import com.example.data.local.CurriculumSeedData
import com.example.data.local.DailyActivityEntity
import com.example.data.local.ExerciseEntity
import com.example.data.local.LearningLanguageEntity
import com.example.data.local.LessonEntity
import com.example.data.local.LessonProgressEntity
import com.example.data.local.ProjectOwlDao
import com.example.data.local.UnitEntity
import com.example.data.local.VocabularyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class LessonWithState(
    val lesson: LessonEntity,
    val isCompleted: Boolean,
    val isUnlocked: Boolean,
    val bestScorePercent: Int,
)

data class LearningOverview(
    val activeLanguage: LearningLanguageEntity?,
    val cefrLevels: List<CefrLevelEntity>,
    val units: List<UnitEntity>,
    val lessons: List<LessonWithState>,
    val totalXp: Int,
    val todayXp: Int,
    val currentStreak: Int,
    val completedLessonCount: Int,
    val totalStarterLessons: Int,
    val currentCefrLevel: String,
    val achievements: List<AchievementEntity>,
    val nextRecommendedLesson: LessonWithState?,
)

data class LessonCompletionResult(
    val xpAwarded: Int,
    val accuracyPercent: Int,
    val newlyUnlockedAchievements: List<AchievementEntity>,
)

class LearningRepository(private val dao: ProjectOwlDao) {

    private val seedMutex = Mutex()
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            if (dao.getLessonCount() == 0) {
                dao.insertLanguages(CurriculumSeedData.languages)
                dao.insertCefrLevels(CurriculumSeedData.cefrLevels)
                dao.insertUnits(CurriculumSeedData.units)
                dao.insertLessons(CurriculumSeedData.lessons)
                dao.insertVocabulary(CurriculumSeedData.vocabulary)
                dao.insertExercises(CurriculumSeedData.exercises)
                dao.insertAchievementsIgnore(CurriculumSeedData.achievements)
            } else {
                // Ensure achievements catalog exists even after any future migration
                dao.insertAchievementsIgnore(CurriculumSeedData.achievements)
            }
        }
    }

    fun observeOverview(languageCode: String): Flow<LearningOverview> {
        val baseFlow = combine(
            dao.observeLanguages(),
            dao.observeCefrLevels(),
            dao.observeUnits(languageCode),
            dao.observeLessons(languageCode),
            dao.observeLessonProgress(),
        ) { languages, levels, units, lessons, progressList ->
            Tuple5(languages, levels, units, lessons, progressList)
        }

        return combine(
            baseFlow,
            dao.observeDailyActivity(),
            dao.observeAchievements(),
        ) { base, activities, achievements ->
            val progressMap = base.progressList.associateBy { it.lessonId }
            val sortedLessons = base.lessons.sortedBy { it.orderIndex }

            var previousCompleted = true
            val lessonsWithState = sortedLessons.mapIndexed { index, lesson ->
                val progress = progressMap[lesson.id]
                val completed = progress?.isCompleted == true
                val unlocked = index == 0 || previousCompleted || completed
                previousCompleted = completed
                LessonWithState(
                    lesson = lesson,
                    isCompleted = completed,
                    isUnlocked = unlocked,
                    bestScorePercent = progress?.bestScorePercent ?: 0,
                )
            }

            val todayKey = LocalDate.now().format(dateFormatter)
            val todayXp = activities.firstOrNull { it.dateKey == todayKey }?.xpEarned ?: 0
            val totalXp = activities.sumOf { it.xpEarned }
            val completedCount = lessonsWithState.count { it.isCompleted }
            val streak = calculateStreak(activities, LocalDate.now())
            val nextLesson = lessonsWithState.firstOrNull { !it.isCompleted && it.isUnlocked }
                ?: lessonsWithState.firstOrNull { it.isUnlocked }

            LearningOverview(
                activeLanguage = base.languages.firstOrNull { it.code == languageCode },
                cefrLevels = base.levels,
                units = base.units,
                lessons = lessonsWithState,
                totalXp = totalXp,
                todayXp = todayXp,
                currentStreak = streak,
                completedLessonCount = completedCount,
                totalStarterLessons = lessonsWithState.size,
                currentCefrLevel = "A1",
                achievements = achievements,
                nextRecommendedLesson = nextLesson,
            )
        }
    }

    fun observeAllVocabulary(languageCode: String): Flow<List<VocabularyEntity>> =
        dao.observeAllVocabulary(languageCode)

    fun observeAllExercises(languageCode: String): Flow<List<ExerciseEntity>> =
        dao.observeAllExercises(languageCode)

    suspend fun getLessonDetail(lessonId: String): Triple<LessonEntity?, List<VocabularyEntity>, List<ExerciseEntity>> {
        ensureSeeded()
        val lesson = dao.getLessonById(lessonId)
        val vocab = dao.getVocabularyForLesson(lessonId)
        val exercises = dao.getExercisesForLesson(lessonId)
        return Triple(lesson, vocab, exercises)
    }

    suspend fun getPracticeExercises(languageCode: String, type: String): List<ExerciseEntity> {
        ensureSeeded()
        return dao.getExercisesByType(languageCode, type)
    }

    suspend fun recordLessonCompletion(
        lessonId: String,
        correctCount: Int,
        totalQuestions: Int,
        todayDate: LocalDate = LocalDate.now(),
    ): LessonCompletionResult {
        ensureSeeded()
        val lesson = dao.getLessonById(lessonId)
        val existingProgress = dao.getLessonProgress(lessonId)
        val accuracyPercent = if (totalQuestions > 0) {
            ((correctCount.toFloat() / totalQuestions.toFloat()) * 100f).toInt().coerceIn(0, 100)
        } else {
            100
        }

        val baseXp = lesson?.xpReward ?: 20
        val xpAwarded = if (existingProgress?.isCompleted == true) {
            (baseXp / 2).coerceAtLeast(10)
        } else {
            baseXp
        }

        val now = System.currentTimeMillis()
        dao.upsertLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = true,
                bestScorePercent = maxOf(existingProgress?.bestScorePercent ?: 0, accuracyPercent),
                attemptsCount = (existingProgress?.attemptsCount ?: 0) + 1,
                lastCompletedAt = now,
            )
        )

        val dateKey = todayDate.format(dateFormatter)
        val todayActivity = dao.getDailyActivityForDate(dateKey)
        dao.upsertDailyActivity(
            DailyActivityEntity(
                dateKey = dateKey,
                xpEarned = (todayActivity?.xpEarned ?: 0) + xpAwarded,
                lessonsCompleted = (todayActivity?.lessonsCompleted ?: 0) + 1,
            )
        )

        val unlocked = evaluateAndUnlockAchievements(todayDate, now)
        return LessonCompletionResult(
            xpAwarded = xpAwarded,
            accuracyPercent = accuracyPercent,
            newlyUnlockedAchievements = unlocked,
        )
    }

    suspend fun recordPracticeXp(xpEarned: Int, todayDate: LocalDate = LocalDate.now()): List<AchievementEntity> {
        ensureSeeded()
        if (xpEarned <= 0) return emptyList()
        val dateKey = todayDate.format(dateFormatter)
        val todayActivity = dao.getDailyActivityForDate(dateKey)
        dao.upsertDailyActivity(
            DailyActivityEntity(
                dateKey = dateKey,
                xpEarned = (todayActivity?.xpEarned ?: 0) + xpEarned,
                lessonsCompleted = todayActivity?.lessonsCompleted ?: 0,
            )
        )
        return evaluateAndUnlockAchievements(todayDate, System.currentTimeMillis())
    }

    suspend fun resetAllProgress() {
        dao.resetAllUserProgress()
    }

    private suspend fun evaluateAndUnlockAchievements(
        todayDate: LocalDate,
        nowMillis: Long,
    ): List<AchievementEntity> {
        val progress = dao.observeLessonProgress().first()
        val activities = dao.observeDailyActivity().first()
        val achievements = dao.observeAchievements().first()

        val completedLessons = progress.count { it.isCompleted }
        val totalXp = activities.sumOf { it.xpEarned }
        val streak = calculateStreak(activities, todayDate)

        val newlyUnlocked = mutableListOf<AchievementEntity>()
        for (ach in achievements) {
            if (!ach.isUnlocked) {
                val meetsXp = ach.xpThreshold == 0 || totalXp >= ach.xpThreshold
                val meetsLessons = ach.lessonsThreshold == 0 || completedLessons >= ach.lessonsThreshold
                val meetsStreak = ach.streakThreshold == 0 || streak >= ach.streakThreshold
                if (meetsXp && meetsLessons && meetsStreak) {
                    dao.unlockAchievement(ach.id, nowMillis)
                    newlyUnlocked.add(ach.copy(isUnlocked = true, unlockedAt = nowMillis))
                }
            }
        }
        return newlyUnlocked
    }

    fun calculateStreak(activities: List<DailyActivityEntity>, today: LocalDate): Int {
        val activeDates = activities
            .filter { it.xpEarned > 0 }
            .mapNotNull { runCatching { LocalDate.parse(it.dateKey, dateFormatter) }.getOrNull() }
            .toSet()

        if (activeDates.isEmpty()) return 0

        var cursor = when {
            activeDates.contains(today) -> today
            activeDates.contains(today.minusDays(1)) -> today.minusDays(1)
            else -> return 0
        }

        var streak = 0
        while (activeDates.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private data class Tuple5<A, B, C, D, E>(
        val languages: A,
        val levels: B,
        val units: C,
        val lessons: D,
        val progressList: E,
    )
}
