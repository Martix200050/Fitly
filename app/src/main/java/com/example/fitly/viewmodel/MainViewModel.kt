package com.example.fitly.viewmodel


import androidx.lifecycle.ViewModel
import com.example.fitly.model.User
import kotlinx.coroutines.flow.MutableStateFlow



class MainViewModel() : ViewModel() {

    private val _user = MutableStateFlow(User())

    fun setUser(user: User) {
        _user.value = user
    }

    fun setMainGoal(goal: String) {
        _user.value.mainGoal = goal
    }

    fun setWorkoutPlan(muscleGroups: List<String>, planDuration: String, difficultyLevel: String) {
        _user.value.muscleGroups = muscleGroups
        _user.value.planDuration = planDuration
        _user.value.difficultyLevel = difficultyLevel
    }

    fun setTrainingDaysAndAlarm(list: List<String>, time: String) {
        _user.value.selectedDays = list
        _user.value.alarmTime = time
    }
    fun getUser(): User{
        return _user.value
    }

    fun canSelectDay(
        muscleGroups: List<String>,
        currentSelected: List<String>,
        dayToToggle: String,
        daysOfWeek: List<String>
    ): Boolean {
        val consecutiveCount = if (muscleGroups.size == 1) 2 else 3
        val newSelected = if (dayToToggle in currentSelected) {
            currentSelected - dayToToggle
        } else {
            currentSelected + dayToToggle
        }

            if (newSelected.size > (consecutiveCount + 1)) return false

        fun hasConsecutiveDays(indexes: List<Int>): Boolean {

            if (indexes.size < consecutiveCount) return false

            val doubled = indexes + indexes.map { it + 7 }

            for (i in 0 until doubled.size - (consecutiveCount - 1)) {
                var isConsecutive = true

                for (j in 1 until consecutiveCount) {
                    if (doubled[i + j] != doubled[i] + j) {
                        isConsecutive = false
                        break
                    }
                }

                if (isConsecutive) {
                    return true
                }
            }
            return false
        }

        return !hasConsecutiveDays(newSelected.map { daysOfWeek.indexOf(it) }.sorted())
    }


}
