package com.example.orderup.model

// Class representing a table
class TableModel (
    var key: String = "",      // Unique identifier for the table
    var numero: String = "",   // Table number
    val capacity: Int = 0,     // Capacity of the table
    val occupied: Boolean = false   // Indicates if the table is occupied
)
