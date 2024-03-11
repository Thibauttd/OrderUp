package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.FormulesBinding // Import correct binding class
import com.example.orderup.databinding.RoleBinding
import com.example.orderup.databinding.ServeurBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Serveur : Fragment() {

    private var _binding: ServeurBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ServeurBinding.inflate(inflater, container, false) // Correct binding class name
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.table.setOnClickListener {
            findNavController().navigate(R.id.action_Serveur_to_Tables)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
