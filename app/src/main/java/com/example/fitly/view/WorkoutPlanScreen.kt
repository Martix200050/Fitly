package com.example.fitly.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitly.navigation.WorkoutNotificationsScreen
import com.example.fitly.ui.theme.FitlyTheme

import com.example.fitly.viewmodel.MainViewModel
import com.example.fitly.viewmodel.SPViewModel

import com.example.fitly.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkoutPlanScreen(navController: NavController, viewModel: MainViewModel) {

    var showOverlay by remember { mutableStateOf(false) }
    var selectedMuscleGroups by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedDuration by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf(false) }
    val muscleGroups = listOf("Upper Body", "Lower Body", "Back")
    val planDurations = listOf("7", "14", "21", "30")
    val difficultyLevels = listOf("Beginner", "Intermediate", "Advanced")
    val arg =
        selectedMuscleGroups.isNotEmpty() && selectedDuration.isNotEmpty() && selectedDifficulty.isNotEmpty()

    FitlyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.workout_plan_title),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back_button),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { showOverlay = true }
                            .padding(top = 20.dp, bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp))) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(R.string.select_muscle_groups),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Info,
                            contentDescription = "info",
                            tint = Color.Gray
                        )
                    }
                    ChipGroup(
                        items = muscleGroups,
                        selectedItems = selectedMuscleGroups,
                        onItemSelected = { item ->
                            selectedMuscleGroups = if (item in selectedMuscleGroups) {
                                selectedMuscleGroups - item
                            } else {
                                selectedMuscleGroups + item
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.plan_duration),
                        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                    )
                    ChipGroup(
                        forDays = true,
                        items = planDurations,
                        selectedItem = selectedDuration,
                        onItemSelected = { item ->
                            selectedDuration = if (selectedDuration == item) "" else item
                        }
                    )
                    Text(
                        text = stringResource(R.string.difficulty),
                        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                    )
                    ChipGroup(
                        items = difficultyLevels,
                        selectedItem = selectedDifficulty,
                        onItemSelected = { item ->
                            selectedDifficulty = if (selectedDifficulty == item) "" else item
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Error(
                        errorMessage,
                        stringResource(R.string.error_select_all_options),
                        onTimeout = { errorMessage = false })
                    Button(
                        onClick = {
                            if (arg) {
                                viewModel.setWorkoutPlan(
                                    selectedMuscleGroups,
                                    selectedDuration,
                                    selectedDifficulty
                                )
                                navController.navigate(WorkoutNotificationsScreen)
                            } else errorMessage = true
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (arg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.continue_button),
                            fontWeight = FontWeight.Bold,
                            color = if (arg) Color.White else MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Overlay(
                boolean = showOverlay,
                primaryText = stringResource(R.string.overlay_primary_text),
                secondaryText = stringResource(R.string.overlay_secondary_text),
                onClick = { showOverlay = false },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    items: List<String>,
    selectedItems: List<String>, // For multi-select
    onItemSelected: (String) -> Unit
) {
    FitlyTheme {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                ChipButton(
                    text = when(item) {
                        "Upper Body" -> {stringResource(R.string.upper_body)}
                        "Lower Body" -> {stringResource(R.string.lower_body)}
                        else -> {stringResource(R.string.lower_body)}
                    },
                    isSelected = item in selectedItems,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    forDays: Boolean = false,
    items: List<String>,
    selectedItem: String?, // For single-select
    onItemSelected: (String) -> Unit
) {
    FitlyTheme {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                ChipButton(
                    text = if (forDays) item + " " + stringResource(R.string.days) else when(item) {
                        "Beginner" -> {stringResource(R.string.beginner)}
                        "Intermediate" -> {stringResource(R.string.intermediate)}
                        else -> {stringResource(R.string.advanced)}
                    },
                    isSelected = item == selectedItem,
                    onClick = { onItemSelected(item) }
                )
            }
        }
    }
}

@Composable
private fun ChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FitlyTheme {
        Card(
            modifier = Modifier
                .height(32.dp) // h-8
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp), // rounded-lg
            colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        ) {
            Column( // To center text like in HTML (flex items-center justify-center)
                modifier = Modifier.padding(horizontal = 16.dp), // pl-4 pr-4
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp, // text-sm
                    fontWeight = FontWeight.Medium // font-medium
                )
            }
        }
    }
}

@Composable
fun Overlay(
    boolean: Boolean,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = boolean,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Text(
                        text = primaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun OverlayButton(
    boolean: Boolean,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit,
    onButtonClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = boolean,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Text(
                        text = primaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    MainButton(true, "Continue", onButtonClick)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverlayLanguage(
    boolean: Boolean,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit,
    spViewModel: SPViewModel,
    currentLanguage: String,
    context: Context
) {
    AnimatedVisibility(
        visible = boolean,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Text(
                        text = primaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.system_language),
                            color = if (currentLanguage == "System") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                spViewModel.setAppLanguage(
                                    "system",
                                    context
                                )
                            })
                        Text(
                            stringResource(R.string.english_language),
                            color = if (currentLanguage == "English") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                spViewModel.setAppLanguage(
                                    "en",
                                    context
                                )
                            })
                        Text(
                            stringResource(R.string.ukrainian_language),
                            color = if (currentLanguage == "Ukrainian") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                spViewModel.setAppLanguage(
                                    "uk",
                                    context
                                )
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun OverlayTimePicker(
    boolean: Boolean,
    primaryText: String,
    secondaryText: String,
    onClick: () -> Unit,
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
    onButtonClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = boolean,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Text(
                        text = primaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTimePicker(selectedHour, selectedMinute, onHourChange, onMinuteChange)
                    Spacer(modifier = Modifier.height(20.dp))
                    MainButton(true, "Finish", onButtonClick)
                }
            }
        }
    }
}