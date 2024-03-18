package com.example.orderup.repository

import com.example.orderup.model.MenuItemModel
import com.example.orderup.model.OrderModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OrderRepository {

    // Reference to the Firebase database for orders
    private val database: DatabaseReference = Firebase.database.reference.child("orders")

    // List to store all menu items
    private var allMenuItems = mutableListOf<MenuItemModel>()

    // Add an order to the database
    fun addOrder(order: OrderModel, callback: (Boolean) -> Unit) {
        val key = database.push().key ?: return // Generate a random key
        val orderWithId = OrderModel(key, order.tableid, order.menuitemid, order.quantity, order.ready) // Associate the generated key with the order

        database.child(key).setValue(orderWithId) // Add the order with the generated key to the database
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Update an existing order in the database
    fun updateOrder(order: OrderModel, callback: (Boolean) -> Unit) {
        val orderRef = database.child(order.id)

        if (order.quantity == 0) {
            // Remove the order if there's no quantity left
            orderRef.removeValue()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            // Update the quantity of the order
            orderRef.setValue(order)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }

    // Get all orders from the database
    fun getAllOrders(callback: (List<OrderModel>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<OrderModel>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(OrderModel::class.java)
                    order?.let { orders.add(it) }
                }
                callback(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // Get all orders for a specific table from the database
    fun getOrdersForTable(tableId: String, callback: (List<OrderModel>) -> Unit) {
        database.orderByChild("tableid").equalTo(tableId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<OrderModel>()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        order?.let { orders.add(it) }
                    }
                    callback(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    // Get the counts of orders for each menu item for a specific table
    fun getOrderCountsForTable(tableId: String, callback: (Map<String, Int>) -> Unit) {
        getOrdersForTable(tableId) { orders ->
            val orderCounts = mutableMapOf<String, Int>()

            for (order in orders) {
                val menuItemId = order.menuitemid
                if (orderCounts.containsKey(menuItemId)) {
                    orderCounts[menuItemId] = orderCounts.getValue(menuItemId) + 1
                } else {
                    orderCounts[menuItemId] = order.quantity
                }
            }

            callback(orderCounts)
        }
    }

    // Get an existing order for a specific table and menu item
    fun getExistingOrderForTableAndMenuItem(tableId: String, menuItemId: String, callback: (OrderModel?) -> Unit) {
        database.orderByChild("tableid").equalTo(tableId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(OrderModel::class.java)
                        if (order != null && order.menuitemid == menuItemId) {
                            callback(order)
                            return
                        }
                    }
                    // No existing order found for this table and item
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    callback(null)
                }
            })
    }

    // Get cookable orders from the database
    fun getCookableOrders(callback: (List<OrderModel>) -> Unit) {
        // Reference to the orders database
        val ordersRef = OrderRepository().database

        // Get all menu items
        getAppetizerItems { appetizerItems ->
            getDishItems { dishItems ->
                getDessertItems { dessertItems ->
                    // Once all menu items are retrieved, add them to allMenuItems
                    val allMenuItems = mutableListOf<MenuItemModel>().apply {
                        addAll(appetizerItems)
                        addAll(dishItems)
                        addAll(dessertItems)
                    }

                    // Listen for new orders added to the database
                    ordersRef.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val newOrder = snapshot.getValue(OrderModel::class.java)
                            newOrder?.let { order ->
                                // Check if the order is for an existing menu item
                                if (allMenuItems.any { it.id == order.menuitemid }) {
                                    callback(listOf(order)) // Call the callback with the new order
                                }
                            }
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                            // Logic to handle order changes if needed
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            // Logic to handle order removals if needed
                        }

                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                            // Logic to handle order movements if needed
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle errors if needed
                        }
                    })
                }
            }
        }
    }

    // Get appetizer items from the database
    private fun getAppetizerItems(callback: (List<MenuItemModel>) -> Unit) {
        if (allMenuItems.isEmpty()) {
            val menuItemRepository = MenuItemRepository("entrees")
            menuItemRepository.getAllItems { items ->
                callback(items)
            }
        } else {
            callback(allMenuItems) // Use already stored items if they exist
        }
    }

    // Get dish items from the database
    private fun getDishItems(callback: (List<MenuItemModel>) -> Unit) {
        if (allMenuItems.isEmpty()) {
            val menuItemRepository = MenuItemRepository("plats")
            menuItemRepository.getAllItems { items ->
                callback(items)
            }
        } else {
            callback(allMenuItems) // Use already stored items if they exist
        }
    }

    // Get dessert items from the database
    private fun getDessertItems(callback: (List<MenuItemModel>) -> Unit) {
        if (allMenuItems.isEmpty()) {
            val menuItemRepository = MenuItemRepository("desserts")
            menuItemRepository.getAllItems { items ->
                callback(items)
            }
        } else {
            callback(allMenuItems) // Use already stored items if they exist
        }
    }

    // Filter orders by menu items
    private fun filterOrdersByMenuItems(orders: List<OrderModel>): List<OrderModel> {
        // Filter orders to keep only those whose menuItemId is present in the allMenuItems list
        return orders.filter { order ->
            allMenuItems.any { it.id == order.menuitemid }
        }
    }
}
