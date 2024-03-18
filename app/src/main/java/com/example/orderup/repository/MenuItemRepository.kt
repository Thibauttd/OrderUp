package com.example.orderup.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.orderup.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch

class MenuItemRepository(var category: String) {
    private var database: DatabaseReference = Firebase.database.reference.child(category)

    fun getItem(itemId: String, callback: (MenuItem?) -> Unit) {
        database.child(itemId).get().addOnSuccessListener { dataSnapshot ->
            val item = dataSnapshot.getValue(MenuItem::class.java)
            callback(item)
        }.addOnFailureListener {
            callback(null)
        }
    }

     suspend fun getItemName(itemId: String): String? {
        val categories = listOf("entrees", "plats", "desserts")

        // Parcours des catégories pour récupérer le nom de l'item
        categories.forEach { category ->
            val dataSnapshot = database.child(category).child(itemId).child("name").get().await()
            val itemName = dataSnapshot.getValue(String::class.java)
            if (itemName != null) {
                return itemName
            }
        }

        // Si aucun nom d'item n'a été trouvé, renvoyer null
        return null
    }

    fun getAllItems(callback: (List<MenuItem>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<MenuItem>()

                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(MenuItem::class.java)
                    item?.let { items.add(it) }
                }
                callback(items)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }


    fun updateItem(itemId: String, newItem: MenuItem) {
        database.child(itemId).setValue(newItem)
    }

    fun deleteItem(itemId: String) {
        database.child(itemId).removeValue()
    }
}
