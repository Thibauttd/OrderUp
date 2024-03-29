package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.WaiterBinding

class Waiter : Fragment() {

    private var _binding: WaiterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = WaiterBinding.inflate(inflater, container, false) // Correct binding class name
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to the tables screen
        binding.table.setOnClickListener {
            findNavController().navigate(R.id.action_Waiter_to_Tables)
        }

        binding.btnLogo.setOnClickListener {
            findNavController().navigate(R.id.action_Waiter_to_Menu)
        }

        binding.btnWaiter.setOnClickListener {
            findNavController().navigate(R.id.action_Waiter_to_Role)
        }
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_Waiter_to_Floor_Plan)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}