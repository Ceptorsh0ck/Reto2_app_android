package com.example.reto2_app_android.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.network.broadcast.NetworkCallBack
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.data.repository.CommonMessageRepository
import com.example.reto2_app_android.data.repository.local.RoomMessageDataSource
import com.example.reto2_app_android.data.services.SocketIoService
import com.example.reto2_app_android.databinding.ActivityMainBinding
import com.example.reto2_app_android.ui.publicChats.HomeFragment
import com.example.reto2_app_android.utils.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    private val roomMessageRepository = RoomMessageDataSource();
    lateinit var myService: SocketIoService
    private var isBind = false
    var isConnected = false
    var wifiOn = false
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var navController: NavController
    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var networkCallback: NetworkCallBack
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, SocketIoService::class.java)
        ContextCompat.startForegroundService(this, intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        var wifiIcon: MenuItem

        //Añade el onCreate del menu superior
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_action_bar, menu)
                wifiIcon = menu.findItem(R.id.action_wifi_icon)

                //Configura el Flow del detector de red
                networkConnectionManager.isNetworkConnectedFlow
                    .onEach {
                        var res = ""
                        if (it) {
                            res = getString(R.string.wifiIsConnected)
                            wifiIcon.setIcon(R.drawable.wifi_on)
                            wifiOn = true
                        } else {
                            res = getString(R.string.wifiIsDisconnected)
                            wifiOn = false
                            wifiIcon.setIcon(R.drawable.wifi_off)
                        }
                        //Habria que cambiar el logo dependiendo de si esta online u offline
                        //tvIsNetworkConnected.setText(res)
                    }
                    .launchIn(lifecycleScope)

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_logout -> {
                        if (MyApp.userPreferences.getLoggedUser() != null) {
                            myService.onDestroy()
                            val intent = Intent(this@MainActivity, AuthActivity::class.java)
                            intent.putExtra("logout", true)
                            startActivity(intent)
                            finish()
                            return true
                        }
                    }

                }
                return true
            }
        })

        networkCallback = NetworkCallBack(this)
        networkCallback.register()


        //networkConnectionManager.startListenNetworkState()


        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)
        val navView: BottomNavigationView = mainActivityBinding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_public, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        this.navController = findNavController(R.id.nav_host_fragment_activity_main)

        mainActivityBinding.navView.setOnNavigationItemSelectedListener { item ->
            handleNavigationItemClick(item.itemId)
            true
        }

        comprobaSiSeMySocketSeHaInicializado()
    }


    private fun comprobaSiSeMySocketSeHaInicializado() {
        val timer = Timer()
        val delay: Long = 500 // Retraso inicial
        val period: Long = 500 // Intervalo de verificación en milisegundos (0.5 segundos)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (::myService.isInitialized) {
                    isConnected = true
                    timer.cancel() // Detiene el temporizador una vez que se inicializa myService
                } else {
                    isConnected = false
                }
            }
        }, delay, period)
    }

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val localService = service as SocketIoService.LocalService
            myService = localService.service
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBind = false
        }
    }

    private fun handleNavigationItemClick(itemId: Int) {
        when (itemId) {
            R.id.navigation_public -> {
                navigateToFragment(R.id.navigation_public)
            }

            R.id.navigation_settings -> {
                navigateToFragment(R.id.navigation_settings)
            }
        }
    }

    private fun navigateToFragment(destinationId: Int) {
        navController.navigate(destinationId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        networkConnectionManager.stopListenNetworkState()
    }

    override fun onResume() {
        super.onResume()
        networkConnectionManager.startListenNetworkState()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkConnectionManager.stopListenNetworkState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}