package com.example.orderup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderup.R
import com.example.orderup.adapter.MenuItemAdapter
import com.example.orderup.databinding.MenuItemBinding
import com.example.orderup.model.MenuItemModel
import com.example.orderup.repository.ItemAdderRepository
import com.example.orderup.repository.MenuItemRepository

open class MenuItem : Fragment(), MenuItemAdapter.MenuItemClickListener {
    protected var pageTitle: String = ""
    protected var imageResId: Int = 1 // Default image
    protected lateinit var repositoryParam: String // Repository parameter
    protected lateinit var menuItemRepository: MenuItemRepository
    protected var produitsList: MutableList<MenuItemModel> = mutableListOf() // Utilisez MutableList
    private lateinit var recyclerView: RecyclerView
    private val existingItemNames: MutableList<String> = mutableListOf()

    private var _binding: MenuItemBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MenuItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation du menuItemRepository avec le paramètre potentiel.
        menuItemRepository = MenuItemRepository(repositoryParam)

        // Mise à jour du titre et de l'image de la page.
        binding.textView2.text = pageTitle
        binding.imageView2.setImageResource(imageResId)

        // Configuration du recyclerView.
        recyclerView = binding.recyclerViewProduits
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Récupération et affichage des éléments du menu.
        menuItemRepository.getAllItems { menuItems ->
            produitsList.clear() // Clear the list before adding items
            produitsList.addAll(menuItems) // Add all items from repository
            recyclerView.adapter = MenuItemAdapter(produitsList, this)
        }

        val itemAdderRepository = ItemAdderRepository()
        itemAdderRepository.getAllItems { itemAdders ->
            existingItemNames.clear() // Clear existing names
            existingItemNames.addAll(itemAdders.map { it.name }) // Add all names from repository
        }

        // Gestion du clic sur le bouton pour revenir à la carte ou à l'écran précédent.
        binding.buttonReturnToTheCard.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.buttonAddItem.setOnClickListener {
            showAddMenuItemDialog()
        }
    }

    private fun showAddMenuItemDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_menu_item, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Ajouter un nouvel élément de menu")
            .setNegativeButton("Annuler", null)
            .setPositiveButton("Ajouter", null)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        // Obtenir une référence à l'EditText pour le nom de l'élément
        val itemNameEditText = dialogView.findViewById<AutoCompleteTextView>(R.id.editTextItemName)

        // Obtenir une référence à l'EditText pour le prix de l'élément
        val itemPriceEditText = dialogView.findViewById<EditText>(R.id.editTextItemPrice)

        // Adapter pour la liste des noms des éléments existants
        val itemNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, existingItemNames)
        itemNameEditText.setAdapter(itemNameAdapter)

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val itemName = itemNameEditText.text.toString().trim()
            val itemPrice = itemPriceEditText.text.toString().trim()

            if (itemName.isNotEmpty() && itemPrice.isNotEmpty()) {
                val newItem = MenuItemModel(name = itemName, price = itemPrice.toInt(), id = "")

                menuItemRepository.addItem(newItem)

                produitsList.add(newItem) // Ajouter le nouvel élément à la liste
                recyclerView.adapter?.notifyItemInserted(produitsList.size - 1) //Notifier l'adapter du nouvel élément
                alertDialog.dismiss()
            } else {
                // Afficher un message d'erreur ou gérer les champs vides
            }
        }
    }



    private fun showEditDeleteDialog(item: MenuItemModel) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_delete_item, null)

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextPrice = dialogView.findViewById<EditText>(R.id.editTextPrice)
        val btnEdit = dialogView.findViewById<Button>(R.id.btnEdit)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDelete)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Remplissage des champs d'édition avec les valeurs actuelles de l'élément
        editTextName.setText(item.name)
        editTextPrice.setText(item.price.toString())

        btnEdit.setOnClickListener {
            // Action pour modifier l'élément
            val newName = editTextName.text.toString().trim()
            val newPrice = editTextPrice.text.toString().trim()

            if (newName.isNotEmpty() && newPrice.isNotEmpty()) {
                // Modifier l'élément avec les nouvelles valeurs
                val updatedItem = item.copy(name = newName, price = newPrice.toInt())
                val itemIndex = produitsList.indexOf(item)
                if (itemIndex != -1) {
                    produitsList[itemIndex] = updatedItem // Update item in the list
                    recyclerView.adapter?.notifyItemChanged(itemIndex) // Notify adapter about the change
                }
            }

            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            // Action pour supprimer l'élément
            menuItemRepository.deleteItem(item.id)
            produitsList.remove(item) // Remove item from list
            recyclerView.adapter?.notifyDataSetChanged() // Notify adapter about the change
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Here you can add methods to set pageTitle, imageResId, and repositoryParam
    protected fun updatePageTitle(title: String) {
        pageTitle = title
    }

    protected fun updateImageResId(resId: Int) {
        imageResId = resId
    }

    protected fun updateRepositoryParam(param: String) {
        repositoryParam = param
    }

    override fun onItemClick(item: MenuItemModel) {
        // Action lorsque l'élément est cliqué
    }

    override fun onItemEdit(item: MenuItemModel) {
        // Action lorsque l'élément est édité
    }

    override fun onItemDelete(item: MenuItemModel) {
        // Action lorsque l'élément est supprimé
    }

    override fun onItemLongClick(item: MenuItemModel) {
        showEditDeleteDialog(item)
    }

}



