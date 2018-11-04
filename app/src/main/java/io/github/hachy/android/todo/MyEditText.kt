package io.github.hachy.android.todo

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class MyEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val imm = MyApplication.getContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        return false
    }
}
