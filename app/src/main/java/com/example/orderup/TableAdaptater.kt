import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.TableModel
import com.example.orderup.R


class TableAdapter(private var tables: List<TableModel>) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        holder.bind(table)
    }

    override fun getItemCount(): Int {
        return tables.size
    }

    fun updateData(newTables: List<TableModel>) {
        println("Updating data with new tables: $newTables")
        tables = newTables
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val frameLayout: FrameLayout = itemView.findViewById(R.id.frameLayout)
        private val textNumero: TextView = itemView.findViewById(R.id.textNumero)
        private val textCapacity: TextView = itemView.findViewById(R.id.textCapacity)

        fun bind(table: TableModel) {
            // Mettez à jour la couleur du fond en fonction du statut de la table
            val backgroundColor = if (table.occupied) {
                Color.parseColor("#FFA500") // Orange pour occupé
            } else {
                Color.parseColor("#00FF00") // Vert pour non occupé
            }
            frameLayout.setBackgroundColor(backgroundColor)

            // Mettez à jour les autres éléments de l'interface utilisateur
            textNumero.text = "Numéro: ${table.numero}"
            textCapacity.text = "Capacité: ${table.capacity}"
        }
    }
}
