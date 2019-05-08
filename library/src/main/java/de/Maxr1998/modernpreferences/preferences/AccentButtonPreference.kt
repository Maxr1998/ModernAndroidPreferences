package de.Maxr1998.modernpreferences.preferences

import android.annotation.SuppressLint
import de.Maxr1998.modernpreferences.Preference

class AccentButtonPreference(key: String) : Preference(key) {
    @SuppressLint("ResourceType")
    override fun getWidgetLayoutResource() = RESOURCE_CONST

    companion object {
        const val RESOURCE_CONST = -3
    }
}