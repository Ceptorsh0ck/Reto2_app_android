package com.example.reto2_app_android.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.databinding.AuthLayoutBinding
import com.example.reto2_app_android.ui.auth.AuthInitialFragment
import com.example.reto2_app_android.ui.auth.AuthLoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity: AppCompatActivity() {

    private lateinit var authLayoutBinding: AuthLayoutBinding
    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val logoutExtra = intent.getBooleanExtra("logout", false)
        if (logoutExtra) {
            authLayoutBinding = AuthLayoutBinding.inflate(layoutInflater)
            setContentView(authLayoutBinding.root)
            MyApp.userPreferences.unLogUser()
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = AuthLoginFragment()
            fragmentTransaction.replace(R.id.authFragmentContainerView, fragment)
            fragmentTransaction.commit()

            return
        }




        var wifiIcon : MenuItem

        //Añade el onCreate del menu superior
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_action_bar,menu)
                wifiIcon = menu.findItem(R.id.action_wifi_icon)

                //Desabilita el grupo de Menu Items del desplegable.
                menu.setGroupVisible(R.id.action_dropdown_group,false)

                //Configura el Flow del detector de red
                networkConnectionManager.isNetworkConnectedFlow
                    .onEach {
                        if (it) {
                            wifiIcon.setIcon(R.drawable.wifi_on)
                        } else {
                            wifiIcon.setIcon(R.drawable.wifi_off)
                        }
                    }
                    .launchIn(lifecycleScope)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })

        //Inicia el comprobador de wifi
        networkConnectionManager.startListenNetworkState()

        //Comprueba la configuracion de RootPreferences
        MyApp.rootPreferences.checkSettings()

        authLayoutBinding = AuthLayoutBinding.inflate(layoutInflater)
        setContentView(authLayoutBinding.root)

        //Inicia primer fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        var fragment = AuthInitialFragment()
        fragmentTransaction.add(R.id.authFragmentContainerView, fragment)
        fragmentTransaction.commit()

    }

}