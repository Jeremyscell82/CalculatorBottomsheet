package com.jl_demo.calculatorbottomsheet.calculator


import androidx.lifecycle.ViewModel
import com.jl_demo.calculatorbottomsheet.database.CalcHistoryModel
import com.jl_demo.calculatorbottomsheet.database.CoreDatabase
import io.reactivex.Flowable

class CalculatorViewModel: ViewModel() {

    //Display a live list for the calculator history
    fun getCalcHistory(coreDatabase: CoreDatabase): Flowable<List<CalcHistoryModel>> {
        return coreDatabase.CalculatorDao().getHistory()
    }
}