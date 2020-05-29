package com.jl_demo.calculatorbottomsheet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class is designed to be used with the Calculator's history feature (CalculatorBottomSheet)
 */

@Entity
data class CalcHistoryModel(
    @PrimaryKey(autoGenerate = true)
    val calcHistDbId: Int,
    @ColumnInfo(name = "formula")
    val formula: String,
    @ColumnInfo(name = "result")
    val result: String
)