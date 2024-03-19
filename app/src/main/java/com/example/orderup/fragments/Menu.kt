package com.example.orderup.fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.orderup.R
import com.example.orderup.databinding.MenuBinding
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class Menu : Fragment() {

    private var _binding: MenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var inputImageBtn: MaterialButton
    private lateinit var recognizedTextBtn: MaterialButton
    private lateinit var recognizedTextEt: EditText
    private lateinit var progressDialog: ProgressDialog

    private lateinit var textRecognizer: TextRecognizer

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

        binding.btnPrecedent.setOnClickListener {
            findNavController().navigate(R.id.action_Menu_to_Formulas)
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
        recognizedTextBtn = binding.recognizedTextBtn
        recognizedTextEt = binding.recognizedTextEt

        inputImageBtn.setOnClickListener {
            showInputImageDialog()
        }

        recognizedTextBtn.setOnClickListener {
            if (imageUri == null) {
                showToast("Sélectionnez d'abord une image...")
            } else {
                recognizeTextFromImage()
            }
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
                    recognizedTextEt.setText(recognizedText)
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

    private fun showInputImageDialog() {
        val popupMenu = PopupMenu(requireContext(), inputImageBtn)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Appareil photo")
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
                    pickImageGallery()
                }
            }
            true
        }
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

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                recognizeTextFromImage()
            } else {
                showToast("Annulé...!")
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

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkCameraPermissions() : Boolean{
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageCamera()
            } else {
                showToast("Autorisation de l'appareil photo refusée")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
