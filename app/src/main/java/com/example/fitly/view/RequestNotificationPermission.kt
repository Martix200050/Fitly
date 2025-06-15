package com.example.fitly.view

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.fitly.R
import com.example.fitly.model.worker.NotificationWorker


@Composable
fun RequestNotificationPermission(
    hour: Int,
    minute: Int
) {
    val context = LocalContext.current

    val permissionDeniedText = stringResource(R.string.permission_denied)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                NotificationWorker.scheduleFirstWork(context, hour, minute)
            } else {
                Toast.makeText(context, permissionDeniedText, Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationWorker.scheduleFirstWork(context, hour, minute)
            }
        } else {
            NotificationWorker.scheduleFirstWork(context, hour, minute)
        }
    }
}