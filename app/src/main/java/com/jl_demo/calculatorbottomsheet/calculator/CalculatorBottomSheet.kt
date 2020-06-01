package com.jl_demo.calculatorbottomsheet.calculator


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jl_demo.calculatorbottomsheet.MainActivity
import com.jl_demo.calculatorbottomsheet.R
import com.jl_demo.calculatorbottomsheet.calculator.helpers.CalculatorImpl
import com.jl_demo.calculatorbottomsheet.calculator.helpers.*
import com.jl_demo.calculatorbottomsheet.database.DB_Controller
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_calculator.*
import timber.log.Timber

/**
 * This is a custom calculator viewcontroller type of class
 * This class relies on CalculatorImpl and it's helper classes with a view model to function correctly
 * This class should not need to be modified
 */
//Todo needs to resized to fit a 16:10 screen ratio
class CalculatorBottomSheet : BottomSheetDialogFragment() {

    lateinit var calc: CalculatorImpl
    lateinit var calcAdapter: CalculatorAdapter
    lateinit var calculatorViewModel: CalculatorViewModel
    private var disposableCalcHistoryList: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        calcAdapter = CalculatorAdapter()
        calculatorViewModel = ViewModelProvider(this).get(CalculatorViewModel::class.java)
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Due to the way android builds the bottoms sheets, this will allow us to set the height or width of the sheet manually
//        resizeCreatedView(view, dialog!!, activity as MainActivity)
        calc = CalculatorImpl(
            formulaView = calc_formula,
            numberView = calc_result,
            context = view.context
        )
        view.apply {

            //History section of UI
            calc_clear_history.setOnClickListener {
                clearHistory()
            }
            calc_history_recyclerview.apply {
                layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
                adapter = calcAdapter
                calcAdapter.onItemClicked = {
                    copyToClipboard(it.result)
                }
            }

            //Keep a live feed with the viewmodel (which has a live feed from the database) to update the adapter if anything changes
            disposableCalcHistoryList = calculatorViewModel.getCalcHistory(MainActivity.database)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        calcAdapter.updateAdapter(result)
                    },
                    { error ->
                        Timber.d("JL_ Something has gone wrong.. ${error.message}")
                    }
                )

            //Right side of UI
            calc_close_btn.setOnClickListener {
                dialog!!.dismiss()
            }

            //Set the button clicks for the calculator
            btn_decimal.setOnClickListener { calc.numpadClicked(it.id) }
            btn_0.setOnClickListener { calc.numpadClicked(it.id) }
            btn_1.setOnClickListener { calc.numpadClicked(it.id) }
            btn_2.setOnClickListener { calc.numpadClicked(it.id) }
            btn_3.setOnClickListener { calc.numpadClicked(it.id) }
            btn_4.setOnClickListener { calc.numpadClicked(it.id) }
            btn_5.setOnClickListener { calc.numpadClicked(it.id) }
            btn_6.setOnClickListener { calc.numpadClicked(it.id) }
            btn_7.setOnClickListener { calc.numpadClicked(it.id) }
            btn_8.setOnClickListener { calc.numpadClicked(it.id) }
            btn_9.setOnClickListener { calc.numpadClicked(it.id) }

            btn_plus.setOnClickListener { calc.handleOperation(PLUS) }
            btn_minus.setOnClickListener { calc.handleOperation(MINUS) }
            btn_multiply.setOnClickListener { calc.handleOperation(MULTIPLY) }
            btn_divide.setOnClickListener { calc.handleOperation(DIVIDE) }
            btn_percent.setOnClickListener { calc.handleOperation(PERCENT) }
            btn_power.setOnClickListener { calc.handleOperation(POWER) }
            btn_root.setOnClickListener { calc.handleOperation(ROOT) }
            btn_equals.setOnClickListener { calc.handleEquals() }
            btn_clear.setOnClickListener { calc.handleClear() }
            btn_clear.setOnLongClickListener { calc.handleReset(); true }

            calc_copy.setOnClickListener {
                copyToClipboard(calc_result.text.toString())
                Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun copyToClipboard(value: String): Boolean {
        return if (value.isEmpty()) {
            false
        } else {
            val clip = ClipData.newPlainText(getString(R.string.calc_value_copied), value)
            ((activity as MainActivity).getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                clip
            )
            view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY) //Needs to be tested on actual device... todo JL_ remove or test
            true
        }
    }

    private fun clearHistory() {
        DB_Controller().clearCalculatorHistory(MainActivity.database)
        view?.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY) //Needs to be tested on actual device... todo JL_ remove or test
    }

    override fun onPause() {
        super.onPause()
        disposableCalcHistoryList?.dispose()
    }

    /** These last two function can be used for other bottom sheets if we want to set a custom width or height **/
    fun resizeCreatedView(view: View, dialog: Dialog, myActivity: MainActivity) {
        val displayMetrics = DisplayMetrics()
        myActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = getLandscapeCardWidth(
            displayMetrics.widthPixels,
            view.resources.getInteger(R.integer.bottomsheet_width_percentage)
        )
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT)
            BottomSheetBehavior.from(bottomSheet).peekHeight = (displayMetrics.heightPixels / 2)
        }
    }

    private fun getLandscapeCardWidth(windowWidth: Int, widthPercentage: Int): Int {
        return ((windowWidth / 100) * widthPercentage)
    }
}