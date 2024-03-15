package com.example.orderup.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.PrisecommandeBinding
import java.util.logging.Logger

class Prisecommande : Fragment() {

    private var _binding: PrisecommandeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tableId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PrisecommandeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToDestination(destinationId: Int) {

        findNavController().navigate(destinationId, bundleOf("tableId" to tableId))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableId = arguments?.getString("tableId") ?: ""

        arguments?.getString("num_table")?.let { tableNumber ->
            val formattedText = getString(R.string.table_number, tableNumber)
            binding.textvu.text = formattedText
        } ?: run {
            throw NullPointerException("Numéro de table manquant dans les arguments")
        }
        binding.boissons.setOnClickListener { navigateToDestination(R.id.action_PriseCom_to_boissons) }
        binding.plats.setOnClickListener { navigateToDestination(R.id.action_PriseCom_to_plats) }
        binding.entrees.setOnClickListener { navigateToDestination(R.id.action_PriseCom_to_entrees) }
        binding.desserts.setOnClickListener { navigateToDestination(R.id.action_PriseCom_to_desserts) }
        binding.valid.setOnClickListener { navigateToDestination(R.id.action_PriseCom_to_tables) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
