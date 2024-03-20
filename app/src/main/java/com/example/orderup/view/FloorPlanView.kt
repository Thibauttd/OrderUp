package com.example.orderup.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.orderup.model.TableModel
import com.example.orderup.model.TablePositionModel
import com.example.orderup.repository.TablePositionRepository

class FloorPlanView : View {

    private val tablePositions = mutableListOf<TablePositionModel>()

    // List to store the tables
    private val tablesList = mutableListOf<Table>()

    // Paint object for drawing tables
    private val tablePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    // Variables to track touch events
    private var movingTableIndex: Int? = null
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f


    private val tablePositionRepository : TablePositionRepository = TablePositionRepository()
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Dessinez chaque table dans la liste
        for (table in tablesList) {
            // Determinez la couleur de trait en fonction de la disponibilité de la table
            val tableStrokeColor = if (table.tableModel.occupied) Color.GREEN else Color.RED
            tablePaint.color = tableStrokeColor

            // Dessinez le contour de la table
            val strokeWidth = 8f // Largeur du contour de la table
            tablePaint.style = Paint.Style.STROKE
            tablePaint.strokeWidth = strokeWidth
            canvas.drawRect(table.x, table.y, table.x + table.width, table.y + table.height, tablePaint)

            // Affichez le numéro de la table et la capacité sur deux lignes séparées à l'intérieur du rectangle de la table
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = table.height / 6 // Taille de la police en fonction de la hauteur de la table
                textAlign = Paint.Align.CENTER // Centrer le texte horizontalement
            }
            val tableNumberText = "Table n°${table.tableModel.numero}"
            val capacityText = "Couverts: ${table.tableModel.capacity}"

            // Centrez le texte verticalement à l'intérieur du rectangle de la table
            val textX = table.x + table.width / 2 // Centrer le texte horizontalement
            val textY1 = table.y + table.height / 3 // Positionner la première ligne de texte
            val textY2 = table.y + table.height * 2 / 3 // Positionner la deuxième ligne de texte
            canvas.drawText(tableNumberText, textX, textY1, textPaint) // Dessiner le numéro de table
            canvas.drawText(capacityText, textX, textY2, textPaint) // Dessiner la capacité
        }
    }

    fun setTablePositions(positions: List<TablePositionModel>) {
        tablePositions.clear()
        tablePositions.addAll(positions)
        // Redraw the view after setting table positions
        invalidate()
    }


    // Méthode pour vérifier si la position d'une table a été sauvegardée
    override fun performClick(): Boolean {
        super.performClick()
        // Handle click actions here if needed
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Vérifiez si le point de toucher est à l'intérieur d'une table
                movingTableIndex = getTouchedTableIndex(event.x, event.y)
                lastTouchX = event.x
                lastTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                // Déplacez la table sélectionnée
                movingTableIndex?.let { index ->
                    val table = tablesList[index]
                    val deltaX = event.x - lastTouchX
                    val deltaY = event.y - lastTouchY
                    val newX = table.x + deltaX
                    val newY = table.y + deltaY
                    // Vérifiez les limites de la vue et ajustez les coordonnées de la table si nécessaire
                    table.x = newX.coerceIn(0f, width - table.width)
                    table.y = newY.coerceIn(0f, height - table.height)
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate() // Redessinez la vue pour afficher le déplacement
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                movingTableIndex = null
            }
        }
        return true
    }


    // Method to check if touch point is inside any table
    private fun getTouchedTableIndex(x: Float, y: Float): Int? {
        for ((index, table) in tablesList.withIndex()) {
            if (x >= table.x && x <= table.x + table.width && y >= table.y && y <= table.y + table.height) {
                return index
            }
        }
        return null
    }

    // Method to add a table to the list
    fun addTable(table: TableModel) {
        tablesList.add(Table(x = 50f, y = 50f, width = 150f, height = 150f, tableModel = table))
        invalidate() // Redraw the view after adding the table
    }

    fun addTableWithPosition(table: TableModel, position: TablePositionModel) {
        tablesList.add(Table(x = position.x, y = position.y, width = 150f, height = 150f, tableModel = table))
        invalidate() // Redraw the view after adding the table
    }

    // Method to undo the last action

    // Data class to represent a table
    data class Table(
        var x: Float,
        var y: Float,
        val width: Float,
        val height: Float,
        val tableModel: TableModel // Table data model
    )


    // Method to save table position
    private fun saveTablePosition(table: TableModel, positionX: Float, positionY: Float) {
        // Save the table position to the repository
        val tablePosition = TablePositionModel(id = "", tableid = table.key, x = positionX, y = positionY)
        tablePositionRepository.addTablePosition(tablePosition)
    }

    private fun isTablePositionSaved(tableId: String): Boolean {
        // Vérifier si la position de la table est déjà sauvegardée dans la liste des positions de table
        return tablePositions.any { it.tableid == tableId }
    }

    fun saveTablePositions() {
        val newTablesToSave = mutableListOf<Table>()

        // Parcourir la liste des tables pour sauvegarder ou mettre à jour les positions
        tablesList.forEach { table ->
            if (isTablePositionSaved(table.tableModel.key)) {
                // La position de la table est déjà sauvegardée, donc mettez à jour sa position

                // Récupérer l'identifiant de la table dans les positions de table
                val tablePositionId = tablePositions.find { it.tableid == table.tableModel.key }?.id

                // Vérifier si l'identifiant de la position de table a été trouvé
                if (tablePositionId != null) {
                    // Créer un nouvel objet TablePositionModel avec l'identifiant récupéré et les nouvelles coordonnées de la table
                    val updatedTablePosition = TablePositionModel(tablePositionId, table.tableModel.key, table.x, table.y)

                    // Mettre à jour la position de la table dans la base de données
                    tablePositionRepository.updateTablePosition(updatedTablePosition)
                }
            } else {
                // La position de la table n'est pas encore sauvegardée, donc ajoutez-la à la liste des nouvelles tables à sauvegarder
                newTablesToSave.add(table)
            }
        }

        // Sauvegarder les nouvelles tables
        newTablesToSave.forEach { table ->
            saveTablePosition(table.tableModel, table.x, table.y)
        }
    }

    fun deleteTheTables(){
        tablePositionRepository.deleteAllTables()
        tablesList.clear()
        tablePositions.clear()
        invalidate()
    }
}

