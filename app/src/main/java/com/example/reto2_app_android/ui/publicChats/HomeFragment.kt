package com.example.reto2_app_android.ui.publicChats

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.model.ChatResponse_Chat
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.data.repository.local.RoomChatDataSource
import com.example.reto2_app_android.data.repository.local.database.AppDatabase
import com.example.reto2_app_android.data.repository.remote.RemoteChatsDataSource
import com.example.reto2_app_android.databinding.FragmentHomeBinding
import com.example.reto2_app_android.ui.auth.AuthScrollingRegisterFragment
import com.example.reto2_app_android.ui.dashboard.DashboardFragment
import com.example.reto2_app_android.utils.Resource
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

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(chatRepository, roomChatRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var localizacion: Location

    private fun onChatListClickItem(chat: ChatResponse_Chat) {
        Log.i("examen", "onCarListClickItem: ${chat.id}")

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
        // val homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        Log.i("Aimar", "HomeViewModeldsasd")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //MainActivity = activity as MainActivity

        homeAdapter = HomeAdapter(::onChatListClickItem)


        binding.chatList.adapter = homeAdapter

        viewModel.items.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Log.i("Lista", it.data?.listChats.toString())

                    homeAdapter.submitList(it.data?.listChats)
                }

                Resource.Status.ERROR -> {
                    Log.i("Aimar", it.message.toString())

                }

                Resource.Status.LOADING -> {

                }
            }
        }

        binding.buttonLocation.setOnClickListener {
            viewModel.updateChatsList()
            /*if(checkLocationPermissions()) {
                ubicacionObtenida = false
                getLocation()
            }*/
        }

        return root
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

}