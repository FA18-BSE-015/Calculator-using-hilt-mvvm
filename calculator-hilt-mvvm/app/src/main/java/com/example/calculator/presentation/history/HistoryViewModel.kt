package com.example.calculator.presentation.history

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.calculator.data.Repository
import com.example.calculator.framework.database.Calculation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for HistoryFragment.
 */
class HistoryViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val calculations: LiveData<List<Calculation>> = repository.getAllCalculations()

    private val _loadSelectedCalculation = MutableLiveData<String>()

    val loadSelectedCalculation: LiveData<String>
        get() = _loadSelectedCalculation

    private val _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackbarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun onLoadCalculation(id: Long) {
        viewModelScope.launch {
            _loadSelectedCalculation.value = getSelectedExpression(id)
        }
    }

    private suspend fun getSelectedExpression(id: Long): String {
        return withContext(Dispatchers.IO) {
            repository.getCalculation(id)?.expression ?: ""
        }
    }

    fun doneLoadingCalculation() {
        _loadSelectedCalculation.value = null
    }

    fun onClearHistory() {
        viewModelScope.launch { clearHistory() }

        // Notify the user that the history list has been successfully cleared
        _showSnackbarEvent.value = true
    }

    private suspend fun clearHistory() {
        withContext(Dispatchers.IO) { repository.clearHistory() }
    }
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }
}