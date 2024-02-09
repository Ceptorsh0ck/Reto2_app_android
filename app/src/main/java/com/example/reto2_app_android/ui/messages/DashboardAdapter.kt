package com.example.reto2_app_android.ui.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.reto2_app_android.MyApp.Companion.userPreferences
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.databinding.ItemMessageMeBinding
import com.example.reto2_app_android.databinding.ItemMessageOtherBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class DashboardAdapter(

) :
    ListAdapter<MessageAdapter, DashboardAdapter.DashboardViewHolder>(DashboardDiffCallback()) {

    private val messageList = mutableListOf<MessageAdapter>()

    fun addMessages(newMessages: List<MessageAdapter>) {
        messageList.addAll(newMessages)
        submitList(messageList.toList())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = if (viewType == MY_MESSAGE_TYPE) {
            ItemMessageMeBinding.inflate(inflater, parent, false)
        } else {
            ItemMessageOtherBinding.inflate(inflater, parent, false)
        }
        return DashboardViewHolder(binding)
    }
    companion object {
        private const val MY_MESSAGE_TYPE = 1
        private const val OTHER_MESSAGE_TYPE = 2
    }
    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        if(userPreferences.getLoggedUser()!=null) {

            val currentUser = userPreferences.getLoggedUser()?.email!!

            return if (message.authorName == currentUser) {
                MY_MESSAGE_TYPE
            } else {
                OTHER_MESSAGE_TYPE
            }
        }
        return 0

    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    fun base64ToFile(base64Data: String): File {
        val file = File.createTempFile("temp_file", null) // Creamos un archivo temporal
        val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT) // Decodificamos los datos Base64
        FileOutputStream(file).use {
            it.write(decodedBytes) // Escribimos los datos decodificados en el archivo temporal
        }
        return file
    }

    fun getFileName(filePath: String): String {
        val file = File(filePath)
        return file.name
    }

    fun convertImageDataToBitmap(base64String: String): Bitmap? {
        // Elimina el prefijo "data:image/jpeg;base64," de la cadena Base64 si está presente
        val base64Image = if (base64String.contains("data:image/jpeg;base64,")) {
            base64String.substringAfter("data:image/jpeg;base64,")
        } else {
            base64String
        }

        // Decodifica la cadena Base64 en un arreglo de bytes
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)

        // Decodifica el arreglo de bytes en un objeto Bitmap
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun getFileNameFromUri(uri: String): String {
        val uriObj = Uri.parse(uri)
        val file = File(uriObj.path)
        return file.name
    }

    // Función para abrir un archivo por su URI
    private fun openFile(fileUri: String, binding: ItemMessageMeBinding) {

        val resolver = binding.root.context.applicationContext.contentResolver
        val readOnlyMode = "r"
        val uri = Uri.parse(fileUri)
        resolver.openFileDescriptor(uri, readOnlyMode).use { pfd ->
            // Perform operations on "pfd".
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUri))
        binding.root.context.startActivity(intent)
    }

    private fun loadThumbnailIntoImageView(context: Context, fileUri: String, imageView: ImageView) {
        val contentResolver = context.contentResolver
        val uri = Uri.parse(fileUri)
        Log.d("aa", fileUri)
        try {
            val thumbnail: Bitmap = contentResolver.loadThumbnail(uri, Size(640, 480), null)
            imageView.setImageBitmap(thumbnail)
        } catch (e: IOException) {
            e.printStackTrace()
            // Manejar la excepción si la miniatura no se puede cargar
        }
    }

    inner class DashboardViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageAdapter) {
            try {


                val dateFormat = SimpleDateFormat("dd/MM/yyyy")

                val timeFormat = SimpleDateFormat("HH:mm")
                val formattedDate = dateFormat.format(message.createdAt)
                val formattedTime = timeFormat.format(message.createdAt)

                if (binding is ItemMessageMeBinding) {
                    binding.textViewChatMeDate.text = if (message.createdAt != null) formattedDate else formattedDate.toString()
                    binding.textViewChatMeTimestamp.text = if (message.createdAt != null) formattedTime else formattedTime.toString()

                    if(message.dataType == RoomDataType.TEXT){
                        binding.textViewChatMeMessage.text = message.text
                        binding.textViewChatMeMessage.visibility = VISIBLE
                        binding.imageViewChatMyMessage.visibility = GONE
                    }else if(message.dataType == RoomDataType.GPS) {
                        binding.textViewChatMeMessage.text = message.text
                        binding.textViewChatMeMessage.visibility = VISIBLE
                        binding.imageViewChatMyMessage.visibility = GONE
                        binding.textViewChatMeMessage.setOnClickListener {
                            Log.i("click", "click")
                            // Dividir el texto del mensaje por la coma para obtener la latitud y longitud
                            val coordinates = message.text.split(",")

                            if (coordinates.size == 2) { // Verificar si se obtuvieron dos partes (latitud y longitud)
                                val latitude = coordinates[0].toDouble()
                                val longitude = coordinates[1].toDouble()

                                // Crear un Intent para abrir Google Maps con las coordenadas
                                val uri = "geo:$latitude,$longitude"
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                mapIntent.setPackage("com.google.android.apps.maps") // Establecer el paquete de Google Maps
                                binding.root.context.startActivity(mapIntent) // Usar binding.root.context para obtener el contexto
                            } else {
                                // Manejar el caso en que el formato del mensaje de GPS no sea el esperado
                                // Puedes mostrar un mensaje de error o realizar otra acción adecuada
                            }
                        }
                    } else if(message.dataType == RoomDataType.IMAGE){
                        binding.textViewChatMeMessage.visibility = GONE
                        binding.imageViewChatMyMessage.visibility = VISIBLE
                        val imageData = message.text
                        val imageBitmap = convertImageDataToBitmap(imageData)
                        binding.imageViewChatMyMessage.setImageBitmap(imageBitmap)
                    }else if(message.dataType == RoomDataType.FILE){
                        binding.textViewChatMeMessage.visibility = VISIBLE
                        binding.imageViewChatMyMessage.visibility = VISIBLE

                        val fileUri = message.text // Suponiendo que la URL o la URI del archivo está en message.text

                        val fileName = getFileNameFromUri(fileUri)

                        binding.textViewChatMeMessage.text = fileName

                        // Cargar miniatura en la vista de imagen
                        loadThumbnailIntoImageView(binding.root.context.applicationContext, fileUri, binding.imageViewChatMyMessage)

                        // Manejar clics en la vista previa del archivo
                        binding.imageViewChatMyMessage.setOnClickListener {
                            openFile(fileUri, binding)

                        }
                    }


                } else if (binding is ItemMessageOtherBinding) {
                    if(message.dataType == RoomDataType.TEXT){
                        binding.textViewChatOtherMessage.text = message.text
                        binding.textViewChatOtherMessage.visibility = View.VISIBLE
                        binding.imageViewChatOtherMessage.visibility = View.GONE
                    }else if (message.dataType == RoomDataType.GPS){
                        binding.textViewChatOtherMessage.visibility = View.VISIBLE
                        binding.imageViewChatOtherMessage.visibility = View.GONE
                        binding.textViewChatOtherMessage.text = message.text
                        binding.textViewChatOtherMessage.setOnClickListener {
                            Log.i("click", "click")
                            // Dividir el texto del mensaje por la coma para obtener la latitud y longitud
                            val coordinates = message.text.split(",")

                            if (coordinates.size == 2) { // Verificar si se obtuvieron dos partes (latitud y longitud)
                                val latitude = coordinates[0].toDouble()
                                val longitude = coordinates[1].toDouble()

                                // Crear un Intent para abrir Google Maps con las coordenadas
                                val uri = "geo:$latitude,$longitude"
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                mapIntent.setPackage("com.google.android.apps.maps") // Establecer el paquete de Google Maps
                                binding.root.context.startActivity(mapIntent) // Usar binding.root.context para obtener el contexto
                            } else {
                                // Manejar el caso en que el formato del mensaje de GPS no sea el esperado
                                // Puedes mostrar un mensaje de error o realizar otra acción adecuada
                            }
                        }

                    }else if(message.dataType == RoomDataType.IMAGE){
                        binding.textViewChatOtherMessage.visibility = View.GONE
                        binding.imageViewChatOtherMessage.visibility = View.VISIBLE
                        val imageData = message.text
                        val imageBitmap = convertImageDataToBitmap(imageData)
                        binding.imageViewChatOtherMessage.setImageBitmap(imageBitmap)
                    }


                    binding.textViewChatOtherDate.text = if (message.fecha !=null) message.fecha else formattedDate
                    binding.textViewChatOtherTimestamp.text = if (message.hora !=null) message.hora else formattedTime
                    val index = message.authorName.indexOf('@')
                    val nombreUsuario = if (index != -1) message.authorName.substring(0, index) else message.authorName
                    binding.textViewChatOtherUser.text = nombreUsuario
                }
            } catch (e: Exception) {
                Toast.makeText(binding.root.context, "Error al cargar los mensajes", Toast.LENGTH_SHORT).show()
            }
        }
    }


    class DashboardDiffCallback : DiffUtil.ItemCallback<MessageAdapter>() {

        override fun areItemsTheSame(oldItem: MessageAdapter, newItem: MessageAdapter): Boolean {
            // TODO
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MessageAdapter,
            newItem: MessageAdapter
        ): Boolean {
            // TODO
            return oldItem == newItem
        }

    }

}