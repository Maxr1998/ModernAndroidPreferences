package de.Maxr1998.modernpreferences.preferences

import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.R

class CollapsePreference(key: String) : Preference(key) {
    private val preferences = ArrayList<Preference>()

    init {
        titleRes = R.string.pref_advanced_block
        iconRes = R.drawable.ic_expand_24dp
    }

    fun addItem(preference: Preference) {
        preferences.add(preference.apply { visible = false })
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        visible = false
        for (i in preferences.indices)
            preferences[i].visible = true
        attachedScreen?.requestRebind(screenPosition, 1 + preferences.size)
    }
}