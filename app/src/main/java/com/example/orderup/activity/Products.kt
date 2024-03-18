package com.example.orderup.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orderup.R
import com.example.orderup.adaptater.ProduitsAdapter
import com.example.orderup.databinding.ProductsBinding
import com.example.orderup.model.MenuItemModel
import com.example.orderup.model.OrderModel
import com.example.orderup.repository.MenuItemRepository
import com.example.orderup.repository.OrderRepository

/**
 * A base [Fragment] class for displaying product categories and managing orders.
 * Subclasses should implement specific functionality for different product categories.
 */
open class Products : Fragment() {

    private var pageTitle: String = ""
    private var imageResId: Int = R.drawable.drink // Default image
    private lateinit var repositoryParam: String // Repository parameter
    private lateinit var menuItemRepository: MenuItemRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var tableId: String
    private var produitsList: List<Pair<MenuItemModel, Int>> = emptyList()
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

        // Update page title
        binding.textView2.text = pageTitle

        // Update image
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

    /**
     * Set the page title.
     * @param title The title to be set.
     */
    fun setPageTitle(title: String) {
        pageTitle = title
    }

    /**
     * Set the image resource for the product category.
     * @param imageResourceId The resource ID of the image.
     */
    fun setImageResource(imageResourceId: Int) {
        imageResId = imageResourceId
    }

    /**
     * Set the repository parameter for fetching product data.
     * @param param The parameter to be set.
     */
    fun setRepositoryParameter(param: String) {
        repositoryParam = param
    }

    private fun getProduitsListAndCounts(callback: (List<Pair<MenuItemModel, Int>>) -> Unit) {
        val newProduitsList = mutableListOf<Pair<MenuItemModel, Int>>()

        menuItemRepository.getAllItems { menuItems ->
            orderRepository.getOrderCountsForTable(tableId) { orderCounts ->
                menuItems.forEach { menuItem ->
                    val quantityOrdered = orderCounts[menuItem.id] ?: 0
                    newProduitsList.add(Pair(menuItem, quantityOrdered))
                }

                // Initialize produitsOriginaux with the initial quantities
                produitsOriginaux = orderCounts

                callback(newProduitsList)
            }
        }
    }

    /**
     * Validate the current order and update the database.
     */
    private fun validerCommande() {
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
                            // Update quantity of existing order
                            val updatedQuantity =
                                existingOrder.quantity + nouvelleQuantite - ancienneQuantite
                            existingOrder.quantity = updatedQuantity
                            orderRepository.updateOrder(existingOrder) {
                            }
                        } else {
                            // No existing order, create a new order
                            val newOrder = OrderModel(
                                tableid = tableId,
                                menuitemid = product.id,
                                quantity = nouvelleQuantite
                            )
                            orderRepository.addOrder(newOrder) {
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
