package com.example.orderup

import com.google.firebase.database.*

class TableRepository {
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("tables")

    fun addTable(table: TableModel) {
        // Ajouter la table à la base de données avec un numéro vide
        val key = databaseRef.push().key ?: return
        databaseRef.child(key).setValue(table)
    }

    fun getAllTables(listener: TablesListener) {
        // Ajouter un écouteur pour les changements dans la base de données
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tablesList = mutableListOf<TableModel>()
                for (tableSnapshot in snapshot.children) {
                    val table = tableSnapshot.getValue(TableModel::class.java)
                    table?.let { tablesList.add(it) }
                }
                listener.onTablesReceived(tablesList)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onTablesError(error.message)
            }
        })
    }

    interface TablesListener {
        fun onTablesReceived(tables: List<TableModel>)
        fun onTablesError(error: String)
    }
}
