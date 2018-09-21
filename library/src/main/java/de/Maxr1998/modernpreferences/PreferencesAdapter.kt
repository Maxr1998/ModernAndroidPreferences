package de.Maxr1998.modernpreferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.helpers.emptyScreen
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import java.util.*

class PreferencesAdapter : RecyclerView.Adapter<PreferencesAdapter.ViewHolder>() {

    private val screenStack: Stack<PreferenceScreen> = Stack<PreferenceScreen>().apply {
        push(emptyScreen)
    }

    val currentScreen: PreferenceScreen
        get() = screenStack.peek()

    /**
     * Listener which will be notified of screen change events
     */
    var onScreenChangeListener: OnScreenChangeListener? = null

    var secondScreenAdapter: PreferencesAdapter? = null

    fun setRootScreen(root: PreferenceScreen) {
        currentScreen.adapter = null
        while (screenStack.peek() != emptyScreen) {
            screenStack.pop()
        }
        screenStack.push(root)
        currentScreen.adapter = this
        notifyDataSetChanged()
        onScreenChangeListener?.onScreenChanged(root, false)
    }

    private fun openScreen(screen: PreferenceScreen) {
        secondScreenAdapter?.setRootScreen(screen) ?: /* ELSE */ run {
            currentScreen.adapter = null
            screenStack.push(screen)
            currentScreen.adapter = this
            notifyDataSetChanged()
        }
        onScreenChangeListener?.onScreenChanged(screen, true)
    }

    fun isInSubScreen() = screenStack.size > 2

    /**
     * If possible, return to the previous screen
     *
     * @return true if it returned to an earlier screen, false if we're already at the root
     */
    fun goBack(): Boolean {
        if (secondScreenAdapter?.goBack() == true) // Check if the second screen can still go back
            return true
        currentScreen.adapter = null
        if (isInSubScreen()) { // If we're in a sub-screen...
            screenStack.pop() // ...remove current screen from stack
            currentScreen.adapter = this
            notifyDataSetChanged()
            onScreenChangeListener?.onScreenChanged(currentScreen, isInSubScreen())
            return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (viewType == CategoryHeader.RESOURCE_CONST) R.layout.preference_category else R.layout.preference
        val view = layoutInflater.inflate(layout, parent, false)
        if (viewType > 0)
            layoutInflater.inflate(viewType, view.findViewById(R.id.widget_frame), true)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pref = currentScreen[position]
        pref.bindViews(holder)

        holder.itemView.setOnClickListener {
            // Item was clicked, check enabled state (not for PreferenceScreens) and send click event
            if (pref is PreferenceScreen) {
                openScreen(pref)
            } else if (!pref.enabled) return@setOnClickListener

            pref.performClick(holder)
        }
    }

    override fun getItemCount() = currentScreen.size()

    @LayoutRes
    override fun getItemViewType(position: Int) = currentScreen[position].getWidgetLayoutResource()

    /**
     * Common ViewHolder in [PreferencesAdapter] for every [Preference] object/every preference extending it
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconFrame: View = itemView.findViewById(R.id.icon_frame)
        val icon: ImageView? = itemView.findViewById(android.R.id.icon)
        val title: TextView = itemView.findViewById(android.R.id.title)
        val summary: TextView? = itemView.findViewById(android.R.id.summary)
        val widget: View? = itemView.findViewById<ViewGroup>(R.id.widget_frame)?.getChildAt(0)
    }

    /**
     * An interface to notify observers in [PreferencesAdapter] of screen change events,
     * when a sub-screen was opened or closed
     */
    interface OnScreenChangeListener {
        fun onScreenChanged(screen: PreferenceScreen, subScreen: Boolean)
    }
}