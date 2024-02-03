package com.example.reto2_app_android.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.MyApp.Companion.context
import com.example.reto2_app_android.MyApp.Companion.userPreferences
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.MessageRecive
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.databinding.ItemMessageMeBinding
import com.example.reto2_app_android.databinding.ItemMessageOtherBinding
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

    inner class DashboardViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageAdapter) {
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val timeFormat = SimpleDateFormat("HH:mm")
            val formattedDate = dateFormat.format(currentDate)
            val formattedTime = timeFormat.format(currentDate)
            Log.i("llegada", message.dataType.toString())
            if (binding is ItemMessageMeBinding) {
                binding.textViewChatMeMessage.text = message.text
                binding.textViewChatMeDate.text = if (message.fecha !=null) message.fecha else formattedDate
                binding.textViewChatMeTimestamp.text = if (message.hora !=null) message.hora else formattedTime

            } else if (binding is ItemMessageOtherBinding) {
                if(message.dataType == RoomDataType.TEXT){
                    binding.textViewChatOtherMessage.text = message.text
                }else if (message.dataType == RoomDataType.GPS){
                    Log.i("sdads", "GPDS")
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

                }


                binding.textViewChatOtherDate.text = if (message.fecha !=null) message.fecha else formattedDate
                binding.textViewChatOtherTimestamp.text = if (message.hora !=null) message.hora else formattedTime
                val index = message.authorName.indexOf('@')
                val nombreUsuario = if (index != -1) message.authorName.substring(0, index) else message.authorName
                binding.textViewChatOtherUser.text = nombreUsuario
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

