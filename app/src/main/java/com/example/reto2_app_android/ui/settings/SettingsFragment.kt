package com.example.reto2_app_android.ui.settings

import android.app.LocaleManager
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.reto2_app_android.MyApp
import com.example.reto2_app_android.R
import java.util.Locale


class SettingsFragment : PreferenceFragmentCompat(),Preference.OnPreferenceChangeListener {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val manager = preferenceManager
        manager.sharedPreferencesName = MyApp.context.getString(R.string.root_preferences)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("language")?.onPreferenceChangeListener = this
        findPreference<Preference>("theme")?.onPreferenceChangeListener = this

    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when(preference.key.toString()) {
            "language" -> {
                MyApp.rootPreferences.changeLanguage(newValue.toString())
            }
//            "language" -> {
//                context?.getSystemService(
//                    LocaleManager::class.java
//                )?.applicationLocales = LocaleList(Locale.forLanguageTag(newValue.toString()))
//            }
            "theme" -> {
                MyApp.rootPreferences.changeTheme(newValue.toString())
//                if (newValue == "dark") {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                }
            }

        }
        return true
    }





}