package com.example.reto2_app_android.ui.publicChats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.ChatResponse_Message
import com.example.reto2_app_android.data.model.ChatResponse_UserOfMessage
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.databinding.ItemChatsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeAdapter(
    private val onClickListener: (ChatResponse_Chat) -> Unit,
): ListAdapter <ChatResponse_Chat, HomeAdapter.HomeViewHolder>(ChatDiffCallback())   {

    private lateinit var recyclerView: RecyclerView

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
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    //Actualizar la lista para cuando llega nuevo mensaje
    fun scrollToItemById(id: Int, content: String, userId: Int, email: String, type: RoomDataType) {

        val position = currentList.indexOfFirst { it.id == id }
        if (position != -1) {
            val newList = currentList.toMutableList()
            var itemToMove = newList.removeAt(position)

            val highestMessageId = newList.maxByOrNull { it.listMessages?.maxOfOrNull { message -> message.id ?: 0 } ?: 0 }
            val newMessageId = (highestMessageId?.listMessages?.maxOfOrNull { it.id ?: 0 } ?: 0) + 1

            val newUser = ChatResponse_UserOfMessage(
                id = userId,
                email = email,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )

            val newMessage = ChatResponse_Message(
                id = newMessageId,
                dataType = type,
                content = content,
                createdAt = Date(),
                updatedAt = Date(),
                userId = newUser 
            )

            itemToMove = itemToMove.copy(listMessages = (itemToMove.listMessages.orEmpty() + newMessage))
            newList.add(0, itemToMove)
            submitList(newList)

            recyclerView.scrollToPosition(0)
        }
    }


    inner class HomeViewHolder(private val binding: ItemChatsBinding) :

        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatResponse_Chat) {
            binding.TextViewChatName.text = chat.name
            binding.TextViewChatId.text = chat.id.toString()
            binding.TextViewUserTotal.text = "Users: " + chat.totalUsers.toString()
            //TODO Hay que descomentar esta linea esta quitada para pruebas
            //binding.TextViewChatId.visibility = View.INVISIBLE
            if (chat.aIsPublic) {
                binding.TextViewNumberOfNewMessages.setImageResource(R.drawable.lock_open)
            } else {
                binding.TextViewNumberOfNewMessages.setImageResource(R.drawable.lock_close)
            }

            if(!chat.listMessages?.isEmpty()!!){
                var user = "null"
                if(chat.listMessages?.last()?.userId?.name != null){
                    user = chat.listMessages?.last()?.userId?.name.toString()
                }else{
                    if(chat.listMessages?.last()?.userId?.email != null){
                        val email = chat.listMessages?.last()?.userId?.email.toString()
                        user = email.substringBefore('@').capitalize()
                    }
                }
                val type= chat.listMessages?.last()?.dataType;
                if(type ==RoomDataType.TEXT){
                    binding.TextViewLastMessage.text = user + ": " + chat.listMessages?.last()?.content.toString()
                }else if(type ==RoomDataType.IMAGE){
                    binding.TextViewLastMessage.text = user + ": " + "Imagen"
                }else if(type ==RoomDataType.GPS){
                    binding.TextViewLastMessage.text = user + ": " + "GPS"
                }else if(type ==RoomDataType.FILE){
                    binding.TextViewLastMessage.text = user + ": " + "Archivo"
                }

                val lastMessageDate = chat.listMessages?.last()?.createdAt

                if (lastMessageDate != null) {
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formattedTime = dateFormat.format(lastMessageDate)

                    binding.TextViewLastMessageHour.text = formattedTime
                }
            }else{
                binding.TextViewLastMessage.text = "no hay mensajes"
                if(chat.createdAt != null) {
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formattedTime = dateFormat.format(chat.createdAt)
                    binding.TextViewLastMessageHour.text = formattedTime
                }
                else{
                    binding.TextViewLastMessageHour.text = "null"
                }

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