package com.example.orderup.repository

import com.google.firebase.database.*
import com.example.orderup.model.ItemAdderModel

class ItemAdderRepository {
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("items")

    // Fonction pour ajouter un nouvel élément
    fun addItem(item: ItemAdderModel) {
        // Génération d'une clé unique pour le nouvel élément
        val newItemKey = database.push().key
        newItemKey?.let {
            // Ajout de l'élément à la base de données avec la clé générée
            database.child(it).setValue(item)
        }
    }

    // Fonction pour récupérer un élément par son ID
    fun getItem(itemId: String, callback: (ItemAdderModel?) -> Unit) {
        database.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ItemAdderModel::class.java)
                callback(item)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    // Fonction pour mettre à jour un élément existant
    fun updateItem(itemId: String, newItem: ItemAdderModel) {
        database.child(itemId).setValue(newItem)
    }

    // Fonction pour supprimer un élément
    fun deleteItem(itemId: String) {
        database.child(itemId).removeValue()
    }

    // Fonction pour récupérer tous les éléments
    fun getAllItems(callback: (List<ItemAdderModel>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemAdderModel>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemAdderModel::class.java)
                    item?.let { items.add(it) }
                }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}
