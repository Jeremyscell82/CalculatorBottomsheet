package com.jl_demo.calculatorbottomsheet.database

import androidx.room.Database
import androidx.room.RoomDatabase

class DatabaseConstants {
    companion object {
        //#constants
        const val database_version = 1
        const val database_name = "arrow_core.db"
    }
}


@Database(entities = [CalcHistoryModel::class], version = DatabaseConstants.database_version, exportSchema = false)
abstract class CoreDatabase: RoomDatabase() {
    abstract fun CalculatorDao(): CalculatorDao
}