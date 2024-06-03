package com.example.uas_ali

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_ali.databinding.ActivityAddItemBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("movie")
        mStorageRef = FirebaseStorage.getInstance().reference.child("movie") // Create a folder for images in Firebase Storage

        binding.buttonAddItem.setOnClickListener {
            val itemName = binding.editTextItemName.text.toString().trim()
            val itemDescription = binding.editTextItemDescription.text.toString().trim()

            if (itemName.isEmpty() || itemDescription.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                uploadImageToFirebaseStorage(itemName, itemDescription)
            } else {
                saveItemToFirebaseDatabase(itemName, itemDescription, "")
            }
        }

        binding.imageViewAddImage.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.imageViewAddImage.setImageURI(selectedImageUri)
        }
    }

    private fun uploadImageToFirebaseStorage(itemName: String, itemDescription: String) {
        val imageRef = mStorageRef.child("${UUID.randomUUID()}.jpg")
        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveItemToFirebaseDatabase(itemName, itemDescription, imageUrl)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveItemToFirebaseDatabase(itemName: String, itemDescription: String, imageUrl: String) {
        val itemId = mDatabaseRef.push().key
        if (itemId != null) {
            val newItem = Billiard(itemName, itemDescription, imageUrl)
            mDatabaseRef.child(itemId).setValue(newItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity and return to the previous one
                } else {
                    Toast.makeText(this, "Failed to add item: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
