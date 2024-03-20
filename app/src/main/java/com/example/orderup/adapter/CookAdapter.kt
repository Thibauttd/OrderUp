package com.example.orderup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.databinding.ItemOrderBinding
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CookAdapter(
    private val onOrderClickListener: ((OrderModel) -> Unit)? = null,
    private val onSwipeRight: ((OrderModel) -> Unit)? = null
) : RecyclerView.Adapter<CookAdapter.OrderViewHolder>() {

    private var orders: List<OrderModel> = listOf()
    private var menuItemRepository: MenuItemRepository = MenuItemRepository("")

    // Inflate the layout for each item of the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    // Bind data to each item of the RecyclerView
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.bind(currentOrder)
    }

    // Return the size of the dataset
    override fun getItemCount() = orders.size

    // Update the list of orders
    fun submitList(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener for each item in the RecyclerView
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClickListener?.invoke(orders[position])
                }
            }
        }

        // Bind data to the item view
        fun bind(order: OrderModel) {
            binding.order = order

            // Fetch item name asynchronously and update UI
            CoroutineScope(Dispatchers.Main).launch {
                val itemName = menuItemRepository.getItemName(order.menuitemid)
                binding.textViewOrderQuantity.text = "Quantité: ${order.quantity}"
                binding.textViewMenuItemName.text = "$itemName"
                binding.executePendingBindings()
            }
        }
    }

    // Attach swipe functionality to the RecyclerView
    fun attachSwipeHelper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedOrder = orders[position]
                orders = orders.toMutableList().apply { removeAt(position) }
                notifyItemRemoved(position)
                onSwipeRight?.invoke(swipedOrder)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
