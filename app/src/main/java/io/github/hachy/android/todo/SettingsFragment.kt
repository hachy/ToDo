package io.github.hachy.android.todo

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        preferenceManager.sharedPreferencesName = getString(R.string.prefs_name)
        addPreferencesFromResource(R.xml.preferences)
    }

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }
}