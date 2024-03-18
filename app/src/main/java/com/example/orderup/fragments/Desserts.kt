package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.orderup.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Desserts : Products() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // For example, modify the page title
        setPageTitle("Desserts")

        // Change the image if necessary
        setImageResource(R.drawable.dessert)

        // Update products specific to desserts
        setRepositoryParameter("desserts")

        super.onViewCreated(view, savedInstanceState)
    }
}
