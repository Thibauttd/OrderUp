package com.example.orderup.repository

import com.example.orderup.model.TableModel
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference

class TableRepository {
    // Reference to the Firebase database for tables
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("tables")

    // List to store all tables
    private var tablesList: List<TableModel> = emptyList()

    // Add a table to the database
    fun addTable(table: TableModel) {
        // Add the table to the database with an empty number
        val key = databaseRef.push().key ?: return
        table.key = key
        databaseRef.child(key).setValue(table)
    }

    // Update a table in the database
    fun updateTable(table: TableModel) {
        // Update the table in the database using the automatically generated key
        databaseRef.child(table.key).setValue(table)
    }

    // Get all tables from the database
    fun getAllTables(listener: TablesListener) {
        // Add a listener for changes in the database
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tablesList = snapshot.children.mapNotNull { tableSnapshot ->
                    // Get the table key from the child key
                    val key = tableSnapshot.key ?: return@mapNotNull null

                    // Get the other properties of the table
                    val table = tableSnapshot.getValue(TableModel::class.java) ?: return@mapNotNull null

                    // Create a TableModelWithKey using the automatically generated key
                    TableModel(key, table.numero, table.capacity, table.occupied)
                }
                listener.onTablesReceived(tablesList)
            }

            override fun onCancelled(error: DatabaseError) {
                listener.onTablesError(error.message)
            }
        })
    }

    // Check if there are occupied tables
    fun hasOccupiedTables(): Boolean {
        return tablesList.any { it.occupied }
    }

    // Listener interface for table data
    interface TablesListener {
        fun onTablesReceived(tables: List<TableModel>)
        fun onTablesError(error: String)
    }
}
