package com.example.orderup.model

class OrderModel (
    val id: String = "", // Unique identifier for the order
    val tableid: String = "", // ID of the table where the order was placed
    val menuitemid: String = "", // ID of the menu item ordered
    var quantity: Int =0, // Quantity of the menu item
    val ready: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderModel

        if (id != other.id) return false
        if (tableid != other.tableid) return false
        if (menuitemid != other.menuitemid) return false
        if (quantity != other.quantity) return false
        if (ready != other.ready) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + tableid.hashCode()
        result = 31 * result + menuitemid.hashCode()
        result = 31 * result + quantity
        result = 31 * result + ready.hashCode()
        return result
    }
}
