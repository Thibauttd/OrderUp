package com.example.orderup.repository

import com.example.orderup.model.RoomModel
import com.google.firebase.database.*


class RoomRepository {
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("rooms")

    // Add a room to the database
    fun addRoom(room: RoomModel) {
        // Génération d'une clé unique pour le nouvel élément
        val roomRef = databaseRef.push().key
        roomRef?.let {
            // Ajout de l'élément à la base de données avec la clé générée
            databaseRef.child(it).setValue(room)
        }
    }

    // Update a room in the database
    fun updateRoom(room: RoomModel) {
        room.id?.let { roomId ->
            databaseRef.child(roomId).setValue(room)
        }
    }

    // Delete a room from the database
    fun deleteRoom(room: RoomModel) {
        room.id?.let { roomId ->
            databaseRef.child(roomId).removeValue()
        }
    }

    // Get all rooms from the database
    fun getAllRooms(callback: (List<RoomModel>) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = mutableListOf<RoomModel>()
                for (roomSnapshot in snapshot.children) {
                    val room = roomSnapshot.getValue(RoomModel::class.java)
                    room?.let { rooms.add(it) }
                }
                callback(rooms)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                callback(emptyList())
            }
        })
    }
}

