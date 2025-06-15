package com.example.fitly.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fitly.R
import com.example.fitly.model.navItemsList
import com.example.fitly.navigation.SettingsScreen
import com.example.fitly.navigation.TodayWorkoutScreen
import com.example.fitly.navigation.WorkoutScreen
import com.example.fitly.ui.theme.FitlyTheme
import com.example.fitly.viewmodel.SPViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayWorkoutScreen(
    navController: NavController,
    spViewModel: SPViewModel,
) {

    var showOverlay by remember { mutableStateOf(false) }
    val trainingDayState = spViewModel.trainingDayState.collectAsStateWithLifecycle()
    val currentUser = spViewModel.currentUser.collectAsStateWithLifecycle()
    val currentTime = LocalDate.now()
    val streak =
        trainingDayState.value.trainingDaysCount >= currentUser.value.planDuration.filter { it.isDigit() }
            .toInt()
    val context = if (streak) {
        LocalContext.current
    } else null
    FitlyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.todays_workout),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                bottomBar = {
                    BottomNavigationBar(navController, 0)
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "${trainingDayState.value.trainingDaysCount} ${stringResource(R.string.of)} ${currentUser.value.planDuration}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    if (!streak) {
                        Text(
                            text = stringResource(
                                R.string.estimated_duration,
                                when (currentUser.value.difficultyLevel) {
                                    "Beginner" -> 4 * currentUser.value.muscleGroups.size
                                    "Intermediate" -> 5 * currentUser.value.muscleGroups.size
                                    "Advanced" -> 6 * currentUser.value.muscleGroups.size
                                    else -> "Error"
                                }
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        MainButton(
                            arg = trainingDayState.value.canTrainToday,
                            text = stringResource(R.string.start_workout)
                        ) {
                            navController.navigate(
                                WorkoutScreen
                            )
                        }

                        if (currentTime.dayOfWeek.name !in currentUser.value.selectedDays.map { it.uppercase() }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    stringResource(R.string.rest_day_message),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                            }
                        } else if (trainingDayState.value.lastTrainingDate == currentTime.toString()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {

                                Text(
                                    stringResource(R.string.completed_workout_message),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {

                        Text(
                            stringResource(R.string.congratulations),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            stringResource(R.string.workout_streak_completed_message),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(bottom = 40.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            MainButton(true, stringResource(R.string.reset_your_data)) { showOverlay = true }
                        }
                    }
                }
            }
            OverlayButton(
                showOverlay,
                stringResource(R.string.reset_data_title),
                stringResource(R.string.reset_data_description),
                { showOverlay = false },
                { spViewModel.deleteAllDataAndExit(context!!) })
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController, selectedIndex: Int) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(selectedIndex) }
    val thisIndex = selectedIndex

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        containerColor = MaterialTheme.colorScheme.onSurface
    ) {
        navItemsList.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    when (item.title) {
                        "Home" -> if (thisIndex != 0) navController.navigate(
                            TodayWorkoutScreen
                        )

                        "Settings" -> if (thisIndex != 1) navController.navigate(
                            SettingsScreen
                        )
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(34.dp),
                        imageVector = item.icon,
                        tint = if (selectedIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}
