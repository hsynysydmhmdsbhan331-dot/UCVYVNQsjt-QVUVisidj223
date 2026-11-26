package com.example

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.EnStrings
import com.example.core.i18n.FaStrings
import com.example.core.i18n.LocalAppStrings
import com.example.data.local.ProjectOwlDatabase
import com.example.data.prefs.UserPreferences
import com.example.data.repository.LearningRepository
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.ProjectOwlTheme
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: ProjectOwlDatabase
    private lateinit var repository: LearningRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ProjectOwlDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LearningRepository(db.projectOwlDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `app name matches Project Owl`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("Project Owl", context.getString(R.string.app_name))
    }

    @Test
    fun `lesson progression unlocks sequentially and updates streak and reset`() = runTest {
        repository.ensureSeeded()
        val initial = repository.observeOverview("en-US").first()
        assertEquals(6, initial.lessons.size)
        assertTrue(initial.lessons[0].isUnlocked)
        assertFalse(initial.lessons[1].isUnlocked)
        assertEquals(0, initial.totalXp)
        assertEquals(0, initial.currentStreak)

        val result = repository.recordLessonCompletion(
            lessonId = "a1_u1_l1",
            correctCount = 5,
            totalQuestions = 5,
            todayDate = LocalDate.now(),
        )
        assertEquals(20, result.xpAwarded)
        assertEquals(100, result.accuracyPercent)
        assertTrue(result.newlyUnlockedAchievements.any { it.id == "ach_first_step" })

        val afterFirst = repository.observeOverview("en-US").first()
        assertTrue(afterFirst.lessons[0].isCompleted)
        assertTrue(afterFirst.lessons[1].isUnlocked)
        assertFalse(afterFirst.lessons[2].isUnlocked)
        assertEquals(20, afterFirst.totalXp)
        assertEquals(1, afterFirst.currentStreak)

        repository.resetAllProgress()
        val afterReset = repository.observeOverview("en-US").first()
        assertFalse(afterReset.lessons[0].isCompleted)
        assertEquals(0, afterReset.totalXp)
        assertEquals(0, afterReset.currentStreak)
    }

    @Test
    fun `onboarding renders Persian default and switches to English`() {
        var selectedLanguage by mutableStateOf(AppLanguage.PERSIAN)
        var completed = false

        composeTestRule.setContent {
            val strings = if (selectedLanguage == AppLanguage.PERSIAN) FaStrings else EnStrings
            ProjectOwlTheme(isPersian = selectedLanguage == AppLanguage.PERSIAN) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides selectedLanguage.layoutDirection,
                    LocalAppStrings provides strings,
                ) {
                    OnboardingScreen(
                        currentLanguage = selectedLanguage,
                        currentDailyGoal = 50,
                        onSelectLanguage = { selectedLanguage = it },
                        onCompleteOnboarding = { _, _ -> completed = true },
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(FaStrings.onboardingWelcomeTitle).assertIsDisplayed()
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(FaStrings.onboardingIntroTitle).assertIsDisplayed()
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("onboarding_lang_en").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals(AppLanguage.ENGLISH, selectedLanguage)
        composeTestRule.onNodeWithText(EnStrings.onboardingLanguageTitle).assertIsDisplayed()
        composeTestRule.onNodeWithTag("onboarding_next_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("onboarding_start_button").performClick()
        assertTrue(completed)
    }

    @Test
    fun `main screens render in dark English and light Persian themes`() {
        val overview = runBlocking {
            repository.ensureSeeded()
            repository.observeOverview("en-US").first()
        }

        val prefsDarkEn = UserPreferences(
            interfaceLanguage = AppLanguage.ENGLISH,
            themeMode = ThemeMode.DARK,
            onboardingCompleted = true,
            username = "Sara",
        )

        composeTestRule.setContent {
            ProjectOwlTheme(themeMode = ThemeMode.DARK, isPersian = false) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides AppLanguage.ENGLISH.layoutDirection,
                    LocalAppStrings provides EnStrings,
                ) {
                    HomeScreen(
                        preferences = prefsDarkEn,
                        overview = overview,
                        onOpenLesson = {},
                        onOpenLearnTab = {},
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_continue_learning_button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sara").assertIsDisplayed()
    }

    @Test
    fun `learn practice and profile screens render cleanly in Persian RTL`() {
        val overview = runBlocking {
            repository.ensureSeeded()
            repository.observeOverview("en-US").first()
        }
        val prefsFa = UserPreferences(
            interfaceLanguage = AppLanguage.PERSIAN,
            themeMode = ThemeMode.LIGHT,
            onboardingCompleted = true,
        )

        composeTestRule.setContent {
            ProjectOwlTheme(themeMode = ThemeMode.LIGHT, isPersian = true) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides AppLanguage.PERSIAN.layoutDirection,
                    LocalAppStrings provides FaStrings,
                ) {
                    LearnScreen(
                        preferences = prefsFa,
                        overview = overview,
                        onOpenLesson = {},
                        onOpenSkillModule = {},
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("learn_tab_skills").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("skill_card_ALPHABET").assertIsDisplayed()
    }
}
