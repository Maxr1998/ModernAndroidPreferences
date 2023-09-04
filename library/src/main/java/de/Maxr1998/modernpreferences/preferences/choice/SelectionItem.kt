package de.Maxr1998.modernpreferences.preferences.choice

import androidx.annotation.StringRes
import de.Maxr1998.modernpreferences.helpers.DEFAULT_RES_ID
import de.Maxr1998.modernpreferences.preferences.Badge

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
    val badge: Badge?,
) {
    /**
     * @see SelectionItem
     */
    constructor(
        key: String,
        @StringRes
        titleRes: Int,
        @StringRes
        summaryRes: Int = DEFAULT_RES_ID,
        badge: Badge? = null,
    ) : this(
        key = key,
        titleRes = titleRes,
        title = "",
        summaryRes = summaryRes,
        summary = null,
        badge = badge,
    )

    /**
     * @see SelectionItem
     */
    constructor(
        key: String,
        title: CharSequence,
        summary: CharSequence? = null,
        badge: Badge? = null,
    ) : this(
        key = key,
        titleRes = DEFAULT_RES_ID,
        title = title,
        summaryRes = DEFAULT_RES_ID,
        summary = summary,
        badge = badge,
    )
}