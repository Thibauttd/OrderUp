package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.adaptater.DrinkAdaptater
import com.example.orderup.databinding.BoissonsBinding
import com.example.orderup.model.MenuItem
import com.example.orderup.repository.MenuItemRepository

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Boissons : Fragment() {

    private var _binding: BoissonsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = BoissonsBinding.inflate(inflater, container, false) // Correct binding class name
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBoissons)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Attacher un LinearLayoutManager
        getBoissonsList { boissonsList ->
            recyclerView.adapter = DrinkAdaptater(boissonsList)
        }
    }

    private fun getBoissonsList(callback: (List<MenuItem>) -> Unit) {
        val menuItemRepository = MenuItemRepository("boissons")
        val boissonsList = mutableListOf<MenuItem>()

        menuItemRepository.getAllItems { menuItems ->
            menuItems.forEach { menuItem ->
                boissonsList.add(menuItem)
            }
            callback(boissonsList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
