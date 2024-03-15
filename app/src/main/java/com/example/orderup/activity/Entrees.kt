package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.BoissonsBinding
import com.example.orderup.databinding.EntreesBinding
import com.example.orderup.databinding.FormulesBinding // Import correct binding class

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Entrees : Products() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // par exemple, modifier le titre de la page
        setPageTitle("Entrées")

        // Modifier l'image si nécessaire
        setImageResource(R.drawable.entree)

        // Mettre à jour les produits spécifiques aux desserts
        setRepositoryParameter("entrees")

        super.onViewCreated(view, savedInstanceState)
    }
}
