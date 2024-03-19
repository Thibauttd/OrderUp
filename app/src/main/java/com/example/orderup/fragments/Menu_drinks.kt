package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.MenuDrinkBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Menu_drinks :  MenuItem() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // For example, modify the page title
        updatePageTitle("Boissons")

        // Change the image if necessary
        updateImageResId(R.drawable.drink)

        // Update products specific to desserts
        updateRepositoryParam("boissons")

        super.onViewCreated(view, savedInstanceState)
    }
}

