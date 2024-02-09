package com.example.reto2_app_android.utils

import android.util.Log
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class MyHostnameVerifier : HostnameVerifier {

    private val TAG = "TrustManager"
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        // host verification logic. Check that is one of our hosts

        // only for debug
        if (hostname != null) {
        }

        if (hostname != null
            && hostname == "10.0.2.2"
        // other hostnames
        ) {
            return true // true is is verified
        }
        return false

    }
}