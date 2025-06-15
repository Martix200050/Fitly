package com.example.fitly.viewmodel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.fitly.model.BaseExercises
import com.example.fitly.model.User
import com.example.fitly.model.generator.TrainingPlanGenerator
import com.google.gson.Gson
import androidx.core.content.edit
import com.example.fitly.model.CurrentExercise
import com.example.fitly.model.CurrentUser
import com.example.fitly.model.TrainingDayState
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.O)
class SPViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val gson = Gson()

    private val _isRegistered = MutableStateFlow(getRegistered())
    val isRegistered: StateFlow<Boolean> = _isRegistered
    private val _trainingDayState = MutableStateFlow(getTrainingDayState())
    val trainingDayState: StateFlow<TrainingDayState> = _trainingDayState
    private val _currentUser = MutableStateFlow(getCurrentUser())
    val currentUser: StateFlow<CurrentUser> = _currentUser
    val language = MutableStateFlow(getAppLanguage())

    init {
        checkTrainingAvailability()
    }
    fun setNewAlarmTime(time: String) {
        val json = sharedPreferences.getString(CURRENT_USER_KEY, null)
        if (json != null) {
            val type = object : TypeToken<CurrentUser>() {}.type
            val currentUser: CurrentUser = gson.fromJson(json, type)
            val updatedUser = currentUser.copy(alarmTime = time)
            sharedPreferences.edit {
                putString(CURRENT_USER_KEY, gson.toJson(updatedUser))
            }
            _currentUser.value = updatedUser
        }
    }
    fun setAppLanguage(language: String, context: Context) {
        sharedPreferences.edit(commit = true) { putString(LANGUAGE_KEY, language) }
        exitFromActivity(context)
    }
    fun getAppLanguage(): String {
        return sharedPreferences.getString(LANGUAGE_KEY, "system") ?: "system"
    }
    fun exitFromActivity(context: Context) {
        (context as? Activity)?.finishAffinity()
        exitProcess(0)
    }
    fun deleteAllDataAndExit(context: Context) {
        sharedPreferences.edit(commit = true) { clear() }
        exitFromActivity(context)
    }
    private fun checkTrainingAvailability() {
        val todayDate = LocalDate.now()
        val todayDayOfWeek = todayDate.dayOfWeek.name

        val lastDate = trainingDayState.value.lastTrainingDate
            .takeIf { it.isNotEmpty() }
            ?.let {
                runCatching { LocalDate.parse(it) }.getOrNull()
            }

        val isTrainingDay = todayDayOfWeek in _currentUser.value.selectedDays.map { it.uppercase() }

        val isAlreadyCompletedToday = lastDate == todayDate

        val isLastTrainingBeforeToday = lastDate?.isBefore(todayDate) ?: true

        val canTrain = isTrainingDay && !isAlreadyCompletedToday && isLastTrainingBeforeToday

        _trainingDayState.value = TrainingDayState(
            canTrainToday = canTrain,
            lastTrainingDate = trainingDayState.value.lastTrainingDate
        )
    }

    fun calculateAndSaveExercisesAndUser(user: User) {
        sharedPreferences.edit(commit = true) {
            putString(
                EXERCISES_KEY, gson.toJson(
                    TrainingPlanGenerator.generatePlan(
                        user.gender,
                        user.weight,
                        user.height,
                        user.difficultyLevel,
                        user.muscleGroups,
                        baseExercises = BaseExercises.exercises
                    )
                )
            )
            putString(
                CURRENT_USER_KEY,
                gson.toJson(
                    CurrentUser(
                        user.name,
                        user.mainGoal,
                        user.planDuration,
                        user.selectedDays,
                        user.muscleGroups,
                        user.difficultyLevel,
                        user.alarmTime
                    )
                )
            )
        }
        _trainingDayState.value = getTrainingDayState()
        _currentUser.value = getCurrentUser()
        checkTrainingAvailability()
    }

    fun setRegistered(boolean: Boolean) {
        sharedPreferences.edit {
            putBoolean(IS_REGISTERED_KEY, boolean)
        }
    }
    fun getRegistered(): Boolean {
        return try {
            sharedPreferences.getBoolean(IS_REGISTERED_KEY, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setTrainingDayState(trainingDayState: TrainingDayState) {
        sharedPreferences.edit {
            putString(
                TRAINING_DAY_STATE_KEY,
                gson.toJson(trainingDayState)
            )
        }
        _trainingDayState.value = getTrainingDayState()
    }

    fun getTrainingDayState(): TrainingDayState {
        val json = sharedPreferences.getString(TRAINING_DAY_STATE_KEY, null) ?: return TrainingDayState()
        val type = object : TypeToken<TrainingDayState>() {}.type
        return gson.fromJson(json, type)
    }

    fun getExercisesForToday(): List<CurrentExercise> {
        if (_currentUser.value.muscleGroups.isEmpty()) return emptyList()

        val json = sharedPreferences.getString(EXERCISES_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<CurrentExercise>>() {}.type
        val allExercises: List<CurrentExercise> = gson.fromJson(json, type)

        val lastGroupIndex = sharedPreferences.getInt(LAST_GROUP_INDEX_KEY, -1)

        val nextGroupIndex = (lastGroupIndex + 1) % _currentUser.value.muscleGroups.size

        sharedPreferences.edit { putInt(LAST_GROUP_INDEX_KEY, nextGroupIndex) }

        val todayGroup = _currentUser.value.muscleGroups[nextGroupIndex]

        return allExercises.filter { it.group.equals(todayGroup, ignoreCase = true) }
    }

    fun getCurrentUser(): CurrentUser {
        val json = sharedPreferences.getString(CURRENT_USER_KEY, null) ?: return CurrentUser()
        val type = object : TypeToken<CurrentUser>() {}.type
        return gson.fromJson(json, type)
    }

    companion object {
        private const val LANGUAGE_KEY = "language"
        private const val EXERCISES_KEY = "EXERCISES_JSON"
        private const val CURRENT_USER_KEY = "CURRENT_USER_JSON"
        private const val IS_REGISTERED_KEY = "IS_REGISTERED_JSON"
        private const val LAST_GROUP_INDEX_KEY = "LAST_GROUP_INDEX_JSON"
        private const val TRAINING_DAY_STATE_KEY = "TRAINING_DAY_STATE_JSON"
    }
}
