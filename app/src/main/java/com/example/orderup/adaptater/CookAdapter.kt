package com.example.orderup.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.OrderModel

class CookAdapter(private val onOrderClickListener: ((OrderModel) -> Unit)? = null) : RecyclerView.Adapter<CookAdapter.OrderViewHolder>() {

    private var orders: List<OrderModel> = listOf() // Initialize with an empty list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders.getOrNull(position)
        currentOrder?.let { holder.bind(it) }
    }

    override fun getItemCount() = orders.size

    // Method to submit the list of orders to the adapter
    fun submitList(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewOrderDetails: TextView = itemView.findViewById(R.id.textViewOrderDetails)

        init {
            // Handle click on an order item
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClickListener?.invoke(orders.getOrNull(position) ?: return@setOnClickListener)
                }
            }
        }

        fun bind(order: OrderModel) {
            textViewOrderDetails.text = "ID: ${order.menuitemid} - Quantity: ${order.quantity}"
            // Add other UI updates here based on the `order` object
        }
    }
}
