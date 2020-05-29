package com.jl_demo.calculatorbottomsheet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface CalculatorDao {

    //Insert new entry into database
    @Insert
    fun updateHistory(vararg entry: CalcHistoryModel)

    //Get all history from calculator
    @Query("SELECT * FROM calchistorymodel")
    fun getHistory(): Flowable<List<CalcHistoryModel>>

    @Query("DELETE FROM calchistorymodel")
    fun clearHistory()

}