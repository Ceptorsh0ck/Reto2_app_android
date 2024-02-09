package com.example.reto2_app_android.utils

import android.content.Context
import android.util.Log
import com.example.reto2_app_android.R
import java.io.IOException
import java.io.InputStream
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class MyTrustManager(val context: Context) {

    private val TAG = "TrustManager"

    private var myCert: X509Certificate? = loadCertificate()

    val trustManager = object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            if (chain.isNotEmpty() && chain[0] == myCert) {
                // El certificado del cliente es el mismo que tu certificado público
            } else {
                throw CertificateException("El certificado del cliente no es válido.")
            }
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            if (chain.isNotEmpty() && chain[0] == myCert) {
                // El certificado del servidor es el mismo que tu certificado público
            } else {
                throw CertificateException("El certificado del servidor no es válido.")
            }
        }
    }


    private fun loadCertificate(): X509Certificate? {

        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificadoResId = R.raw.certificate

        try {
            val inputStream: InputStream = context.resources.openRawResource(certificadoResId)
            val myCert = certificateFactory.generateCertificate(inputStream) as X509Certificate
            inputStream.close()
            return myCert
        } catch (ex: IOException) {
        }
        return null
    }


}