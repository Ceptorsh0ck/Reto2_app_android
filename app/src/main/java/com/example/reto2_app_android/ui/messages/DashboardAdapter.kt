package com.example.reto2_app_android.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.MyApp.Companion.userPreferences
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.MessageRecive
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.databinding.ItemMessageMeBinding
import com.example.reto2_app_android.databinding.ItemMessageOtherBinding
import java.text.SimpleDateFormat
import java.util.Date

class DashboardAdapter() :
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
        val currentUser = userPreferences.getLoggedUser()?.email


        return if (message.authorName == currentUser) {
            MY_MESSAGE_TYPE
        } else {
            OTHER_MESSAGE_TYPE
        }
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class DashboardViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageAdapter) {
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy") // Formato para la fecha (por ejemplo: 26/01/2024)
            val timeFormat = SimpleDateFormat("HH:mm") // Formato para la hora (por ejemplo: 16:30:00)
            val formattedDate = dateFormat.format(currentDate) // Fecha formateada
            val formattedTime = timeFormat.format(currentDate) // Hora formateada

            if (binding is ItemMessageMeBinding) {
                binding.textViewChatMeMessage.text = message.text
                binding.textViewChatMeDate.text = if (message.fecha !=null) message.fecha else formattedDate
                binding.textViewChatMeTimestamp.text = if (message.hora !=null) message.hora else formattedDate

            } else if (binding is ItemMessageOtherBinding) {

                binding.textViewChatOtherMessage.text = message.text
                binding.textViewChatOtherDate.text = if (message.fecha !=null) message.fecha else formattedDate
                binding.textViewChatOtherTimestamp.text = if (message.hora !=null) message.hora else formattedDate
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

