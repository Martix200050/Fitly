package com.example.fitly.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.fitly.navigation.TodayWorkoutScreen
import com.example.fitly.ui.theme.FitlyTheme
import com.example.fitly.viewmodel.SPViewModel
import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.fitly.R
import com.example.fitly.model.CurrentExercise
import com.example.fitly.model.TrainingDayState
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.util.Locale

enum class WorkoutState {
    Exercise,
    RestBetweenExercises
}

@androidx.annotation.OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(navController: NavController, spViewModel: SPViewModel, difficulty: String, trainingDaysCount: Int) {

    val context = LocalContext.current
    val exercises by remember { mutableStateOf(spViewModel.getExercisesForToday()) }
    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    var workoutState by remember { mutableStateOf(WorkoutState.Exercise) }
    var restTime by remember { mutableIntStateOf(60) }
    val currentExercise = exercises[currentExerciseIndex]
    val normalizedName =
        currentExercise.name.lowercase().replace(" ", "_").replace("-", "_")
    val videoResId =
        context.resources.getIdentifier(normalizedName, "raw", context.packageName)

    LaunchedEffect(workoutState) {
        if (workoutState == WorkoutState.RestBetweenExercises) {
            while (restTime > 0) {
                delay(1000)
                restTime -= 1
            }
            currentExerciseIndex++
            workoutState = WorkoutState.Exercise
            restTime = 60
        }
    }

    FitlyTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.workout_title),
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
                if (workoutState == WorkoutState.Exercise) {
                    Column {
                        MainButton(
                            arg = true,
                            text = if (currentExerciseIndex == exercises.size - 1) stringResource(R.string.finish) else stringResource(R.string.next),
                            onClick = {
                                if (currentExerciseIndex < exercises.size - 1) {
                                    workoutState = WorkoutState.RestBetweenExercises
                                } else {
                                    spViewModel.setTrainingDayState(TrainingDayState(false,  LocalDate.now().toString(), trainingDaysCount + 1))
                                    navController.navigate(TodayWorkoutScreen)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (workoutState == WorkoutState.Exercise) {
                    LoopingVideoPlayer(context, videoResId)
                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = getExerciseName(context, currentExercise.name),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(text = when(currentExercise.group) {
                        "Upper Body" -> {stringResource(R.string.upper_body)}
                        "Lower Body" -> {stringResource(R.string.lower_body)}
                        else -> {stringResource(R.string.lower_body)}
                    }, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(50.dp))
                    Text(
                        text = if (currentExercise.isStatic) {
                            "${currentExercise.value} sec"
                        } else {
                            val sets = when (difficulty.lowercase()) {
                                "beginner" -> 2
                                "intermediate" -> 3
                                "advanced" -> 4
                                else -> 2
                            }
                            "${currentExercise.value} X $sets"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                    ExerciseTimerDial(
                        exercise = currentExercise,
                        difficulty = difficulty,
                    )
                } else if (workoutState == WorkoutState.RestBetweenExercises) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(top = 230.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(220.dp, 60.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.rest_time_sec, restTime),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxHeight().padding(bottom = 16.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            MainButton(true, text = stringResource(R.string.skip), onClick = {
                                currentExerciseIndex++
                                workoutState = WorkoutState.Exercise
                                restTime = 60
                            })
                        }

                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun LoopingVideoPlayer(
    context: Context,
    videoResId: Int,
) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    LaunchedEffect(videoResId) {
        val mediaItem = MediaItem.fromUri(
            RawResourceDataSource.buildRawResourceUri(videoResId)
        )
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                setShutterBackgroundColor(backgroundColor)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        },
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}


@Composable
fun ExerciseTimerDial(
    exercise: CurrentExercise,
    difficulty: String,
    modifier: Modifier = Modifier
) {
    val difficultyMultiplier = when (difficulty.lowercase()) {
        "beginner" -> 2
        "intermediate" -> 3
        "advanced" -> 4
        else -> 2
    }

    Box(
        modifier = modifier
            .size(180.dp, 60.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (exercise.isStatic) {
            var isCountdown by remember(exercise) { mutableStateOf(true) }
            var countdownTime by remember(exercise) { mutableIntStateOf(10) }
            var timeLeft by remember(exercise) { mutableIntStateOf(exercise.value) }

            LaunchedEffect(exercise) {
                while (isCountdown) {
                    delay(1000)
                    countdownTime -= 1
                    if (countdownTime <= 0) {
                        isCountdown = false
                    }
                }
                while (timeLeft > 0) {
                    delay(1000)
                    timeLeft -= 1
                }
            }

            if (isCountdown) {
                Text(
                    text = String.format(Locale.getDefault(), stringResource(R.string.start_in), countdownTime),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                Text(
                    text = String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            var isCountdown by remember(exercise) { mutableStateOf(true) }
            var countdownTime by remember(exercise) { mutableIntStateOf(10) }
            var repsLeft by remember(exercise) { mutableIntStateOf(exercise.value) }
            var setsLeft by remember(exercise) { mutableIntStateOf(difficultyMultiplier) }
            val baseReps = exercise.value

            LaunchedEffect(exercise) {
                while (isCountdown) {
                    delay(1000)
                    countdownTime -= 1
                    if (countdownTime <= 0) {
                        isCountdown = false
                    }
                }
                while (setsLeft > 0) {
                    while (repsLeft > 0) {
                        delay(2000)
                        repsLeft -= 1
                    }
                    if (setsLeft > 1) {
                        repsLeft = baseReps
                    }
                    setsLeft -= 1
                }
            }

            if (isCountdown) {
                Text(
                    text = String.format(Locale.getDefault(), stringResource(R.string.start_in), countdownTime),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = String.format(Locale.getDefault(), "%02d : %02d", repsLeft, setsLeft),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

fun getExerciseName(context: Context, exerciseKey: String): String {
    val resId = context.resources.getIdentifier(exerciseKey, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else exerciseKey
}