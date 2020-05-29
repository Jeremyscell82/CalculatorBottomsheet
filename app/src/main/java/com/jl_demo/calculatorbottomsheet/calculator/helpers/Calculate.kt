package com.jl_demo.calculatorbottomsheet.calculator.helpers

/**
 * Holds all calculator functions/calculations/operations
 * This class should not need updating 3/1
 */
class Calculate(val id: String, 
                val baseValue: Double, 
                val secondValue: Double) {

    fun operation(): Double? {
        return when (id) {
            PLUS -> addOperation()
            MINUS -> subtractOperation()
            DIVIDE -> divideOperation()
            MULTIPLY -> multiplyOperation()
            PERCENT -> percentOperation()
            POWER -> powerOperation()
            ROOT -> rootOperation()
            FACTORIAL -> factorialOperation()
            else -> null
        }
    }

    private fun addOperation() = baseValue + secondValue

    private fun subtractOperation() = baseValue - secondValue
    
    private fun divideOperation(): Double {
        var result = 0.0
        if (secondValue != 0.0) {
            result = baseValue / secondValue
        }
        return result
    }

    private fun multiplyOperation() = baseValue * secondValue

    private fun percentOperation(): Double {
        var result = 0.0
        if (secondValue != 0.0) {
            result = baseValue / 100 * secondValue
        }
        return result
    }

    private fun powerOperation(): Double {
        var result = Math.pow(baseValue, secondValue)
        if (java.lang.Double.isInfinite(result) || java.lang.Double.isNaN(result))
            result = 0.0
        return result
    }

    private fun rootOperation() = Math.sqrt(baseValue)

    private fun factorialOperation(): Double{
        var result = 1.0
        if (baseValue==0.0 || baseValue==1.0){
            return result
        }else{
            var base = baseValue.toInt()
            for(i in 1..base){
                result *= i
            }
        }
        return result
    }
}