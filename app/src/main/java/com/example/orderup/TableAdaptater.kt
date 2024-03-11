import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.TableModel

class TableAdapter(
    private var tables: List<TableModel>,
    private val onTableClickListener: OnTableClickListener? = null,
    private val onTableLongClickListener: OnTableLongClickListener? = null
) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

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

    private fun copyTable(original: TableModel): TableModel {
        return TableModel(
            key = original.key,
            numero = original.numero,
            capacity = original.capacity,
            occupied = original.occupied
        )
    }
    fun updateData(newTables: List<TableModel>) {
        tables = newTables
        notifyDataSetChanged()
    }

    interface OnTableClickListener {
        fun onTableClick(table: TableModel)
    }

    interface OnTableLongClickListener {
        fun onTableLongClick(table: TableModel)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val frameLayout: FrameLayout = itemView.findViewById(R.id.frameLayout)
        private val textNumero: TextView = itemView.findViewById(R.id.textNumero)
        private val textCapacity: TextView = itemView.findViewById(R.id.textCapacity)

        init {
            // Gestion des clics sur une table
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTableClickListener?.onTableClick(tables[position])
                }
            }

            // Gestion des clics longs sur une table
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTableLongClickListener?.onTableLongClick(tables[position])
                }
                true
            }
        }

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
