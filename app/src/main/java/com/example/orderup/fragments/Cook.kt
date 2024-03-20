package com.example.orderup.fragments

import CookAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orderup.R
import com.example.orderup.databinding.CookBinding
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.OrderRepository

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Cook : Fragment() {

    private var _binding: CookBinding? = null
    private val binding get() = _binding!!

    // Declare the adapter as lateinit var to initialize it later
    private lateinit var adapter: CookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allOrders = mutableListOf<OrderModel>()

        // Initialize the RecyclerView with a LinearLayoutManager
        adapter = CookAdapter()

        binding.btnLogo.setOnClickListener {
            findNavController().navigate(R.id.action_Cook_to_Menu)
        }

        binding.btnCook.setOnClickListener {
            findNavController().navigate(R.id.action_Cook_to_Role)
        }

        // Make sure to set the adapter on the RecyclerView before calling getCookableOrders
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Fetch and display orders
        val orderRepository = OrderRepository()
        orderRepository.getCookableOrders { orders ->
            allOrders.addAll(orders)
            // Make sure to submit the list to the adapter on the main thread
                adapter.submitList(allOrders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set _binding to null to avoid memory leaks
        _binding = null
    }
}