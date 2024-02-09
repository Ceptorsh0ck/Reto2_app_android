package com.example.reto2_app_android

import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import com.example.reto2_app_android.MyApp.Companion.context
import java.util.Locale


class RootPreferences() {

    private val rootPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.root_preferences), Context.MODE_PRIVATE)
    }

    companion object {
        const val USER_LANGUAGE = "language"
        const val USER_THEME = "theme"

        val defaultTheme = context.getString(R.string.theme_value_auto)
        val defaultLanguage = context.getString(R.string.language_value_spanish)
    }

    private fun fetchAppConfig(): MutableMap<String, *>? {
        //Saca un array con todos los valores de configuracion
        val configurationList = ArrayList<String>()

        val configuration: MutableMap<String, *>? = rootPreferences.all

        rootPreferences.getString(USER_LANGUAGE,null)?.let { configurationList.add(it) }
        rootPreferences.getString(USER_THEME,null)?.let { configurationList.add(it) }
        return configuration
    }

    private fun fetchLanguage(): String? {
        return rootPreferences.getString(USER_LANGUAGE,null)
    }

    private fun fetchTheme(): String? {
        return rootPreferences.getString(USER_THEME,null)
    }

    private fun saveLanguage(language: String) {
        val editor = rootPreferences.edit()
        editor.putString(USER_LANGUAGE,language)
        editor.apply()
    }


    fun checkSettings() {
        //Creamos variables de configuracion
        var language = ""
        var theme = ""
        //Buscamos el idioma del sistema operativo android
        var systemLocale = context.packageManager.getResourcesForApplication("android").configuration.locales.toString()
        //La variable anterior da como resultado [en_US], por lo que hay que quitarle las []
        systemLocale = systemLocale.substring(1,systemLocale.length-1)
        //Saca el array de los idiomas disponibles
        val localeArray : ArrayList<String> =  context.resources.getStringArray(R.array.language_values).toList() as ArrayList<String>

        val configArray = fetchAppConfig()
        if (configArray != null) {
            for (config in configArray) {
                //Cada item del array tiene nombreSettings=valor por lo que la el siguiente codigo separa el String en un array de 2 campos.
                val array = config.toString().split("=").toTypedArray()
                //Mira en la posicion 0 el nombre de la configuracion y rellena cada respectiva variable con su valor.
                when (array.elementAt(0)) {
                    USER_LANGUAGE -> {
                        language = array.elementAt(1).toString()
                    }
                    USER_THEME -> {
                        theme = array.elementAt(1).toString()
                    }
                }
            }
        }
        // Si no habia nada configurado en RootPreferences, entra
        if (language.isBlank()) {
            language = if(localeArray.contains(systemLocale)) {
                saveLanguage(systemLocale)
                systemLocale
            } else {
                defaultLanguage
            }
        }
        if (theme.isBlank()) {
            theme = defaultTheme
        }
    }


    fun changeLanguage(language: String){
        context.getSystemService(
            LocaleManager::class.java
        )?.applicationLocales = LocaleList(Locale.forLanguageTag(language))
    }

    fun changeTheme(theme: String) {
        when (theme) {
            context.getString(R.string.theme_value_auto) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            context.getString(R.string.theme_value_dark) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            context.getString(R.string.theme_value_light) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}