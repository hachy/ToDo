package io.github.hachy.android.todo

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        preferenceManager.sharedPreferencesName = getString(R.string.prefs_name)
        addPreferencesFromResource(R.xml.preferences)
    }

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}