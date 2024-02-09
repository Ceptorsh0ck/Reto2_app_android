package com.example.reto2_app_android.ui.auth

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.UserNew
import com.example.reto2_app_android.data.repository.remote.RemoteUsersDataSource
import com.example.reto2_app_android.databinding.FragmentAuthScrollingRegisterBinding
import com.example.reto2_app_android.ui.MainActivity
import java.io.ByteArrayOutputStream


private const val ARG_USER = "usuario"

class AuthScrollingRegisterFragment : Fragment() {

    private val REQUEST_CODE = "REQUEST_CODE"
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var registerBinding: FragmentAuthScrollingRegisterBinding
    private var userNew: UserNew? = null

    private val userRepositoryRemote = RemoteUsersDataSource()
    private val viewModel: AuthLoginViewModel by viewModels {
        AuthLoginViewModelFactory(
            userRepositoryRemote
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userNew = it.getParcelable(ARG_USER, UserNew::class.java)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        registerBinding =
            FragmentAuthScrollingRegisterBinding.inflate(layoutInflater, container, false)

        if(MyApp.userPreferences.getLoggedUser() != null){
            registerBinding.registerName.setText(MyApp.userPreferences.getLoggedUser()?.name)
            registerBinding.registerSurname1.setText(MyApp.userPreferences.getLoggedUser()?.surname)
            registerBinding.registerSurname2.setText(MyApp.userPreferences.getLoggedUser()?.surname2)
            registerBinding.registerDocumentation.setText(MyApp.userPreferences.getLoggedUser()?.dni)
            registerBinding.registerTelephone1.setText(MyApp.userPreferences.getLoggedUser()?.phone1.toString())
            registerBinding.registerTelephone2.setText(MyApp.userPreferences.getLoggedUser()?.phone2.toString())
        }

        registerBinding.registerButton.setOnClickListener() {
            if (checkAllInputs() && validatePhone(
                    registerBinding.registerTelephone1.text.toString().toInt()
                )
                && validatePhone(registerBinding.registerTelephone2.text.toString().toInt())
                && validateDNI(registerBinding.registerDocumentation.text.toString())
            ) {
                userNew?.name = registerBinding.registerName.text.toString()
                userNew?.surname1 = registerBinding.registerSurname1.text.toString()
                userNew?.surname2 = registerBinding.registerSurname2.text.toString()
                userNew?.dni = registerBinding.registerDocumentation.text.toString()
                userNew?.phone1 = registerBinding.registerTelephone1.text.toString().toInt()
                userNew?.phone2 = registerBinding.registerTelephone2.text.toString().toInt()
                val imageView: ImageView = registerBinding.registerUserPhoto
                val drawable = imageView.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)
                userNew?.photo = encodedImage


                UserNew(
                    MyApp.userPreferences.getLoggedUser()?.id!!,
                    MyApp.userPreferences.getLoggedUser()?.email.toString(),
                    userNew!!.newPassword,
                    registerBinding.registerName.text.toString(),
                    registerBinding.registerSurname1.text.toString(),
                    registerBinding.registerSurname2.text.toString(),
                    registerBinding.registerDocumentation.text.toString(),
                    MyApp.userPreferences.getLoggedUser()?.firstLogin!!,
                    registerBinding.registerTelephone1.text.toString().toInt(),
                    registerBinding.registerTelephone2.text.toString().toInt(),
                    encodedImage

                ).let {
                    viewModel.registerUser(it)
                }


            } else {
                registerBinding.registerName.error = getString(R.string.invalidRegister)
            }
        }
        viewModel.register.observe(viewLifecycleOwner) {
            when (it.status) {
                com.example.reto2_app_android.utils.Resource.Status.SUCCESS -> {
                    it.data?.let { data ->
                        //TODO hay que volver al login con los nuevos datos precargados
                        MyApp.userPreferences.unLogUser()

                        val newFragment = AuthLoginFragment().apply {
                            arguments = Bundle().apply {
                                putString("email", data.email)
                            }
                        }
                        val transaction = parentFragmentManager.beginTransaction()
                        transaction.replace(R.id.authFragmentContainerView, newFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()

                    }
                }

                com.example.reto2_app_android.utils.Resource.Status.ERROR -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                }

                com.example.reto2_app_android.utils.Resource.Status.LOADING -> {
                    //Toast.makeText(activity, "Cargando", Toast.LENGTH_LONG).show()
                }
            }
        }

        registerBinding.registerBackButton.setOnClickListener() {
            parentFragmentManager.popBackStack()
        }
        registerBinding.registerTermsScrollView.setOnScrollChangeListener { scrollView, _, _, _, _ ->
            registerBinding.registerTermsCheckbox.isEnabled =
                scrollView.scrollY >= scrollView.height
        }

        registerBinding.registerUserPhoto.setOnClickListener() {
            dispatchTakePictureIntent()
        }


        return registerBinding.root
    }

    // Take a photo

    private var startActivityIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {
            val requestCode = it.data?.extras?.getInt(REQUEST_CODE)

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.getParcelable("data", Bitmap::class.java)
                imageBitmap?.let {
                    registerBinding.registerUserPhoto.setImageBitmap(imageBitmap)
                } ?: run {
                    Toast.makeText(
                        activity,
                        getString(com.example.reto2_app_android.R.string.registerErrorPhoto),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityIntent.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT)
        }
    }

    private fun validatePhone(phone: Int): Boolean {
       if (phone.toString().length == 9) {
            return true
        }
        registerBinding.registerTelephone1.error = "Los telefonos deben tener un formato de 9 dígitos"
        return false
    }

    private fun validateDNI(dni: String): Boolean {
        val dniRegex = """^\d{8}[a-zA-Z]$""".toRegex()
        return if (dni.matches(dniRegex)) {
            true
        }else{
            registerBinding.registerDocumentation.error = "El DNI debe tener 8 dígitos y una letra"
            false
        }
    }

    // Function checks all input values
    private fun checkAllInputs(): Boolean {

        if (registerBinding.registerName.text.toString()
                .isNotEmpty() && registerBinding.registerSurname1.text.toString()
                .isNotEmpty() && registerBinding.registerSurname2.text.toString().isNotEmpty()
        ) {
            return true
        }
        return false
    }
    //registerBinding.registerScrollView.fullScroll(ScrollView.FOCUS_UP)


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as? Bitmap
//            imageBitmap?.let {
//                registerBinding.registerUserPhoto.setImageBitmap(imageBitmap)
//            } ?: run {
//                Toast.makeText(activity, "No se pudo obtener la imagen", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri? {
//        val OutImage = Bitmap.createScaledBitmap(inImage!!, 1000, 1000, true)
//        val path =
//            MediaStore.Images.Media.insertImage(inContext.contentResolver, OutImage, "Title", null)
//        return Uri.parse(path.drop(8))
//    }
}