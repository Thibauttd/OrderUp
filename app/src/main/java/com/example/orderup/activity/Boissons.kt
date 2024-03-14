package com.example.orderup.activity

import DrinkAdaptater
import OrderRepository
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.databinding.BoissonsBinding
import com.example.orderup.model.MenuItem
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Boissons : Fragment() {

    private var _binding: BoissonsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tableId: String
    private lateinit var menuItemRepository: MenuItemRepository
    private lateinit var orderRepository: OrderRepository
    private var boissonsList: List<Pair<MenuItem, Int>> = emptyList()
    private var boissonsOriginales: Map<String, Int> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BoissonsBinding.inflate(inflater, container, false) // Correct binding class name
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuItemRepository = MenuItemRepository("boissons")
        orderRepository = OrderRepository()

        tableId = arguments?.getString("tableId") ?: ""

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBoissons)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getBoissonsListAndCounts { boissonsList ->
            recyclerView.adapter = DrinkAdaptater(boissonsList)
        }

        // Référencez le bouton "Valider"
        val buttonValider = view.findViewById<Button>(R.id.buttonValider)

        // Ajoutez un écouteur de clic pour le bouton "Valider"
        buttonValider.setOnClickListener {
            validerCommande()
            parentFragmentManager.popBackStack()
        }
    }


    private fun getBoissonsListAndCounts(callback: (List<Pair<MenuItem, Int>>) -> Unit) {
        val newBoissonsList = mutableListOf<Pair<MenuItem, Int>>()

        menuItemRepository.getAllItems { menuItems ->
            orderRepository.getOrderCountsForTable(tableId) { orderCounts ->
                menuItems.forEach { menuItem ->
                    val quantityOrdered = orderCounts[menuItem.id] ?: 0
                    newBoissonsList.add(Pair(menuItem, quantityOrdered))
                }

                // Initialiser boissonsOriginales avec les quantités initiales
                boissonsOriginales = orderCounts

                callback(newBoissonsList)
            }
        }
    }


    fun validerCommande() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewBoissons)
        val adapter = recyclerView?.adapter as? DrinkAdaptater

        adapter?.let { drinkAdapter ->
            boissonsList = drinkAdapter.getCurrentQuantities()

            for ((boisson, nouvelleQuantite) in boissonsList) {
                val ancienneQuantite = boissonsOriginales[boisson.id] ?: 0
                if (nouvelleQuantite != ancienneQuantite) {
                    // Vérifier si un ordre existe déjà pour cette boisson et cette table
                    orderRepository.getExistingOrderForTableAndMenuItem(tableId, boisson.id) { existingOrder ->
                        if (existingOrder != null) {
                            // Mettre à jour la quantité de l'ordre existant
                            val updatedQuantity = existingOrder.quantity + nouvelleQuantite - ancienneQuantite
                            existingOrder.quantity = updatedQuantity
                            orderRepository.updateOrder(existingOrder) { success ->
                                if (success) {
                                    // Afficher un message de succès
                                } else {
                                    // Afficher un message d'erreur
                                }
                            }
                        } else {
                            // Aucun ordre existant, créer un nouvel ordre
                            val newOrder = OrderModel(tableid = tableId, menuitemid = boisson.id, quantity = nouvelleQuantite)
                            orderRepository.addOrder(newOrder) { success ->
                                if (success) {
                                    // Afficher un message de succès
                                } else {
                                    // Afficher un message d'erreur
                                }
                            }
                        }
                    }
                }
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
