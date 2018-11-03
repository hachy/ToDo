package io.github.hachy.android.todo

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private lateinit var preferences: SharedPreferences
    private val name = MyApplication.getContext().getString(R.string.prefs_name)
    private val key = MyApplication.getContext().getString(R.string.order_pref_key)
    private val value = MyApplication.getContext().getString(R.string.order_value_ascending)
    private val ORDER_PREF = Pair(key, value)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var order: String?
        get() = preferences.getString(ORDER_PREF.first, ORDER_PREF.second)
        set(value) = preferences.edit {
            it.putString(ORDER_PREF.first, value)
        }
}