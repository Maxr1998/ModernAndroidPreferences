package de.Maxr1998.modernpreferences.preferences

import de.Maxr1998.modernpreferences.R

class SwitchPreference(key: String) : TwoStatePreference(key) {
    override fun getWidgetLayoutResource() = R.layout.preference_widget_switch
}