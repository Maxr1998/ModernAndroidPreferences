package de.Maxr1998.modernpreferences.storage

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesStorage(
    context: Context,
    preferenceFileName: String = (context.packageName ?: "package") + "_preferences",
    private val mode: Mode = Mode.Apply
) : Storage {

    enum class Mode {
        Commit,
        Apply
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)

    override fun setInt(key: String, value: Int) = edit {
        putInt(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int =
        prefs.getInt(key, defaultValue)

    override fun setBoolean(key: String, value: Boolean) = edit {
        putBoolean(key, value)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        prefs.getBoolean(key, defaultValue)

    override fun setString(key: String, value: String) = edit {
        putString(key, value)
    }

    override fun getString(key: String, defaultValue: String?): String? =
        prefs.getString(key, defaultValue)

    override fun setStringSet(key: String, values: Set<String>) = edit {
        putStringSet(key, values)
    }

    override fun getStringSet(key: String, defaultValue: Set<String>?): Set<String>? =
        prefs.getStringSet(key, defaultValue)

    private fun edit(
        action: SharedPreferences.Editor.() -> Unit
    ) {
        val editor = prefs.edit()
        action(editor)
        if (mode == Mode.Commit) {
            editor.commit()
        } else {
            editor.apply()
        }
    }

}