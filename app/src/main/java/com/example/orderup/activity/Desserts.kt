package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.orderup.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Desserts : Products() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // par exemple, modifier le titre de la page
        setPageTitle("Desserts")

        // Modifier l'image si nécessaire
        setImageResource(R.drawable.dessert)

        // Mettre à jour les produits spécifiques aux desserts
        setRepositoryParameter("desserts")

        super.onViewCreated(view, savedInstanceState)
    }


}

