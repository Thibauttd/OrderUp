import com.example.orderup.model.MenuItem
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OrderRepository {

    private val database: DatabaseReference = Firebase.database.reference.child("orders")
    private var allMenuItems = mutableListOf<MenuItem>()

    // Ajouter une commande à la base de données
    fun addOrder(order: OrderModel, callback: (Boolean) -> Unit) {
        val key = database.push().key ?: return // Générer une clé aléatoire
        val orderWithId = OrderModel(key, order.tableid, order.menuitemid, order.quantity, order.ready) // Associer la clé générée à la commande

        database.child(key).setValue(orderWithId) // Ajouter la commande avec la clé générée à la base de données
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun updateOrder(order: OrderModel, callback: (Boolean) -> Unit) {
        val orderRef = database.child(order.id)

        if (order.quantity == 0) {
            // Supprimer l'ordre s'il n'y a plus de quantité
            orderRef.removeValue()
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            // Mettre à jour la quantité de l'ordre
            orderRef.setValue(order)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }

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

    // Récupérer toutes les commandes pour une table spécifique
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
                    // Aucun ordre existant trouvé pour cette table et cet item
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Gestion de l'erreur
                    callback(null)
                }
            })
    }

    fun getCookableOrders(callback: (List<OrderModel>) -> Unit) {
        // Référence à la base de données des commandes
        val ordersRef = OrderRepository().database

        // Récupérer tous les éléments de menu
        getAppetizerItems { appetizerItems ->
            getDishItems { dishItems ->
                getDessertItems { dessertItems ->
                    // Une fois que tous les éléments de menu sont récupérés, les ajouter à allMenuItems
                    val allMenuItems = mutableListOf<MenuItem>().apply {
                        addAll(appetizerItems)
                        addAll(dishItems)
                        addAll(dessertItems)
                    }
                    println("all_menu_items : $allMenuItems")

                    // Écouter les nouvelles commandes ajoutées à la base de données
                    ordersRef.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val newOrder = snapshot.getValue(OrderModel::class.java)
                            newOrder?.let { order ->
                                // Vérifier si la commande est pour un élément de menu existant
                                if (allMenuItems.any { it.id == order.menuitemid }) {
                                    callback(listOf(order)) // Appeler le callback avec la nouvelle commande
                                }
                            }
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                            // Logique pour gérer les modifications de commande si nécessaire
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                            // Logique pour gérer les suppressions de commande si nécessaire
                        }

                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                            // Logique pour gérer les déplacements de commande si nécessaire
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Gérer les erreurs si nécessaire
                        }
                    })
                }
            }
        }
    }




    private fun getAppetizerItems(callback: (List<MenuItem>) -> Unit) {
        if (allMenuItems.isEmpty()) {
            val menuItemRepository: MenuItemRepository = MenuItemRepository("entrees")
            menuItemRepository.getAllItems { items ->
                callback(items)
            }
        } else {
            callback(allMenuItems) // Utilisez les éléments déjà stockés s'ils existent
        }
    }

    private fun getDishItems(callback: (List<MenuItem>) -> Unit) {
        if (allMenuItems.isEmpty()) {
            val menuItemRepository: MenuItemRepository = MenuItemRepository("plats")
            menuItemRepository.getAllItems { items ->
                callback(items)
            }
        } else {
            callback(allMenuItems) // Utilisez les éléments déjà stockés s'ils existent
        }
    }

    private fun getDessertItems(callback: (List<MenuItem>) -> Unit) {
        if (allMenuItems.isEmpty()) {
            val menuItemRepository: MenuItemRepository = MenuItemRepository("desserts")
            menuItemRepository.getAllItems { items ->
                callback(items)
            }
        } else {
            callback(allMenuItems) // Utilisez les éléments déjà stockés s'ils existent
        }
    }

    private fun filterOrdersByMenuItems(orders: List<OrderModel>): List<OrderModel> {
        // Filtrer les commandes pour garder uniquement celles dont le menuItemId est présent dans la liste allMenuItems
        return orders.filter { order ->
            println("order : ${order.menuitemid}")
            println("menu_items : $allMenuItems")
            allMenuItems.any {
                println("it_id : $it.id")
                it.id == order.menuitemid }
        }
    }

}
