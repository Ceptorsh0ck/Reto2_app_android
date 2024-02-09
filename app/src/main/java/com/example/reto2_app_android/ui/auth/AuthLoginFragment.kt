package com.example.reto2_app_android.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.repository.remote.RemoteUsersDataSource
import com.example.reto2_app_android.databinding.FragmentAuthLoginBinding
import com.example.reto2_app_android.utils.Resource


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
    private val userRepositoryRemote = RemoteUsersDataSource()
    private val viewModel: AuthLoginViewModel by viewModels {
        AuthLoginViewModelFactory(
            userRepositoryRemote
        )
    }

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
        loginBinding = FragmentAuthLoginBinding.inflate(layoutInflater, container, false)
        val args = arguments
        if (args != null) {
            val loginUsername = args.getString("email")
            if (loginUsername != null) {
                loginUsername?.let {
                    loginBinding.loginUsername.setText(it)
                    loginBinding.loginUsername.setBackgroundColor(resources.getColor(R.color.yellow))

                }
            }

        }

        loginBinding.loginButton.setOnClickListener() {
            if (loginBinding.loginUsername.text.isNotEmpty() && loginBinding.loginPassword.text.isNotEmpty()) {
                val login = loginBinding.loginUsername.text.toString()
                val password = loginBinding.loginPassword.text.toString()
                viewModel.loginUser(
                    login,
                    password,
                    loginBinding.loginRememberMe.isChecked

                )
                loginBinding.loginUsername.text.clear()
                loginBinding.loginPassword.text.clear()
            } else {
                loginBinding.loginUsername.error = getString(R.string.emptyLogin)

            }


        }

        viewModel.login.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { data ->

                        if (MyApp.userPreferences.getLoggedUser()?.firstLogin == false) {
                            val rememberMe = loginBinding.loginRememberMe.isChecked
                            MyApp.userPreferences.saveRememberMeStatus(rememberMe)

                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            var arguments = Bundle()
                            if (loginBinding.loginUsername.text.isNotEmpty()) {
                                arguments.putString(
                                    "loginUsername",
                                    loginBinding.loginUsername.text.toString()
                                )
                            }
                            if (loginBinding.loginPassword.text.isNotEmpty()) {
                                arguments.putString(
                                    "loginPassword",
                                    loginBinding.loginPassword.text.toString()
                                )
                            }
                            var userNew = UserNew()
                            userNew.oldPassword = loginBinding.loginPassword.text.toString()
                            val newFragment = AuthChangePasswordFragment()
                            val args = Bundle()
                            args.putParcelable("usuario", userNew)
                            newFragment.arguments = args

                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.replace(R.id.authFragmentContainerView, newFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }

                    }
                }

                Resource.Status.ERROR -> {
                    // TODO sin gestionarlo en el VM. Y si envia en una sala que ya no esta? a tratar
                    Toast.makeText(activity, getString(R.string.wrongLogin), Toast.LENGTH_LONG)
                        .show()
                }

                Resource.Status.LOADING -> {
                    //DE MOMENTO NADA
                }
            }
        }


        loginBinding.loginResetPasswordButton.setOnClickListener() {
            //TODO Realizar recordar contraseña
            val newFragment = RecoverPasswordFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.authFragmentContainerView, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()


        }

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