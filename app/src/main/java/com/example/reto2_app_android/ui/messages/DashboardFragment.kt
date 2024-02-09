package com.example.reto2_app_android.ui.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.AddPeople
import com.example.reto2_app_android.data.AddPeopleResponse
import com.example.reto2_app_android.data.DeletePeople
import com.example.reto2_app_android.data.MessageAdapter
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.model.RoleEnum
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.data.repository.local.RoomChatDataSource
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.repository.local.tables.RoomDataType
import com.example.reto2_app_android.data.repository.remote.RemoteChatsDataSource
import com.example.reto2_app_android.data.repository.remote.RemoteMessagesDataSource
import com.example.reto2_app_android.data.services.SocketIoService
import com.example.reto2_app_android.databinding.FragmentDashboardBinding
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.ui.messages.AddPeopleAdapter
import com.example.reto2_app_android.ui.messages.DeletePeopleAdapter
import com.example.reto2_app_android.ui.publicChats.HomeViewModel
import com.example.reto2_app_android.ui.publicChats.HomeViewModelFactory
import com.example.reto2_app_android.utils.Resource
import com.example.reto2_app_android.utils.ValidateUserRoles
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64
import javax.inject.Inject

private const val ARG_CHAT = "chat"
private const val DESIRED_WIDTH = 800
private const val DESIRED_HEIGHT = 600
private const val REQUEST_SELECT_FILE = 1001

class DashboardFragment : Fragment(), LocationListener {

    //Ubicacion
    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager
    private lateinit var locationManager: LocationManager
    private lateinit var localizacion: Location
    private val locationPermissionCode = 2
    private var ubicacionObtenida = false
    private var locationUpdateRequested = false
    //Fin ubicacion

    //Camara
    private val REQUEST_IMAGE_CAPTURE = 1
    //Fin camara

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

    private val chatRepository = RemoteChatsDataSource();
    private val roomChatRepository = RoomChatDataSource();
    private val chatViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(chatRepository, roomChatRepository)
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
        multimedia()
        binding.textToolbarChatName.text = chat!!.name
        viewModel.getAllMessages(chat!!.id)
        returnServerUsers()
        llamadaAMetodoDelServicio()
        onClickTeclado(binding)
        showRoomMessage(binding)
        onMessagesChange()
        buttonsListeners(binding)
        onMessageSendRoom(binding)
        return root
    }

    private fun multimedia() {
        val buttonAddOptions = binding.buttonAddOptions

        buttonAddOptions.setOnClickListener {
            // Inflar el menú
            val popupMenu = PopupMenu(requireContext(), buttonAddOptions)
            popupMenu.inflate(R.menu.menu_mutimedia)

            // Manejar los clics de los ítems del menú
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_location -> {
                        mandarUbicacionShocket()
                        true
                    }
                    R.id.action_file -> {
                        takeFile()
                        true
                    }
                    R.id.action_photo -> {
                        takePhoto()
                        true
                    }
                    else -> false
                }
            }

            // Mostrar el menú emergente
            popupMenu.show()
        }
    }

    private fun takeFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Esto permite seleccionar cualquier tipo de archivo
        startActivityForResult(intent, REQUEST_SELECT_FILE)
    }


    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Manejar la captura de imágenes
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val scaledBitmap = scaleBitmap(imageBitmap, DESIRED_WIDTH, DESIRED_HEIGHT)
            val imageBase64String = bitmapToBase64(scaledBitmap)

            chat?.id?.let { chatId ->
                userId?.let { userId ->
                    viewModel.saveNewMessageRoom(imageBase64String, chatId, userId, RoomDataType.IMAGE)
                }
            }
        } else if (requestCode == REQUEST_SELECT_FILE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            // Pasar la URI del archivo seleccionado a un método para procesarla
            handleSelectedFile(selectedFileUri)
        }
    }

    private fun handleSelectedFile(selectedFileUri: Uri?) {
        selectedFileUri?.let { uri ->
            // Aquí puedes realizar las acciones necesarias con la URI del archivo
            // Por ejemplo, puedes enviar la URI a otra actividad o fragmento
            chat?.id?.let { chatId ->
                userId?.let { userId ->
                    viewModel.saveNewMessageRoom(uri.toString(), chatId, userId, RoomDataType.FILE)
                }
            }
        }
    }


    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.getEncoder().encodeToString(byteArray)
    }

    private fun fileToBase64(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        val bytes = outputStream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun getSelectedFileAsBase64(selectedFileUri: Uri?): String? {
        return selectedFileUri?.let { uri ->
            requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                fileToBase64(inputStream)
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun mandarUbicacionShocket() {
        if (checkLocationPermissions() && !locationUpdateRequested) {
            locationUpdateRequested = true
            ubicacionObtenida = false
            getLocation()
        }
    }

    override fun onLocationChanged(location: Location) {
        if (!ubicacionObtenida) {
            localizacion = location
            chat?.id?.let { chatId ->
                userId?.let { userId ->
                    viewModel.saveNewMessageRoom(location.latitude.toString() + "," + location.longitude.toString(), chatId, userId, RoomDataType.GPS)
                }
            }
            ubicacionObtenida = true
            stopLocationUpdates() // Detener actualizaciones después de obtener la ubicación
        }
    }

    private fun stopLocationUpdates() {
        if (locationUpdateRequested) {
            locationManager.removeUpdates(this)
            locationUpdateRequested = false
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

    private fun isAdim() {


        viewModel.admin.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val isAdmin = it.data ?: false
                    if(isAdmin){
                        binding.buttonToolbarAddPeopleChat.visibility = VISIBLE
                        binding.buttonToolbarDeletePeopleChat.visibility = VISIBLE
                        binding.buttonToolbarExitChat.visibility = INVISIBLE
                        binding.buttonToolbarDeleteChat.visibility = VISIBLE
                    }else{
                        binding.buttonToolbarAddPeopleChat.visibility = INVISIBLE
                        binding.buttonToolbarDeletePeopleChat.visibility = INVISIBLE
                        binding.buttonToolbarExitChat.visibility = VISIBLE
                        binding.buttonToolbarDeleteChat.visibility = INVISIBLE

                    }
                    val roles =  MyApp.userPreferences.getLoggedUser()?.listRoles
                    val validateUserRoles = ValidateUserRoles()
                    val listRolesPermitidos: List<RoleEnum> = listOf(RoleEnum.ADMINISTRADOR, RoleEnum.PROFESOR)
                    if(!validateUserRoles.validateUserRoles(roles!!, listRolesPermitidos)) {
                        binding.buttonToolbarExitChat.visibility = INVISIBLE
                    }
                }

                Resource.Status.ERROR -> {
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
                    val filteredList = it.data?.filter { user ->
                        user.userId != userId
                    }
                    addPeopleAdapter.submitList(filteredList)
                }
                Resource.Status.ERROR -> {
                    Log.d(TAG, "error al conectar...")
                }
                Resource.Status.LOADING -> {
                    // Puedes agregar un código de carga aquí si lo necesitas
                }
            }
        }
        viewModel.usersDelete.observe(viewLifecycleOwner) { it ->
            when (it.status) {
                Resource.Status.SUCCESS -> {
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

                    messageAdapter.addMessages(it.data!!)
                    val recyclerView: RecyclerView = binding.recyclerGroupChat
                    recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    //if(MainActivity.)
                    myService.onSaveMessage(it.data.first().text, "Group- " +  chat?.id, it.data.first().idRoom!!, it.data.first().dataType)
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
                        viewModel.saveNewMessageRoom(lastMessage, chatId, userId, RoomDataType.TEXT)
                    }
                }
                binding.editGroupChatMessage.text.clear()
            }
        }
        binding.buttonToolbarAddPeopleChat.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.popup_layout, null)
            dialogView.findViewById<TextView>(R.id.titleTextView).text = "Añadir Usuario"
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
                selectedPeopleList.forEach{
                    myService.addUsersToChats(it.userId, it.chatId, it.admin)
                }
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

                // Iterar sombre los elementos del RecyclerView
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
                selectedPeopleList.forEach{
                    myService.deleteUsersToChats(it.userId, it.chatId, it.admin)
                }
            }
            builder.setNegativeButton("Cancelar") { _, _ ->

            }
            val dialog = builder.create()
            dialog.show()
        }

        binding.buttonToolbarExitChat.setOnClickListener {
            val selectedPeopleList = mutableListOf<AddPeopleResponse>()
            selectedPeopleList.add(AddPeopleResponse(userId = userId!!, chat!!.id, false))
            selectedPeopleList.forEach{
                myService.deleteUsersToChats(it.userId, it.chatId, it.admin)
            }
        }
        binding.buttonToolbarDeleteChat.setOnClickListener {
            viewModel.deleteChat(chat!!.id)
            myService.deleteChat(chatId = chat!!.id)
            chatViewModel.getChatFromRoom()
            requireActivity().supportFragmentManager.popBackStack()


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
        messageAdapter.addMessages(message)
        val recyclerView: RecyclerView = binding.recyclerGroupChat
        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotificationEmployee(userDeleted: DeletePeople) {
        if(chat!!.id == userDeleted.chatId){
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}