package com.jl_demo.calculatorbottomsheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.jl_demo.calculatorbottomsheet.calculator.CalculatorBottomSheet
import com.jl_demo.calculatorbottomsheet.database.CoreDatabase
import com.jl_demo.calculatorbottomsheet.database.DatabaseConstants
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    companion object {
        //Just for demo purposes
        lateinit var database: CoreDatabase
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        database = Room.databaseBuilder(
            this,
            CoreDatabase::class.java,
            DatabaseConstants.database_name
        ).build()

        calculate_button.setOnClickListener {
            val bottomSheet = CalculatorBottomSheet()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        source_code_link.setOnClickListener {
            loadSourceCode()
        }
    }

    private fun loadSourceCode(){
        val url = "https://github.com/Jeremyscell82/CalculatorBottomsheet"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
}
