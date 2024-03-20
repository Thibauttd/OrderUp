package com.example.orderup.model

data class RoomModel(
    var id: String = "",
    val name: String = "",
    val tables: List<TablePositionModel>
)
