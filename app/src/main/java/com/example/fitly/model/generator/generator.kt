package com.example.fitly.model.generator

import com.example.fitly.model.BaseExercise
import com.example.fitly.model.CurrentExercise

object TrainingPlanGenerator {

     fun calculateBMI(height: Int, weight: Int): Double {
        val heightInMeters = height.toDouble() / 100
        return weight.toDouble() / (heightInMeters * heightInMeters)
    }

    private fun adjustReps(baseReps: Int, bmi: Double, gender: String): Int {
        val bmiFactor = when {
            bmi < 18.5 -> 0.8
            bmi in 18.5..24.9 -> 1.0
            bmi in 25.0..29.9 -> 0.9
            else -> 0.8
        }

        val genderFactor = if (gender.lowercase() == "female") 0.9 else 1.0

        val adjusted = baseReps * bmiFactor * genderFactor

        return adjusted.toInt().coerceAtLeast(10)
    }

    private fun adjustStatic(baseSeconds: Int, bmi: Double, gender: String): Int {
        val bmiFactor = when {
            bmi < 18.5 -> 0.8
            bmi in 18.5..24.9 -> 1.0
            bmi in 25.0..29.9 -> 0.9
            else -> 0.8
        }

        val genderFactor = if (gender.lowercase() == "female") 0.9 else 1.0

        val adjusted = baseSeconds * bmiFactor * genderFactor

        return adjusted.toInt().coerceAtLeast(20)
    }

    fun generatePlan(
        gender: String,
        weight: String,
        height: String,
        difficulty: String,
        muscleGroups: List<String>,
        baseExercises: List<BaseExercise>
    ): List<CurrentExercise> {
        val w = weight.toInt()
        val h = height.toInt()

        val bmi = calculateBMI(h, w)

        return baseExercises
            .filter { it.group in muscleGroups }
            .map { exercise ->
                val baseValue = when (difficulty.lowercase()) {
                    "beginner" -> exercise.beginner
                    "intermediate" -> exercise.intermediate
                    "advanced" -> exercise.advanced
                    else -> exercise.beginner
                }

                val finalValue = if (exercise.isStatic) {
                    adjustStatic(baseValue, bmi, gender)
                } else {
                    adjustReps(baseValue, bmi, gender)
                }

                CurrentExercise(
                    group = exercise.group,
                    name = exercise.name,
                    isStatic = exercise.isStatic,
                    value = finalValue
                )
            }
    }
}