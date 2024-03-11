package com.example.orderup

import com.google.firebase.database.*

class TableRepository {
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("tables")
    private var tablesList: List<TableModel> = emptyList()

    fun addTable(table: TableModel) {
        // Ajouter la table à la base de données avec un numéro vide
        val key = databaseRef.push().key ?: return
        table.key = key
        databaseRef.child(key).setValue(table)
    }

    fun updateTable(table: TableModel) {
        // Mettre à jour la table dans la base de données en utilisant la clé générée automatiquement
        databaseRef.child(table.key).setValue(table)
    }

    fun getAllTables(listener: TablesListener) {
        // Ajouter un écouteur pour les changements dans la base de données
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tablesList = snapshot.children.mapNotNull { tableSnapshot ->
                    // Récupérer la clé de la table depuis la clé de l'enfant
                    val key = tableSnapshot.key ?: return@mapNotNull null

                    // Récupérer les autres propriétés de la table
                    val table = tableSnapshot.getValue(TableModel::class.java) ?: return@mapNotNull null

                    // Créer un TableModelWithKey en utilisant la clé générée automatiquement
                    TableModel(key, table.numero, table.capacity, table.occupied)
                }
                listener.onTablesReceived(tablesList)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onTablesError(error.message)
            }
        })
    }

    fun hasOccupiedTables(): Boolean {
        return tablesList.any { it.occupied }
    }

    interface TablesListener {
        fun onTablesReceived(tables: List<TableModel>)
        fun onTablesError(error: String)
    }
}


