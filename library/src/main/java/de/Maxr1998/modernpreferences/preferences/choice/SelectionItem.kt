package de.Maxr1998.modernpreferences.preferences.choice

import androidx.annotation.StringRes
import de.Maxr1998.modernpreferences.helpers.DEFAULT_RES_ID

/**
 * Represents a selectable item in a selection dialog preference,
 * e.g. the [SingleChoiceDialogPreference]
 *
 * @param key The key of this item, will be committed to preferences if selected
 */
@Suppress("DataClassPrivateConstructor")
data class SelectionItem private constructor(
    val key: String,
    @StringRes
    val titleRes: Int,
    val title: CharSequence,
    @StringRes
    val summaryRes: Int,
    val summary: CharSequence?,
    @StringRes
    val badgeRes: Int,
    val badge: CharSequence? = null,
) {
    /**
     * @see SelectionItem
     */
    constructor(
        key: String,
        @StringRes titleRes: Int,
        @StringRes summaryRes: Int = DEFAULT_RES_ID,
        @StringRes badgeRes: Int = DEFAULT_RES_ID,
    ) :
        this(key, titleRes, "", summaryRes, null, badgeRes, null)

    /**
     * @see SelectionItem
     */
    constructor(key: String, title: CharSequence, summary: CharSequence? = null, badge: CharSequence? = null) :
        this(key, DEFAULT_RES_ID, title, DEFAULT_RES_ID, summary, DEFAULT_RES_ID, badge)
}