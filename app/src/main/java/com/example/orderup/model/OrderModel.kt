package com.example.orderup.model

class OrderModel (
    val id: String = "", // Unique identifier for the order
    val tableid: String = "", // ID of the table where the order was placed
    val menuitemid: String = "", // ID of the menu item ordered
    var quantity: Int =0, // Quantity of the menu item
    val ready: Boolean = false
)