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
        val key = database.push().key
        key?.let {
            val orderWithId = OrderModel(order.id, order.tableId, order.menuItemId, order.quantity, order.ready)

            database.child(it).setValue(orderWithId)
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
        database.orderByChild("tableId").equalTo(tableId)
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
}
