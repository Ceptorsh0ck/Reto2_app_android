package com.example.reto2_app_android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.repository.remote.RemoteUsersDataSource
import com.example.reto2_app_android.databinding.FragmentRecoverPasswordBinding
import com.example.reto2_app_android.ui.MainActivity
import com.example.reto2_app_android.utils.Resource

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecoverPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecoverPasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recoverPasswordBinding: FragmentRecoverPasswordBinding
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

        recoverPasswordBinding =
            FragmentRecoverPasswordBinding.inflate(layoutInflater, container, false)

        recoverPasswordBinding.recoverPasswordButton.setOnClickListener {
            val email = recoverPasswordBinding.recoverPasswordEmail.text.toString()

            if (email.isNotEmpty()) {
                if (isValidEmail(email)) {
                    val request = PasswordRecoverRequest(email)
                    viewModel.onRecoverPassword(request)
                } else {
                    recoverPasswordBinding.recoverPasswordEmail.error =
                        "Correo electrónico no válido"
                }

            } else {
                recoverPasswordBinding.recoverPasswordEmail.error =
                    "El campo de correo electrónico no puede estar vacío"
            }
        }

        recoverPasswordBinding.returnButton.setOnClickListener {
            parentFragmentManager.popBackStack()

        }
        viewModel.recoverPassword.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { data ->
                        Toast.makeText(
                            activity,
                            "Se ha enviado un correo para recuperar la contraseña",
                            Toast.LENGTH_SHORT
                        ).show()

                        recoverPasswordBinding.recoverPasswordEmail.text.clear()


                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(
                        activity,
                        "Este usuario no existe o no se pudo enviar",
                        Toast.LENGTH_SHORT
                    )
                        .show()


                }

                Resource.Status.LOADING -> {
                    Toast.makeText(activity, "Cargando", Toast.LENGTH_SHORT).show()
                }
            }

        }




        return recoverPasswordBinding.root

    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecoverPasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecoverPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}