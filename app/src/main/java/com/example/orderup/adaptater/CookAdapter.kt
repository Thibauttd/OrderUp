import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.databinding.ItemOrderBinding
import com.example.orderup.model.OrderModel

class CookAdapter(private val onOrderClickListener: ((OrderModel) -> Unit)? = null) : RecyclerView.Adapter<CookAdapter.OrderViewHolder>() {

    private var orders: List<OrderModel> = listOf() // Initialisation avec une liste vide

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.bind(currentOrder)
    }

    override fun getItemCount() = orders.size

    // Méthode pour soumettre la liste des commandes à l'adaptateur
    fun submitList(newOrders: List<OrderModel>) {
        orders = newOrders
        println("submitList: $orders")
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewOrderDetails: TextView = itemView.findViewById(R.id.textViewOrderDetails)

        init {
            // Gestion du clic sur un élément de commande
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClickListener?.invoke(orders[position])
                }
            }
        }

        fun bind(order: OrderModel) {
            println("bin_orders : $order")
            textViewOrderDetails.text = "ID: ${order.menuitemid} - Quantity: ${order.quantity}"
            // Ajoutez ici d'autres mises à jour de l'interface utilisateur basées sur l'objet `order`
        }
    }
}



