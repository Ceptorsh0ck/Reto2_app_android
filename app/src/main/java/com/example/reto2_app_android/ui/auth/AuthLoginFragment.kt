package com.example.reto2_app_android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.R
import com.example.reto2_app_android.databinding.FragmentAuthLoginBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuthLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AuthLoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var loginBinding: FragmentAuthLoginBinding
    private lateinit var loginUsername: String
    private lateinit var loginPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loginBinding = FragmentAuthLoginBinding.inflate(layoutInflater,container, false)

        loginBinding.loginButton.setOnClickListener() {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        loginBinding.loginRegisterButton.setOnClickListener() {
            var arguments = Bundle()
            if (loginBinding.loginUsername.text.isNotEmpty()) {
                arguments.putString("loginUsername",loginBinding.loginUsername.text.toString())
            }
            if (loginBinding.loginPassword.text.isNotEmpty()) {
                arguments.putString("loginPassword",loginBinding.loginPassword.text.toString())
            }
            val newFragment = AuthUserConfirmationFragment()
            newFragment.arguments = arguments
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.authFragmentContainerView, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        loginBinding.loginResetPasswordButton.setOnClickListener() {
            //TODO Realizar recordar contraseña
            Toast.makeText(activity,"Recordar Contraseña",Toast.LENGTH_SHORT).show()
        }

//        val view =  inflater.inflate(R.layout.fragment_auth_login, container, false)
//
//        val registerButton : TextView = view.findViewById(R.id.registerButton2)
//        registerButton.setOnClickListener() {
//            val newFragment = AuthUserConfirmationFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.authFragmentContainerView, newFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
//        val changePasswordButton : TextView = view.findViewById(R.id.resetPasswordButton2)
//        changePasswordButton.setOnClickListener() {
//            Toast.makeText(activity,"Recordar Contraseña",Toast.LENGTH_SHORT).show()
//        }
//         return view
        return loginBinding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}