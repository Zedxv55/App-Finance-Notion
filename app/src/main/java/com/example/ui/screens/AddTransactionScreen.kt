package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Transaction
import com.example.ui.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: FinanceViewModel, onNavigateBack: () -> Unit) {
    var type by remember { mutableStateOf("Expense") }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    
    val accounts by viewModel.allAccounts.collectAsStateWithLifecycle()
    var selectedAccountId by remember { mutableStateOf<Int?>(null) }
    var toAccountId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Add Transaction", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SegmentedButton(
                onClick = { type = "Income" },
                selected = type == "Income",
                text = "Income"
            )
            SegmentedButton(
                onClick = { type = "Expense" },
                selected = type == "Expense",
                text = "Expense"
            )
            SegmentedButton(
                onClick = { type = "Transfer" },
                selected = type == "Transfer",
                text = "Transfer"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate)),
            onValueChange = { },
            readOnly = true,
            label = { Text("Date") },
            trailingIcon = { 
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (type != "Transfer") {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (type != "Transfer") {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(if (type == "Income") "Category (e.g. Salary)" else "Category (e.g. Food)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = source,
                onValueChange = { source = it },
                label = { Text("Source / Destination") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            val selectedAccount = accounts.find { it.id == selectedAccountId }?.name ?: "Select Account"
            OutlinedTextField(
                value = selectedAccount,
                onValueChange = {},
                readOnly = true,
                label = { Text(if (type == "Transfer") "From Account" else "Account") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                accounts.forEach { acc ->
                    DropdownMenuItem(
                        text = { Text(acc.name) },
                        onClick = {
                            selectedAccountId = acc.id
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        if (type == "Transfer") {
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = !toExpanded }
            ) {
                val toAccount = accounts.find { it.id == toAccountId }?.name ?: "Select Destination Account"
                OutlinedTextField(
                    value = toAccount,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To Account") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false }
                ) {
                    accounts.forEach { acc ->
                        DropdownMenuItem(
                            text = { Text(acc.name) },
                            onClick = {
                                toAccountId = acc.id
                                toExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                if (parsedAmount > 0) {
                    if (type == "Transfer") {
                        val fromAcc = accounts.find { it.id == selectedAccountId }
                        val toAcc = accounts.find { it.id == toAccountId }
                        if (fromAcc != null && toAcc != null && fromAcc.id != toAcc.id) {
                            viewModel.addTransaction(
                                Transaction(
                                    name = "Transfer to ${toAcc.name}",
                                    type = "Expense",
                                    amount = parsedAmount,
                                    date = selectedDate,
                                    accountId = fromAcc.id,
                                    category = "Transfer Out",
                                    source = "",
                                    note = note
                                )
                            )
                            viewModel.addTransaction(
                                Transaction(
                                    name = "Transfer from ${fromAcc.name}",
                                    type = "Income",
                                    amount = parsedAmount,
                                    date = selectedDate,
                                    accountId = toAcc.id,
                                    category = "Transfer In",
                                    source = "",
                                    note = note
                                )
                            )
                            onNavigateBack()
                        }
                    } else if (name.isNotBlank()) {
                        val accId = selectedAccountId ?: accounts.firstOrNull()?.id ?: 0
                        viewModel.addTransaction(
                            Transaction(
                                name = name,
                                type = type,
                                amount = parsedAmount,
                                date = selectedDate,
                                accountId = accId,
                                category = if(category.isBlank()) "General" else category,
                                source = source,
                                note = note
                            )
                        )
                        onNavigateBack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Transaction")
        }
    }
}

@Composable
fun SegmentedButton(onClick: () -> Unit, selected: Boolean, text: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text)
    }
}
