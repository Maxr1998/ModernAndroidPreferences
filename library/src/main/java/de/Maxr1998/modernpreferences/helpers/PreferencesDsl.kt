package de.Maxr1998.modernpreferences.helpers

import android.content.Context
import android.view.View
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import de.Maxr1998.modernpreferences.preferences.SwitchPreference

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

inline fun Preference.click(crossinline callback: (View) -> Unit) {
    clickListener = View.OnClickListener {
        callback(it)
    }
}

inline fun <reified T : Preference> PreferenceScreen.Builder.custom(key: String, block: T.() -> Unit) {
    addPreferenceItem(T::class.java.getConstructor(String::class.java).newInstance(key).apply(block))
}