package prj.simplenotes.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import prj.simplenotes.data.Settings

class FakeSettings: Settings {
    private val map = hashMapOf<String, Any>()

    override suspend fun readInt(key: String, defaultValue: Int): Int {
        if (map.containsKey(key))
            return map[key] as Int
        else
            return defaultValue
    }

    override fun readFloat(key: String, defaultValue: Float): Flow<Float> {
        return flow {
            emit(map[key] as? Float ?: defaultValue)
        }
    }

    override suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        if (map.containsKey(key))
            return map[key] as Boolean
        else
            return defaultValue
    }

    override fun write(key: String, value: Float) {
        map[key] = value
    }

    override fun write(key: String, value: Int) {
        map[key] = value
    }

    override fun write(key: String, value: Boolean) {
        map[key] = value
    }

    override fun setOnExceptionListener(value: Settings.OnExceptionListener?) {
        //noop
    }
}