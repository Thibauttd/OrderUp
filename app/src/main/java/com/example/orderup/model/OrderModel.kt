package com.example.orderup.model

class OrderModel (
    val id: String = "", // Unique identifier for the order
    val tableId: String = "", // ID of the table where the order was placed
    val menuItemId: String = "", // ID of the menu item ordered
    val quantity: Int =0 , // Quantity of the menu item
    val ready: Boolean = false
)