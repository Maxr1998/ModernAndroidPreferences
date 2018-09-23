package de.Maxr1998.modernpreferences.preferences

import android.widget.CompoundButton
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter

abstract class TwoStatePreference(key: String) : Preference(key) {
    var checked = false
    var checkedChangeListener: OnCheckedChangeListener? = null

    override fun onAttach() {
        checked = getBoolean(checked)
    }

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        updateButton(holder)
    }

    private fun updateButton(holder: PreferencesAdapter.ViewHolder) {
        (holder.widget as CompoundButton).isChecked = checked
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        checked = !checked
        commitBoolean(checked)
        if (checkedChangeListener?.onCheckedChanged(this, holder, checked) == true)
            bindViews(holder)
        else updateButton(holder)
    }

    interface OnCheckedChangeListener {
        /**
         * Notified when the [checked][TwoStatePreference.checked] state of the connected [TwoStatePreference] changes
         *
         * @return true if the preference changed and needs to update its views
         */
        fun onCheckedChanged(preference: TwoStatePreference, holder: PreferencesAdapter.ViewHolder, checked: Boolean): Boolean
    }
}