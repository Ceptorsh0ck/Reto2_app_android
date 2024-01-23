package com.example.reto2_app_android.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.data.MessageRecive
import com.example.reto2_app_android.databinding.ItemMessageMeBinding
import com.example.reto2_app_android.databinding.ItemMessageOtherBinding

class DashboardAdapter()  : ListAdapter<MessageRecive, DashboardAdapter.DashboardViewHolder>(
    DashboardDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding = ItemMessageMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class DashboardViewHolder(private val binding: ItemMessageMeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageRecive) {
            if (message.authorName == "me") {
                binding.textViewChatMeMessage.text = message.text
                binding.textViewChatMeDate.text = "Dia"
                binding.textViewChatMeTimestamp.text = "Hora"
            } else {
                val otherBinding = ItemMessageOtherBinding.bind(binding.root)
                otherBinding.textViewChatOtherMessage.text = message.text
                otherBinding.textViewChatOtherDate.text = "Dia"
                otherBinding.textViewChatOtherTimestamp.text = "Hora"
                otherBinding.textViewChatOtherUser.text = message.authorName
            }
        }
    }

    class DashboardDiffCallback : DiffUtil.ItemCallback<MessageRecive>() {

        override fun areItemsTheSame(oldItem: MessageRecive, newItem: MessageRecive): Boolean {
            // TODO
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageRecive, newItem: MessageRecive): Boolean {
            // TODO
            return oldItem == newItem
        }

    }


}