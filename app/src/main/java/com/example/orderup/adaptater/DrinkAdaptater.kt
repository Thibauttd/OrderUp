package com.example.orderup.adaptater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.model.MenuItem

class DrinkAdaptater(private val boissonsList: List<MenuItem>) : RecyclerView.Adapter<DrinkAdaptater.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewBoisson: TextView = itemView.findViewById(R.id.textViewBoisson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_boisson, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val boisson = boissonsList[position]
        holder.textViewBoisson.text = boisson.name + "      " + boisson.price +"€"
    }

    override fun getItemCount(): Int {
        return boissonsList.size
    }
}
