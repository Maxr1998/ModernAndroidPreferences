package de.Maxr1998.modernpreferences.preferences

import android.widget.CompoundButton
import androidx.core.view.isVisible
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter

abstract class TwoStatePreference(key: String) : Preference(key) {
    private var checkedInternal = false
    var checked: Boolean
        get() = checkedInternal
        set(value) {
            if (value != checkedInternal)
                updateState(null, value)
        }
    var defaultValue = false
    var checkedChangeListener: OnCheckedChangeListener? = null

    var summaryOn: String? = null
    var summaryOnRes: Int = -1

    override fun onAttach() {
        checkedInternal = getBoolean(defaultValue)
    }

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        holder.summary?.apply {
            when {
                !isVisible -> return@apply
                summaryOnRes != -1 -> setText(summaryOnRes)
                summaryOn != null -> text = summaryOn
            }
        }
        updateButton(holder)
    }

    private fun updateState(holder: PreferencesAdapter.ViewHolder?, new: Boolean) {
        if (checkedChangeListener?.onCheckedChanged(this, holder, new) != false) {
            commitBoolean(new)
            checkedInternal = new // Update internal state
            if (holder != null)
                updateButton(holder)
        }
    }

    private fun updateButton(holder: PreferencesAdapter.ViewHolder) {
        (holder.widget as CompoundButton).isChecked = checkedInternal
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        updateState(holder, !checkedInternal)
    }

    interface OnCheckedChangeListener {
        /**
         * Notified when the [checked][TwoStatePreference.checked] state of the connected [TwoStatePreference] changes.
         * This is called before the change gets persisted and can be prevented by returning false.
         *
         * @param holder the [ViewHolder][PreferencesAdapter.ViewHolder] with the views of the Preference instance,
         * or null if the change didn't occur as part of a click event
         * @param checked the new state
         *
         * @return true to commit the new button state to [SharedPreferences][android.content.SharedPreferences]
         */
        fun onCheckedChanged(preference: TwoStatePreference, holder: PreferencesAdapter.ViewHolder?, checked: Boolean): Boolean
    }
}