package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.FinanceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    
    val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
    val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val balance = income - expense

    val formatter = NumberFormat.getCurrencyInstance(Locale("th", "TH"))

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Good morning,", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onBackground)
        Text("Here's your summary.", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Balance", style = MaterialTheme.typography.titleMedium)
                Text(formatter.format(balance), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.IncomeContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Income", tint = com.example.ui.theme.IncomeColor)
                    }
                    Column {
                        Text("Income", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatter.format(income), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.IncomeColor)
                    }
                }
            }
            Card(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.ExpenseContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = "Expense", tint = com.example.ui.theme.ExpenseColor)
                    }
                    Column {
                        Text("Expense", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatter.format(expense), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExpenseColor)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "RECENT TRANSACTIONS",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions) { tx ->
                    val isIncome = tx.type == "Income"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = { Text(tx.name, fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("${tx.category} • ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(tx.date))}") },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${if (isIncome) "+" else "-"}${formatter.format(tx.amount)}",
                                        color = if (isIncome) com.example.ui.theme.IncomeColor else com.example.ui.theme.ExpenseColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = { viewModel.deleteTransaction(tx.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
