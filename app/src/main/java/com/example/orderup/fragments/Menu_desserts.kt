package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.orderup.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Menu_desserts : MenuItem() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Modify the page title
        updatePageTitle("Desserts")

        // Change the image if necessary
        updateImageResId(R.drawable.dessert)

        // Update products specific to desserts
        updateRepositoryParam("desserts")

        super.onViewCreated(view, savedInstanceState)
    }
}
