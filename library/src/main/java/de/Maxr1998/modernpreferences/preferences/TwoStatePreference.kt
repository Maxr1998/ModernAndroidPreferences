package de.Maxr1998.modernpreferences.preferences

import android.widget.Switch
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter

abstract class TwoStatePreference(key: String) : Preference(key) {
    var checked = false

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        checked = getBoolean(false)
        updateView(holder)
    }

    private fun updateView(holder: PreferencesAdapter.ViewHolder) {
        (holder.widget as Switch).isChecked = checked
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        checked = !checked
        commitBoolean(checked)
        updateView(holder)
    }
}