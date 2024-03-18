package com.example.orderup.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.OrdertakingBinding

/**
 * A [Fragment] subclass for handling order taking.
 */
class OrderTaking : Fragment() {

    private var _binding: OrdertakingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tableId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OrdertakingBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Navigate to the specified destination fragment.
     * @param destinationId The ID of the destination fragment.
     */
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
            throw NullPointerException("Table number missing in arguments")
        }

        // Set click listeners for menu categories
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
