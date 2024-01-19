package com.example.reto2_app_android.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.data.Chat
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.databinding.ItemChatsBinding

class HomeAdapter(): ListAdapter <ChatResponse_Chat, HomeAdapter.HomeViewHolder>(ChatDiffCallback())   {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {


    }

    inner class HomeViewHolder(private val binding: ItemChatsBinding) :

        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatResponse_Chat) {
            binding.TextViewChatName.text = chat.name
            Log.i("Adapter", chat.name)
            var lastMessageUserId = chat.listMessages.last().userId.id
            var lastMessageUserName = "";
            for (user in chat.listUsers){
                if(user.id.userId == lastMessageUserId){
                    lastMessageUserName = user.user.name.toString();
                }
            }
            /*
            binding.TextViewLastMessage.text = lastMessageUserName + chat.listMessages.last().content.toString()
            binding.TextViewLastMessageHour.text = chat.listMessages.last().createdAt.toString()*/
            binding.TextViewLastMessage.text = "Mesnaje"
            binding.TextViewLastMessageHour.text = "Hoa"


        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatResponse_Chat>() {

        override fun areItemsTheSame(oldItem: ChatResponse_Chat, newItem: ChatResponse_Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatResponse_Chat, newItem: ChatResponse_Chat): Boolean {
            return oldItem == newItem
        }

    }
}