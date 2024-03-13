package com.example.orderup.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.MenuItem

class DrinkAdaptater(private val boissonsList: List<MenuItem>) : RecyclerView.Adapter<DrinkAdaptater.ViewHolder>() {

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
        val boisson = boissonsList[position]
        holder.textViewBoisson.text = boisson.name
        // Initialisez le compteur à 0 ou à la valeur actuelle si vous la maintenez quelque part
        var quantity = 0
        holder.textViewQuantity.text = quantity.toString()

        holder.buttonPlus.setOnClickListener {
            quantity++
            holder.textViewQuantity.text = quantity.toString()
            // Ici, vous pouvez également mettre à jour la quantité dans un objet ou une base de données
        }

        holder.buttonMinus.setOnClickListener {
            if (quantity > 0) {
                quantity--
                holder.textViewQuantity.text = quantity.toString()
                // Mise à jour de l'objet ou de la base de données si nécessaire
            }
        }
    }


    override fun getItemCount(): Int {
        return boissonsList.size
    }
}
