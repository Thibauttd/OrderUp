import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.databinding.ItemOrderBinding
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CookAdapter(private val onOrderClickListener: ((OrderModel) -> Unit)? = null) : RecyclerView.Adapter<CookAdapter.OrderViewHolder>() {

    private var orders: List<OrderModel> = listOf() // Initialisation avec une liste vide
    private var menuItemRepository: MenuItemRepository = MenuItemRepository("")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]
        holder.bind(currentOrder)
    }

    override fun getItemCount() = orders.size

    // Méthode pour soumettre la liste des commandes à l'adaptateur
    fun submitList(newOrders: List<OrderModel>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Gestion du clic sur un élément de commande
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClickListener?.invoke(orders[position])
                }
            }
        }

        fun bind(order: OrderModel) {
            binding.order = order

            // Utiliser la coroutine pour récupérer le nom de l'item de manière asynchrone
            CoroutineScope(Dispatchers.Main).launch {
                val itemName = menuItemRepository.getItemName(order.menuitemid)
                println("quantity : $order.quantity")
                // Mettre à jour les TextViews avec le nom de l'item et la quantité de commande
                binding.textViewOrderQuantity.text = "Quantité: ${order.quantity}"
                binding.textViewMenuItemName.text = "Nom: $itemName"

                // Exécuter les mises à jour d'interface utilisateur
                binding.executePendingBindings()
            }
        }

    }
}




