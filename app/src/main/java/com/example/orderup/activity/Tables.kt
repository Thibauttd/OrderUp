package com.example.orderup.activity

import TableAdapter
import TableItemTouchHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.databinding.TablesBinding
import com.example.orderup.model.TableModel
import com.example.orderup.repository.TableRepository
import com.google.android.material.snackbar.Snackbar

class Tables : Fragment(), TableAdapter.OnTableClickListener, TableItemTouchHelper.OnTableSwipeListener {

    private val tableRepository = TableRepository()
    private lateinit var tableAdapter: TableAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemTouchHelper: ItemTouchHelper

    private var _binding: TablesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TablesBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerViewTables
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tableAdapter = TableAdapter(emptyList(), this, null)
        recyclerView.adapter = tableAdapter

        // Créer l'ItemTouchHelper et l'attacher au RecyclerView
        val tableSwipeListener: TableItemTouchHelper.OnTableSwipeListener = this
        val tableItemTouchHelper = TableItemTouchHelper(tableAdapter, tableSwipeListener)
        itemTouchHelper = ItemTouchHelper(tableItemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Récupérer toutes les tables et afficher
        tableRepository.getAllTables(object : TableRepository.TablesListener {
            override fun onTablesReceived(tables: List<TableModel>) {
                requireActivity().runOnUiThread {
                    println("Tables received: $tables")
                    tableAdapter.updateData(tables)
                }
            }

            override fun onTablesError(error: String) {
                Snackbar.make(view, "Erreur: $error", Snackbar.LENGTH_SHORT).show()
            }
        })

        // Initialisation du bouton
        val btnAddTable: ImageButton = view.findViewById(R.id.btnAddTable)

        // Gestionnaire de clic du bouton
        btnAddTable.setOnClickListener {
            createNewTable()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Fonction pour créer une nouvelle table
    private fun createNewTable() {
        // Afficher la boîte de dialogue pour ajouter une nouvelle table
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_table, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Ajouter Table")

        val alertDialog = dialogBuilder.create()

        // Gestionnaire de clic pour le bouton "Ajouter Table" dans la boîte de dialogue
        val btnAddTable = dialogView.findViewById<Button>(R.id.btnAddTable)
        btnAddTable.setOnClickListener {
            // Récupérer les informations depuis les champs de la boîte de dialogue
            val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
            val editTextCapacity = dialogView.findViewById<EditText>(R.id.editTextCapacity)

            val numero = editTextNumber.text.toString()
            val capacity = editTextCapacity.text.toString().toIntOrNull()

            // Vérifier si les informations sont valides
            if (numero.isNotEmpty() && capacity != null && capacity > 0) {
                // Créer une nouvelle table avec les informations
                val newTable =
                    TableModel(key = "none", numero = numero, capacity = capacity, occupied = false)

                // Ajouter la nouvelle table à la base de données
                tableRepository.addTable(newTable)

                // Fermer la boîte de dialogue
                alertDialog.dismiss()

                // Afficher un message ou effectuer d'autres actions si nécessaire
                Snackbar.make(requireView(), "Nouvelle table ajoutée!", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                // Afficher un message d'erreur si les informations ne sont pas valides
                Snackbar.make(
                    dialogView,
                    "Veuillez fournir des informations valides",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Afficher la boîte de dialogue
        alertDialog.show()
    }

    override fun onTableClick(table: TableModel) {
        // Afficher la boîte de dialogue pour mettre à jour la table sélectionnée
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_table, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Modifier Table")

        val alertDialog = dialogBuilder.create()

        // Récupérer les vues de la boîte de dialogue
        val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
        val editTextCapacity = dialogView.findViewById<EditText>(R.id.editTextCapacity)
        val btnUpdateTable = dialogView.findViewById<Button>(R.id.btnUpdateTable)

        // Pré-remplir les champs avec les informations de la table sélectionnée
        editTextNumber.setText(table.numero)
        editTextCapacity.setText(table.capacity.toString())

        // Gestionnaire de clic pour le bouton "Modifier Table" dans la boîte de dialogue
        btnUpdateTable.setOnClickListener {
            // Récupérer les nouvelles informations depuis les champs de la boîte de dialogue
            val newNumero = editTextNumber.text.toString()
            val newCapacity = editTextCapacity.text.toString().toIntOrNull()

            // Vérifier si les nouvelles informations sont valides
            if (newNumero.isNotEmpty() && newCapacity != null && newCapacity > 0) {
                // Mettez à jour la table avec les nouvelles informations
                // Créer une nouvelle instance de TableModel avec les valeurs mises à jour
                val updatedTable = TableModel(
                    key = table.key,
                    numero = newNumero,
                    capacity = newCapacity,
                    occupied = table.occupied // Conservez la valeur existante d'occupied
                )
                // Mettez à jour la table dans la base de données
                tableRepository.updateTable(updatedTable)

                // Fermer la boîte de dialogue
                alertDialog.dismiss()

                // Afficher un message ou effectuer d'autres actions si nécessaire
                Snackbar.make(dialogView, "Table mise à jour!", Snackbar.LENGTH_SHORT).show()
            } else {
                // Afficher un message d'erreur si les nouvelles informations ne sont pas valides
                Snackbar.make(dialogView, "Veuillez fournir des informations valides", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Afficher la boîte de dialogue
        alertDialog.show()
    }


    override fun onTableSwipedLeft(table: TableModel) {
        // Mettre à jour l'attribut occupied de la table
        val updatedTable = TableModel(
            key = table.key,
            numero = table.numero,
            capacity = table.capacity,
            occupied = !table.occupied
        )
        // Mettre à jour la table dans la base de données
        tableRepository.updateTable(updatedTable)

        // Actualiser l'adaptateur pour refléter les changements
        tableAdapter.notifyDataSetChanged()
    }

    override fun onTableSwipedRight(table: TableModel) {
        findNavController().navigate(
            R.id.action_Tables_to_PrisesCommande,
            bundleOf("num_table" to table.numero, "tableId" to table.key)
        )
    }
}
