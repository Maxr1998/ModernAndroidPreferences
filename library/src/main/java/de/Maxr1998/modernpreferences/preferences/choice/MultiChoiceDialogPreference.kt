package de.Maxr1998.modernpreferences.preferences.choice

import android.content.Context

class MultiChoiceDialogPreference(key: String, items: List<SelectionItem>, default: Set<String>?) : AbstractChoiceDialogPreference(key, items, true) {

    /**
     * The initial selections if no choice has been made yet and no value
     * was persisted to [SharedPreferences][android.content.SharedPreferences]
     * Using the provided default value, if no default value provided, select none by default
     */
    var initialSelections: Set<String>? = default

    private val selections: MutableSet<SelectionItem> = HashSet()

    val currentSelections: Set<SelectionItem>
        get() = HashSet(selections)

    var selectionChangeListener: OnSelectionChangeListener? = null

    override fun onAttach() {
        super.onAttach()
        if (selections.isEmpty())
            resetSelection()
    }

    override fun select(item: SelectionItem) {
        if (!selections.add(item)) {
            selections.remove(item)
        }
        selectionAdapter?.notifySelectionChanged()
    }

    override fun isSelected(item: SelectionItem): Boolean = item in selections

    override fun persistSelection() {
        val resultSet = HashSet<String>()
        selections.mapTo(resultSet, SelectionItem::key)
        if (selectionChangeListener?.onSelectionChange(this, HashSet(resultSet)) != false)
            commitStringSet(resultSet)
    }

    override fun resetSelection() {
        val persisted = getStringSet() ?: initialSelections?.toList() ?: emptyList()
        selections.clear()
        selections += persisted.mapNotNull { key -> items.find { item -> item.key == key } }
        selectionAdapter?.notifySelectionChanged()
    }

    override fun resolveSummary(context: Context): CharSequence? = when {
        selections.isNotEmpty() -> selections.joinToString(limit = 3, truncated = "…") { selection ->
            when {
                selection.titleRes != -1 -> context.resources.getText(selection.titleRes)
                else -> selection.title
            }
        }
        else -> super.resolveSummary(context)
    }

    fun interface OnSelectionChangeListener {
        /**
         * Notified when the selection of the connected [MultiChoiceDialogPreference] changes,
         * meaning after the user closes the dialog by pressing "ok".
         * This is called before the change gets persisted and can be prevented by returning false.
         *
         * @param selection the new selection
         *
         * @return true to commit the new selection to [SharedPreferences][android.content.SharedPreferences]
         */
        fun onSelectionChange(preference: MultiChoiceDialogPreference, selection: Set<String>): Boolean
    }
}