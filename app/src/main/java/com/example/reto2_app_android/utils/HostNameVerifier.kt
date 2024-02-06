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
            Log.d(TAG, "MyHostnameVerifier hostname: $hostname")
        }

        Log.e(TAG, "MyHostnameVerifier verify KO")
        if (hostname != null && (hostname == "10.0.2.2" || hostname == "10.5.7.16")
        // other hostnames
        ) {
            return true // true is is verified
        }
        return false

    }
}