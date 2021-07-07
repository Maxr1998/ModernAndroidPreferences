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

package de.Maxr1998.modernpreferences.preferences

import android.app.Dialog
import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferencesAdapter

/**
 * DialogPreference is a helper class to display custom [Dialog]s on preference clicks.
 * It is recommended to override this class once and then inflate different dialogs
 * based on their keys in [createDialog].
 *
 * Also, the context which this preference's views get attached to should be a [LifecycleOwner].
 */
abstract class DialogPreference(key: String) : Preference(key), LifecycleObserver {

    private var dialog: Dialog? = null

    /**
     * This flag tells the preference whether to recreate the dialog after a configuration change
     */
    private var recreateDialog = false

    var style: Int = 0

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        super.bindViews(holder)
        holder.itemView.context.apply {
            if (this is LifecycleOwner) lifecycle.addObserver(this@DialogPreference)
        }

        if (recreateDialog) {
            recreateDialog = false
            onClick(holder)
        }
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        dialog = (dialog ?: createDialog(holder.itemView.context, style)).apply {
            show()
        }
    }

    /**
     * Subclasses must create the dialog which will managed by this preference here.
     * However, they should not [show][Dialog.show] it already, that will be done in [onClick].
     *
     * @param context the context to create your Dialog with, has a window attached
     */
    abstract fun createDialog(context: Context): Dialog

    /**
     * Subclasses must create the dialog which will managed by this preference here.
     * However, they should not [show][Dialog.show] it already, that will be done in [onClick].
     *
     * @param context the context to create your Dialog with, has a window attached
     * @param style the style passed to the Dialog
     */
    abstract fun createDialog(context: Context, style: Int): Dialog

    /**
     * Dismiss the currently attached dialog, if any
     */
    fun dismiss() = dialog?.dismiss()

    /**
     * Used internally to react to lifecycle changes
     */
    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
        dialog?.apply {
            recreateDialog = isShowing
            dismiss()
        }
        dialog = null
    }
}