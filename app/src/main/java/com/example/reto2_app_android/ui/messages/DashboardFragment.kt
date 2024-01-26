package com.example.reto2_app_android.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.MessageRecive
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.databinding.FragmentDashboardBinding
import com.example.reto2_app_android.utils.Resource
import java.text.SimpleDateFormat
import java.util.Date

private const val ARG_CHAT = "chat"

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val roomMessageRepository = RoomMessageDataSource();
    private val TAG = "SocketActivity"
    private val userId: Int? = MyApp.userPreferences.getLoggedUser()?.id?.toInt();
    private lateinit var messageAdapter: DashboardAdapter
    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(roomMessageRepository)
    }
    private var lastMessage: String = ""
    private var chat: ChatResponse_Chat? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chat = it.getParcelable(ARG_CHAT, ChatResponse_Chat::class.java)

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        messageAdapter = DashboardAdapter()
        binding.recyclerGroupChat.adapter = messageAdapter

        binding.buttonGroupChatSend.isEnabled = false
        val recyclerView = ViewBindings.findChildViewById<RecyclerView>(root, R.id.recycler_group_chat)
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(context)

        }
        viewModel.getAllMessages(chat!!.id)
        viewModel.startSocket()


        showRoomMessage(binding)
        onConnectedChange(binding)
        onMessagesChange()
        buttonsListeners(binding)
        onMessageSendRoom(binding)
        return root
    }

    private fun showRoomMessage(binding: FragmentDashboardBinding) {
        viewModel.messagesRoom.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    var messages: List<MessageAdapter> = emptyList()
                    it.data?.forEach { dataItem ->
                        var message = dataItem.dataType?.let { dataType ->
                            val fechaHora = dataItem.createdAt
                            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
                            val timeFormat = SimpleDateFormat("HH:mm")
                            dataItem.content?.let { content ->
                                MessageAdapter(
                                    dataItem.chatId.toString(),
                                    content,
                                    dataItem.userId.toString(),
                                    dataItem.userId,
                                    dataType,
                                    dateFormat.format(fechaHora),
                                    timeFormat.format(fechaHora)
                                )
                            }
                        }
                        Log.i("Fehca", message.toString())
                        message?.let {
                            messages = messages.plus(it)
                        }
                    }
                    messageAdapter.addMessages(messages) // Cambio aquí
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    private fun onMessageSendRoom(binding: FragmentDashboardBinding) {
        viewModel.message.observe(viewLifecycleOwner) {
            when (it.status){
                Resource.Status.SUCCESS-> {
                    Log.i("gaardado en room", it.data.toString())
                    viewModel.onSaveMessage(lastMessage, "Group- " +  chat?.id)
                }
                Resource.Status.ERROR -> {
                    // TODO sin gestionarlo en el VM. Y si envia en una sala que ya no esta? a tratar
                    Log.d(TAG, "error al conectar...")
                }

                Resource.Status.LOADING -> {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onConnectedChange(binding: FragmentDashboardBinding) {
        viewModel.connected.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    binding.buttonGroupChatSend.isEnabled = it.data!!
                }

                Resource.Status.ERROR -> {
                    // TODO sin gestionarlo en el VM. Y si envia en una sala que ya no esta? a tratar
                    Log.d(TAG, "error al conectar...")
                    binding.buttonGroupChatSend.isEnabled = it.data!!
                }

                Resource.Status.LOADING -> {
                    binding.buttonGroupChatSend.isEnabled = it.data!!
                }
            }
        }
    }
    private fun onMessagesChange() {
        viewModel.messages.observe(viewLifecycleOwner) {
            Log.d(TAG, "messages change")
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Log.d(TAG, "messages observe success")
                    messageAdapter.addMessages(it.data!!)

                }

                Resource.Status.ERROR -> {
                    Log.d(TAG, "messages observe error")
                    //Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                Resource.Status.LOADING -> {
                    // de momento
                    Log.d(TAG, "messages observe loading")
                    //val toast = Toast.makeText(this, "Cargando..", Toast.LENGTH_LONG)
                    //toast.setGravity(Gravity.TOP, 0, 0)
                    //toast.show()
                }
            }
        }
    }

    private fun buttonsListeners(binding: FragmentDashboardBinding) {
        binding.buttonGroupChatSend.setOnClickListener() {
            Log.i("Aimar", "Buton send")
            lastMessage = binding.editGroupChatMessage.text.toString();
            chat?.id?.let { it1 ->
                if (userId != null) {
                    viewModel.saveNewMessageRoom(lastMessage, it1, userId)
                }
            }
            binding.editGroupChatMessage.text.clear()
        }
    }
}