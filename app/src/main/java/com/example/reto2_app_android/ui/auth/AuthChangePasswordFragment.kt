package com.example.reto2_app_android.ui.auth

import android.graphics.Bitmap
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.databinding.FragmentAuthChangePasswordBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_USER = "usuario"

/**
 * A simple [Fragment] subclass.
 * Use the [AuthChangePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuthChangePasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var userNew: UserNew? = null
    private lateinit var changePasswordBinding: FragmentAuthChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            userNew = it.getParcelable(ARG_USER, UserNew::class.java)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        changePasswordBinding = FragmentAuthChangePasswordBinding.inflate(layoutInflater,container,false)
        changePasswordBinding.changePasswordButton.isEnabled = false
        if(MyApp.userPreferences.isRememberMeEnabled() && MyApp.userPreferences.getLoggedUser()?.firstLogin == true){
            MyApp.userPreferences.saveRememberMeStatus(false)
        }
        changePasswordBinding.changePassword1.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (changePasswordBinding.changePassword1.text.length >= 8) {
                    changePasswordBinding.changePasswordButton.isEnabled = true
                    changePasswordBinding.textViewPassCorta.visibility = INVISIBLE

                }else {
                    changePasswordBinding.changePasswordButton.isEnabled = false
                    changePasswordBinding.textViewPassCorta.visibility = VISIBLE
                }
            }
        }

        changePasswordBinding.changePassword2.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (changePasswordBinding.changePassword1 == changePasswordBinding.changePassword2) {
                    changePasswordBinding.changePasswordButton.isEnabled = true
                    changePasswordBinding.textViewContraseAsNoCoinciden.visibility = INVISIBLE

                }else {
                    changePasswordBinding.changePasswordButton.isEnabled = false
                    changePasswordBinding.textViewContraseAsNoCoinciden.visibility = VISIBLE
                }
            }
        }

        changePasswordBinding.changePasswordButton.setOnClickListener() {
            val password1 = changePasswordBinding.changePassword1.text.toString()
            val password2 = changePasswordBinding.changePassword2.text.toString()
            // TODO Comproprobar que Password1 cumple los requisitos y que Password2 es identica.

            if(password1 == password2 && password1.length > 8 && password2.length > 8) {
                userNew?.newPassword = password1
                val newFragment = AuthScrollingRegisterFragment()
                val args = Bundle()
                args.putParcelable("usuario", userNew)
                newFragment.arguments = args
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.authFragmentContainerView, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }else{
                changePasswordBinding.changePassword1.error = "Las contraseñas no coinciden"
            }
        }

        changePasswordBinding.changePasswordBackButton.setOnClickListener() {
            val newFragment = AuthLoginFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.authFragmentContainerView, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return changePasswordBinding.root

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuthChangePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthChangePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}