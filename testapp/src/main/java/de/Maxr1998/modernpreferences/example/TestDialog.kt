package de.Maxr1998.modernpreferences.example

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import de.Maxr1998.modernpreferences.preferences.DialogPreference

class TestDialog : DialogPreference("dialog") {
    override fun createDialog(context: Context): Dialog =
            AlertDialog.Builder(context)
                    .setTitle("Info")
                    .setMessage("You opened this dialog!")
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
}