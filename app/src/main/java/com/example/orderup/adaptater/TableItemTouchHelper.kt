package com.example.orderup.adaptater

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.model.TableModel

class TableItemTouchHelper(
    private val tableAdapter: TableAdapter,
    private val listener: OnTableSwipeListener
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    // Interface for handling table swipe actions
    interface OnTableSwipeListener {
        fun onTableSwipedLeft(table: TableModel)
        fun onTableSwipedRight(table: TableModel)
    }

    // Method to handle movement (not used)
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    // Method to handle swipe actions
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val table = tableAdapter.getTableAtPosition(position)
        when (direction) {
            ItemTouchHelper.LEFT -> listener.onTableSwipedLeft(table)
            ItemTouchHelper.RIGHT -> listener.onTableSwipedRight(table)
        }
    }
}
