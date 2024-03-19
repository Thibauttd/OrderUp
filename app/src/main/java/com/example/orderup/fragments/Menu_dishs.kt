package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.orderup.R
import com.example.orderup.databinding.MenuDishBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Menu_dishs : MenuItem() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // For example, modify the page title
        updatePageTitle("Plats")

        // Change the image if necessary
        updateImageResId(R.drawable.dish)

        // Update products specific to desserts
        updateRepositoryParam("plats")

        super.onViewCreated(view, savedInstanceState)
    }
}
