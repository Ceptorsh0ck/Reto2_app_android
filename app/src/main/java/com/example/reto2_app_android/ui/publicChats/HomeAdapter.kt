package com.example.reto2_app_android.ui.publicChats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.databinding.ItemChatsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HomeAdapter(
    private val onClickListener: (ChatResponse_Chat) -> Unit,
): ListAdapter <ChatResponse_Chat, HomeAdapter.HomeViewHolder>(ChatDiffCallback())   {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val  chat = getItem(position)
        holder.bind(chat)
        holder.itemView.setOnClickListener {
            onClickListener(chat)
        }
    }

    inner class HomeViewHolder(private val binding: ItemChatsBinding) :

        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatResponse_Chat) {
            binding.TextViewChatName.text = chat.name
            if (chat.public) {
                binding.TextViewNumberOfNewMessages.setImageResource(R.drawable.lock_open)
            } else {
                binding.TextViewNumberOfNewMessages.setImageResource(R.drawable.lock_close)
            }

            if(!chat.listMessages?.isEmpty()!!){
                binding.TextViewLastMessage.text = chat.listMessages?.last()?.userId?.name.toString() + ": " + chat.listMessages?.last()?.content.toString()
                val lastMessageDate = chat.listMessages?.last()?.createdAt

                if (lastMessageDate != null) {
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formattedTime = dateFormat.format(lastMessageDate)

                    binding.TextViewLastMessageHour.text = formattedTime
                }
            }else{
                binding.TextViewLastMessage.text = "no hay mensajes"
                binding.TextViewLastMessageHour.text = "null"
            }

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