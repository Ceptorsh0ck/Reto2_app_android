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
import com.example.reto2_app_android.data.MessageRecive
import com.example.reto2_app_android.databinding.ItemMessageMeBinding
import com.example.reto2_app_android.databinding.ItemMessageOtherBinding

class DashboardAdapter() :
    ListAdapter<MessageRecive, DashboardAdapter.DashboardViewHolder>(DashboardDiffCallback()) {
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
        val currentUser = userPreferences.fetchLogin().toString().lowercase()


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
        fun bind(message: MessageRecive) {
            if (binding is ItemMessageMeBinding) {
                binding.textViewChatMeMessage.text = message.text
                binding.textViewChatMeDate.text = "Dia"
                binding.textViewChatMeTimestamp.text = "Hora"

            } else if (binding is ItemMessageOtherBinding) {

                binding.textViewChatOtherMessage.text = message.text
                binding.textViewChatOtherDate.text = "Dia"
                binding.textViewChatOtherTimestamp.text = "Hora"
                binding.textViewChatOtherUser.text = message.authorName
            }
        }
    }


        class DashboardDiffCallback : DiffUtil.ItemCallback<MessageRecive>() {

            override fun areItemsTheSame(oldItem: MessageRecive, newItem: MessageRecive): Boolean {
                // TODO
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MessageRecive,
                newItem: MessageRecive
            ): Boolean {
                // TODO
                return oldItem == newItem
            }

        }

    }

