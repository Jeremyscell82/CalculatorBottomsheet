package com.jl_demo.calculatorbottomsheet.calculator


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jl_demo.calculatorbottomsheet.R
import com.jl_demo.calculatorbottomsheet.database.CalcHistoryModel
import kotlinx.android.synthetic.main.item_calculator_history.view.*

/**
 * This is what displays the history for the calculator, as each formula/equation is calculated this class is updated
 */
class CalculatorAdapter: RecyclerView.Adapter<CalculatorAdapter.CalculatorViewHolder>() {

    var historyItems: List<CalcHistoryModel> = emptyList()
    var onItemClicked: ((CalcHistoryModel) -> Unit)? = null

    //Call this class to update the list
    fun updateAdapter(items: List<CalcHistoryModel>) {
        historyItems = items
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculatorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CalculatorViewHolder(layoutInflater.inflate(R.layout.item_calculator_history, parent, false))
    }

    override fun getItemCount() = historyItems.size

    override fun onBindViewHolder(holder: CalculatorViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    inner class CalculatorViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(item: CalcHistoryModel){
            itemView.apply {
                item_calc_formula.text = item.formula
                item_calc_result.text = item.result
                setOnClickListener { onItemClicked?.invoke(item) }
            }
        }
    }

}