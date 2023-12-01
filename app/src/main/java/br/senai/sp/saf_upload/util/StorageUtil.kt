package br.senai.sp.saf_upload.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.util.UUID

class StorageUtil {
    companion object {

        fun uploadToStorage(
            uri: Uri,
            context: Context,
            type: String,
            onSaveComplete: (String) -> Unit
        ) {
            val storage = Firebase.storage

            // Create a storage reference from our app
            var storageRef = storage.reference

            val unique_image_name = UUID.randomUUID()
            var spaceRef: StorageReference

            if (type == "image"){
                spaceRef = storageRef.child("imagens/$unique_image_name.jpg")
            }else{
                spaceRef = storageRef.child("videos/$unique_image_name.mp4")
            }

            val byteArray: ByteArray? = context.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }

            byteArray?.let{

                var uploadTask = spaceRef.putBytes(byteArray)
                uploadTask.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Falha ao salvar foto. Tente novamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // Get the download URL
                    spaceRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        // Use the download URL as needed
                        Toast.makeText(
                            context,
                            "Foto salva com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        onSaveComplete(downloadUrl)
                        Log.i("URL-STORAGE-UTIL", downloadUrl)

                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Falha ao baixar a imagem.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}