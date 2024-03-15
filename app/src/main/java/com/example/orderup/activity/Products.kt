package com.example.orderup.activity

import OrderRepository
import ProduitsAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.databinding.ProductsBinding
import com.example.orderup.model.MenuItem
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository
open class Products : Fragment() {

    private var pageTitle: String = ""
    private var imageResId: Int = R.drawable.boissons // Image par défaut
    private lateinit var repositoryParam: String // Paramètre du repository
    private lateinit var menuItemRepository: MenuItemRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var tableId: String
    private var produitsList: List<Pair<MenuItem, Int>> = emptyList()
    private var produitsOriginaux: Map<String, Int> = emptyMap()

    private var _binding: ProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mettre à jour le titre de la page
        binding.textView2.text = pageTitle

        // Mettre à jour l'image
        binding.imageView2.setImageResource(imageResId)

        menuItemRepository = MenuItemRepository(repositoryParam)
        orderRepository = OrderRepository()

        tableId = arguments?.getString("tableId") ?: ""

        val recyclerView = binding.recyclerViewProduits
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getProduitsListAndCounts { produitsList ->
            recyclerView.adapter = ProduitsAdapter(produitsList)
        }
        val buttonValider = view.findViewById<Button>(R.id.buttonValider)

        buttonValider.setOnClickListener {
            validerCommande()
            parentFragmentManager.popBackStack()
        }
    }

    fun setPageTitle(title: String) {
        pageTitle = title
    }

    fun setImageResource(imageResourceId: Int) {
        imageResId = imageResourceId
    }

    fun setRepositoryParameter(param: String) {
        repositoryParam = param
    }

    private fun getProduitsListAndCounts(callback: (List<Pair<MenuItem, Int>>) -> Unit) {
        val newProduitsList = mutableListOf<Pair<MenuItem, Int>>()

        menuItemRepository.getAllItems { menuItems ->
            orderRepository.getOrderCountsForTable(tableId) { orderCounts ->
                menuItems.forEach { menuItem ->
                    val quantityOrdered = orderCounts[menuItem.id] ?: 0
                    newProduitsList.add(Pair(menuItem, quantityOrdered))
                }

                // Initialiser produitsOriginaux avec les quantités initiales
                produitsOriginaux = orderCounts

                callback(newProduitsList)
            }
        }
    }

    fun validerCommande() {
        val recyclerView = binding.recyclerViewProduits
        val adapter = recyclerView.adapter as? ProduitsAdapter

        adapter?.let { produitsAdapter ->
            produitsList = produitsAdapter.getCurrentQuantities()

            for ((product, nouvelleQuantite) in produitsList) {
                val ancienneQuantite = produitsOriginaux[product.id] ?: 0
                if (nouvelleQuantite != ancienneQuantite) {
                    orderRepository.getExistingOrderForTableAndMenuItem(
                        tableId,
                        product.id
                    ) { existingOrder ->
                        if (existingOrder != null) {
                            // Mettre à jour la quantité de l'ordre existant
                            val updatedQuantity =
                                existingOrder.quantity + nouvelleQuantite - ancienneQuantite
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
                            val newOrder = OrderModel(
                                tableid = tableId,
                                menuitemid = product.id,
                                quantity = nouvelleQuantite
                            )
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
