package com.example.fitly.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitly.R
import com.example.fitly.navigation.TodayWorkoutScreen
import com.example.fitly.ui.theme.FitlyTheme
import com.example.fitly.viewmodel.MainViewModel
import com.example.fitly.viewmodel.SPViewModel
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutNotificationsScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    spViewModel: SPViewModel
) {

    val user = mainViewModel.getUser()
    var errorMessage by remember { mutableStateOf(false) }
    val daysOfWeek =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val initialSelectedDays = if (user.muscleGroups.size == 1) listOf(
        "Monday",
        "Wednesday",
        "Friday"
    ) else listOf("Monday", "Tuesday", "Thursday", "Friday")
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }
    var mainHour by remember { mutableIntStateOf(10) }
    var mainMinute by remember { mutableIntStateOf(30) }
    var selectedHour by remember { mutableStateOf(mainHour.toString().padStart(2, '0')) }
    var selectedMinute by remember { mutableStateOf(mainMinute.toString().padStart(2, '0')) }
    var showOverlay by remember { mutableStateOf(false) }
    val arg = selectedDays.isNotEmpty()

    FitlyTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.workout_notifications_title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
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
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .clickable { showOverlay = true }
                        .clip(RoundedCornerShape(12.dp))) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.training_days_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp, end = 5.dp)
                    )
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.info_icon_description),
                        tint = Color.Gray
                    )
                }

                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        daysOfWeek.take(4).forEach { day ->
                            DayCheckboxRow(
                                day = day,
                                isChecked = day in selectedDays,
                                onCheckedChange = { isChecked ->
                                    if (mainViewModel.canSelectDay(
                                            user.muscleGroups,
                                            selectedDays,
                                            day,
                                            daysOfWeek
                                        )
                                    ) {
                                        selectedDays = if (day in selectedDays) {
                                            selectedDays - day
                                        } else {
                                            selectedDays + day
                                        }
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        daysOfWeek.drop(4).forEach { day ->
                            DayCheckboxRow(
                                day = day,
                                isChecked = day in selectedDays,
                                onCheckedChange = { isChecked ->
                                    if (mainViewModel.canSelectDay(
                                            user.muscleGroups,
                                            selectedDays,
                                            day,
                                            daysOfWeek
                                        )
                                    ) {
                                        selectedDays = if (day in selectedDays) {
                                            selectedDays - day
                                        } else {
                                            selectedDays + day
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.reminder_time_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTimePicker(
                    mainHour,
                    mainMinute,
                    { selectedHour = it },
                    { selectedMinute = it })
                Spacer(modifier = Modifier.weight(1f))
                Error(
                    errorMessage,
                    stringResource(R.string.error_select_training_day_and_time),
                    onTimeout = { errorMessage = false })
                Button(
                    onClick = {
                        if (arg) {
                            mainViewModel.setTrainingDaysAndAlarm(
                                selectedDays,
                                "$selectedHour:$selectedMinute"
                            )
                            spViewModel.calculateAndSaveExercisesAndUser(user)
                            navController.navigate(TodayWorkoutScreen) {
                                popUpTo(0)
                            }
                            spViewModel.setRegistered(true)
                        } else errorMessage =
                            true
                    },
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (arg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.finish_setup_button),
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


@Composable
fun CustomTimePicker(
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                TimePickerWheel(
                    values = (0..23).map { it.toString().padStart(2, '0') },
                    initialIndex = selectedHour,
                    onValueChange = onHourChange
                )
            }
            Text(
                stringResource(R.string.hour),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimePickerWheel(
                values = (0..59).map { it.toString().padStart(2, '0') },
                initialIndex = selectedMinute,
                onValueChange = onMinuteChange
            )
            Text(
                stringResource(R.string.minute),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DayCheckboxRow(
    day: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 12.dp), // py-3
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp) // gap-x-3 (approx 12.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary, // checked:bg-[#2094f3]
                uncheckedColor = MaterialTheme.colorScheme.onSurface, // border-[#cedde8]
                checkmarkColor = Color(0xFFF8FAFC) // From --checkbox-tick-svg fill
            ),
            modifier = Modifier.size(20.dp) // h-5 w-5
        )
        Text(
            text = when (day) {
                "Monday" -> {
                    stringResource(R.string.monday)
                }

                "Tuesday" -> {
                    stringResource(R.string.tuesday)
                }

                "Wednesday" -> {
                    stringResource(R.string.wednesday)
                }

                "Thursday" -> {
                    stringResource(R.string.thursday)
                }

                "Friday" -> {
                    stringResource(R.string.friday)
                }

                "Saturday" -> {
                    stringResource(R.string.saturday)
                }

                else -> {
                    stringResource(R.string.sunday)
                }
            },
            color = MaterialTheme.colorScheme.onBackground, // text-[#0d151c]
            fontSize = 16.sp, // text-base
            fontWeight = FontWeight.Normal // font-normal
        )
    }
}

@Composable
fun TimePickerWheel(
    modifier: Modifier = Modifier,
    values: List<String>,
    onValueChange: (String) -> Unit,
    initialIndex: Int = 0
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val density = LocalDensity.current
    val itemHeightPx = remember(density) { with(density) { 40.dp.toPx() } }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val offsetItem = (listState.firstVisibleItemScrollOffset / itemHeightPx).roundToInt()
            val currentIndex = listState.firstVisibleItemIndex + offsetItem
            val targetIndex = currentIndex.coerceIn(0, values.lastIndex)
            listState.animateScrollToItem(targetIndex)
            onValueChange(values[targetIndex])
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.height(200.dp),
        contentPadding = PaddingValues(vertical = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(values.size) { index ->
            val centerIndex by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex +
                            (listState.firstVisibleItemScrollOffset / itemHeightPx).roundToInt()
                }
            }
            val isSelected = index == centerIndex

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = values[index],
                    fontSize = 20.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surface
                )
            }

        }
    }
}
