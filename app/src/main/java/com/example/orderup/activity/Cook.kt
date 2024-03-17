package com.example.orderup.activity

import CookAdapter
import OrderRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orderup.databinding.CookBinding
import com.example.orderup.model.OrderModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Cook : Fragment() {

    private var _binding: CookBinding? = null
    private val binding get() = _binding!!

    // Déclarez l'adapter en tant que lateinit var pour l'initialiser plus tard
    private lateinit var adapter: CookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allOrders = mutableListOf<OrderModel>()

        // Initialisez le RecyclerView avec un LinearLayoutManager
        adapter = CookAdapter()

        // Assurez-vous de définir l'adapter sur le RecyclerView avant d'appeler getCookableOrders
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Récupérez et affichez les commandes
        val orderRepository = OrderRepository()
        orderRepository.getCookableOrders { orders ->
            allOrders.addAll(orders)
            // Assurez-vous de soumettre la liste à l'adapter sur le thread principal
            requireActivity().runOnUiThread {
                adapter.submitList(allOrders)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Assurez-vous de définir _binding sur null pour éviter les fuites de mémoire
        _binding = null
    }
}


