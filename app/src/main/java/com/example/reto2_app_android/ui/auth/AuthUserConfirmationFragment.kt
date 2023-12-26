package com.example.reto2_app_android.ui.auth

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_app_android.R
import com.example.reto2_app_android.databinding.FragmentAuthUserConfirmationBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "loginUsername"
private const val ARG_PARAM2 = "loginPassword"

/**
 * A simple [Fragment] subclass.
 * Use the [AuthUserConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuthUserConfirmationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var loginUsername: String? = null
    private var loginPassword: String? = null
    private lateinit var userConfirmationBinding: FragmentAuthUserConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            loginUsername = it.getString(ARG_PARAM1)
            loginPassword = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        userConfirmationBinding = FragmentAuthUserConfirmationBinding.inflate(layoutInflater,container, false)




        if (!loginUsername.isNullOrBlank()) {
            userConfirmationBinding.checkLoginUsername.setText(loginUsername)
        }
        if (!loginPassword.isNullOrBlank()) {
            userConfirmationBinding.checkLoginPassword.setText(loginPassword)
        }

        userConfirmationBinding.checkLoginButton.setOnClickListener() {
            val newFragment = AuthChangePasswordFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(com.example.reto2_app_android.R.id.authFragmentContainerView, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        userConfirmationBinding.checkLoginBackButton.setOnClickListener() {
            parentFragmentManager.popBackStack()
//            val newFragment = AuthLoginFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.authFragmentContainerView, newFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
        }


        return userConfirmationBinding.root
        //val view =  inflater.inflate(R.layout.fragment_auth_user_confirmation, container, false)

//        val loginButton : Button = view.findViewById(R.id.loginButton3)
//        loginButton.setOnClickListener() {
//            val newFragment = AuthUserConfirmationFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.authFragmentContainerView, newFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//
//        val backButton : Button = view.findViewById(R.id.backButton2)
//        backButton.setOnClickListener() {
//            val newFragment = AuthLoginFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.authFragmentContainerView, newFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuthUserConfirmationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthUserConfirmationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}