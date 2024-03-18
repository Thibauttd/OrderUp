package com.example.orderup.model

// Data class representing a menu item
data class MenuItemModel(
    val name: String = "", // Name of the menu item
    val price: Int = 2,    // Price of the menu item
    val id: String = ""    // Unique identifier for the menu item
)
