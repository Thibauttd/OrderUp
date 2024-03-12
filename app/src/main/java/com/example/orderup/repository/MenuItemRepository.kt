package com.example.orderup.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.orderup.model.MenuItem

class MenuItemRepository(private val category: String) {
    private val database: DatabaseReference = Firebase.database.reference.child("menu_items").child(category)

    fun addItem(item: MenuItem) {
        val key = database.push().key
        key?.let {
            val itemWithId = item.copy(id = it)
            database.child(it).setValue(itemWithId)
        }
    }

    fun getItem(itemId: String, callback: (MenuItem?) -> Unit) {
        database.child(itemId).get().addOnSuccessListener { dataSnapshot ->
            val item = dataSnapshot.getValue(MenuItem::class.java)
            callback(item)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun updateItem(itemId: String, newItem: MenuItem) {
        database.child(itemId).setValue(newItem)
    }

    fun deleteItem(itemId: String) {
        database.child(itemId).removeValue()
    }
}
