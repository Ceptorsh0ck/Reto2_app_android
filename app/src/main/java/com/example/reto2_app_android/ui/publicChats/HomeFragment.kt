package com.example.reto2_app_android.ui.publicChats

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.RoleEnum
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.data.repository.local.RoomChatDataSource
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.local.database.AppDatabase
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.repository.remote.RemoteChatsDataSource
import com.example.reto2_app_android.data.repository.remote.RemoteMessagesDataSource
import com.example.reto2_app_android.data.services.SocketIoService
import com.example.reto2_app_android.databinding.FragmentHomeBinding
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.ui.dashboard.DashboardFragment
import com.example.reto2_app_android.ui.dashboard.DashboardViewModel
import com.example.reto2_app_android.ui.dashboard.DashboardViewModelFactory
import com.example.reto2_app_android.utils.Resource
import com.example.reto2_app_android.utils.ValidateUserRoles
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
class HomeFragment : Fragment(), LocationListener {

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var db: AppDatabase
    private var _binding: FragmentHomeBinding? = null
    private var ubicacionObtenida = false
    private val chatRepository = RemoteChatsDataSource();
    private val roomChatRepository = RoomChatDataSource();
    private lateinit var homeAdapter: HomeAdapter
    private val roomMessageRepository = RoomMessageDataSource();
    private val serverMessageRepository = RemoteMessagesDataSource();
    private var userId = MyApp.userPreferences.getLoggedUser()!!.id
    private lateinit var publicChatAdapter: PublicChatAdapter
    private val messagesViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(roomMessageRepository, serverMessageRepository)
    }
    private lateinit var myService: SocketIoService

    private val chatViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(chatRepository, roomChatRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var localizacion: Location

    private fun onChatListClickItem(chat: ChatResponse_Chat) {

        val newFragment = DashboardFragment()
        val args = Bundle()
        args.putParcelable("chat", chat)
        newFragment.arguments = args
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeAdapter = HomeAdapter(::onChatListClickItem)
        binding.chatList.adapter = homeAdapter
        binding.idUserName.text = "Hola, ${MyApp.userPreferences.getLoggedUser()?.name}!"
        onMessagesChange()
        chatViewModel.items.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    homeAdapter.submitList(it.data)
                }

                Resource.Status.ERROR -> {

                }

                Resource.Status.LOADING -> {

                }
            }
        }

        binding.addNewChat.setOnClickListener {
            openNewChat()
        }
        binding.buttonShowAllPublicChat.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            chatViewModel.getAllPublicChat(userId)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.popup_add_people, null)

            // Encuentra el RecyclerView en el diseño del popup
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewPublicChats)

            // Configura el LinearLayoutManager (o cualquier otro LayoutManager que desees)
            val layoutManager = LinearLayoutManager(requireContext())
            recyclerView.layoutManager = layoutManager

            // Crea y configura el adaptador para el RecyclerView
            publicChatAdapter = PublicChatAdapter()
            recyclerView.adapter = publicChatAdapter

            builder.setView(dialogView)
            builder.setPositiveButton("Aceptar") { _, _ ->



                // Iterar sobre los elementos del RecyclerView
                for (i in 0 until recyclerView.childCount) {
                    val selectedPeopleList = mutableListOf<AddPeopleResponse>()
                    val view = recyclerView.getChildAt(i)
                    val emailCheckBox = view.findViewById<CheckBox>(R.id.chatCheckBox)
                    val chatIdTextView = view.findViewById<TextView>(R.id.idChatTextView)
                    val chatId = chatIdTextView.text.toString().toInt()
                    if (emailCheckBox.isChecked) {
                        val mainActivity = activity as MainActivity
                        myService = mainActivity.myService
                        myService.addUsersToChats(userId!!, chatId, false)
                    }
                }

            }
            builder.setNegativeButton("Cancelar") { _, _ ->

            }
            val dialog = builder.create()
            dialog.show()

        }


        binding.chatPrivacityFilter.setOnClickListener {
            val searchTerm = binding.filterText.text.toString()
            val filterPrivacityChat = binding.chatPrivacityFilter.isChecked
            chatViewModel.onGetChatsFromUser(searchTerm, filterPrivacityChat)
        }
        binding.filterText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val searchTerm = charSequence.toString()
                chatViewModel.onGetChatsFromUser(searchTerm, binding.chatPrivacityFilter.isChecked)

            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        chatViewModel.filteredChats.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if (!it.data.isNullOrEmpty()) {
                        homeAdapter.submitList(it.data)
                        homeAdapter.notifyDataSetChanged()
                    } else {
                    }
                }

                Resource.Status.ERROR -> {
                    Log.e("ListSongsActivity", "Error al cargar datos: ${it.message}")
                }

                Resource.Status.LOADING -> {
                    Log.d("ListSongsActivity", "Cargando datos...")
                }
            }

        }

        chatViewModel.created.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val mainActivity = activity as MainActivity
                    myService = mainActivity.myService
                    myService.createChat(userId!!, it.data!!.name!!, it.data!!.aIsPublic, it.data!!.id!!)
                }

                Resource.Status.ERROR -> {
                    Log.e("ListSongsActivity", "Error al cargar datos: ${it.message}")
                }

                Resource.Status.LOADING -> {
                    Log.d("ListSongsActivity", "Cargando datos...")
                }
            }

        }



        showAllPublicChat()
        return root
    }

    private fun showAllPublicChat() {

        chatViewModel.publicChats.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    publicChatAdapter.submitList(it.data!!)
                }

                Resource.Status.ERROR -> {
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


    private fun openNewChat() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val validateUserRoles = ValidateUserRoles()
        val dialogView = inflater.inflate(R.layout.popup_add_chat, null)
        builder.setView(dialogView)
        val roles =  MyApp.userPreferences.getLoggedUser()?.listRoles
        val isPublicCheckBox = dialogView.findViewById<CheckBox>(R.id.checkBoxPublic)
        val listRolesPermitidos: List<RoleEnum> = listOf(RoleEnum.ADMINISTRADOR, RoleEnum.PROFESOR)
        if(!validateUserRoles.validateUserRoles(roles!!, listRolesPermitidos)) {
            isPublicCheckBox.visibility = View.GONE
        }
        builder.setPositiveButton("Crear Chat") { _, _ ->

            val name = dialogView.findViewById<EditText>(R.id.editTextChatName)
            if(!name.text.isNullOrBlank()){
                if(!validateUserRoles.validateUserRoles(roles!!, listRolesPermitidos)) {
                    chatViewModel.onAddChat(
                        true,
                        name.text.toString()
                    )
                }else{
                    chatViewModel.onAddChat(
                        isPublicCheckBox.isChecked,
                        name.text.toString()
                    )
                }
            }

  

        }
        builder.setNegativeButton("Cancelar") { _, _ ->

        }
        builder.create()
        builder.show()


    }



    private fun onMessagesChange() {
        messagesViewModel.messages.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {

                    val primerMensaje =  it.data?.first()!!
                    val id = primerMensaje.room.substring(primerMensaje.room.length - 1).toIntOrNull()
                    id?.let {
                        homeAdapter.scrollToItemById(it, primerMensaje.text, primerMensaje.authorId!!, primerMensaje.authorName, primerMensaje.dataType)
                        val recyclerView: RecyclerView = binding.chatList
                        recyclerView.scrollToPosition(0)
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


    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        val uri = "geo:$latitude,$longitude?q=$latitude,$longitude"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onLocationChanged(location: Location) {
        if (!ubicacionObtenida) {
            localizacion = location
            //openGoogleMaps(location.latitude, location.longitude)
            ubicacionObtenida = true
        }
    }

    fun checkLocationPermissions(): Boolean {
        try {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationPermissionCode
                )
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            return true
        } catch(e : Exception) {
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    fun onNotificationEmployee(message: RoomMessages) {
        // viewModel.updateEmployeeList()
        chatViewModel.getChatFromRoom()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationEmployee(chat:  Resource<List<ChatResponse_Chat>>) {
        homeAdapter.submitList(chat.data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationEmployee(chat:  Boolean) {
        GlobalScope.launch {
            kotlinx.coroutines.delay(200) // Espera 200 milisegundos
            chatViewModel.getChatFromRoom() // Llama a la función getChatFromRoom() después de 200 milisegundos
        }
    }

}