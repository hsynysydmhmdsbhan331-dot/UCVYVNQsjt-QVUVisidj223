package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LearningLanguageEntity::class,
        CefrLevelEntity::class,
        UnitEntity::class,
        LessonEntity::class,
        VocabularyEntity::class,
        ExerciseEntity::class,
        LessonProgressEntity::class,
        DailyActivityEntity::class,
        AchievementEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class ProjectOwlDatabase : RoomDatabase() {
    abstract fun projectOwlDao(): ProjectOwlDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectOwlDatabase? = null

        fun getInstance(context: Context): ProjectOwlDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProjectOwlDatabase::class.java,
                    "project_owl.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
