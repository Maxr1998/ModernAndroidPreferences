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
import de.Maxr1998.modernpreferences.helpers.DependencyManager
import de.Maxr1998.modernpreferences.helpers.KEY_ROOT_SCREEN
import de.Maxr1998.modernpreferences.helpers.PreferenceMarker
import de.Maxr1998.modernpreferences.preferences.CollapsePreference
import de.Maxr1998.modernpreferences.preferences.SeekBarPreference
import de.Maxr1998.modernpreferences.storage.SharedPreferencesStorage
import de.Maxr1998.modernpreferences.storage.Storage
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractPreference internal constructor(val key: String) {
    // UI
    @StringRes
    var titleRes: Int = -1
    var title: CharSequence = ""

    @StringRes
    var summaryRes: Int = -1
    var summary: CharSequence? = null

    @StringRes
    var summaryDisabledRes: Int = -1
    var summaryDisabled: CharSequence? = null

    @DrawableRes
    var iconRes: Int = -1
    var icon: Drawable? = null

    @StringRes
    var badgeRes: Int = -1
    var badge: CharSequence? = null

    // State
    var visible = true

    internal fun copyFrom(other: AbstractPreference) {
        title = other.title
        titleRes = other.titleRes
        summary = other.summary
        summaryRes = other.summaryRes
        summaryDisabled = other.summaryDisabled
        summaryDisabledRes = other.summaryDisabledRes
        icon = other.icon
        iconRes = other.iconRes
        badge = other.badge
        badgeRes = other.badgeRes

        visible = other.visible
    }

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        this === other -> true
        this::class.java == other::class.java && key == (other as AbstractPreference).key -> true
        else -> false
    }

    override fun hashCode() = key.hashCode()
}

/**
 * The base class for the Preference system - it corresponds to a single item in the list, regardless
 * if [switch][de.Maxr1998.modernpreferences.preferences.SwitchPreference],
 * [category header][de.Maxr1998.modernpreferences.preferences.CategoryHeader] or
 * [sub-screen][PreferenceScreen].
 *
 * Further (global) configuration options can be found in [Preference.Config].
 */
@PreferenceMarker
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

    private var storage: Storage? = null

    /**
     * Whether or not to persist changes to this preference to the attached [SharedPreferences] instance
     */
    var persistent: Boolean = true

    private var highlightOnNextBind = AtomicBoolean(false)

    var includeInCollapseSummary = true

    @LayoutRes
    open fun getWidgetLayoutResource(): Int {
        return -1
    }

    internal fun attachToScreen(screen: PreferenceScreen, position: Int) {
        check(parent == null) { "Preference was already attached to a screen!" }
        parent = screen
        screenPosition = position
        storage = if (persistent) screen.storage else null
        DependencyManager.register(this)
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

    protected open fun resolveSummary(context: Context): CharSequence? = when {
        !enabled && summaryDisabledRes != -1 -> context.resources.getText(summaryDisabledRes)
        !enabled && summaryDisabled != null -> summaryDisabled
        summaryRes != -1 -> context.resources.getText(summaryRes)
        summary != null -> summary
        else -> null
    }

    /**
     * Binds the preference-data to its views from the [view holder][PreferencesAdapter.ViewHolder]
     * Don't call this yourself, it will get called from the [PreferencesAdapter].
     */
    @CallSuper
    open fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        val preferenceParent = checkNotNull(parent) {
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
            isVisible = itemVisible || !preferenceParent.collapseIcon
            if (isVisible && this is LinearLayout) {
                gravity = if (preferenceParent.centerIcon) Gravity.CENTER else Gravity.START or Gravity.CENTER_VERTICAL
            }
        }
        holder.title.apply {
            if (titleRes != -1) setText(titleRes) else text = title
            maxLines = Config.titleMaxLines
        }
        holder.summary?.apply {
            val summary = resolveSummary(context)
            text = summary
            maxLines = Config.summaryMaxLines
            isVisible = summary != null
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
        parent?.requestRebind(screenPosition)
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
        storage?.setInt(key, value)
    }

    fun getInt(defaultValue: Int): Int =
        storage?.getInt(key, defaultValue) ?: defaultValue

    /**
     * Save a boolean for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitBoolean(value: Boolean) {
        storage?.setBoolean(key, value)
    }

    fun getBoolean(defaultValue: Boolean): Boolean =
        storage?.getBoolean(key, defaultValue) ?: defaultValue

    /**
     * Save a String for this [Preference]s' [key] to the [SharedPreferences] of the attached [PreferenceScreen]
     */
    fun commitString(value: String) {
        storage?.setString(key, value)
    }

    fun getString(): String? = storage?.getString(key, null)

    @Deprecated(
        "Passing a default value is not supported anymore, " +
                "use the nullable replacement getString() and an elvis operator",
        ReplaceWith("getString() ?: defaultValue"),
        DeprecationLevel.ERROR
    )
    fun getString(defaultValue: String): String = throw UnsupportedOperationException("Not implemented")

    fun commitStringSet(values: Set<String>) {
        storage?.setStringSet(key, values)
    }

    fun getStringSet(): Set<String>? = storage?.getStringSet(key, null)

    /**
     * Can be set to [Preference.preBindListener]
     */
    fun interface OnPreBindListener {
        /**
         * Called before [Preference.bindViews], allows you to set data right before the [Preference][preference]
         * is bound to a view.
         * Note that you mustn't compute any data here, as you'll block the UI thread by doing that.
         */
        fun onPreBind(preference: Preference, holder: PreferencesAdapter.ViewHolder)
    }

    fun interface OnClickListener {
        /**
         * Notified when the connected [Preference] is clicked
         *
         * @return true if the preference changed and needs to update its views
         */
        fun onClick(preference: Preference, holder: PreferencesAdapter.ViewHolder): Boolean
    }

    /**
     * Global configuration object that allows to apply settings to *all* [Preference] instances
     */
    object Config {
        /**
         * Override the maximum allowed number of lines for the title text.
         *
         * 1 by default, allowed values are 1 to 3.
         */
        var titleMaxLines: Int = 1
            set(value) {
                require(value in 1..3) { "titleMaxLines must be within [1,3]" }
                field = value
            }

        /**
         * Override the maximum allowed number of lines for the summary text.
         *
         * 3 by default, allowed values are 1 to 5.
         */
        var summaryMaxLines: Int = 3
            set(value) {
                require(value in 1..5) { "subtitleMaxLines must be within [1,5]" }
                field = value
            }
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
@Suppress("unused", "MemberVisibilityCanBePrivate")
class PreferenceScreen private constructor(builder: Builder) : Preference(builder.key) {
    internal val storage = builder.storage
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
        if (key in this) {
            for (i in preferences.indices) {
                if (key == preferences[i].key) return i
            }
        }
        return -1
    }

    fun size() = preferences.size

    operator fun contains(key: String) = keyMap.containsKey(key)

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

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        this === other -> true
        this::class.java == other::class.java
                && key == (other as PreferenceScreen).key
                && preferences == other.preferences -> true
        else -> false
    }

    override fun hashCode() = (31 * key.hashCode()) + preferences.hashCode()

    @PreferenceMarker
    class Builder private constructor(private var context: Context?, internal val storage: Storage?, key: String) : AbstractPreference(key), Appendable {
        constructor(context: Context?, storage: Storage?) : this(context, storage, KEY_ROOT_SCREEN)
        constructor(builder: Builder, key: String = "") : this(builder.context, builder.storage, key)
        constructor(collapse: CollapsePreference, key: String = "") : this(collapse.screen?.context, collapse.screen?.storage, key)

        // Internal structures
        internal val keyMap = HashMap<String, Preference>()
        internal val preferences = ArrayList<Preference>()

        /**
         * If true, the preference items in this screen will have a smaller left padding when they have no icon
         */
        var collapseIcon: Boolean = false

        /**
         * Center the icon inside its keylines. If false, it will be aligned with a potential back arrow in the toolbar
         */
        var centerIcon: Boolean = true

        /**
         * Add the specified preference to this screen - it doesn't make sense to call this directly,
         * use the dsl helper methods like [pref][de.Maxr1998.modernpreferences.helpers.pref],
         * [switch][de.Maxr1998.modernpreferences.helpers.switch] and
         * [subScreen][de.Maxr1998.modernpreferences.helpers.subScreen] for this.
         */
        override fun addPreferenceItem(p: Preference) {
            if (p.key == KEY_ROOT_SCREEN) {
                val message by lazy {
                    """
                    A screen with key '$KEY_ROOT_SCREEN' cannot be added as a sub-screen!
                    If you are trying to add a sub-screen to your preferences model,
                    use the `subScreen {}` function.
                    """.trimIndent()
                }
                throw UnsupportedOperationException(message)
            }
            if (p.key.isEmpty() && p !is PreferenceScreen)
                throw UnsupportedOperationException("Preference key may not be empty!")
            if (p.key.isNotEmpty() && keyMap.put(p.key, p) != null)
                throw UnsupportedOperationException("A preference with this key is already in the screen!")

            preferences.add(p)
        }

        fun build(): PreferenceScreen {
            context = null
            return PreferenceScreen(this)
        }
    }

    fun interface Appendable {
        fun addPreferenceItem(p: Preference)

        operator fun plusAssign(p: Preference) {
            addPreferenceItem(p)
        }

        operator fun <T : Preference> T.unaryPlus(): T {
            addPreferenceItem(this)
            return this
        }
    }
}