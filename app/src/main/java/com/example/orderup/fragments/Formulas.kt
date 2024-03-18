package com.example.orderup.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.FormulasBinding // Import correct binding class

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Formulas : Fragment() {

    private var _binding: FormulasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FormulasBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSuivant.setOnClickListener {
            findNavController().navigate(R.id.action_Formule_to_Carte)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
