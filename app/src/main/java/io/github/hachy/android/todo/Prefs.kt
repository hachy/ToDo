package io.github.hachy.android.todo

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "io.github.hachy.android.todo"
    private lateinit var preferences: SharedPreferences
    private val IS_ASCENDING_PREF = Pair("is_ascending", true)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isAsc: Boolean
        get() = preferences.getBoolean(IS_ASCENDING_PREF.first, IS_ASCENDING_PREF.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_ASCENDING_PREF.first, value)
        }
}