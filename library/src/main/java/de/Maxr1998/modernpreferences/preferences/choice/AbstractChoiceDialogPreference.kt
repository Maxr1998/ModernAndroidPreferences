package de.Maxr1998.modernpreferences.preferences.choice

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.preferences.DialogPreference


abstract class AbstractChoiceDialogPreference(
    key: String,
    protected val items: List<SelectionItem>,
    private val allowMultiSelect: Boolean,
) : DialogPreference(key) {

    internal var selectionAdapter: SelectionAdapter? = null

    init {
        require(items.isNotEmpty()) { "Supplied list of items may not be empty!" }
    }

    override fun createDialog(context: Context): Dialog = AlertDialog.Builder(context).apply {
        if (titleRes != -1) setTitle(titleRes) else setTitle(title)
        setView(RecyclerView(context).apply {
            selectionAdapter = SelectionAdapter(this@AbstractChoiceDialogPreference, items, allowMultiSelect)
            adapter = selectionAdapter
            layoutManager = LinearLayoutManager(context)
        })
        setCancelable(false)
        setPositiveButton(android.R.string.ok) { _, _ ->
            persistSelection()
            requestRebind()
        }
        setNegativeButton(android.R.string.cancel) { _, _ ->
            resetSelection()
        }
    }.create()

    internal abstract fun select(item: SelectionItem)

    protected abstract fun persistSelection()

    protected abstract fun resetSelection()

    abstract fun isSelected(item: SelectionItem): Boolean

    override fun onStop() {
        super.onStop()
        selectionAdapter = null
    }
}