package com.arsenosov.weatherapp.searchactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.city.CityItem
import kotlinx.android.synthetic.main.search_item.view.*

class RecyclerViewAdapter(
    private var stringList:List<CityItem>,
    private val listener: (CityItem) -> (Unit)
): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: CityItem, listener: (CityItem) -> Unit) = with(itemView) {
            tvSearchItem.text = item.toString()
            setOnClickListener { listener(item) }
        }
    }

    override fun getItemCount(): Int = stringList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stringList[position], listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false))

    fun refreshItems(list: List<CityItem>) {
        stringList = list
        notifyDataSetChanged()
    }
}