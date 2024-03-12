package com.example.orderup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.orderup.R
import com.example.orderup.databinding.PriseCommandeBinding

class Prise_commande : AppCompatActivity() {

    private lateinit var binding: PriseCommandeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PriseCommandeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val tableNumber = intent.getStringExtra("num_table")

            if (tableNumber != null) {
                val formattedText = getString(R.string.table_number, tableNumber)
                binding.textvu.text = formattedText
            } else {
                throw NullPointerException("Numéro de table manquant dans l'intent")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}

