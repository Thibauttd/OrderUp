package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.orderup.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MenuStarters :  MenuItem() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // For example, modify the page title
        updatePageTitle("Entrées")

        // Change the image if necessary
        updateImageResId(R.drawable.starter)

        // Update products specific to desserts
        updateRepositoryParam("entrees")

        super.onViewCreated(view, savedInstanceState)
    }
}

