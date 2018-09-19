package de.Maxr1998.modernpreferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.core.view.isVisible

abstract class AbstractPreference internal constructor(val key: String) {
    // UI
    var title: String = ""
    @StringRes
    var titleRes: Int = -1
    var description: String? = null
    @StringRes
    var descriptionRes: Int = -1
    var icon: Drawable? = null
    @DrawableRes
    var iconRes: Int = -1

    // State
    var visible = true

    internal fun copyFrom(other: AbstractPreference) {
        title = other.title
        titleRes = other.titleRes
        description = other.description
        descriptionRes = other.descriptionRes
        icon = other.icon
        iconRes = other.iconRes

        visible = other.visible
    }
}

/**
 * The base class for the Preference system - it corresponds to a single item in the list, regardless
 * if [switch][de.Maxr1998.modernpreferences.preferences.SwitchPreference],
 * [category header][de.Maxr1998.modernpreferences.preferences.CategoryHeader] or
 * [sub-screen][PreferenceScreen].
 */
open class Preference(key: String) : AbstractPreference(key) {
    // State
    var enabled = true
    var dependency: String? = null

    var clickListener: OnClickListener? = null

    private var attachedScreen: PreferenceScreen? = null

    @LayoutRes
    open fun getWidgetLayoutResource(): Int {
        return -1
    }

    internal fun attachToScreen(screen: PreferenceScreen) {
        if (attachedScreen != null)
            throw IllegalStateException("Preference was already attached to a screen!")
        attachedScreen = screen
    }

    /**
     * Binds the preference-data to its views from the [view holder][PreferencesAdapter.ViewHolder]
     * Don't call this yourself, it will get called from the [PreferencesAdapter].
     */
    @CallSuper
    open fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        holder.itemView.isVisible = visible

        var itemVisible = false
        holder.icon?.apply {
            itemVisible = true
            if (iconRes != -1) setImageResource(iconRes) else if (icon != null) setImageDrawable(icon) else itemVisible = false
        }
        holder.iconFrame?.isVisible = itemVisible
        holder.title.apply {
            if (titleRes != -1) setText(titleRes) else text = title
        }
        holder.summary?.apply {
            itemVisible = true
            if (descriptionRes != -1) setText(descriptionRes) else if (description != null) text = description else itemVisible = false
            isVisible = itemVisible
        }
    }

    internal fun performClick(holder: PreferencesAdapter.ViewHolder) {
        onClick(holder)
        if (clickListener?.onClick(this, holder) == true)
            bindViews(holder)
    }

    open fun onClick(holder: PreferencesAdapter.ViewHolder) {}

    /**
     * Save an int for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitInt(value: Int) {
        attachedScreen?.prefs?.edit {
            putInt(key, value)
        }
    }

    fun getInt(defaultValue: Int) = try {
        attachedScreen?.prefs?.getInt(key, defaultValue) ?: defaultValue
    } catch (e: ClassCastException) {
        defaultValue
    }

    /**
     * Save a boolean for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitBoolean(value: Boolean) {
        attachedScreen?.prefs?.edit {
            putBoolean(key, value)
        }
    }

    fun getBoolean(defaultValue: Boolean) = try {
        attachedScreen?.prefs?.getBoolean(key, defaultValue) ?: false
    } catch (e: ClassCastException) {
        defaultValue
    }

    /**
     * Save a String for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitString(value: String) {
        attachedScreen?.prefs?.edit {
            putString(key, value)
        }
    }

    fun getString(defaultValue: String) = try {
        attachedScreen?.prefs?.getString(key, defaultValue) ?: defaultValue
    } catch (e: ClassCastException) {
        defaultValue
    }

    interface OnClickListener {
        /**
         * Notified when the connected [Preference] is clicked
         *
         * @return true if the preference changed and needs to update its views
         */
        fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder): Boolean
    }
}

/**
 * Management class for Preference views. Contains a list of preferences, created through [PreferenceScreen.Builder].
 *
 * It extends the [Preference] class, but gets handled slightly differently in a few things:
 * - [PreferenceScreen]s don't have a key attached to them
 * - Every [PreferenceScreen] can be bound to a different [SharedPreferences] file
 * - Even though you can change the [enabled] state, it doesn't have any effect in this instance
 */
class PreferenceScreen private constructor(builder: Builder) : Preference("") {
    internal val prefs = builder.prefs
    private val keyMap: Map<String, Preference> = builder.keyMap
    private val preferences: List<Preference> = builder.preferences

    init {
        copyFrom(builder)
        for (i in preferences.indices)
            preferences[i].attachToScreen(this)
    }

    /**
     * Gets the [Preference] at the specified index on this screen
     *
     * @throws IndexOutOfBoundsException if index > [size]
     */
    operator fun get(index: Int) = preferences[index]

    /**
     * Gets a [Preference] on this screen by its key
     */
    operator fun get(key: String) = keyMap[key]

    fun size() = preferences.size

    fun contains(preference: String) = keyMap.containsKey(preference)

    class Builder(private var context: Context?) : AbstractPreference("") {
        constructor(builder: Builder) : this(builder.context)

        var preferenceFileName: String = (context?.packageName ?: "package") + "_preferences"
        internal var prefs: SharedPreferences? = null
        internal val keyMap = HashMap<String, Preference>()
        internal val preferences = ArrayList<Preference>()

        /**
         * Add the specified preference to this screen - it doesn't make sense to call this directly,
         * use the dsl helper methods like [pref][de.Maxr1998.modernpreferences.helpers.pref],
         * [switch][de.Maxr1998.modernpreferences.helpers.switch] and
         * [subScreen][de.Maxr1998.modernpreferences.helpers.subScreen] for this.
         */
        fun addPreferenceItem(p: Preference) {
            if (p.key.isEmpty() && p !is PreferenceScreen)
                throw UnsupportedOperationException("Preference key may not be empty!")

            if (p is PreferenceScreen || keyMap.put(p.key, p) == null)
                preferences.add(p)
            else throw UnsupportedOperationException("A preference with this key is already in the screen!")
        }

        fun build(): PreferenceScreen {
            prefs = context?.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
            context = null
            return PreferenceScreen(this)
        }
    }
}