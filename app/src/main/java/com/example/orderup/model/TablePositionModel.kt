package com.example.orderup.model

data class TablePositionModel(
    var id: String ="",
    val tableid:  String ="", // L'ID de la table associée
    val x: Float =0f,
    val y: Float =0f
)