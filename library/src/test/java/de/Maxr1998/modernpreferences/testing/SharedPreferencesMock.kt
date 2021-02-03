/**
 * Taken from https://github.com/IvanShafran/shared-preferences-mock
 *
 * Copyright (c) 2019 Ivan Shafran
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.Maxr1998.modernpreferences.testing

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

class SharedPreferencesMock : SharedPreferences {
    private val preferencesMap: MutableMap<String, Any?> = HashMap()
    private val listeners: MutableSet<OnSharedPreferenceChangeListener> = HashSet()
    override fun getAll(): Map<String, *> {
        return HashMap(preferencesMap)
    }

    override fun getString(key: String, defValue: String?): String? {
        val string = preferencesMap[key] as String?
        return string ?: defValue
    }

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        val stringSet = preferencesMap[key] as Set<String>?
        return stringSet ?: defValues
    }

    override fun getInt(key: String, defValue: Int): Int {
        val integer = preferencesMap[key] as Int?
        return integer ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        val longValue = preferencesMap[key] as Long?
        return longValue ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val floatValue = preferencesMap[key] as Float?
        return floatValue ?: defValue
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val booleanValue = preferencesMap[key] as Boolean?
        return booleanValue ?: defValue
    }

    override fun contains(key: String): Boolean {
        return preferencesMap.containsKey(key)
    }

    override fun edit(): Editor {
        return EditorImpl()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        listeners.remove(listener)
    }

    open inner class EditorImpl : Editor {
        private val newValuesMap: MutableMap<String, Any?> = HashMap()
        private var shouldClear = false
        override fun putString(key: String, value: String?): Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>?): Editor {
            newValuesMap[key] = values?.let { HashSet(it) }
            return this
        }

        override fun putInt(key: String, value: Int): Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): Editor {
            newValuesMap[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): Editor {
            newValuesMap[key] = value
            return this
        }

        override fun remove(key: String): Editor {
            // 'this' is marker for remove operation
            newValuesMap[key] = this
            return this
        }

        override fun clear(): Editor {
            shouldClear = true
            return this
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun apply() {
            clearIfNeeded()
            val changedKeys = applyNewValues()
            notifyListeners(changedKeys)
        }

        private fun clearIfNeeded() {
            if (shouldClear) {
                shouldClear = false
                preferencesMap.clear()
            }
        }

        /** @return changed keys list
         */
        private fun applyNewValues(): MutableList<String> {
            val changedKeys: MutableList<String> = ArrayList()
            for ((key, value) in newValuesMap) {
                val isSomethingChanged = if (isRemoveValue(value)) {
                    removeIfNeeded(key)
                } else {
                    putValueIfNeeded(key, value)
                }
                if (isSomethingChanged) {
                    changedKeys.add(key)
                }
            }
            newValuesMap.clear()
            return changedKeys
        }

        private fun isRemoveValue(value: Any?): Boolean {
            // 'this' is marker for remove operation
            return value === this || value == null
        }

        /** @return true if element was removed
         */
        private fun removeIfNeeded(key: String): Boolean {
            return if (preferencesMap.containsKey(key)) {
                preferencesMap.remove(key)
                true
            } else {
                false
            }
        }

        /** @return true if element was changed
         */
        private fun putValueIfNeeded(key: String, value: Any?): Boolean {
            if (preferencesMap.containsKey(key)) {
                val oldValue = preferencesMap[key]
                if (value == oldValue) {
                    return false
                }
            }
            preferencesMap[key] = value
            return true
        }

        private fun notifyListeners(changedKeys: MutableList<String>) {
            for (i in changedKeys.indices.reversed()) {
                for (listener in listeners) {
                    listener.onSharedPreferenceChanged(this@SharedPreferencesMock, changedKeys[i])
                }
            }
            changedKeys.clear()
        }
    }
}