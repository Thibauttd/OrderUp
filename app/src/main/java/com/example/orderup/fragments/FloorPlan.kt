package com.example.orderup.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.orderup.R
import com.example.orderup.model.TableModel
import com.example.orderup.repository.TablePositionRepository
import com.example.orderup.repository.TableRepository
import com.example.orderup.view.FloorPlanView

class FloorPlan : Fragment() {

    private val tablesList = mutableListOf<TableModel>()
    private lateinit var floorPlanView: FloorPlanView
    private lateinit var tableSpinner: Spinner
    private lateinit var addTableButton: Button

    private var isViewCreated: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.floor_plan, container, false)
        // Initialiser votre vue ici
        isViewCreated = true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser la liste des tables
        initTablesList()

        val saveButton: Button = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            floorPlanView.saveTablePositions() // Appeler la fonction de sauvegarde dans FloorPlanView
        }
        val resetButton: Button = view.findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmation") // Titre de la pop-up
                .setMessage("Êtes-vous sûr de vouloir réinitialiser le plan ?") // Le message affiché
                .setPositiveButton("Oui") { dialog, which ->
                    // Code à exécuter si l'utilisateur confirme
                    floorPlanView.deleteTheTables()
                }
                .setNegativeButton("Non", null) // Si l'utilisateur annule, rien ne se passe
                .show() // Affiche l'AlertDialog
        }

    }

    private fun initTablesList() {
        val tableRepository = TableRepository()
        tableRepository.getAllTables(object : TableRepository.TablesListener {
            override fun onTablesReceived(tables: List<TableModel>) {
                println("table : $tables")
                tablesList.clear() // Clear existing tables
                tablesList.addAll(tables) // Add tables from repository
                // Configurer la vue FloorPlanView
                if (isViewCreated) {
                    // Configurer la vue FloorPlanView
                    setupFloorPlanView()

                    // Configurer le Spinner et le bouton
                    setupSpinnerAndButton()
                }
            }

            override fun onTablesError(error: String) {
                // Handle error if needed
            }
        })
    }

    private fun isTablePositionSaved(tableId: String, callback: (Boolean) -> Unit) {
        // Récupérer toutes les positions des tables depuis la base de données
        val tablePositionRepository = TablePositionRepository()
        tablePositionRepository.getAllTablePositions { tablePositions ->
            val isSaved = tablePositions.any { it.tableid == tableId }
            callback(isSaved)
        }
    }

    private fun setupFloorPlanView() {
        floorPlanView = requireView().findViewById(R.id.floorPlanView)

        val tablePositionRepository = TablePositionRepository()
        tablePositionRepository.getAllTablePositions { positions ->
            // Set table positions in FloorPlanView
            floorPlanView.setTablePositions(positions)

            // Liste pour stocker les tables avec des positions sauvegardées
            val tablesWithPositions = mutableListOf<TableModel>()

            // Fonction récursive pour vérifier chaque table
            fun checkTablesWithPositions(index: Int) {
                if (index < tablesList.size) {
                    val table = tablesList[index]
                    isTablePositionSaved(table.key) { isSaved ->
                        if (isSaved) {
                            // Trouver la position sauvegardée correspondant à cette table
                            val position = positions.find { it.tableid == table.key }
                            position?.let { pos ->
                                tablesWithPositions.add(table)
                                // Ajouter la table avec sa position sauvegardée
                                floorPlanView.addTableWithPosition(table, pos)
                            }
                        }
                        // Vérifiez la table suivante
                        checkTablesWithPositions(index + 1)
                    }
                }
            }

            // Commencez la vérification avec la première table
            checkTablesWithPositions(0)
        }
    }



    private fun setupSpinnerAndButton() {
        tableSpinner = requireView().findViewById(R.id.tableSpinner)

        // Liste pour stocker les tables sans positions sauvegardées
        val tablesWithoutPositions = mutableListOf<TableModel>()

        // Fonction récursive pour vérifier chaque table
        fun checkTablesWithoutPositions(index: Int) {
            if (index < tablesList.size) {
                val table = tablesList[index]
                isTablePositionSaved(table.key) { isSaved ->
                    if (!isSaved) {
                        tablesWithoutPositions.add(table)
                    }
                    // Vérifiez la table suivante
                    checkTablesWithoutPositions(index + 1)
                }
            } else {
                // Lorsque toutes les tables ont été vérifiées, remplissez le Spinner avec les tables sans positions sauvegardées
                val tableNames = tablesWithoutPositions.map { it.numero }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tableNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                tableSpinner.adapter = adapter
            }
        }

        // Commencez la vérification avec la première table
        checkTablesWithoutPositions(0)

        addTableButton = requireView().findViewById(R.id.addTableButton)
        // Ajouter un écouteur de clic au bouton
        addTableButton.setOnClickListener {
            // Récupérer la table sélectionnée depuis le Spinner
            val selectedTableIndex = tableSpinner.selectedItemPosition
            if (selectedTableIndex != Spinner.INVALID_POSITION) {
                val selectedTable = tablesWithoutPositions[selectedTableIndex]
                // Ajouter la table sélectionnée à la vue FloorPlanView
                floorPlanView.addTable(selectedTable)
            }
        }
    }

    override fun onDestroyView() {
        // Nettoyer les ressources ou arrêter les opérations associées à la vue ici
        isViewCreated = false
        super.onDestroyView()
    }

}





