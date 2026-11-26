package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectOwlDao {

    @Query("SELECT * FROM learning_languages ORDER BY code ASC")
    fun observeLanguages(): Flow<List<LearningLanguageEntity>>

    @Query("SELECT * FROM cefr_levels ORDER BY orderIndex ASC")
    fun observeCefrLevels(): Flow<List<CefrLevelEntity>>

    @Query("SELECT * FROM units WHERE languageCode = :languageCode ORDER BY orderIndex ASC")
    fun observeUnits(languageCode: String): Flow<List<UnitEntity>>

    @Query("SELECT * FROM lessons WHERE languageCode = :languageCode ORDER BY orderIndex ASC")
    fun observeLessons(languageCode: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: String): LessonEntity?

    @Query("SELECT * FROM vocabulary WHERE lessonId = :lessonId")
    suspend fun getVocabularyForLesson(lessonId: String): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE languageCode = :languageCode")
    fun observeAllVocabulary(languageCode: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM exercises WHERE lessonId = :lessonId ORDER BY orderIndex ASC")
    suspend fun getExercisesForLesson(lessonId: String): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE languageCode = :languageCode")
    fun observeAllExercises(languageCode: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE languageCode = :languageCode AND type = :type")
    suspend fun getExercisesByType(languageCode: String, type: String): List<ExerciseEntity>

    @Query("SELECT * FROM lesson_progress")
    fun observeLessonProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getLessonProgress(lessonId: String): LessonProgressEntity?

    @Query("SELECT * FROM daily_activity ORDER BY dateKey DESC")
    fun observeDailyActivity(): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM daily_activity WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailyActivityForDate(dateKey: String): DailyActivityEntity?

    @Query("SELECT * FROM achievements ORDER BY lessonsThreshold ASC, xpThreshold ASC")
    fun observeAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(items: List<LearningLanguageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCefrLevels(items: List<CefrLevelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(items: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(items: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(items: List<VocabularyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(items: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievementsIgnore(items: List<AchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessonProgress(progress: LessonProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyActivity(activity: DailyActivityEntity)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id AND isUnlocked = 0")
    suspend fun unlockAchievement(id: String, unlockedAt: Long)

    @Query("DELETE FROM lesson_progress")
    suspend fun clearLessonProgress()

    @Query("DELETE FROM daily_activity")
    suspend fun clearDailyActivity()

    @Query("UPDATE achievements SET isUnlocked = 0, unlockedAt = 0")
    suspend fun resetAchievements()

    @Transaction
    suspend fun resetAllUserProgress() {
        clearLessonProgress()
        clearDailyActivity()
        resetAchievements()
    }
}
