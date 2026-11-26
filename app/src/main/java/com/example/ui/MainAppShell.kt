package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.EnStrings
import com.example.core.i18n.FaStrings
import com.example.core.i18n.LocalAppStrings
import com.example.data.local.PracticeMode
import com.example.data.local.SkillCategory
import com.example.di.ProjectOwlAppContainer
import com.example.ui.navigation.AppRoute
import com.example.ui.navigation.MainTab
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LessonPlayerScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.PracticeSessionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SkillModuleScreen
import com.example.ui.theme.ProjectOwlTheme
import com.example.viewmodel.AppUiState
import com.example.viewmodel.LessonPlayerViewModel
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.PracticeSessionViewModel

private data class NavTabSpec(
    val tab: MainTab,
    val route: AppRoute,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
)

@Composable
fun ProjectOwlApp(
    appContainer: ProjectOwlAppContainer,
    mainViewModel: MainViewModel,
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        AppUiState.Loading -> {
            ProjectOwlTheme {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        is AppUiState.Ready -> {
            val prefs = state.preferences
            val language = prefs.interfaceLanguage
            val strings = if (language == AppLanguage.PERSIAN) FaStrings else EnStrings

            ProjectOwlTheme(
                themeMode = prefs.themeMode,
                isPersian = language == AppLanguage.PERSIAN,
            ) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides language.layoutDirection,
                    LocalAppStrings provides strings,
                ) {
                    val navController = rememberNavController()
                    val startDestination: AppRoute = if (prefs.onboardingCompleted) {
                        AppRoute.Home
                    } else {
                        AppRoute.Onboarding
                    }

                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = backStackEntry?.destination

                    val showMainChrome = currentDestination != null && (
                        currentDestination.hasRoute<AppRoute.Home>() ||
                            currentDestination.hasRoute<AppRoute.Learn>() ||
                            currentDestination.hasRoute<AppRoute.Practice>() ||
                            currentDestination.hasRoute<AppRoute.Profile>()
                        )

                    val tabs = listOf(
                        NavTabSpec(MainTab.HOME, AppRoute.Home, strings.navHome, Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
                        NavTabSpec(MainTab.LEARN, AppRoute.Learn, strings.navLearn, Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_learn"),
                        NavTabSpec(MainTab.PRACTICE, AppRoute.Practice, strings.navPractice, Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "nav_practice"),
                        NavTabSpec(MainTab.PROFILE, AppRoute.Profile, strings.navProfile, Icons.Filled.Person, Icons.Outlined.Person, "nav_profile"),
                    )

                    fun navigateToTab(route: AppRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val useNavigationRail = maxWidth >= 600.dp

                        Scaffold(
                            contentWindowInsets = WindowInsets.safeDrawing,
                            bottomBar = {
                                if (showMainChrome && !useNavigationRail) {
                                    NavigationBar(
                                        windowInsets = WindowInsets.navigationBars,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ) {
                                        tabs.forEach { spec ->
                                            val selected = when (spec.tab) {
                                                MainTab.HOME -> currentDestination?.hasRoute<AppRoute.Home>() == true
                                                MainTab.LEARN -> currentDestination?.hasRoute<AppRoute.Learn>() == true
                                                MainTab.PRACTICE -> currentDestination?.hasRoute<AppRoute.Practice>() == true
                                                MainTab.PROFILE -> currentDestination?.hasRoute<AppRoute.Profile>() == true
                                            }
                                            NavigationBarItem(
                                                selected = selected,
                                                onClick = { navigateToTab(spec.route) },
                                                icon = {
                                                    Icon(
                                                        imageVector = if (selected) spec.selectedIcon else spec.unselectedIcon,
                                                        contentDescription = spec.label,
                                                    )
                                                },
                                                label = { Text(spec.label) },
                                                modifier = Modifier.testTag(spec.testTag),
                                            )
                                        }
                                    }
                                }
                            },
                        ) { innerPadding ->
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                            ) {
                                if (showMainChrome && useNavigationRail) {
                                    NavigationRail(
                                        modifier = Modifier.fillMaxHeight(),
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ) {
                                        tabs.forEach { spec ->
                                            val selected = when (spec.tab) {
                                                MainTab.HOME -> currentDestination?.hasRoute<AppRoute.Home>() == true
                                                MainTab.LEARN -> currentDestination?.hasRoute<AppRoute.Learn>() == true
                                                MainTab.PRACTICE -> currentDestination?.hasRoute<AppRoute.Practice>() == true
                                                MainTab.PROFILE -> currentDestination?.hasRoute<AppRoute.Profile>() == true
                                            }
                                            NavigationRailItem(
                                                selected = selected,
                                                onClick = { navigateToTab(spec.route) },
                                                icon = {
                                                    Icon(
                                                        imageVector = if (selected) spec.selectedIcon else spec.unselectedIcon,
                                                        contentDescription = spec.label,
                                                    )
                                                },
                                                label = { Text(spec.label) },
                                                modifier = Modifier.testTag(spec.testTag),
                                            )
                                        }
                                    }
                                }

                                NavHost(
                                    navController = navController,
                                    startDestination = startDestination,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    composable<AppRoute.Onboarding> {
                                        OnboardingScreen(
                                            currentLanguage = language,
                                            currentDailyGoal = prefs.dailyXpGoal,
                                            onSelectLanguage = { mainViewModel.setInterfaceLanguage(it) },
                                            onCompleteOnboarding = { chosenLang, chosenGoal ->
                                                mainViewModel.completeOnboarding(chosenLang, chosenGoal)
                                                navController.navigate(AppRoute.Home) {
                                                    popUpTo<AppRoute.Onboarding> { inclusive = true }
                                                }
                                            },
                                        )
                                    }

                                    composable<AppRoute.Home> {
                                        HomeScreen(
                                            preferences = prefs,
                                            overview = state.overview,
                                            onOpenLesson = { lessonId ->
                                                navController.navigate(AppRoute.LessonPlayer(lessonId))
                                            },
                                            onOpenLearnTab = { navigateToTab(AppRoute.Learn) },
                                        )
                                    }

                                    composable<AppRoute.Learn> {
                                        LearnScreen(
                                            preferences = prefs,
                                            overview = state.overview,
                                            onOpenLesson = { lessonId ->
                                                navController.navigate(AppRoute.LessonPlayer(lessonId))
                                            },
                                            onOpenSkillModule = { category ->
                                                navController.navigate(AppRoute.SkillModule(category.key))
                                            },
                                        )
                                    }

                                    composable<AppRoute.Practice> {
                                        PracticeScreen(
                                            avatarVariant = prefs.avatarId,
                                            onStartPractice = { mode ->
                                                navController.navigate(AppRoute.PracticeSession(mode.key))
                                            },
                                        )
                                    }

                                    composable<AppRoute.Profile> {
                                        ProfileScreen(
                                            preferences = prefs,
                                            overview = state.overview,
                                            onUpdateUsername = { mainViewModel.setUsername(it) },
                                            onUpdateAvatar = { mainViewModel.setAvatarId(it) },
                                            onOpenSettings = { navController.navigate(AppRoute.Settings) },
                                        )
                                    }

                                    composable<AppRoute.LessonPlayer> { entry ->
                                        val route = entry.toRoute<AppRoute.LessonPlayer>()
                                        val lessonVm: LessonPlayerViewModel = viewModel(
                                            key = "lesson_${route.lessonId}",
                                            factory = LessonPlayerViewModel.Factory(
                                                lessonId = route.lessonId,
                                                learningRepository = appContainer.learningRepository,
                                                audioController = appContainer.audioController,
                                                soundEffectsEnabled = prefs.soundEffectsEnabled,
                                                slowSpeechDefault = prefs.slowSpeechDefault,
                                            ),
                                        )
                                        LessonPlayerScreen(
                                            viewModel = lessonVm,
                                            language = language,
                                            avatarVariant = prefs.avatarId,
                                            onBack = { navController.popBackStack() },
                                        )
                                    }

                                    composable<AppRoute.SkillModule> { entry ->
                                        val route = entry.toRoute<AppRoute.SkillModule>()
                                        SkillModuleScreen(
                                            category = SkillCategory.fromKey(route.skillKey),
                                            language = language,
                                            vocabulary = state.allVocabulary,
                                            onSpeak = { text, slow -> mainViewModel.speakEnglish(text, slow) },
                                            onPlayCorrect = { mainViewModel.playCorrectSound() },
                                            onPlayIncorrect = { mainViewModel.playIncorrectSound() },
                                            onBack = { navController.popBackStack() },
                                        )
                                    }

                                    composable<AppRoute.PracticeSession> { entry ->
                                        val route = entry.toRoute<AppRoute.PracticeSession>()
                                        val mode = PracticeMode.fromKey(route.modeKey)
                                        val practiceVm: PracticeSessionViewModel = viewModel(
                                            key = "practice_${route.modeKey}",
                                            factory = PracticeSessionViewModel.Factory(
                                                mode = mode,
                                                languageCode = prefs.learningLanguageCode,
                                                learningRepository = appContainer.learningRepository,
                                                audioController = appContainer.audioController,
                                                soundEffectsEnabled = prefs.soundEffectsEnabled,
                                                slowSpeechDefault = prefs.slowSpeechDefault,
                                            ),
                                        )
                                        PracticeSessionScreen(
                                            viewModel = practiceVm,
                                            language = language,
                                            avatarVariant = prefs.avatarId,
                                            onBack = { navController.popBackStack() },
                                        )
                                    }

                                    composable<AppRoute.Settings> {
                                        SettingsScreen(
                                            preferences = prefs,
                                            onSelectLanguage = { mainViewModel.setInterfaceLanguage(it) },
                                            onSelectTheme = { mainViewModel.setThemeMode(it) },
                                            onSelectDailyGoal = { mainViewModel.setDailyXpGoal(it) },
                                            onToggleSoundEffects = { mainViewModel.setSoundEffectsEnabled(it) },
                                            onToggleSlowSpeech = { mainViewModel.setSlowSpeechDefault(it) },
                                            onResetProgress = { mainViewModel.resetProgress() },
                                            onBack = { navController.popBackStack() },
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
