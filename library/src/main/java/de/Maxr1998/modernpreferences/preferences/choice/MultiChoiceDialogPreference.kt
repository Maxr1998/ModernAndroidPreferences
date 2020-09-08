package de.Maxr1998.modernpreferences.preferences.choice

import android.content.Context

class MultiChoiceDialogPreference(key: String, items: List<SelectionItem>) : AbstractChoiceDialogPreference(key, items, true) {

    /**
     * The initial selections if no choice has been made yet and no value
     * was persisted to [SharedPreferences][android.content.SharedPreferences]
     */
    val initialSelections: Set<String> = HashSet()

    val currentSelections: MutableSet<SelectionItem> = HashSet()

    override fun onAttach() {
        super.onAttach()
        if (currentSelections.isEmpty())
            resetSelection()
    }

    override fun select(item: SelectionItem) {
        if (!currentSelections.add(item)) {
            currentSelections.remove(item)
        }
        selectionAdapter?.notifySelectionChanged()
    }

    override fun isSelected(item: SelectionItem): Boolean = item in currentSelections

    override fun persistSelection() {
        val resultSet = HashSet<String>()
        currentSelections.mapTo(resultSet, SelectionItem::key)
        commitStringSet(resultSet)
    }

    override fun resetSelection() {
        val persisted = getStringSet() ?: initialSelections
        currentSelections.clear()
        currentSelections += persisted.mapNotNull { key -> items.find { item -> item.key == key } }
        selectionAdapter?.notifySelectionChanged()
    }

    override fun resolveSummary(context: Context): CharSequence? = currentSelections.joinToString(limit = 3, truncated = "…") { selection ->
        when {
            selection.titleRes != -1 -> context.resources.getText(selection.titleRes)
            else -> selection.title
        }
    }
}