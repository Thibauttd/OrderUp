package com.example.orderup.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.CarteBinding
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class Carte : Fragment() {

    private var _binding: CarteBinding? = null
    private val binding get() = _binding!!

    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognizedTextBtn: MaterialButton
    private lateinit var recognizedTextEt: EditText
    private lateinit var progressDialog: ProgressDialog

    private val boissonsList = mutableListOf<String>()
    private val entreesList = mutableListOf<String>()
    private val platsList = mutableListOf<String>()
    private val dessertsList = mutableListOf<String>()

    private companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST = 101
    }

    private var imageUri: Uri? = null

    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private lateinit var textRecognizer: TextRecognizer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CarteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSuivant.setOnClickListener {
            findNavController().navigate(R.id.action_Carte_to_Role)
        }

        binding.btnPrecedent.setOnClickListener {
            findNavController().navigate(R.id.action_Carte_to_Formules)
        }

        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        inputImageBtn = binding.inputImageBtn
        recognizedTextBtn = binding.recognizedTextBtn
        recognizedTextEt = binding.recognizedTextEt

        inputImageBtn.setOnClickListener {
            showInputImageDialog()
        }

        recognizedTextBtn.setOnClickListener {
            if (imageUri == null) {
                showToast("Pick Image First...")
            } else {
                recognizeTextFromImage()
            }
        }
    }

    private fun recognizeTextFromImage() {
        progressDialog.setMessage("Preparing Image...")
        progressDialog.show()

        try {
            val inputImage: InputImage = InputImage.fromFilePath(requireContext(), imageUri!!)
            progressDialog.setMessage("Recognizing text...")
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText: Text ->
                    progressDialog.dismiss()
                    val recognizedText: String = visionText.text
                    recognizedTextEt.setText(recognizedText)
                    // Filtrer et répartir le texte reconnu
                    filterAndCategorizeText(recognizedText)
                }
                .addOnFailureListener { e: Exception ->
                    progressDialog.dismiss()
                    showToast("Failed to recognize text due to ${e.message}")
                }
        } catch (e: Exception) {
            progressDialog.dismiss()
            showToast("Failed to prepare image due to ${e.message}")
        }
    }

    private fun filterAndCategorizeText(text: String) {
        val lines = text.split("\n")
        var currentCategory: MutableList<String>? = null

        for (line in lines) {
            // Identifier la catégorie actuelle
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
                // Si la ligne ne contient pas de catégorie, ajouter l'élément à la liste actuelle
                currentCategory != null -> {
                    currentCategory.add(line)
                }
            }
        }

        println("Boissons: $boissonsList")
        println("Entrées: $entreesList")
        println("Plats: $platsList")
        println("Desserts: $dessertsList")
    }






    private fun showInputImageDialog() {
        val popupMenu = PopupMenu(requireContext(), inputImageBtn)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Caméra")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galerie")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    if (checkCameraPermissions()) {
                        pickImageCamera()
                    } else {
                        requestCameraPermission()
                    }
                }
                2 -> {
                    if (checkStoragePermission()) {
                        pickImageGallery()
                    } else {
                        requestStoragePermission()
                    }
                }
            }
            true
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLaucher.launch(intent)
    }

    private val galleryActivityResultLaucher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                // Remove the line below to hide the image view
                // imageTv.setImageURI(imageUri)
            } else {
                showToast("Cancelled...!")
            }
        }

    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Sample Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description")
        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){

            }else{
                showToast("Cancelled...")
            }
        }


    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkStoragePermission() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermissions() : Boolean{
        val cameraResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return cameraResult && storageResult
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(requireActivity(), storagePermission, STORAGE_REQUEST)
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(requireActivity(), cameraPermission, CAMERA_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){
                        pickImageCamera()
                    }else{
                        showToast("Camera & Storage permission are required...")
                    }
                }
            }
            STORAGE_REQUEST -> {
                if(grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(storageAccepted){
                        pickImageGallery()
                    }else{
                        showToast("Storage permission is required...")
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
