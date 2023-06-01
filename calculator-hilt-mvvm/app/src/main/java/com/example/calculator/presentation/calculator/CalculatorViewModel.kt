package com.example.calculator.presentation.calculator

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.calculator.data.Repository
import com.example.calculator.domain.Operator
import com.example.calculator.framework.database.Calculation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CalculatorViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val expression: MutableLiveData<String>
        get() = repository.currentExpression

    val resultPreview: LiveData<String>
        get() = repository.currentResult

    fun onApply() {
        viewModelScope.launch {
            val newExpression = expression.value ?: ""
            val newResult = resultPreview.value ?: ""

            if (repository.apply() && newResult.isNotEmpty())
            {
                val calculation = Calculation(
                    expression = newExpression, result = newResult)

                saveCalculation(calculation)
            }
        }
    }
    private suspend fun saveCalculation(calculation: Calculation) {
        withContext(Dispatchers.IO) {
            repository.saveCalculation(calculation)
        }
    }
    fun onAddDecimal() { repository.addDecimal() }
    fun onAddDigit(digit: Char) { repository.addDigit(digit) }
    fun onAddOperator(operator: Operator) { repository.addOperator(operator) }
    fun onDelete() { repository.delete() }
    fun onClear() { repository.clear() }
}
