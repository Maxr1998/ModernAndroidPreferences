package de.Maxr1998.modernpreferences.storage

interface Storage {

    fun setInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int): Int

    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun setString(key: String, value: String)
    fun getString(key: String, defaultValue: String?): String?

    fun setStringSet(key: String, values: Set<String>)
    fun getStringSet(key: String, defaultValue: Set<String>?): Set<String>?
}