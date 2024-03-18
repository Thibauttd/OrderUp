package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.orderup.R

/**
 * A simple [Fragment] subclass for displaying main courses.
 */
class Dish : Products() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // The page title
        setPageTitle("Plats")

        // The image if necessary
        setImageResource(R.drawable.dish)

        // Update products specific to main courses
        setRepositoryParameter("plats")

        super.onViewCreated(view, savedInstanceState)
    }
}
