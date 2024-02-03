package com.example.reto2_app_android.ui.dashboard

import android.app.AlertDialog
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.remote.RemoteMessagesDataSource
import com.example.reto2_app_android.data.services.SocketIoService
import com.example.reto2_app_android.databinding.FragmentDashboardBinding
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.ui.messages.AddPeopleAdapter
import com.example.reto2_app_android.ui.messages.DeletePeopleAdapter
import com.example.reto2_app_android.utils.Resource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private const val ARG_CHAT = "chat"

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private lateinit var myService: SocketIoService
    private lateinit var addPeopleAdapter: AddPeopleAdapter
    private lateinit var deletePeopleAdapter: DeletePeopleAdapter
    private val roomMessageRepository = RoomMessageDataSource();
    private val serverMessageRepository = RemoteMessagesDataSource();
    private val TAG = "SocketActivity"
    private val userId: Int? = MyApp.userPreferences.getLoggedUser()?.id?.toInt();
    private lateinit var messageAdapter: DashboardAdapter
    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(roomMessageRepository, serverMessageRepository)
    }
    private var lastMessage: String = ""
    private var chat: ChatResponse_Chat? = null
    private val binding get() = _binding!!
    private var isUserScrolling = false
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

        val recyclerView = ViewBindings.findChildViewById<RecyclerView>(root, R.id.recycler_group_chat)
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addOnScrollListener(scrollListener)
        }
        viewModel.isAdmin(chat!!.id, userId)

        isAdim()

        binding.textToolbarChatName.text = chat!!.name
        viewModel.getAllMessages(chat!!.id)
        returnServerUsersAdd()
        returnServerUsers()
        llamadaAMetodoDelServicio()
        onClickTeclado(binding)
        showRoomMessage(binding)
        onMessagesChange()
        buttonsListeners(binding)
        onMessageSendRoom(binding)
        return root
    }

    private fun isAdim() {
        viewModel.admin.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val isAdmin = it.data ?: false
                    if(isAdmin){
                        binding.buttonToolbarAddPeopleChat.visibility = VISIBLE
                        binding.buttonToolbarDeletePeopleChat.visibility = VISIBLE
                    }
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {

                }
            }
        }
    }

    private fun returnServerUsers() {
        viewModel.users.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Log.i("lista de ", it.data.toString())
                    addPeopleAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {

                }
            }
        }
        viewModel.usersDelete.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Log.i("lista de ", it.data.toString())
                    deletePeopleAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {

                }
            }
        }
    }

    private fun returnServerUsersAdd() {
        viewModel.addPeople.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data!!.forEach {

                        myService.addUsersToChats(it.userId, it.chatId, it.admin)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {

                }
            }
        }
        viewModel.deletePeople.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data!!.forEach {

                        myService.deleteUsersToChats(it.userId, it.chatId, it.admin)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {

                }
            }
        }
    }


    fun llamadaAMetodoDelServicio() {
        //val intent = Intent(requireContext(), SocketIoService::class.java)
        //requireContext().startService(intent)

        val mainActivity = activity as MainActivity
        myService = mainActivity.myService
    }



    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            isUserScrolling = newState != RecyclerView.SCROLL_STATE_IDLE
        }
    }
    private fun onClickTeclado(binding: FragmentDashboardBinding) {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val recyclerView: RecyclerView = binding.recyclerGroupChat
                if (!isUserScrolling) {
                    recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        })
    }

    private fun showRoomMessage(binding: FragmentDashboardBinding) {
        viewModel.messagesRoom.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {

                    messageAdapter.addMessages(it.data!!)
                    val recyclerView: RecyclerView = binding.recyclerGroupChat
                    recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
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

                    messageAdapter.addMessages(it.data!!)
                    val recyclerView: RecyclerView = binding.recyclerGroupChat
                    recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    //if(MainActivity.)
                    myService.onSaveMessage(lastMessage, "Group- " +  chat?.id, it.data.first().idRoom!!)
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


    private fun onMessagesChange() {
        viewModel.messages.observe(viewLifecycleOwner) {
            Log.d(TAG, "messages change")
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if(chat?.id == it.data!!.last().room.last().toString().toInt()){
                        messageAdapter.addMessages(it.data!!)
                        val recyclerView: RecyclerView = binding.recyclerGroupChat
                        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }

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
            val message = binding.editGroupChatMessage.text.toString().trim()
            if (message.isNotBlank()) {
                lastMessage = message
                chat?.id?.let { chatId ->
                    userId?.let { userId ->
                        Log.i("chat", chatId.toString())
                        viewModel.saveNewMessageRoom(lastMessage, chatId, userId)
                    }
                }
                binding.editGroupChatMessage.text.clear()
            }
        }
        binding.buttonToolbarAddPeopleChat.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.popup_layout, null)

            // Encuentra el RecyclerView en el diseño del popup
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)

            // Configura el LinearLayoutManager (o cualquier otro LayoutManager que desees)
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager

            // Crea y configura el adaptador para el RecyclerView
            addPeopleAdapter = AddPeopleAdapter()
            recyclerView.adapter = addPeopleAdapter
            viewModel.getUsersToInsertIntoChats(chat!!.id)

            builder.setView(dialogView)
            builder.setPositiveButton("Aceptar") { _, _ ->

                val selectedPeopleList = mutableListOf<AddPeopleResponse>()

                // Iterar sobre los elementos del RecyclerView
                for (i in 0 until recyclerView.childCount) {
                    val view = recyclerView.getChildAt(i)
                    val emailCheckBox = view.findViewById<CheckBox>(R.id.emailCheckBox)
                    val adminCheckBox = view.findViewById<CheckBox>(R.id.adminCheckBox)
                    val id = view.findViewById<TextView>(R.id.idTextView).text.toString().toInt()
                    val email = emailCheckBox.text.toString()

                    // Comprobar si el CheckBox de correo electrónico está marcado
                    if (emailCheckBox.isChecked) {
                        // Agregar un objeto AddPeople con isAdmin según el estado del CheckBox de administrador
                        selectedPeopleList.add(AddPeopleResponse(id, chat!!.id, adminCheckBox.isChecked))
                    }
                }
                Log.i("lista de", selectedPeopleList.toString())
                viewModel.updateChatUsers( chat!!.id, selectedPeopleList)
            }
            builder.setNegativeButton("Cancelar") { _, _ ->

            }
            val dialog = builder.create()
            dialog.show()
        }
        binding.buttonToolbarDeletePeopleChat.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.popup_layout, null)

            // Encuentra el RecyclerView en el diseño del popup
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerView)

            // Configura el LinearLayoutManager (o cualquier otro LayoutManager que desees)
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager

            // Crea y configura el adaptador para el RecyclerView
            deletePeopleAdapter = DeletePeopleAdapter()
            recyclerView.adapter = deletePeopleAdapter
            viewModel.getUsersToDeleteIntoChats(chat!!.id)

            builder.setView(dialogView)
            builder.setPositiveButton("Aceptar") { _, _ ->

                val selectedPeopleList = mutableListOf<AddPeopleResponse>()

                // Iterar sobre los elementos del RecyclerView
                for (i in 0 until recyclerView.childCount) {
                    val view = recyclerView.getChildAt(i)
                    val emailCheckBox = view.findViewById<CheckBox>(R.id.emailCheckBox)
                    val id = view.findViewById<TextView>(R.id.idTextView).text.toString().toInt()
                    val email = emailCheckBox.text.toString()

                    // Comprobar si el CheckBox de correo electrónico está marcado
                    if (emailCheckBox.isChecked) {
                        // Agregar un objeto AddPeople con isAdmin según el estado del CheckBox de administrador
                        selectedPeopleList.add(AddPeopleResponse(id, chat!!.id, false))
                    }
                }
                Log.i("lista de", selectedPeopleList.toString())
                viewModel.updateChatUsersDelete( chat!!.id, selectedPeopleList)
            }
            builder.setNegativeButton("Cancelar") { _, _ ->

            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    // para el EventBus
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationEmployee(message: MessageAdapter) {
        if(chat!!.id == message.room.toInt()){
            val list: MutableList<MessageAdapter> = mutableListOf()
            list.add(message)
            messageAdapter.addMessages(list)
            val recyclerView: RecyclerView = binding.recyclerGroupChat
            recyclerView.scrollToPosition(messageAdapter.itemCount - 1)

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationEmployee(message: List<MessageAdapter>) {
        Log.d("chats", "onNot")
        messageAdapter.addMessages(message)
        val recyclerView: RecyclerView = binding.recyclerGroupChat
        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }
}