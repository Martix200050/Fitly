package com.example.fitly.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitly.R
import com.example.fitly.navigation.WorkoutPlanScreen
import com.example.fitly.ui.theme.FitlyTheme
import com.example.fitly.viewmodel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessGoals(navController: NavController, viewModel: MainViewModel) {

    var errorMessage by remember { mutableStateOf(false) }
    var isItPicked by remember { mutableStateOf("") }

    @Composable
    fun GoalItem(icon: Int, title: String, description: String) {
        Card(
            modifier = Modifier
                .clickable {
                    isItPicked = if (isItPicked == title) "" else title
                    errorMessage = false
                }
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Card(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isItPicked == title) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) // bg-[#e7edf4]
                ) {
                    Icon(
                        tint = if (isItPicked == title) Color.White else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp),
                        painter = painterResource(id = icon),
                        contentDescription = title
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF49739C),
                        maxLines = 2
                    )
                }
            }
        }
    }

    FitlyTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            color = MaterialTheme.colorScheme.onBackground,
                            text = stringResource(R.string.fitness_goals),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                tint = MaterialTheme.colorScheme.onBackground,
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button)
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
            ) {
                Text(
                    text = stringResource(R.string.main_goal_question),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                )
                Text(
                    text = stringResource(R.string.workout_plan_tailor_text),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                GoalItem(
                    title = stringResource(R.string.lose_weight_title),
                    description = stringResource(R.string.lose_weight_description),
                    icon = R.drawable.icons8_lose_weight_64
                )
                GoalItem(
                    title = stringResource(R.string.gain_muscle_title),
                    description = stringResource(R.string.gain_muscle_description),
                    icon = R.drawable.icons8_muscle_flexing_50
                )
                GoalItem(
                    title = stringResource(R.string.stay_healthy_title),
                    description = stringResource(R.string.stay_healthy_description),
                    icon = R.drawable.icons8_healthy_50
                )
                GoalItem(
                    title = stringResource(R.string.improve_flexibility_title),
                    description = stringResource(R.string.improve_flexibility_description),
                    icon = R.drawable.icons8_yoga_50
                )

                Spacer(modifier = Modifier.weight(1f))
                Error(errorMessage, stringResource(R.string.please_choose_goal), onTimeout = { errorMessage = false })
                Button(
                    onClick = {
                        if (isItPicked.isNotEmpty()) {
                            viewModel.setMainGoal(isItPicked)
                            navController.navigate(WorkoutPlanScreen)
                        } else errorMessage = true
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isItPicked.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.continue_button),
                        fontWeight = FontWeight.Bold,
                        color = if (isItPicked.isNotEmpty()) Color.White else MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
