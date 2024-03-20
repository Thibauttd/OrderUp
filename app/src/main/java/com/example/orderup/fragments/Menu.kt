package com.example.orderup.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.MenuBinding
import com.example.orderup.repository.ItemAdderRepository
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class Menu : Fragment() {

    private var _binding: MenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var recognizedTextEt: EditText
    private lateinit var inputImageBtn: MaterialButton
    private lateinit var progressDialog: ProgressDialog
    private lateinit var textRecognizer: TextRecognizer

    private val boissonsList = mutableListOf<String>()
    private val entreesList = mutableListOf<String>()
    private val platsList = mutableListOf<String>()
    private val dessertsList = mutableListOf<String>()
    private val item_repo =  ItemAdderRepository()

    private companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST = 101
    }

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSuivant.setOnClickListener {
            findNavController().navigate(R.id.action_Menu_to_Role)
        }

        binding.desserts.setOnClickListener {
            findNavController().navigate(R.id.action_Menu_to_Dessert)
        }

        binding.dish.setOnClickListener {
            findNavController().navigate(R.id.action_Menu_to_Dish)
        }

        binding.starters.setOnClickListener {
            findNavController().navigate(R.id.action_Menu_to_Starter)
        }

        binding.drinks.setOnClickListener {
            findNavController().navigate(R.id.action_Menu_to_Drink)
        }

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Veuillez patienter")
        progressDialog.setCanceledOnTouchOutside(false)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        inputImageBtn = binding.inputImageBtn

        inputImageBtn.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun recognizeTextFromImage() {
        progressDialog.setMessage("Préparation de l'image...")
        progressDialog.show()

        try {
            val inputImage: InputImage = InputImage.fromFilePath(requireContext(), imageUri!!)
            progressDialog.setMessage("Reconnaissance du texte...")
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText: Text ->
                    progressDialog.dismiss()
                    val recognizedText: String = visionText.text

                    // Extraire les éléments du menu
                    filterAndCategorizeText(recognizedText)
                }
                .addOnFailureListener { e: Exception ->
                    progressDialog.dismiss()
                    showToast("Échec de la reconnaissance du texte : ${e.message}")
                }
        } catch (e: Exception) {
            progressDialog.dismiss()
            showToast("Échec de la préparation de l'image : ${e.message}")
        }
    }

    private fun filterAndCategorizeText(text: String) {
        val lines = text.split("\n")
        var currentCategory: MutableList<String>? = null

        for (line in lines) {
            // Identify the current category
            when {
                line.contains("Boisson", ignoreCase = true) -> {
                    currentCategory = boissonsList
                }
                line.contains("Entrée", ignoreCase = true) -> {
                    currentCategory = entreesList
                }
                line.contains("Plat", ignoreCase = true) -> {
                    currentCategory = platsList
                }
                line.contains("Dessert", ignoreCase = true) -> {
                    currentCategory = dessertsList
                }
                // If the line doesn't contain a category, add the item to the current list
                currentCategory != null -> {
                    currentCategory.add(line)
                }
            }
        }

        println("Boissons: $boissonsList")
        println("Entrées: $entreesList")
        println("Plats: $platsList")
        println("Desserts: $dessertsList")


        item_repo.addListToItem(boissonsList)
        item_repo.addListToItem(entreesList)
        item_repo.addListToItem(platsList)
        item_repo.addListToItem(dessertsList)

    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titre d'exemple")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Description d'exemple")
        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun pickDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*" // change the MIME type if you want to allow other file types
        documentActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                recognizeTextFromImage()
            } else {
                showToast("Annulé...")
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                recognizeTextFromImage()
            }else{
                showToast("Annulé...")
            }
        }

    private val documentActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data?.data
                recognizeTextFromImage()
            }else{
                showToast("Annulé...")
            }
        }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkCameraPermissions() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST)
    }

    private fun showImageSourceDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Choisir une source d'image")
            .setItems(arrayOf("Appareil photo", "Galerie", "Documents")) { _, which ->
                when (which) {
                    0 -> {
                        if (checkCameraPermissions()) {
                            pickImageCamera()
                        } else {
                            requestCameraPermission()
                        }
                    }
                    1 -> pickImageGallery()
                    2 -> pickDocument()
                }
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, vous pouvez effectuer les opérations de stockage ici
            } else {
                showToast("Autorisation de stockage refusée")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

