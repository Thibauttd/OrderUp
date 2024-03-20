package com.example.orderup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.TableModel

class FloorPlanAdapter(private val tables: List<TableModel>) : RecyclerView.Adapter<FloorPlanAdapter.FloorPlanViewHolder>() {

    inner class FloorPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tableName: TextView = itemView.findViewById(R.id.table_name)
        val tableCapacity: TextView = itemView.findViewById(R.id.table_capacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorPlanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_table_builder, parent, false)
        return FloorPlanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FloorPlanViewHolder, position: Int) {
        val currentTable = tables[position]
        holder.tableName.text = currentTable.numero
        holder.tableCapacity.text = currentTable.capacity.toString()
    }

    override fun getItemCount() = tables.size
}
