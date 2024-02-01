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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.data.repository.local.RoomChatDataSource
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.local.database.AppDatabase
import com.example.reto2_app_android.data.repository.local.tables.RoomMessages
import com.example.reto2_app_android.data.repository.remote.RemoteChatsDataSource
import com.example.reto2_app_android.data.services.SocketIoService
import com.example.reto2_app_android.databinding.FragmentHomeBinding
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.ui.dashboard.DashboardFragment
import com.example.reto2_app_android.ui.dashboard.DashboardViewModel
import com.example.reto2_app_android.ui.dashboard.DashboardViewModelFactory
import com.example.reto2_app_android.utils.Resource
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Timer
import java.util.TimerTask
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
    private val messagesViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(roomMessageRepository)
    }
    //private lateinit var myService: SocketIoService

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

        binding.buttonLocation.setOnClickListener {
            chatViewModel.updateChatsList()
            /*if(checkLocationPermissions()) {
                ubicacionObtenida = false
                getLocation()
            }*/
        }
        binding.addNewChat.setOnClickListener {
            Log.d("fff","bton")
            openNewChat()
        }
        comprobarSiSeMySocketSeHaInicializado()
        return root
    }

    private fun openNewChat() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_add_chat, null)
        builder.setView(dialogView)
        Log.d("hhds,","openchar")
        builder.setPositiveButton("Crear Chat") { _, _ ->

            val name = dialogView.findViewById<EditText>(R.id.editTextChatName).text.toString()
            val isPublicCheckBox = dialogView.findViewById<CheckBox>(R.id.checkBoxPublic)
            val isPublic = isPublicCheckBox.isChecked


            chatViewModel.onAddChat(

                isPublic,
                name
            )

        }
        builder.setNegativeButton("Cancelar") { _, _ ->

        }
        builder.create()
        builder.show()


    }

    fun onServiceInitialized() {
        val mainActivity = activity as MainActivity
        onOtherMessageFromServer(mainActivity.myService)
    }
    private fun comprobarSiSeMySocketSeHaInicializado() {
        val mainActivity = activity as MainActivity
        val timer = Timer()
        val delay: Long = 500 // Retraso inicial
        val period: Long = 500 // Intervalo de verificación en milisegundos (0.5 segundos)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (mainActivity.isConnected) {
                    Log.i("MainActivity", "Connect")
                    onOtherMessageFromServer(mainActivity.myService)
                    timer.cancel() // Detiene el temporizador una vez que se inicializa myService
                } else {
                }
            }
        }, delay, period)
    }
    private fun onMessagesChange() {
        messagesViewModel.messages.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {

                    val primerMensaje =  it.data?.first()!!
                    Log.d("Socket", "messages observe")
                    val id = primerMensaje.room.substring(primerMensaje.room.length - 1).toIntOrNull()
                    id?.let {
                        homeAdapter.scrollToItemById(it, primerMensaje.text, primerMensaje.authorId!!, primerMensaje.authorName)
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

    private fun onOtherMessageFromServer(myService: SocketIoService) {
        Log.i("nuevo mensaje", "d")
        viewLifecycleOwner.lifecycleScope.launch {
            myService.messagesFromOtherServer.observe(viewLifecycleOwner) { it ->
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        Log.d("Socket", "aaa" + it.data.toString())
                        messagesViewModel.onNewMessageJsonObject(it.data!!)
                    }
                    Resource.Status.ERROR -> {
                        //Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Resource.Status.LOADING -> {
                        // Handle loading state if needed
                    }
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
            Log.i("GPS", "Latitude: " + location.latitude + " , Longitude: " + location.longitude)
            openGoogleMaps(location.latitude, location.longitude)
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
        Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show()
        chatViewModel.getChatFromRoom()

    }

}