package com.example.reto2_app_android.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.reto2_app_android.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}