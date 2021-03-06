package com.jl_demo.calculatorbottomsheet.calculator.helpers

import android.content.Context
import android.widget.TextView
import com.jl_demo.calculatorbottomsheet.MainActivity
import com.jl_demo.calculatorbottomsheet.R
import com.jl_demo.calculatorbottomsheet.database.CalcHistoryModel
import com.jl_demo.calculatorbottomsheet.database.DB_Controller
import timber.log.Timber

/**
 * This is the front facing class, the wrapper, the controller for the calculator.
 * All functions triggered by the UI are here
 */
class CalculatorImpl(val formulaView: TextView, val operatorView: TextView, val numberView: TextView, val context: Context) {
    var displayedNumber: String? = null
    var displayedFormula: String? = null
    var lastKey: String? = null
    private var mLastOperation: String? = null

    private var mIsFirstOperation = false
    private var mResetValue = false
    private var mBaseValue = 0.0
    private var mSecondValue = 0.0

    init {
        resetValues()
        setValue("0")
        setFormula("")
    }

    private fun resetValueIfNeeded() {
        if (mResetValue)
            displayedNumber = "0"

        mResetValue = false
    }

    private fun resetValues() {
        mBaseValue = 0.0
        mSecondValue = 0.0
        mResetValue = false
        mLastOperation = ""
        displayedNumber = ""
        displayedFormula = ""
        mIsFirstOperation = true
        lastKey = ""
    }

    fun setValue(value: String) {
        numberView.text = value
        displayedNumber = value
    }

    private fun setFormula(value: String) {
        formulaView.text = value
        displayedFormula = value
    }

    private fun setOperator(value: String){
        operatorView.text = value
    }

    private fun updateFormula() {
        val first = Formatter.doubleToString(mBaseValue)
        val second = Formatter.doubleToString(mSecondValue)
        val sign = getSign(mLastOperation)

        Timber.d("JL_ Sign function is $sign")
        if (sign == "√") {
            setFormula(sign + first)
        } else if (sign == "!") {
            setFormula(first + sign)
        } else if (!sign.isEmpty()) {
            var formula = first + sign + second
            setFormula(formula)
        }
    }

    fun addDigit(number: Int) {
        val currentValue = displayedNumber
        val newValue = formatString(currentValue!! + number)
        setValue(newValue)
    }

    private fun formatString(str: String): String {
        // if the number contains a decimal, do not try removing the leading zero anymore, nor add group separator
        // it would prevent writing values like 1.02
        if (str.contains(".")) {
            return str
        }

        val doubleValue = Formatter.stringToDouble(str)
        return Formatter.doubleToString(doubleValue)
    }

    private fun updateResult(value: Double) {
        setValue(Formatter.doubleToString(value))
        mBaseValue = value
    }

    private fun getDisplayedNumberAsDouble() = Formatter.stringToDouble(displayedNumber!!)

    fun handleResult() {
        mSecondValue = getDisplayedNumberAsDouble()
        calculateResult()
        mBaseValue = getDisplayedNumberAsDouble()
    }

    private fun handleRoot() {
        mBaseValue = getDisplayedNumberAsDouble()
        calculateResult()
    }

    private fun handleFactorial() {
        mBaseValue = getDisplayedNumberAsDouble()
        calculateResult()
    }

    private fun calculateResult() {
        updateFormula()

        val operation = Calculate(mLastOperation!!, mBaseValue, mSecondValue).operation()
        if (operation != null) {
            updateResult(operation)
            //Save entry to the Room Database
            DB_Controller().addCalcEntry(
                database = MainActivity.database,
                entry = CalcHistoryModel(
                    calcHistDbId = 0,
                    formula = displayedFormula?:"NA",
                    result = displayedNumber?:"NA"
                )
            )
        }


        mIsFirstOperation = false
    }

    fun handleOperation(operation: String) {
        if (lastKey == DIGIT && operation != ROOT && operation != FACTORIAL) {
            handleResult()
        }

        mResetValue = true
        lastKey = operation
        mLastOperation = operation
        setOperator(getSign(operation))


        if (operation == ROOT) {
            handleRoot()
            mResetValue = false
        }
        if (operation == FACTORIAL) {
            handleFactorial()
            mResetValue = false
        }
    }

    fun handleClear() {
        if (displayedNumber.equals(NAN)) {
            handleReset()
        } else {
            val oldValue = displayedNumber
            var newValue = "0"
            val len = oldValue!!.length
            var minLen = 1
            if (oldValue.contains("-"))
                minLen++

            if (len > minLen) {
                newValue = oldValue.substring(0, len - 1)
            }

            newValue = newValue.replace("\\.$".toRegex(), "")
            newValue = formatString(newValue)
            setValue(newValue)
        }
    }

    fun handleReset() {
        resetValues()
        setValue("0")
        setFormula("")
        setOperator("")
    }

    fun handleEquals() {
        if (lastKey == EQUALS)
            calculateResult()

        if (lastKey != DIGIT)
            return

        mSecondValue = getDisplayedNumberAsDouble()
        calculateResult()
        lastKey = EQUALS
        setOperator("")
    }

    private fun decimalClicked() {
        var value = displayedNumber
        if (!value!!.contains(".")) {
            value += "."
        }
        setValue(value)
    }

    private fun zeroClicked() {
        val value = displayedNumber
        if (value != "0") {
            addDigit(0)
        }
    }

    private fun getSign(lastOperation: String?) = when (lastOperation) {
        PLUS -> "+"
        MINUS -> "-"
        MULTIPLY -> "*"
        DIVIDE -> "/"
        PERCENT -> "%"
        POWER -> "^"
        ROOT -> "√"
        FACTORIAL -> "!"
        else -> ""
    }

    fun numpadClicked(id: Int) {
        if (lastKey == EQUALS) {
            mLastOperation =
                EQUALS
        }

        lastKey = DIGIT
        resetValueIfNeeded()

        when (id) {
            R.id.btn_decimal -> decimalClicked()
            R.id.btn_0 -> zeroClicked()
            R.id.btn_1 -> addDigit(1)
            R.id.btn_2 -> addDigit(2)
            R.id.btn_3 -> addDigit(3)
            R.id.btn_4 -> addDigit(4)
            R.id.btn_5 -> addDigit(5)
            R.id.btn_6 -> addDigit(6)
            R.id.btn_7 -> addDigit(7)
            R.id.btn_8 -> addDigit(8)
            R.id.btn_9 -> addDigit(9)
        }
    }
}