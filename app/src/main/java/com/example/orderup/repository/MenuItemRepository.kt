package com.example.orderup.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.orderup.model.MenuItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MenuItemRepository(private val category: String) {
    private val database: DatabaseReference = Firebase.database.reference.child(category)

    fun addItem(item: MenuItemModel) {
        val key = database.push().key
        key?.let {
            val itemWithId = item.copy(id = it)
            database.child(it).setValue(itemWithId)
        }
    }

    fun getItem(itemId: String, callback: (MenuItemModel?) -> Unit) {
        database.child(itemId).get().addOnSuccessListener { dataSnapshot ->
            val item = dataSnapshot.getValue(MenuItemModel::class.java)
            callback(item)
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun getAllItems(callback: (List<MenuItemModel>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<MenuItemModel>()

                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(MenuItemModel::class.java)
                    item?.let { items.add(it) }
                }
                callback(items)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }


    fun updateItem(itemId: String, newItem: MenuItemModel) {
        database.child(itemId).setValue(newItem)
    }

    fun deleteItem(itemId: String) {
        database.child(itemId).removeValue()
    }
}
