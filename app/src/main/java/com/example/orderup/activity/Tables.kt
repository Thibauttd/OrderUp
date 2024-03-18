package com.example.orderup.activity

import android.annotation.SuppressLint
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
import com.example.orderup.adaptater.TableAdapter
import com.example.orderup.adaptater.TableItemTouchHelper
import com.example.orderup.databinding.TablesBinding
import com.example.orderup.model.TableModel
import com.example.orderup.repository.TableRepository
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment representing the tables section of the application.
 */
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
    ): View {
        _binding = TablesBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerViewTables
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tableAdapter = TableAdapter(emptyList(), this, null)
        recyclerView.adapter = tableAdapter

        // Create ItemTouchHelper and attach it to RecyclerView
        val tableSwipeListener: TableItemTouchHelper.OnTableSwipeListener = this
        val tableItemTouchHelper = TableItemTouchHelper(tableAdapter, tableSwipeListener)
        itemTouchHelper = ItemTouchHelper(tableItemTouchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Get all tables and display
        tableRepository.getAllTables(object : TableRepository.TablesListener {
            override fun onTablesReceived(tables: List<TableModel>) {
                requireActivity().runOnUiThread {
                    tableAdapter.updateData(tables)
                }
            }

            override fun onTablesError(error: String) {
                Snackbar.make(view, "Error: $error", Snackbar.LENGTH_SHORT).show()
            }
        })

        // Initialize the add table button
        val btnAddTable: ImageButton = view.findViewById(R.id.btnAddTable)

        // Button click handler
        btnAddTable.setOnClickListener {
            createNewTable()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to create a new table
    private fun createNewTable() {
        // Show the dialog to add a new table
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_table, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Table")

        val alertDialog = dialogBuilder.create()

        // Click handler for the "Add Table" button in the dialog
        val btnAddTable = dialogView.findViewById<Button>(R.id.btnAddTable)
        btnAddTable.setOnClickListener {
            // Get information from the fields in the dialog
            val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
            val editTextCapacity = dialogView.findViewById<EditText>(R.id.editTextCapacity)

            val numero = editTextNumber.text.toString()
            val capacity = editTextCapacity.text.toString().toIntOrNull()

            // Check if the information is valid
            if (numero.isNotEmpty() && capacity != null && capacity > 0) {
                // Create a new table with the information
                val newTable =
                    TableModel(key = "none", numero = numero, capacity = capacity, occupied = false)

                // Add the new table to the database
                tableRepository.addTable(newTable)

                // Dismiss the dialog
                alertDialog.dismiss()

                // Show a message or perform other actions if needed
                Snackbar.make(requireView(), "New table added!", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                // Show an error message if the information is not valid
                Snackbar.make(
                    dialogView,
                    "Please provide valid information",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Show the dialog
        alertDialog.show()
    }

    override fun onTableClick(table: TableModel) {
        // Show the dialog to update the selected table
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_table, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Table")

        val alertDialog = dialogBuilder.create()

        // Get views from the dialog
        val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
        val editTextCapacity = dialogView.findViewById<EditText>(R.id.editTextCapacity)
        val btnUpdateTable = dialogView.findViewById<Button>(R.id.btnUpdateTable)

        // Pre-fill fields with information from the selected table
        editTextNumber.setText(table.numero)
        editTextCapacity.setText(table.capacity.toString())

        // Click handler for the "Update Table" button in the dialog
        btnUpdateTable.setOnClickListener {
            // Get new information from the fields in the dialog
            val newNumero = editTextNumber.text.toString()
            val newCapacity = editTextCapacity.text.toString().toIntOrNull()

            // Check if the new information is valid
            if (newNumero.isNotEmpty() && newCapacity != null && newCapacity > 0) {
                // Update the table with the new information
                // Create a new instance of TableModel with the updated values
                val updatedTable = TableModel(
                    key = table.key,
                    numero = newNumero,
                    capacity = newCapacity,
                    occupied = table.occupied // Keep the existing value of occupied
                )
                // Update the table in the database
                tableRepository.updateTable(updatedTable)

                // Dismiss the dialog
                alertDialog.dismiss()

                // Show a message or perform other actions if needed
                Snackbar.make(dialogView, "Table updated!", Snackbar.LENGTH_SHORT).show()
            } else {
                // Show an error message if the new information is not valid
                Snackbar.make(dialogView, "Please provide valid information", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Show the dialog
        alertDialog.show()
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onTableSwipedLeft(table: TableModel) {
        // Update the occupied attribute of the table
        val updatedTable = TableModel(
            key = table.key,
            numero = table.numero,
            capacity = table.capacity,
            occupied = !table.occupied
        )
        // Update the table in the database
        tableRepository.updateTable(updatedTable)

        // Refresh the adapter to reflect the changes
        tableAdapter.notifyDataSetChanged()
    }

    override fun onTableSwipedRight(table: TableModel) {
        findNavController().navigate(
            R.id.action_Tables_to_PrisesCommande,
            bundleOf("num_table" to table.numero, "tableId" to table.key)
        )
    }
}
