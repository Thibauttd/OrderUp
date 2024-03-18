package com.example.orderup.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.orderup.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Drinks : Products() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The page title
        setPageTitle("Boissons")

        // The image if needed
        setImageResource(R.drawable.drink)

        // Update specific drinks products
        setRepositoryParameter("boissons")
        super.onViewCreated(view, savedInstanceState)
    }
}
