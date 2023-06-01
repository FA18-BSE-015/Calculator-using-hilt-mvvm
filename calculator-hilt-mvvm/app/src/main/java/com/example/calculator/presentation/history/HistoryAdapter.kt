package com.example.calculator.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.databinding.ListItemCalculationBinding
import com.example.calculator.framework.database.Calculation


class HistoryAdapter(private val clickListener: CalculationListener) :
    ListAdapter<Calculation, HistoryAdapter.ViewHolder>(HistoryDiffCallback())
{
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
    class ViewHolder private constructor(private val binding: ListItemCalculationBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: CalculationListener, item: Calculation) {
            binding.calculation = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCalculationBinding.inflate(
                    layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}
class HistoryDiffCallback : DiffUtil.ItemCallback<Calculation>() {
    override fun areItemsTheSame(oldItem: Calculation, newItem: Calculation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Calculation, newItem: Calculation): Boolean {
        return oldItem == newItem
    }
}
class CalculationListener(val clickListener: (id: Long) -> Unit) {
    fun onClick(calculation: Calculation) = clickListener(calculation.id)
}
