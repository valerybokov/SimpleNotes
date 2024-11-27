package prj.simplenotes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface Settings {
    suspend fun readInt(key: String, defaultValue: Int): Int

    fun readFloat(key: String, defaultValue: Float): Flow<Float>

    suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean

    fun write(key: String, value: Float)

    fun write(key: String, value: Int)

    fun write(key: String, value: Boolean)
}

class AndroidSettings(
    private val appContext: Context): Settings {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
    private var scope: CoroutineScope? = null

    fun setScope(value: CoroutineScope?) {
        scope = value
    }

    override fun readFloat(key: String, defaultValue: Float): Flow<Float> {
        return appContext.dataStore.data.map { prefs ->
            val prefsKey = floatPreferencesKey(key)
            prefs[prefsKey] ?: defaultValue
        }
    }

    override suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        val prefsKey = booleanPreferencesKey(key)
        val prefs = appContext.dataStore.data.first()
        return prefs[prefsKey] ?: defaultValue
    }

    override fun write(key: String, value: Float) {
        scope?.launch(Dispatchers.IO) {
            appContext.dataStore.edit { prefs ->
                val prefsKey = floatPreferencesKey(key)
                prefs[prefsKey] = value
            }
        }
    }

    override suspend fun readInt(key: String, defaultValue: Int): Int {
        val prefsKey = intPreferencesKey(key)
        val prefs = appContext.dataStore.data.first()
        return prefs[prefsKey] ?: defaultValue
    }

    override fun write(key: String, value: Int) {
        scope?.launch(Dispatchers.IO) {
            appContext.dataStore.edit { prefs ->
                val prefsKey = intPreferencesKey(key)
                prefs[prefsKey] = value
            }
        }
    }

    override fun write(key: String, value: Boolean) {
        scope?.launch(Dispatchers.IO) {
            appContext.dataStore.edit { prefs ->
                val prefsKey = booleanPreferencesKey(key)
                prefs[prefsKey] = value
            }
        }
    }
}