package com.example.reto2_app_android.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.reto2_app_android.databinding.FragmentAuthScrollingRegisterBinding
import com.google.android.material.textfield.TextInputLayout


class AuthScrollingRegisterFragment : Fragment() {

    private lateinit var registerBinding: FragmentAuthScrollingRegisterBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        registerBinding =  FragmentAuthScrollingRegisterBinding.inflate(layoutInflater, container, false)

        registerBinding.registerButton.setOnClickListener() {
            if (checkAllInputs()) {
                val newFragment = AuthLoginFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(com.example.reto2_app_android.R.id.authFragmentContainerView, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        registerBinding.registerBackButton.setOnClickListener() {
            parentFragmentManager.popBackStack()
        }
        registerBinding.registerTermsScrollView.setOnScrollChangeListener { scrollView, _, _, _, _ ->
            registerBinding.registerTermsCheckbox.isEnabled = scrollView.scrollY >= scrollView.height
        }


        return registerBinding.root
    }



    private fun checkAllInputs():Boolean{
        var focus = false
        if (registerBinding.registerName.text.isNullOrEmpty()) {
            registerBinding.registerNameLayout.error = getString(com.example.reto2_app_android.R.string.inputEmpty)
            if (!focus) {
                focus = focusOnError(registerBinding.registerNameLayout)
            }
        }
        if (registerBinding.registerSurname1.text.isNullOrEmpty()) {
            registerBinding.registerSurname1Layout.error = getString(com.example.reto2_app_android.R.string.inputEmpty)
            if (!focus) {
                registerBinding.registerNameLayout.requestFocus()
                focus = true
            }
        }
        if (!registerBinding.registerTermsCheckbox.isChecked) {
            if (!focus) {

            }
        }
        return !focus
    }

    private fun focusOnError(registerNameLayout: TextInputLayout):Boolean {
        //registerBinding.registerScrollView.fullScroll(ScrollView.FOCUS_UP)
        registerNameLayout.requestFocus()
        return true
    }
 }