package de.Maxr1998.modernpreferences.preferences.choice

import android.content.Context
import de.Maxr1998.modernpreferences.helpers.DISABLED_RESOURCE_ID

class SingleChoiceDialogPreference(
    key: String,
    items: List<SelectionItem>,
) : AbstractChoiceDialogPreference(key, items, false) {

    /**
     * The initial selection if no choice has been made and no value persisted
     * to [SharedPreferences][android.content.SharedPreferences] yet.
     *
     * Must match a [SelectionItem.key] in [items].
     */
    var initialSelection: String? = null

    var currentSelection: SelectionItem? = null
        internal set

    var selectionChangeListener: OnSelectionChangeListener? = null

    override fun onAttach() {
        super.onAttach()
        if (currentSelection == null) {
            resetSelection()
        }
    }

    override fun select(item: SelectionItem) {
        currentSelection = item
        selectionAdapter?.notifySelectionChanged()
    }

    override fun isSelected(item: SelectionItem): Boolean = item == currentSelection

    override fun persistSelection() {
        currentSelection?.let { selection ->
            if (selectionChangeListener?.onSelectionChange(this, selection.key) != false) {
                commitString(selection.key)
            }
        }
    }

    override fun resetSelection() {
        val persisted = getString() ?: initialSelection
        currentSelection = persisted?.let { items.find { item -> item.key == persisted } }
        selectionAdapter?.notifySelectionChanged()
    }

    override fun resolveSummary(context: Context): CharSequence? {
        val selection = currentSelection
        return when {
            autoGeneratedSummary && selection != null -> when {
                selection.titleRes != DISABLED_RESOURCE_ID -> context.resources.getText(selection.titleRes)
                else -> selection.title
            }
            else -> super.resolveSummary(context)
        }
    }

    fun interface OnSelectionChangeListener {
        /**
         * Notified when the selection of the connected [SingleChoiceDialogPreference] changes,
         * meaning after the user closes the dialog by pressing "ok".
         * This is called before the change gets persisted and can be prevented by returning false.
         *
         * @param selection the new selection
         *
         * @return true to commit the new selection to [SharedPreferences][android.content.SharedPreferences]
         */
        fun onSelectionChange(preference: SingleChoiceDialogPreference, selection: String): Boolean
    }
}