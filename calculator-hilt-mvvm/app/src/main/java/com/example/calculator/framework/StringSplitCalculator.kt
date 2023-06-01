package com.example.calculator.framework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.calculator.domain.Operator
import com.example.calculator.interactors.StringCalculator
import java.math.BigDecimal
import javax.inject.Inject

/**
 * An implementation of [StringCalculator] that recursively splits the current
 * expression into individual numerical terms and aggregates them together
 * following the PEMDAS Order of Operations to produce the calculation's result.
 * 
 * If the current expression is valid, applying the expression will replace the
 * current expression with its known result. Otherwise, the result will instead
 * be changed to the appropriate error message.
 */
class StringSplitCalculator @Inject constructor() : StringCalculator {
    override val expression = MutableLiveData("")

    private val _result = MediatorLiveData<String>().apply {
        addSource(this@StringSplitCalculator.expression) { value = compute() }
    }

    override val result: LiveData<String>
        get() = _result

    private var errorMessage = ""

    override fun apply(): Boolean {
        return if (errorMessage.isEmpty() && !_result.value.isNullOrEmpty()) {
            this.expression.value = _result.value
            _result.value = ""
            errorMessage = INVALID_EXPRESSION_MESSAGE

            true
        } else {
            _result.value = errorMessage
            false
        }
    }
    private fun compute(): String {
        var newResult = ""
        var newErrorMessage = ""

        // Compute the result and error message using the order of operations
        try {
            val orderOfOperations = Operator.values().asList()
            this.expression.value?.let {
                newResult =
                    if (it != getLastTerm()) {
                        compute(it, orderOfOperations)?.toEngineeringString() ?: ""
                    } else {
                        ""
                    }
            }

            if (newResult.isEmpty()) newErrorMessage = INVALID_EXPRESSION_MESSAGE

        } catch (e: ArithmeticException) {
            e.printStackTrace()
            newErrorMessage = DIVIDE_BY_ZERO_MESSAGE
        }

        errorMessage = newErrorMessage

        return newResult
    }

    private fun compute(
        subExpression: String,
        orderOfOperations: List<Operator>,
    ): BigDecimal? {

        val operator = orderOfOperations.last()
        val subExpressions = subExpression.split(operator.symbol)
        val terms: List<BigDecimal?> =
            if (orderOfOperations.size > 1) {
                subExpressions.map { compute(it, orderOfOperations.dropLast(1)) }
            } else {
                subExpressions.map { it.toBigDecimalOrNull() }
            }
        return if (terms.contains(null)) null else terms.reduce(operator.function)
    }
    private fun getLastTerm(): String {
        val operatorSymbols = Operator.values().map { it.symbol }
        return this.expression.value?.takeLastWhile { !operatorSymbols.contains(it) } ?: ""
    }

    override fun addDecimal() {
        if (!getLastTerm().contains(DECIMAL_SIGN)) {
            this.expression.value += DECIMAL_SIGN
        }
    }

    override fun addDigit(digit: Char) {
        val lastTerm = getLastTerm()
        val hasRedundantZero = !lastTerm.contains(DECIMAL_SIGN)
                    && lastTerm.toBigDecimalOrNull() == BigDecimal.ZERO

        this.expression.value = if (hasRedundantZero) {
            this.expression.value?.dropLast(1).plus(digit)
        } else {
            this.expression.value?.plus(digit)
        }
    }

    override fun addOperator(operator: Operator) {
        var newExpression = this.expression.value ?: ""

        // Remove the trailing decimal point, if present
        if (newExpression.endsWith(DECIMAL_SIGN)) {
            newExpression = newExpression.dropLast(1)
        }

        var operatorToAdd = operator.symbol

        when (newExpression.lastOrNull()) {
            null, Operator.MULTIPLY.symbol, Operator.DIVIDE.symbol -> {
                if (operator == Operator.SUBTRACT) operatorToAdd = NEGATIVE_SIGN
            }
        }

        if (operatorToAdd != NEGATIVE_SIGN) {
            newExpression = newExpression.dropLastWhile { !it.isDigit() }
        }

        if (operatorToAdd == NEGATIVE_SIGN || newExpression.isNotEmpty()) {
            newExpression = newExpression.plus(operatorToAdd)
        }

        this.expression.value = newExpression
    }

    override fun delete() {
        this.expression.value = this.expression.value?.dropLast(1) ?: ""
    }

    override fun clear() {
        this.expression.value = ""
    }

    private companion object {

        const val NEGATIVE_SIGN = '-'
        const val DECIMAL_SIGN = '.'

        const val INVALID_EXPRESSION_MESSAGE = "Invalid expression"
        const val DIVIDE_BY_ZERO_MESSAGE = "Can't divide by zero"
    }
}
