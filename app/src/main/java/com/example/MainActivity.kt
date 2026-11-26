package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.di.ProjectOwlAppContainer
import com.example.ui.ProjectOwlApp
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: ProjectOwlAppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appContainer = ProjectOwlAppContainer(applicationContext)
        val mainViewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(
                preferencesRepository = appContainer.preferencesRepository,
                learningRepository = appContainer.learningRepository,
                audioController = appContainer.audioController,
            )
        )[MainViewModel::class.java]

        setContent {
            ProjectOwlApp(
                appContainer = appContainer,
                mainViewModel = mainViewModel,
            )
        }
    }

    override fun onDestroy() {
        if (isFinishing && ::appContainer.isInitialized) {
            appContainer.audioController.release()
        }
        super.onDestroy()
    }
}
