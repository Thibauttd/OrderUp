import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.model.TableModel

class TableItemTouchHelper(
    private val tableAdapter: TableAdapter,
    private val listener: OnTableSwipeListener
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    interface OnTableSwipeListener {
        fun onTableSwiped(table: TableModel)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val table = tableAdapter.getTableAtPosition(position)
        listener.onTableSwiped(table)
    }
}
