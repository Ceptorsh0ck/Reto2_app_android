package com.example.reto2_app_android.ui.publicChats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.ChatShow
import com.example.reto2_app_android.databinding.PopupListAddItemBinding
import com.example.reto2_app_android.databinding.PopupListPublicChatsBinding


class PublicChatAdapter() :
    ListAdapter<ChatShow, PublicChatAdapter.PublicChatViewHolder>(PublicChatDiffCallback()) {
    private lateinit var recyclerView: RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicChatViewHolder {
        val binding = PopupListPublicChatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublicChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublicChatViewHolder, position: Int) {
        val people = getItem(position)
        holder.bind(people)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    inner class PublicChatViewHolder(private val binding: PopupListPublicChatsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatShow) {
            binding.chatCheckBox.text = chat.name
            binding.idChatTextView.text = chat.chatId.toString()
        }
    }


    class PublicChatDiffCallback : DiffUtil.ItemCallback<ChatShow>() {

        override fun areItemsTheSame(oldItem: ChatShow, newItem: ChatShow): Boolean {
            // TODO
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ChatShow,
            newItem: ChatShow
        ): Boolean {
            // TODO
            return oldItem == newItem
        }

    }
}