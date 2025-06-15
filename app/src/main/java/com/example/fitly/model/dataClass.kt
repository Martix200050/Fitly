package com.example.fitly.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

val navItemsList = listOf(
    NavigationItems(
        title = "Home",
        icon = Icons.Default.Home,
    ),
    NavigationItems(
        title = "Settings",
        icon = Icons.Default.Settings,
    )
)

data class NavigationItems(
    val title: String,
    val icon: ImageVector
)

data class TrainingDayState(
    val canTrainToday: Boolean = false,
    val lastTrainingDate: String = "",
    val trainingDaysCount: Int = 0
)

data class User(
    val name: String = "empty",
    val age: String = "empty",
    val gender: String = "empty",
    val height: String = "empty",
    val weight: String = "empty",
    var mainGoal: String = "empty",
    var muscleGroups: List<String> = emptyList(),
    var planDuration: String = "empty",
    var difficultyLevel: String = "empty",
    var selectedDays: List<String> = emptyList(),
    var alarmTime: String = "10:30"
)

data class CurrentUser(
    val name: String = "empty",
    var mainGoal: String = "empty",
    var planDuration: String = "empty",
    var selectedDays: List<String> = emptyList(),
    var muscleGroups: List<String> = emptyList(),
    var difficultyLevel: String = "empty",
    var alarmTime: String = "10:30"
)

data class BaseExercise(
    val group: String,
    val name: String,
    val isStatic: Boolean,
    val beginner: Int,
    val intermediate: Int,
    val advanced: Int
)

object BaseExercises {
    val exercises = listOf(

        // Upper Body
        BaseExercise("Upper Body", "push_ups", false, 12, 15, 18),
        BaseExercise("Upper Body", "triceps_dips", false, 10, 12, 15),
        BaseExercise("Upper Body", "arm_circles", false, 10, 20, 25),
        BaseExercise("Upper Body", "wall_push_ups", false, 12, 15, 18),

// Lower Body
        BaseExercise("Lower Body", "squats", false, 12, 15, 18),
        BaseExercise("Lower Body", "lunges", false, 10, 12, 15),
        BaseExercise("Lower Body", "glute_bridges", false, 12, 15, 18),
        BaseExercise("Lower Body", "calf_raises", false, 15, 18, 20),

// Back
        BaseExercise("Back", "superman_hold", true, 30, 45, 60),
        BaseExercise("Back", "plank", true, 30, 45, 60),
        BaseExercise("Back", "plank_up_downs", false, 10, 12, 15)
    )
}


data class CurrentExercise(
    val group: String,
    val name: String,
    val isStatic: Boolean,
    val value: Int
)