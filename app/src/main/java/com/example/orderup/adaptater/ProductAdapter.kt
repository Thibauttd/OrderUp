import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.orderup.R
import com.example.orderup.model.MenuItem

class ProduitsAdapter(private var produitsList: List<Pair<MenuItem, Int>>) : RecyclerView.Adapter<ProduitsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewProduit: TextView = itemView.findViewById(R.id.textViewProduct)
        val buttonMinus: Button = itemView.findViewById(R.id.buttonMinus)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val buttonPlus: Button = itemView.findViewById(R.id.buttonPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (produit, quantity) = produitsList[position]
        holder.textViewProduit.text = produit.name
        holder.textViewQuantity.text = quantity.toString()

        holder.buttonPlus.setOnClickListener {
            val updatedQuantity = quantity + 1
            holder.textViewQuantity.text = updatedQuantity.toString()
            val updatedList = produitsList.toMutableList()
            updatedList[position] = Pair(produit, updatedQuantity)
            produitsList = updatedList.toList()
            notifyItemChanged(position) // Pour mettre à jour l'affichage de l'élément modifié
        }

        holder.buttonMinus.setOnClickListener {
            if (quantity > 0) {
                val updatedQuantity = quantity - 1
                holder.textViewQuantity.text = updatedQuantity.toString()
                val updatedList = produitsList.toMutableList()
                updatedList[position] = Pair(produit, updatedQuantity)
                produitsList = updatedList.toList()
                notifyItemChanged(position) // Pour mettre à jour l'affichage de l'élément modifié
            }
        }
    }

    fun getCurrentQuantities(): List<Pair<MenuItem, Int>> {
        return produitsList
    }

    override fun getItemCount(): Int {
        return produitsList.size
    }
}
