package com.example.calculator.interactors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.calculator.domain.Operator
import com.example.calculator.framework.StringSplitCalculator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent


interface StringCalculator {

    val expression: MutableLiveData<String>
    val result: LiveData<String>

    fun addDigit(digit: Char)
    fun addDecimal()
    fun addOperator(operator: Operator)
    fun delete()
    fun clear()
    fun apply(): Boolean
}
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class StringCalculatorModule {

    @Binds
    abstract fun bindStringCalculator(
        stringCalculatorImpl: StringSplitCalculator
    ): StringCalculator
}
