package com.example.reto2_app_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto2_app_android.databinding.AuthLayoutBinding
import com.example.reto2_app_android.ui.auth.AuthInitialFragment


class AuthActivity: AppCompatActivity() {

    private lateinit var authLayoutBinding: AuthLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authLayoutBinding = AuthLayoutBinding.inflate(layoutInflater)
        setContentView(authLayoutBinding.root)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        var fragment = AuthInitialFragment()
        fragmentTransaction.add(R.id.authFragmentContainerView, fragment)
        fragmentTransaction.commit()



    }
}