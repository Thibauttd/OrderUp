package com.example.orderup.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.TableModel

class TableAdapter(
    private var tables: List<TableModel>,
    private val onTableClickListener: OnTableClickListener? = null,
    private val onTableLongClickListener: OnTableLongClickListener? = null
) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    // Create ViewHolder for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        holder.bind(table)
    }

    // Get the total number of items in the list
    override fun getItemCount(): Int {
        return tables.size
    }

    // Method to create a copy of the table model
    private fun copyTable(original: TableModel): TableModel {
        return TableModel(
            key = original.key,
            numero = original.numero,
            capacity = original.capacity,
            occupied = original.occupied
        )
    }

    // Method to update the dataset and notify changes
    fun updateData(newTables: List<TableModel>) {
        tables = newTables
        notifyDataSetChanged()
    }

    // Interface for handling table clicks
    interface OnTableClickListener {
        fun onTableClick(table: TableModel)
    }

    // Interface for handling long clicks on tables
    interface OnTableLongClickListener {
        fun onTableLongClick(table: TableModel)
    }

    // ViewHolder class to hold the views for each table item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val frameLayout: LinearLayout = itemView.findViewById(R.id.frameLayout)
        private val textNumero: TextView = itemView.findViewById(R.id.textNumero)
        private val textCapacity: TextView = itemView.findViewById(R.id.textCapacity)

        init {
            // Handle clicks on a table
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTableClickListener?.onTableClick(tables[position])
                }
            }

            // Handle long clicks on a table
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTableLongClickListener?.onTableLongClick(tables[position])
                }
                true
            }
        }

        // Bind table data to views
        fun bind(table: TableModel) {
            val context = frameLayout.context

            // Update border color based on table status
            val borderColor = if (table.occupied) {
                Color.parseColor("#ff0000") // Orange for occupied
            } else {
                Color.parseColor("#15ad4c") // Green for not occupied
            }

            val roundedRectangleDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle) as GradientDrawable
            roundedRectangleDrawable.setStroke(3.dpToPx(context.resources), borderColor)
            frameLayout.background = roundedRectangleDrawable

            // Update other UI elements
            textNumero.text = "Table n°${table.numero}"
            textCapacity.text = "Capacité: ${table.capacity}"
        }
    }

    // Extension function to convert dp to pixels
    fun Int.dpToPx(resources: Resources): Int =
        (this * resources.displayMetrics.density).toInt()

    // Method to get the table model at a specific position
    fun getTableAtPosition(position: Int): TableModel {
        return tables[position]
    }
}
