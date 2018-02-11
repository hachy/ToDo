package io.github.hachy.android.todo

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.EditText

class HeaderDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_header, null)
        val editText = view.findViewById<EditText>(R.id.editHeader)

        builder.setMessage(R.string.header)
                .setView(view)
                .setPositiveButton(R.string.add, { _, _ ->
                    val header = "${editText.text}"
                    if (!TextUtils.isEmpty(header)) {
                        (activity as MainActivity).doPositiveClick(header)
                        editText.text.clear()
                    }
                })
                .setNegativeButton(R.string.cancel, { _, _ ->
                    dismiss()
                })

        val d = builder.create()
        d.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return d
    }
}
