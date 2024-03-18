package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.orderup.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Starters : Products() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // For example, modify the page title
        setPageTitle("Entrées")

        // Change the image if necessary
        setImageResource(R.drawable.starter)

        // Update products specific to entrees
        setRepositoryParameter("entrees")

        super.onViewCreated(view, savedInstanceState)
    }
}
