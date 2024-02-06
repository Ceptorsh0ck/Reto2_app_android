package com.example.reto2_app_android.utils.allowAll

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

val trustManager = object : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }

    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        // not implemented
    }

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        // not implemented
    }
}