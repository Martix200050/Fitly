package com.example.fitly.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitly.model.User
import com.example.fitly.model.generator.TrainingPlanGenerator
import com.example.fitly.navigation.FitnessGoalsScreen
import com.example.fitly.ui.theme.FitlyTheme
import com.example.fitly.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import com.example.fitly.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutYouScreen(navController: NavController, viewModel: MainViewModel) {

    var errorMessageWithAgree by remember { mutableStateOf(false) }
    var agree by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var currentGender by remember { mutableStateOf("") }
    val isItCorrect =
        currentGender.isNotEmpty() &&
                name.isNotEmpty() && name.length <= 10 &&
                age.toIntOrNull()?.let { it in 16..80 } == true &&
                height.toIntOrNull()?.let { it in 150..220 } == true &&
                weight.toIntOrNull()?.let { it in 40..200 } == true &&
                TrainingPlanGenerator.calculateBMI(height.toInt(), weight.toInt()) in 17.9..38.1

    @Composable
    fun GenderButton(gender: String) {
        Box(
            modifier = Modifier
                .clickable { currentGender = gender }
                .clip(RoundedCornerShape(6.dp))
                .background(if (gender == currentGender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                .size(170.dp, 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                color = if (gender == currentGender) Color.White else MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                text = gender
            )
        }
    }
    FitlyTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            color = MaterialTheme.colorScheme.onBackground,
                            text = stringResource(R.string.about_you),
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        stringResource(R.string.name),
                        value = name
                    ) { if (it.length <= 10) name = it }
                    CustomTextField(stringResource(R.string.age), value = age) {
                        if (it.all { it.isDigit() }) {
                            if (it.length <= 2) age = it
                        }
                    }
                    Spacer(modifier = Modifier.height(0.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GenderButton(stringResource(R.string.male))
                        GenderButton(stringResource(R.string.female))
                    }
                    Text(
                        text = stringResource(R.string.gender),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    CustomTextField(stringResource(R.string.height_cm), value = height) {
                        if (it.all { it.isDigit() }) {
                            if (it.length <= 3) height = it
                        }
                    }
                    CustomTextField(stringResource(R.string.weight_kg), value = weight) {
                        if (it.all { it.isDigit() }) {
                            if (it.length <= 3) weight = it
                        }
                    }
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
                        Text(
                            if (height.isNotEmpty() && weight.isNotEmpty()) "${stringResource(R.string.bmi)}: %.1f".format(
                                TrainingPlanGenerator.calculateBMI(height.toInt(), weight.toInt())
                            ) else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Column {
                    Error(
                        errorMessageWithAgree,
                        stringResource(R.string.please_agree_to_the_terms_and_conditions),
                        onTimeout = { errorMessageWithAgree = false })
                    Error(
                        errorMessage,
                        stringResource(R.string.the_biggest_error),
                        onTimeout = { errorMessage = false }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agree,
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.surface,
                                checkmarkColor = Color(0xFFF8FAFC)
                            ),
                            onCheckedChange = { agree = !agree },
                        )
                        Text(
                            stringResource(R.string.confirm_age_and_accept),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Text(
                            stringResource(R.string.terms),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(start = 3.dp)
                                .clickable { /* the terms of use */ })
                    }
                    MainButton(
                        arg = isItCorrect,
                        text = stringResource(R.string.continue_label),
                        onClick = {
                            if (agree) {
                                if (isItCorrect) {
                                    viewModel.setUser(
                                        User(
                                            name,
                                            age,
                                            currentGender,
                                            height,
                                            weight
                                        )
                                    )
                                    navController.navigate(FitnessGoalsScreen)
                                } else errorMessage =
                                    true
                            } else {
                                errorMessageWithAgree = true
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    FitlyTheme {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = if (label == "Name") KeyboardType.Text else KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
            ),
            value = value,
            label = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun Error(visible: Boolean, message: String, onTimeout: () -> Unit) {
    if (visible) {
        LaunchedEffect(key1 = true) {
            delay(3000)
            onTimeout()
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        Box(Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .fillMaxWidth()

            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    text = message
                )
            }
        }
    }
}

@Composable
fun MainButton(arg: Boolean, text: String, onClick: () -> Unit) {
    Button(
        onClick = {
            if (arg) onClick()
        },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (arg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (arg) Color.White else MaterialTheme.colorScheme.secondary
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
}
