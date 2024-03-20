package com.example.orderup.repository

import com.example.orderup.model.TablePositionModel
import com.google.firebase.database.*
class TablePositionRepository {
    private var databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("table_positions")

    // Ajouter une position de table à la base de données

    fun addTablePosition(newTable: TablePositionModel) {
        // Génération d'une clé unique pour le nouvel élément
        val tablePosition = databaseRef.push().key
        tablePosition?.let {
            // Ajout de l'élément à la base de données avec la clé générée
            newTable.id = tablePosition
            databaseRef.child(it).setValue(newTable)
        }
    }


    // Mettre à jour une position de table dans la base de données
    fun updateTablePosition(tablePosition: TablePositionModel) {
        // Mettre à jour la position de la table dans la base de données en utilisant la clé générée automatiquement
        databaseRef.child(tablePosition.id).setValue(tablePosition)
    }

    // Supprimer une position de table de la base de données
    fun deleteTablePosition(tablePositionId: String) {
        databaseRef.child(tablePositionId).removeValue()
    }

    // Récupérer toutes les positions de table de la base de données
    fun getAllTablePositions(callback: (List<TablePositionModel>) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tablePositions = mutableListOf<TablePositionModel>()
                for (tablePositionSnapshot in snapshot.children) {
                    val tablePosition = tablePositionSnapshot.getValue(TablePositionModel::class.java)
                    tablePosition?.let { tablePositions.add(it) }
                }
                callback(tablePositions)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun deleteAllTables() {
        // Supprimer toutes les tables de la base de données
        databaseRef.removeValue()
    }

}
