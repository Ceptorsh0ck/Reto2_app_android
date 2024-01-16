package com.example.reto2_app_android.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    private lateinit var mainActivityBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var wifiIcon : MenuItem

        //Añade el onCreate del menu superior
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_action_bar,menu)
                wifiIcon = menu.findItem(R.id.action_wifi_icon)

                //Configura el Flow del detector de red
                networkConnectionManager.isNetworkConnectedFlow
                    .onEach {
                        var res = ""
                        if (it) {
                            res = getString(R.string.wifiIsConnected)
                            wifiIcon.setIcon(R.drawable.wifi_on)
                        } else {
                            res = getString(R.string.wifiIsDisconnected)
                            wifiIcon.setIcon(R.drawable.wifi_off)
                        }
                        //Habria que cambiar el logo dependiendo de si esta online u offline
                        Log.i("Gorka",res)
                        //tvIsNetworkConnected.setText(res)
                    }
                    .launchIn(lifecycleScope)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Log.i("Gorka - Menu",menuItem.title.toString())
                when (menuItem.title.toString()) {
//                    R.string.log -> {
//
//                    }
                }
                return true
            }

        })

        //networkConnectionManager.startListenNetworkState()

        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        //Rudimentaria
//        val timer = Timer()
//        val MILLISECONDS = 5000 //5 seconds
//
//        timer.schedule(CheckConnection(this), 0, MILLISECONDS.toLong())

        val navView: BottomNavigationView = mainActivityBinding.navView
        Log.i("Aimar", "MAIN")
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)




    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
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

}