import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.MenuItemModel

class MenuItemAdapter(
    private val items: List<MenuItemModel>,
    private val itemClickListener: MenuItemClickListener
) : RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    interface MenuItemClickListener {
        fun onItemClick(item: MenuItemModel)
        fun onItemEdit(item: MenuItemModel)
        fun onItemDelete(item: MenuItemModel)
        fun onItemLongClick(item: MenuItemModel)
    }
    inner class MenuItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)

        init {
            // Gestion du clic sur l'élément
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(items[position])
                }
            }

            // Gestion du long clic sur l'élément
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemLongClick(items[position])
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_item, parent, false)
        return MenuItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemName.text = currentItem.name
        holder.itemPrice.text = currentItem.price.toString()+"€" // Assumption: price is stored as Int
    }

    override fun getItemCount() = items.size
}
