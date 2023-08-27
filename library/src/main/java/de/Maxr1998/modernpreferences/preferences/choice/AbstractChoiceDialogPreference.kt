package de.Maxr1998.modernpreferences.preferences.choice

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.helpers.DEFAULT_RES_ID
import de.Maxr1998.modernpreferences.preferences.DialogPreference

abstract class AbstractChoiceDialogPreference(
    key: String,
    protected val items: List<SelectionItem>,
    private val allowMultiSelect: Boolean,
) : DialogPreference(key) {

    internal var selectionAdapter: SelectionAdapter? = null

    var onItemClickListener: OnItemClickListener? = null

    /**
     * Whether the summary should be auto-generated from the current selection.
     * If true, [summary] and [summaryRes] are ignored.
     *
     * Default true, set to false to turn off this feature.
     */
    var autoGeneratedSummary = true

    init {
        require(items.isNotEmpty()) { "Supplied list of items may not be empty!" }
    }

    override fun createDialog(context: Context): Dialog = Config.dialogBuilderFactory(context).apply {
        if (titleRes != DEFAULT_RES_ID) setTitle(titleRes) else setTitle(title)
        val dialogContent = RecyclerView(context).apply {
            selectionAdapter = SelectionAdapter(
                this@AbstractChoiceDialogPreference,
                items,
                allowMultiSelect,
            )
            adapter = selectionAdapter
            layoutManager = LinearLayoutManager(context)
        }
        setView(dialogContent)
        setCancelable(false)
        setPositiveButton(android.R.string.ok) { _, _ ->
            persistSelection()
            requestRebind()
        }
        setNegativeButton(android.R.string.cancel) { _, _ ->
            resetSelection()
        }
    }.create()

    internal fun shouldSelect(item: SelectionItem): Boolean {
        return onItemClickListener?.onItemSelected(item) ?: true
    }

    internal abstract fun select(item: SelectionItem)

    protected abstract fun persistSelection()

    protected abstract fun resetSelection()

    abstract fun isSelected(item: SelectionItem): Boolean

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        super.onStateChanged(source, event)
        if (event == Lifecycle.Event.ON_DESTROY) selectionAdapter = null
    }
}