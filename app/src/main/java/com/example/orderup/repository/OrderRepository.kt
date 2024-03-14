import com.example.orderup.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OrderRepository {

    private val database: DatabaseReference = Firebase.database.reference.child("orders")

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

}
