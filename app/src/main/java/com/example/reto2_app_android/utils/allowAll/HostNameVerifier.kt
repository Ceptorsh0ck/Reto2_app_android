package com.example.reto2_app_android.utils.allowAll

import javax.net.ssl.HostnameVerifier

var hostnameVerifier = HostnameVerifier { hostname, sslSession -> true }

