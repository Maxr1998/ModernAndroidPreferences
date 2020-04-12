/*
 * Copyright (C) 2018 Max Rumpf alias Maxr1998
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.Maxr1998.modernpreferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import androidx.core.view.isVisible
import de.Maxr1998.modernpreferences.helpers.KEY_ROOT_SCREEN
import de.Maxr1998.modernpreferences.preferences.CollapsePreference
import de.Maxr1998.modernpreferences.preferences.SeekBarPreference
import de.Maxr1998.modernpreferences.preferences.TwoStatePreference
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractPreference internal constructor(val key: String) {
    // UI
    @StringRes
    var titleRes: Int = -1
    var title: String = ""

    @StringRes
    var summaryRes: Int = -1
    var summary: String? = null

    @DrawableRes
    var iconRes: Int = -1
    var icon: Drawable? = null

    @StringRes
    var badgeRes: Int = -1
    var badge: String? = null

    // State
    var visible = true

    internal fun copyFrom(other: AbstractPreference) {
        title = other.title
        titleRes = other.titleRes
        summary = other.summary
        summaryRes = other.summaryRes
        icon = other.icon
        iconRes = other.iconRes

        visible = other.visible
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            this === other -> true
            this::class.java == other::class.java && key == (other as AbstractPreference).key -> true
            else -> false
        }
    }

    override fun hashCode() = key.hashCode()
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
        set(value) {
            field = value
            requestRebind()
        }

    var dependency: String? = null

    var preBindListener: OnPreBindListener? = null

    var clickListener: OnClickListener? = null

    /**
     * The screen this Preference currently is attached to, or null
     */
    var parent: PreferenceScreen? = null
        private set

    var screenPosition: Int = 0
        private set

    private var prefs: SharedPreferences? = null

    /**
     * Whether or not to persist changes to this preference to the attached [SharedPreferences] instance
     */
    var persistent: Boolean = true

    private var highlightOnNextBind = AtomicBoolean(false)

    @LayoutRes
    open fun getWidgetLayoutResource(): Int {
        return -1
    }

    internal fun attachToScreen(screen: PreferenceScreen, position: Int) {
        check(this.parent == null) { "Preference was already attached to a screen!" }
        this.parent = screen
        screenPosition = position
        prefs = if (persistent) screen.prefs else null
        dependency?.also {
            val p = this.parent?.get(it)
            if (p != null && p is TwoStatePreference)
                p.addDependent(this)
            else dependency = null // Invalid
        }
        onAttach()
    }

    internal open fun onAttach() {}

    /**
     * Check if this preference has a parent of the given [key]
     *
     * That is, somewhere in the hierarchy above this preference or screen
     * there's a parent screen with that key.
     */
    fun hasParent(key: String): Boolean {
        return generateSequence(parent, PreferenceScreen::parent).any { it.key == key }
    }

    /**
     * Binds the preference-data to its views from the [view holder][PreferencesAdapter.ViewHolder]
     * Don't call this yourself, it will get called from the [PreferencesAdapter].
     */
    @CallSuper
    open fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        checkNotNull(this.parent) {
            "Trying to bind view for a preference not attached to a screen!"
        }

        preBindListener?.onPreBind(this, holder)

        holder.itemView.layoutParams.height = if (visible) ViewGroup.LayoutParams.WRAP_CONTENT else 0
        if (!visible) {
            holder.itemView.isVisible = false
            return
        }
        if (enabled != holder.itemView.isEnabled) // Only set if different from ViewHolder
            holder.setEnabledState(enabled)

        var itemVisible = false
        holder.icon?.apply {
            itemVisible = true
            when {
                iconRes != -1 -> setImageResource(iconRes)
                icon != null -> setImageDrawable(icon)
                else -> {
                    setImageDrawable(null)
                    itemVisible = false
                }
            }
        }
        holder.iconFrame.apply {
            isVisible = itemVisible || !this@Preference.parent!!.collapseIcon
            if (isVisible && this is LinearLayout) {
                gravity = if (this@Preference.parent!!.centerIcon) Gravity.CENTER else Gravity.START or Gravity.CENTER_VERTICAL
            }
        }
        holder.title.apply {
            if (titleRes != -1) setText(titleRes) else text = title
        }
        holder.summary?.apply {
            itemVisible = true
            when {
                summaryRes != -1 -> setText(summaryRes)
                summary != null -> text = summary
                else -> {
                    text = null
                    itemVisible = false
                }
            }
            isVisible = itemVisible
        }
        holder.badge?.apply {
            itemVisible = true
            when {
                badgeRes != -1 -> setText(badgeRes)
                badge != null -> text = badge
                else -> {
                    text = null
                    itemVisible = false
                }
            }
            isVisible = itemVisible
        }
        holder.widgetFrame?.apply {
            isVisible = childCount > 0 && this@Preference !is SeekBarPreference
        }
        holder.itemView.isVisible = true
        if (highlightOnNextBind.getAndSet(false)) {
            val v = holder.itemView
            val highlightRunnable = Runnable {
                v.background.setHotspot(v.width / 2f, v.height / 2f)
                v.isPressed = true
                v.isPressed = false
            }
            v.postDelayed(highlightRunnable, 300)
            v.postDelayed(highlightRunnable, 600)
        }
    }

    fun requestRebind() {
        this.parent?.requestRebind(screenPosition)
    }

    fun requestRebindAndHighlight() {
        highlightOnNextBind.set(true)
        requestRebind()
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
        prefs?.edit {
            putInt(key, value)
        }
    }

    fun getInt(defaultValue: Int) = try {
        prefs?.getInt(key, defaultValue) ?: defaultValue
    } catch (e: ClassCastException) {
        defaultValue
    }

    /**
     * Save a boolean for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitBoolean(value: Boolean) {
        prefs?.edit {
            putBoolean(key, value)
        }
    }

    fun getBoolean(defaultValue: Boolean) = try {
        prefs?.getBoolean(key, defaultValue) ?: false
    } catch (e: ClassCastException) {
        defaultValue
    }

    /**
     * Save a String for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitString(value: String) {
        prefs?.edit {
            putString(key, value)
        }
    }

    fun getString(defaultValue: String) = try {
        prefs?.getString(key, defaultValue) ?: defaultValue
    } catch (e: ClassCastException) {
        defaultValue
    }

    /**
     * Can be set to [Preference.preBindListener]
     */
    interface OnPreBindListener {
        /**
         * Called before [Preference.bindViews], allows you to set data right before the [Preference][preference]
         * is bound to a view.
         * Note that you mustn't compute any data here, as you'll block the UI thread by doing that.
         */
        fun onPreBind(preference: Preference, holder: PreferencesAdapter.ViewHolder)
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
 * - Even though you can change the [enabled] or the [persistent] state, it doesn't have any effect in this instance
 */
class PreferenceScreen private constructor(builder: Builder) : Preference(builder.key) {
    internal val prefs = builder.prefs
    private val keyMap: Map<String, Preference> = builder.keyMap
    private val preferences: List<Preference> = builder.preferences
    internal val collapseIcon: Boolean = builder.collapseIcon
    internal val centerIcon: Boolean = builder.centerIcon

    internal var adapter: PreferencesAdapter? = null

    var scrollPosition = 0
    var scrollOffset = 0

    init {
        copyFrom(builder)
        for (i in preferences.indices)
            preferences[i].attachToScreen(this, i)
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

    /**
     * Find the index of the Preference with the supplied [key]
     *
     * @return the index or -1 if it wasn't found
     */
    fun indexOf(key: String): Int {
        if (!contains(key))
            return -1
        for (i in preferences.indices) {
            if (key == preferences[i].key)
                return i
        }
        throw IllegalStateException("Preference not found although it's in the keyMap, how could this happen??")
    }

    fun size() = preferences.size

    fun contains(key: String) = keyMap.containsKey(key)

    /**
     * Request rebind of the Preference in this screen with the specified [key]
     * No-op if this screen doesn't contain such preference
     */
    fun requestRebind(key: String) {
        val index = indexOf(key)
        if (index > 0)
            requestRebind(index)
    }

    internal fun requestRebind(position: Int, itemCount: Int = 1) {
        adapter?.notifyItemRangeChanged(position, itemCount)
    }

    class Builder private constructor(private var context: Context?, key: String) : AbstractPreference(key) {
        constructor(context: Context?) : this(context, KEY_ROOT_SCREEN)
        constructor(builder: Builder, key: String = "") : this(builder.context, key)

        /**
         * The filename to use for the [SharedPreferences] of this [PreferenceScreen]
         */
        var preferenceFileName: String = (context?.packageName ?: "package") + "_preferences"

        /**
         * If true, the preference items in this screen will have a smaller left padding when they have no icon
         */
        var collapseIcon: Boolean = false

        /**
         * Center the icon inside its keylines. If false, it will be aligned with a potential back arrow in the toolbar
         */
        var centerIcon: Boolean = true
        internal var prefs: SharedPreferences? = null
        internal val keyMap = HashMap<String, Preference>()
        internal val preferences = ArrayList<Preference>()

        private var collapsePreference: CollapsePreference? = null

        /**
         * Add the specified preference to this screen - it doesn't make sense to call this directly,
         * use the dsl helper methods like [pref][de.Maxr1998.modernpreferences.helpers.pref],
         * [switch][de.Maxr1998.modernpreferences.helpers.switch] and
         * [subScreen][de.Maxr1998.modernpreferences.helpers.subScreen] for this.
         */
        fun addPreferenceItem(p: Preference) {
            if (p.key == KEY_ROOT_SCREEN)
                throw UnsupportedOperationException("" +
                        "A screen with key '$KEY_ROOT_SCREEN' cannot be added as a sub-screen! " +
                        "If you are trying to add a sub-screen to your preferences model, " +
                        "use the `subScreen {}` function.")
            if (p.key.isEmpty() && p !is PreferenceScreen)
                throw UnsupportedOperationException("Preference key may not be empty!")

            if (p.key.isEmpty() || keyMap.put(p.key, p) == null)
                preferences.add(p)
            else throw UnsupportedOperationException("A preference with this key is already in the screen!")

            collapsePreference?.addItem(p)
        }

        fun collapseNext(key: String) {
            val collapse = CollapsePreference(key)
            addPreferenceItem(collapse)
            collapsePreference = collapse
        }

        fun collapseEnd() {
            collapsePreference = null
        }

        fun build(): PreferenceScreen {
            prefs = context?.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
            context = null
            return PreferenceScreen(this)
        }
    }
}