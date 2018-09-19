package de.Maxr1998.modernpreferences.preferences

import android.widget.CompoundButton
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter

abstract class TwoStatePreference(key: String) : Preference(key) {
    var checked = false
    var checkedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        checked = getBoolean(checked)
        updateView(holder)
    }

    private fun updateView(holder: PreferencesAdapter.ViewHolder) {
        (holder.widget as CompoundButton).isChecked = checked
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        checked = !checked
        commitBoolean(checked)
        updateView(holder)
        checkedChangeListener?.onCheckedChanged(holder.widget as CompoundButton, checked)
    }
}