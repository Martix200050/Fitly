package com.example.fitly.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitly.view.AboutYouScreen
import com.example.fitly.view.FitnessGoals
import com.example.fitly.view.SettingsScreen
import com.example.fitly.view.TodayWorkoutScreen
import com.example.fitly.view.WorkoutNotificationsScreen
import com.example.fitly.view.WorkoutPlanScreen
import com.example.fitly.view.WorkoutScreen
import com.example.fitly.viewmodel.MainViewModel
import com.example.fitly.viewmodel.SPViewModel
import kotlinx.serialization.Serializable


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(spViewModel: SPViewModel){
    val mainViewModel: MainViewModel = viewModel()
    val navController = rememberNavController()
    val isRegistered = spViewModel.isRegistered.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = if (isRegistered.value) TodayWorkoutScreen else AboutYouScreen

    ) {
        composable<AboutYouScreen> {
            AboutYouScreen(navController, mainViewModel)
        }
        composable<FitnessGoalsScreen> {
            FitnessGoals(navController, mainViewModel)
        }
        composable<WorkoutPlanScreen> {
            WorkoutPlanScreen(navController, mainViewModel)
        }
        composable<WorkoutNotificationsScreen> {
            WorkoutNotificationsScreen(navController,mainViewModel,spViewModel)
        }
        composable<TodayWorkoutScreen> {
            TodayWorkoutScreen(navController, spViewModel)
        }
        composable<WorkoutScreen> {
            WorkoutScreen(navController, spViewModel, spViewModel.currentUser.value.difficultyLevel, spViewModel.trainingDayState.value.trainingDaysCount)
        }
        composable<SettingsScreen> {
            SettingsScreen(navController, spViewModel)
        }
    }
}

@Serializable
object AboutYouScreen

@Serializable
object FitnessGoalsScreen

@Serializable
object WorkoutPlanScreen

@Serializable
object WorkoutNotificationsScreen

@Serializable
object TodayWorkoutScreen

@Serializable
object WorkoutScreen

@Serializable
object SettingsScreen
