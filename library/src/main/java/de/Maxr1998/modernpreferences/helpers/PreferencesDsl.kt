package de.Maxr1998.modernpreferences.helpers

import android.content.Context
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import de.Maxr1998.modernpreferences.preferences.SwitchPreference
import de.Maxr1998.modernpreferences.preferences.TwoStatePreference

inline fun screen(context: Context?, block: PreferenceScreen.Builder.() -> Unit): PreferenceScreen {
    return PreferenceScreen.Builder(context).apply(block).build()
}

val emptyScreen: PreferenceScreen by lazy { screen(null) {} }

inline fun PreferenceScreen.Builder.subScreen(block: PreferenceScreen.Builder.() -> Unit) {
    addPreferenceItem(PreferenceScreen.Builder(this).apply(block).build())
}

inline fun PreferenceScreen.Builder.categoryHeader(key: String, block: Preference.() -> Unit) {
    addPreferenceItem(CategoryHeader(key).apply(block))
}

inline fun PreferenceScreen.Builder.pref(key: String, block: Preference.() -> Unit) {
    addPreferenceItem(Preference(key).apply(block))
}

inline fun PreferenceScreen.Builder.switch(key: String, block: SwitchPreference.() -> Unit) {
    addPreferenceItem(SwitchPreference(key).apply(block))
}

inline fun PreferenceScreen.Builder.collapse(block: PreferenceScreen.Builder.() -> Unit) {
    collapseNext()
    block()
    collapseEnd()
}

inline fun Preference.click(crossinline callback: (Preference) -> Boolean) {
    clickListener = object : Preference.OnClickListener {
        override fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder) =
                callback(preference)
    }
}

inline fun Preference.clickView(crossinline callback: (Preference, PreferencesAdapter.ViewHolder) -> Boolean) {
    clickListener = object : Preference.OnClickListener {
        override fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder) =
                callback(preference, holder)
    }
}

inline fun TwoStatePreference.changed(crossinline callback: (TwoStatePreference, PreferencesAdapter.ViewHolder?, Boolean) -> Boolean) {
    checkedChangeListener = object : TwoStatePreference.OnCheckedChangeListener {
        override fun onCheckedChanged(preference: TwoStatePreference, holder: PreferencesAdapter.ViewHolder?, checked: Boolean) =
                callback(preference, holder, checked)
    }
}

inline fun <reified T : Preference> PreferenceScreen.Builder.custom(key: String, block: T.() -> Unit) {
    addPreferenceItem(T::class.java.getConstructor(String::class.java).newInstance(key).apply(block))
}