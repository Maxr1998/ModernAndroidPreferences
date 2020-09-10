package de.Maxr1998.modernpreferences.preferences.choice

import android.content.Context

class MultiChoiceDialogPreference(key: String, items: List<SelectionItem>) : AbstractChoiceDialogPreference(key, items, true) {

    /**
     * The initial selections if no choice has been made yet and no value
     * was persisted to [SharedPreferences][android.content.SharedPreferences]
     */
    var initialSelections: Set<String>? = null

    private val selections: MutableSet<SelectionItem> = HashSet()

    val currentSelections: Set<SelectionItem>
        get() = HashSet(selections)

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
        commitStringSet(resultSet)
    }

    override fun resetSelection() {
        val persisted = getStringSet() ?: initialSelections?.toList() ?: emptyList()
        selections.clear()
        selections += persisted.mapNotNull { key -> items.find { item -> item.key == key } }
        selectionAdapter?.notifySelectionChanged()
    }

    override fun resolveSummary(context: Context): CharSequence? = selections.joinToString(limit = 3, truncated = "…") { selection ->
        when {
            selection.titleRes != -1 -> context.resources.getText(selection.titleRes)
            else -> selection.title
        }
    }
}