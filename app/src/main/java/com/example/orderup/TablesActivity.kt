package com.example.orderup

import TableAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class TablesActivity : AppCompatActivity(), TableAdapter.OnTableClickListener, TableAdapter.OnTableLongClickListener {

    private val tableRepository = TableRepository()
    private lateinit var tableAdapter: TableAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        recyclerView = findViewById(R.id.recyclerViewTables)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter(emptyList(), this, this)
        recyclerView.adapter = tableAdapter

        // Récupérer toutes les tables et afficher
        tableRepository.getAllTables(object : TableRepository.TablesListener {
            override fun onTablesReceived(tables: List<TableModel>) {
                runOnUiThread {
                    println("Tables received: $tables")
                    tableAdapter.updateData(tables)
                    // Mettez à jour la couleur du RecyclerView
                    updateRecyclerViewColor()
                }
            }

            override fun onTablesError(error: String) {
                Snackbar.make(findViewById(R.id.main), "Erreur: $error", Snackbar.LENGTH_SHORT).show()
            }
        })

        // Initialisation du bouton
        val btnAddTable: FloatingActionButton = findViewById(R.id.btnAddTable)

        // Gestionnaire de clic du bouton
        btnAddTable.setOnClickListener {
            createNewTable()
        }
    }

    // Fonction pour créer une nouvelle table
    private fun createNewTable() {
        // Afficher la boîte de dialogue pour ajouter une nouvelle table
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_table, null)
        val dialogBuilder = AlertDialog.Builder(this)
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
                val newTable = TableModel(key= "none", numero = numero, capacity = capacity, occupied = false)

                // Ajouter la nouvelle table à la base de données
                tableRepository.addTable(newTable)

                // Fermer la boîte de dialogue
                alertDialog.dismiss()

                // Afficher un message ou effectuer d'autres actions si nécessaire
                Snackbar.make(findViewById(R.id.main), "Nouvelle table ajoutée!", Snackbar.LENGTH_SHORT).show()
            } else {
                // Afficher un message d'erreur si les informations ne sont pas valides
                Snackbar.make(dialogView, "Veuillez fournir des informations valides", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Afficher la boîte de dialogue
        alertDialog.show()
    }

    // Fonction pour mettre à jour la couleur du RecyclerView en fonction de l'état des tables
    private fun updateRecyclerViewColor() {
        val backgroundColor = if (tableRepository.hasOccupiedTables()) {
            Color.parseColor("#FFA500") // Orange s'il y a des tables occupées
        } else {
            Color.parseColor("#00FF00") // Vert s'il n'y a pas de tables occupées
        }
        recyclerView.setBackgroundColor(backgroundColor)
    }

    // Fonction pour afficher la boîte de dialogue de modification de la table
    private fun showEditTableDialog(selectedTable: TableModel) {
        // Ajoutez ici le code pour afficher la boîte de dialogue de modification
        // Par exemple, vous pouvez créer une boîte de dialogue personnalisée avec les champs nécessaires
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_table, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Modifier Table")

        val alertDialog = dialogBuilder.create()

        // Récupérer les vues de la boîte de dialogue
        val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
        val editTextCapacity = dialogView.findViewById<EditText>(R.id.editTextCapacity)
        val btnUpdateTable = dialogView.findViewById<Button>(R.id.btnUpdateTable)

        // Pré-remplir les champs avec les informations de la table sélectionnée
        editTextNumber.setText(selectedTable.numero)
        editTextCapacity.setText(selectedTable.capacity.toString())

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
                    key = selectedTable.key,
                    numero = newNumero,
                    capacity = newCapacity,
                    occupied = selectedTable.occupied // Conservez la valeur existante d'occupied
                )


                // Mettez à jour la table dans la base de données
//                tableRepository.updateTable(updatedTable)

                // Fermer la boîte de dialogue
                alertDialog.dismiss()

                // Afficher un message ou effectuer d'autres actions si nécessaire
                Snackbar.make(findViewById(R.id.main), "Table mise à jour!", Snackbar.LENGTH_SHORT).show()
            } else {
                // Afficher un message d'erreur si les nouvelles informations ne sont pas valides
                Snackbar.make(dialogView, "Veuillez fournir des informations valides", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Afficher la boîte de dialogue
        alertDialog.show()
    }

    override fun onTableClick(table: TableModel) {
        // Gérer le clic sur la table (par exemple, afficher des détails)
    }

    override fun onTableLongClick(table: TableModel) {
        // Gérer le clic long sur la table (par exemple, afficher la boîte de dialogue de modification)
        showEditTableDialog(table)
    }
}

