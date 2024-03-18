package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.RoleBinding

/**
 * A simple [Fragment] subclass serving as the default destination in the navigation.
 * This fragment allows users to select their role in the application.
 */
class Role : Fragment() {

    private var _binding: RoleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = RoleBinding.inflate(inflater, container, false) // Correct binding class name
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to the previous screen
        binding.btnPrecedent.setOnClickListener {
            findNavController().navigate(R.id.action_Role_to_Carte)
        }

        // Navigate to the manager role screen
        binding.iconeGerant.setOnClickListener {
            findNavController().navigate(R.id.action_Role_to_Gerant)
        }

        // Navigate to the cook role screen
        binding.iconeCuisinier.setOnClickListener {
            findNavController().navigate(R.id.action_Role_to_Cook)
        }

        // Navigate to the waiter role screen
        binding.iconeServeur.setOnClickListener {
            findNavController().navigate(R.id.action_Role_to_Serveur)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
