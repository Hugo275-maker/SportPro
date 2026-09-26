package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SportProBottomBar
import com.example.ui.components.SportProTopBar
import com.example.ui.components.StoryGuideModal
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SportProViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                SportProApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SportProApp(viewModel: SportProViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val showStoryGuide by viewModel.showStoryGuide.collectAsState()
    val screenBackStack by viewModel.screenBackStack.collectAsState()

    // Handle Android system Back button
    BackHandler(enabled = screenBackStack.size > 1) {
        viewModel.navigateBack()
    }

    val isAuthScreen = currentScreen == Screen.LOGIN ||
            currentScreen == Screen.REGISTER ||
            currentScreen == Screen.FORGOT_PASSWORD

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAuthScreen) {
                SportProTopBar(viewModel = viewModel)
            }
        },
        bottomBar = {
            if (!isAuthScreen) {
                SportProBottomBar(viewModel = viewModel)
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            Screen.LOGIN -> LoginScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.REGISTER -> RegisterScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.FORGOT_PASSWORD -> LoginScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.HOME -> HomeScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.TEAMS, Screen.USERS_TEAMS -> TeamsScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.TRAINING -> TrainingPlanScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.MATCHES -> MatchesListScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.COMMUNITY -> CommunityScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.MY_PROFILE, Screen.MY_CHILD -> PlayerProfileScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.MONTHLY_FEES -> MonthlyFeesScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.ACADEMY, Screen.INVITATION_CODES -> AcademyScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.EVENT_CATALOG -> EventCatalogScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.LIVE_MATCH -> LiveMatchRecordingScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.MATCH_SPECTATOR -> SpectatorMatchScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.MATCH_CLOSURE -> MatchClosureScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.AI_SUMMARY -> AISummaryScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.AI_EVALUATION -> SummaryEvaluationScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.STATISTICS -> AccumulatedStatsScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.ANNOUNCEMENTS -> AnnouncementsScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.SCOUTING -> ScoutingNoticesScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.MODERATION -> ModerationQueueScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.DRILLS -> DrillsLibraryScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.ATTENDANCE -> AttendanceScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.LINEUP -> LineupScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.CONVOCATORIA_RESPONSE -> ConvocatoriaResponseScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.PARTICIPATION_HISTORY -> ParticipationHistoryScreen(viewModel = viewModel, modifier = contentModifier)
            Screen.PARENT_LINK_REQUESTS -> ParentLinkRequestsScreen(viewModel = viewModel, modifier = contentModifier)
        }

        // 30 HUs Story Guide Navigator Sheet
        if (showStoryGuide) {
            StoryGuideModal(
                viewModel = viewModel,
                onDismiss = { viewModel.openStoryGuide(false) }
            )
        }
    }
}
