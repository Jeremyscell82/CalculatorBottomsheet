package com.jl_demo.calculatorbottomsheet.database

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DB_Controller {


    fun addCalcEntry(database: CoreDatabase, entry: CalcHistoryModel){
        GlobalScope.launch {
            database.CalculatorDao().updateHistory(entry)
        }
    }

    fun clearCalculatorHistory(database: CoreDatabase){
        GlobalScope.launch {
            database.CalculatorDao().clearHistory()
        }
    }
}