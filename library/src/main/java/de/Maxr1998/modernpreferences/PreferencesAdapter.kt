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

import android.preference.Preference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.helpers.emptyScreen
import de.Maxr1998.modernpreferences.preferences.CategoryHeader
import de.Maxr1998.modernpreferences.preferences.CollapsePreference
import java.util.*

class PreferencesAdapter(root: PreferenceScreen? = null) : RecyclerView.Adapter<PreferencesAdapter.ViewHolder>() {

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

    init {
        root?.let(::setRootScreen)
    }

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
            val oldScreen = screenStack.pop() // ...remove current screen from stack
            currentScreen.adapter = this
            notifyDataSetChanged()
            onScreenChangeListener?.onScreenChanged(currentScreen, isInSubScreen())
            for (i in 0 until oldScreen.size()) {
                val p = oldScreen[i]
                if (p.javaClass == CollapsePreference::class.java)
                    (p as CollapsePreference).reset()
            }
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
     * Restores the last scroll position if needed and (re-)attaches this adapter's scroll listener
     */
    fun restoreAndObserveScrollPosition(preferenceView: RecyclerView) {
        (preferenceView.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(currentScreen.scrollPosition, currentScreen.scrollOffset)
        preferenceView.addOnScrollListener(scrollListener)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val position = layoutManager.findFirstCompletelyVisibleItemPosition()
            val top = recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.top ?: 0
            currentScreen.scrollPosition = position
            currentScreen.scrollOffset = top
        }
    }

    /**
     * Common ViewHolder in [PreferencesAdapter] for every [Preference] object/every preference extending it
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: ViewGroup get() = itemView as ViewGroup
        val iconFrame: View = itemView.findViewById(R.id.icon_frame)
        val icon: ImageView? = itemView.findViewById(android.R.id.icon)
        val title: TextView = itemView.findViewById(android.R.id.title)
        val summary: TextView? = itemView.findViewById(android.R.id.summary)
        val widget: View? = itemView.findViewById<ViewGroup>(R.id.widget_frame)?.getChildAt(0)

        internal fun setEnabledState(enabled: Boolean) {
            setEnabledStateRecursive(itemView, enabled)
        }

        private fun setEnabledStateRecursive(v: View, enabled: Boolean) {
            v.isEnabled = enabled
            if (v is ViewGroup) {
                for (i in v.childCount - 1 downTo 0) {
                    setEnabledStateRecursive(v[i], enabled)
                }
            }
        }
    }

    /**
     * An interface to notify observers in [PreferencesAdapter] of screen change events,
     * when a sub-screen was opened or closed
     */
    interface OnScreenChangeListener {
        fun onScreenChanged(screen: PreferenceScreen, subScreen: Boolean)
    }
}