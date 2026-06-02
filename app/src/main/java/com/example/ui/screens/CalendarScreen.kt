package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FinanceViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed (Sunday = 0)

    val currentMonthStr = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
    val year = calendar.get(Calendar.YEAR)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("$currentMonthStr $year", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Days of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            for (day in days) {
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(firstDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f)) // Empty box for day offset
            }
            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                
                // Check for transactions on this day
                val startOfDay = Calendar.getInstance().apply {
                    set(year, calendar.get(Calendar.MONTH), day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val endOfDay = startOfDay + 86400000L - 1
                
                val dayTransactions = transactions.filter { it.date in startOfDay..endOfDay }
                val hasIncome = dayTransactions.any { it.type == "Income" }
                val hasExpense = dayTransactions.any { it.type == "Expense" }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            val selected = Calendar.getInstance().apply {
                                set(year, calendar.get(Calendar.MONTH), day)
                            }
                            selectedDate = selected
                            showBottomSheet = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = day.toString(), style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 2.dp)) {
                            if (hasIncome) {
                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF2E7D32)))
                            }
                            if (hasIncome && hasExpense) Spacer(modifier = Modifier.width(2.dp))
                            if (hasExpense) {
                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFC62828)))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedDate != null) {
        val startOfDay = Calendar.getInstance().apply {
            timeInMillis = selectedDate!!.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfDay = startOfDay + 86400000L - 1
        
        val dayTransactions = transactions.filter { it.date in startOfDay..endOfDay }
        val formatter = NumberFormat.getCurrencyInstance(Locale("th", "TH"))

        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Transactions for ${selectedDate!!.get(Calendar.DAY_OF_MONTH)} ${selectedDate!!.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (dayTransactions.isEmpty()) {
                    Text("No transactions for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(32.dp))
                } else {
                    LazyColumn {
                        items(dayTransactions) { tx ->
                            val isIncome = tx.type == "Income"
                            ListItem(
                                headlineContent = { Text(tx.name) },
                                supportingContent = { Text(tx.category) },
                                trailingContent = {
                                    Text(
                                        text = "${if (isIncome) "+" else "-"}${formatter.format(tx.amount)}",
                                        color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
