package io.github.hachy.android.todo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText

class HeaderDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val nullParent: ViewGroup? = null
        val view = inflater?.inflate(R.layout.dialog_add_header, nullParent)
        val errMsg = view?.findViewById<TextInputLayout>(R.id.text_input_header)
        val editText = view?.findViewById<EditText>(R.id.editHeader)

        val d = builder.setView(view)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
                .create()

        d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val header = "${editText?.text}"
            if (TextUtils.isEmpty(header)) {
                errMsg?.error = getString(R.string.header_empty)
            } else {
                (activity as MainActivity).doPositiveClick(header)
                editText?.text?.clear()
                dismiss()
            }
        }

        editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!TextUtils.isEmpty("${editText.text}")) {
                    errMsg?.error = null
                    errMsg?.isErrorEnabled = false
                }
            }
        })

        return d
    }
}
