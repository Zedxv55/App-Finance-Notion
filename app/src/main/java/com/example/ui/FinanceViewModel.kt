package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAdvisorService
import com.example.data.Account
import com.example.data.FinanceDatabase
import com.example.data.FinanceRepository
import com.example.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository
    private val aiService = AiAdvisorService()

    val allAccounts: StateFlow<List<Account>>
    val allTransactions: StateFlow<List<Transaction>>

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse
    
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading

    init {
        val database = FinanceDatabase.getDatabase(application)
        repository = FinanceRepository(database.financeDao())

        allAccounts = repository.allAccounts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allTransactions = repository.allTransactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        // Initialize with default accounts if empty
        viewModelScope.launch {
            val accounts = repository.allAccounts.first()
            if (accounts.isEmpty()) {
                addAccount(Account(name = "Bank Account", type = "Bank", initialBalance = 0.0, color = "#2563eb"))
                addAccount(Account(name = "Cash", type = "Cash", initialBalance = 0.0, color = "#16a34a"))
            }
        }
    }

    fun addAccount(account: Account) = viewModelScope.launch {
        repository.insertAccount(account)
    }

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    fun deleteTransaction(id: Int) = viewModelScope.launch {
        repository.deleteTransaction(id)
    }

    fun askAiAdvisor(userMessage: String) = viewModelScope.launch {
        _isAiLoading.value = true
        _aiResponse.value = "Thinking..."
        val transactions = allTransactions.value
        val result = aiService.generateFinancialAdvice(transactions, userMessage)
        _aiResponse.value = result
        _isAiLoading.value = false
    }
}
