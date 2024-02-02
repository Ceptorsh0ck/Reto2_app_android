package com.example.reto2_app_android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import com.example.reto2_app_android.data.network.NetworkConnectionManager
import com.example.reto2_app_android.databinding.FragmentAuthInitialBinding
import com.example.reto2_app_android.ui.AuthActivity
import com.example.reto2_app_android.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuthInitialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//@AndroidEntryPoint
class AuthInitialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var initialBinding : FragmentAuthInitialBinding
    private lateinit var authActivity: AuthActivity

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
        //val view =  inflater.inflate(R.layout.fragment_auth_initial, container, false)
        initialBinding = FragmentAuthInitialBinding.inflate(layoutInflater,container, false)

        authActivity = activity as AuthActivity

        // Return the fragment view/layout
        return initialBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Funciona tambien en onCreateView pero considero que la barra deberia empezar a cargar cuando la vista se haya creado
        val loadingBar: ProgressBar = initialBinding.loadingBar
        lifecycleScope.launch {
            while (loadingBar.progress < loadingBar.max)
                initLoading(loadingBar)
        }
    }



    private suspend fun initLoading(
        progressBar: ProgressBar
    ) {
        delay(2000)
        progressBar.isIndeterminate = false
        progress(progressBar)

    }

    private suspend fun progress(progressBar: ProgressBar) {
        while (progressBar.progress < progressBar.max) {
            delay(300)
            progressBar.incrementProgressBy(PROGRESS_INCREMENT)
        }
        if (authActivity.networkConnectionManager.isNetworkConnected) {

            if(isUserLogged()){
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }else {

                val newFragment = AuthLoginFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.authFragmentContainerView, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        } else {
            if(isUserLogged()){
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }else {
                progressBar.progress = 0
                progressBar.secondaryProgress = 0
                progressBar.isIndeterminate = true
                Toast.makeText(context,getString(R.string.noWifi), Toast.LENGTH_SHORT).show()
                delay(2000)
                Toast.makeText(context,getString(R.string.reconnecting), Toast.LENGTH_SHORT).show()

            }

        }

    }
    private fun isUserLogged() : Boolean {
        val rememberMe = MyApp.userPreferences.isRememberMeEnabled()
        val loggedUser = MyApp.userPreferences.getLoggedUser()
        if (rememberMe || loggedUser != null) {
            return true
        }
        return false
    }




//    fun isNetworkAvailable(context: Context): Boolean {
//        val connectivityManager =
//            context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (connectivityManager != null) {
//            val capabilities =
//                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                return true
//            }
//        }
//        return false
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuthInitialFragment.
         */
        // TODO: Rename and change types and number of parameters

        const val PROGRESS_INCREMENT = 50

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthInitialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }


}