package com.example.fitly.view

import android.app.Activity
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitly.navigation.Navigation
import com.example.fitly.viewmodel.SPViewModel
import com.example.fitly.viewmodel.SPViewModelFactory
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("my_prefs", MODE_PRIVATE)
        enableEdgeToEdge()
        setContent {
            val spViewModel: SPViewModel =
                viewModel(factory = SPViewModelFactory(sharedPreferences))
            DoubleBackToExit()
            Navigation(spViewModel)
        }
    }
}

@Composable
fun DoubleBackToExit() {
    var backPressedOnce by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(backPressedOnce) {
        if (backPressedOnce) {
            delay(2000)
            backPressedOnce = false
        }
    }

    BackHandler {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press again to exit", Toast.LENGTH_SHORT).show()
        }
    }
}

