package prj.simplenotes.data

import android.content.SharedPreferences

interface Settings {
    fun readInt(key: String, defaultValue: Int): Int

    fun readFloat(key: String, defaultValue: Float): Float

    fun readBoolean(key: String, defaultValue: Boolean): Boolean

    fun write(key: String, value: Float)

    fun write(key: String, value: Int)

    fun write(key: String, value: Boolean)

    fun setOnChangedListener(value: OnChangedListener)

    fun removeOnChangedListener()

    interface OnChangedListener {
        fun onChanged(key: String)
    }
}

class AndroidSettings(
    private val prefs: SharedPreferences): Settings {
    private val onSharedPreferenceChangeListener = OnSharedPreferenceChangeListener()

    init {
        //no need to unsubscribe
        prefs.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun readFloat(key: String, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }

    override fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun write(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    override fun readInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun write(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun write(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun setOnChangedListener(value: Settings.OnChangedListener) {
        onSharedPreferenceChangeListener.onChangedListener = value
    }

    override fun removeOnChangedListener() {
        onSharedPreferenceChangeListener.onChangedListener = null
    }

    private class OnSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener {
        var onChangedListener: Settings.OnChangedListener? = null

        override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String?) {
            key ?: return
            onChangedListener?.onChanged(key)
        }
    }
}