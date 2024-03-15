package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.CarteBinding
import com.example.orderup.databinding.FormulesBinding // Import correct binding class

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Carte : Fragment() {

    private var _binding: CarteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = CarteBinding.inflate(inflater, container, false) // Correct binding class name
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSuivant.setOnClickListener {
            findNavController().navigate(R.id.action_Carte_to_Role)
        }


        binding.btnPrecedent.setOnClickListener {
            findNavController().navigate(R.id.action_Carte_to_Formules)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
