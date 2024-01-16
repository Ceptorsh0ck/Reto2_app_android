package com.example.reto2_app_android.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.databinding.ItemChatsBinding

class HomeAdapter(): ListAdapter <Chat, HomeAdapter.HomeViewHolder>(ChatDiffCallback())   {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {


    }

    inner class HomeViewHolder(private val binding: ItemChatsBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.TextViewChatName.text = chat.name

            var lastMessageUserId = chat.messages.last().userId
            var lastMessageUserName = "";
            for (user in chat.users){
                if(user.id == lastMessageUserId){
                    lastMessageUserName = user.name;
                }
            }

            binding.TextViewLastMessage.text = lastMessageUserName + chat.messages.last().text.toString()
            binding.TextViewLastMessageHour.text = chat.messages.last().creation.toString()

        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {

        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }
}