package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.FinanceViewModel

@Composable
fun CalendarScreen(viewModel: FinanceViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Calendar View", style = MaterialTheme.typography.headlineMedium)
            Text("Coming Soon - Will show daily transaction markers.")
        }
    }
}
