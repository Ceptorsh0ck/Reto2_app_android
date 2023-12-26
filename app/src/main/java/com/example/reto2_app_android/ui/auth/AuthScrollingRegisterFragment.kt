package com.example.reto2_app_android.ui.auth

import android.R
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.databinding.FragmentAuthScrollingRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AuthScrollingRegisterFragment : Fragment() {

    private val REQUEST_CODE = "REQUEST_CODE"
    private val REQUEST_IMAGE_CAPTURE = 1
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
            Log.i("Gorka-Prueba","Request Code: "+requestCode.toString()+" / "+REQUEST_IMAGE_CAPTURE.toString())
            Log.i("Gorka-Prueba","Result Code: "+it.resultCode.toString()+" / "+AppCompatActivity.RESULT_OK)
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.getParcelable("data",Bitmap::class.java)
                imageBitmap?.let {
                    registerBinding.registerUserPhoto.setImageBitmap(imageBitmap)
                } ?: run {
                    Toast.makeText(activity, getString(com.example.reto2_app_android.R.string.registerErrorPhoto), Toast.LENGTH_LONG).show()
                }
            }
        })

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
//          Deprecated
//          startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)


//          Extra info para introducir al Intent
//            if (multiple_files) {
//                contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            }
//            val intentArray: Array<Intent?>
//            intentArray = if (takePictureIntent != null) {
//                arrayOf(takePictureIntent)
//            } else {
//                arrayOfNulls(0)
//            }
//
//            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
//            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
//            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
//          takePictureIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
//          takePictureIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            startActivityIntent.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity,e.toString(),Toast.LENGTH_SHORT)
        }
    }


    // Function checks all input values
    private fun checkAllInputs():Boolean{

        var registerLayout: LinearLayout = registerBinding.registerForm
        var i = 0
        val count =registerLayout.childCount
        while (i < count) {
            val view = registerLayout.getChildAt(i)
            if (view is TextInputEditText) {
                view.setText("") //here it will be clear all the EditText field
            }
            ++i
        }


        var focusInputLayout: TextInputLayout? = null
        if (registerBinding.registerName.text.isNullOrEmpty()) {
            registerBinding.registerNameLayout.error = getString(com.example.reto2_app_android.R.string.inputEmpty)
            if (focusInputLayout == null) {
                focusInputLayout = registerBinding.registerNameLayout
            }
        }
        if (registerBinding.registerSurname1.text.isNullOrEmpty()) {
            registerBinding.registerSurname1Layout.error = getString(com.example.reto2_app_android.R.string.inputEmpty)
            if (focusInputLayout == null) {
                focusInputLayout = registerBinding.registerNameLayout
            }
        }
        if (!registerBinding.registerTermsCheckbox.isChecked) {
            if (focusInputLayout == null) {
                focusInputLayout = registerBinding.registerNameLayout
            }
        }
        return if (focusInputLayout == null) {
            true
        } else {
            focusInputLayout.requestFocus()
            false
        }
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