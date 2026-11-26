package com.example.di

import android.content.Context
import com.example.audio.TtsAudioController
import com.example.data.local.ProjectOwlDatabase
import com.example.data.prefs.UserPreferencesRepository
import com.example.data.repository.LearningRepository

class ProjectOwlAppContainer(context: Context) {
    private val appContext = context.applicationContext

    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(appContext)
    }

    val database: ProjectOwlDatabase by lazy {
        ProjectOwlDatabase.getInstance(appContext)
    }

    val learningRepository: LearningRepository by lazy {
        LearningRepository(database.projectOwlDao())
    }

    val audioController: TtsAudioController by lazy {
        TtsAudioController(appContext)
    }
}
