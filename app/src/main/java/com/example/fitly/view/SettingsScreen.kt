package com.example.fitly.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fitly.R
import com.example.fitly.model.worker.NotificationWorker
import com.example.fitly.ui.theme.FitlyTheme
import com.example.fitly.viewmodel.SPViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    spViewModel: SPViewModel,
) {
    val currentUser by spViewModel.currentUser.collectAsStateWithLifecycle()
    val language by spViewModel.language.collectAsStateWithLifecycle()
    var openedItem by remember { mutableIntStateOf(0) }
    var showOverlay by remember { mutableStateOf(false) }
    var showRequestPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val parts = currentUser.alarmTime.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    var selectedHour by remember { mutableStateOf(hour.toString().padStart(2, '0')) }
    var selectedMinute by remember { mutableStateOf(minute.toString().padStart(2, '0')) }
    val currentLanguage = when(language){
        "uk" -> "Ukrainian"
        "en" -> "English"
        else -> "System"
    }

    FitlyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.settings_title),
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
                    BottomNavigationBar(navController, 1)
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    NameOfColumn(stringResource(R.string.personal_info_title))
                    ItemOfColumn(
                        stringResource(R.string.change_personal_data_title),
                        stringResource(R.string.warning_progress_lost),
                        onClick = {
                            openedItem = 1
                            showOverlay = true
                        })
                    NameOfColumn(stringResource(R.string.preferences_title))
                    ItemOfColumn(stringResource(R.string.language_title)+ "     Will work soon", when (language) {
                        "uk" -> stringResource(R.string.ukrainian_language)
                        "en" -> stringResource(R.string.english_language)
                        else -> stringResource(R.string.system_language)
                    }) {
                        openedItem = 2
                        showOverlay = true
                    }
                    ItemOfColumn(
                        stringResource(R.string.enable_notifications_title),
                        stringResource(R.string.stay_updated_reminders)
                    ) { showRequestPermission = true }
                    ItemOfColumn(stringResource(R.string.alarm_time_title), currentUser.alarmTime) {
                        openedItem = 3
                        showOverlay = true
                    }
                    if (showRequestPermission) {
                        RequestNotificationPermission(
                            hour = hour,
                            minute = minute
                        )
                        LaunchedEffect(Unit) {
                            showRequestPermission = false
                        }
                    }
                }
            }
            when (openedItem) {
                1 -> OverlayButton(
                    showOverlay,
                    stringResource(R.string.reset_data_title),
                    stringResource(R.string.reset_data_description),
                    { showOverlay = false },
                    { spViewModel.deleteAllDataAndExit(context) })

                2 -> OverlayLanguage(
                    showOverlay,
                    stringResource(R.string.select_language_title),
                    stringResource(R.string.restart_to_apply_language),
                    { showOverlay = false },
                    spViewModel,
                    currentLanguage,
                    context
                )

                3 -> OverlayTimePicker(
                    showOverlay,
                    stringResource(R.string.select_alarm_time_title),
                    stringResource(R.string.choose_workout_reminder_time),
                    { showOverlay = false },
                    selectedHour.toInt(),
                    selectedMinute.toInt(),
                    { selectedHour = it }, { selectedMinute = it },
                    {
                        NotificationWorker.scheduleFirstWork(
                            context,
                            selectedHour.toInt(),
                            selectedMinute.toInt()
                        )
                        spViewModel.setNewAlarmTime("$selectedHour:$selectedMinute")
                        showOverlay = false
                    }
                )
            }
        }
    }
}

@Composable
fun NameOfColumn(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.onSurface),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
fun ItemOfColumn(primaryText: String, secondaryText: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .height(75.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    primaryText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 20.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    secondaryText,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 20.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Button",
                modifier = Modifier
                    .padding(end = 30.dp)
                    .size(32.dp)
            )
        }
    }
}
