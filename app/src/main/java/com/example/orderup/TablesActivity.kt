package com.example.orderup

import TableAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class TablesActivity : AppCompatActivity() {

    private val tableRepository = TableRepository()
    private lateinit var tableAdapter: TableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTables)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter(emptyList())
        recyclerView.adapter = tableAdapter

        // Récupérer toutes les tables et afficher
        tableRepository.getAllTables(object : TableRepository.TablesListener {
            override fun onTablesReceived(tables: List<TableModel>) {
                runOnUiThread {
                    println("Tables received: $tables")
                    tableAdapter.updateData(tables)
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
        // Mettez ici le code pour créer une nouvelle table
        // Par exemple, vous pouvez appeler la fonction addTable du TableRepository
        val newTable = TableModel(capacity = 4, occupied = false)
        tableRepository.addTable(newTable)

        // Afficher un message ou effectuer d'autres actions si nécessaire
        Snackbar.make(findViewById(R.id.main), "Nouvelle table ajoutée!", Snackbar.LENGTH_SHORT).show()
    }
}

