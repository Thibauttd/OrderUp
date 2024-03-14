import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.MenuItem

class DrinkAdaptater(private var boissonsList: List<Pair<MenuItem, Int>>) : RecyclerView.Adapter<DrinkAdaptater.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewBoisson: TextView = itemView.findViewById(R.id.textViewBoisson)
        val buttonMinus: Button = itemView.findViewById(R.id.buttonMinus)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val buttonPlus: Button = itemView.findViewById(R.id.buttonPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_boisson, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (boisson, quantity) = boissonsList[position]
        holder.textViewBoisson.text = boisson.name
        holder.textViewQuantity.text = quantity.toString()

        holder.buttonPlus.setOnClickListener {
            val updatedQuantity = quantity + 1
            holder.textViewQuantity.text = updatedQuantity.toString()
            val updatedList = boissonsList.toMutableList()
            updatedList[position] = Pair(boisson, updatedQuantity)
            boissonsList = updatedList.toList()
            notifyItemChanged(position) // Pour mettre à jour l'affichage de l'élément modifié
        }

        holder.buttonMinus.setOnClickListener {
            if (quantity > 0) {
                val updatedQuantity = quantity - 1
                holder.textViewQuantity.text = updatedQuantity.toString()
                val updatedList = boissonsList.toMutableList()
                updatedList[position] = Pair(boisson, updatedQuantity)
                boissonsList = updatedList.toList()
                notifyItemChanged(position) // Pour mettre à jour l'affichage de l'élément modifié
            }
        }
    }

    fun getCurrentQuantities(): List<Pair<MenuItem, Int>> {
        return boissonsList
    }

    override fun getItemCount(): Int {
        return boissonsList.size
    }
}
