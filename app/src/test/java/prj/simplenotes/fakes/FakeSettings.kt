package prj.simplenotes.fakes

import prj.simplenotes.data.Settings

class FakeSettings: Settings {
    private val map = hashMapOf<String, Any>()

    override fun readInt(key: String, defaultValue: Int): Int {
        if (map.containsKey(key))
            return map[key] as Int
        else
            return defaultValue
    }

    override fun readFloat(key: String, defaultValue: Float): Float {
        if (map.containsKey(key))
            return map[key] as Float
        else
            return defaultValue
    }

    override fun readBoolean(key: String, defaultValue: Boolean): Boolean {
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

    override fun setOnChangedListener(value: Settings.OnChangedListener) {
        throw UnsupportedOperationException("FakeSettings.addOnChangedListener")
    }

    override fun removeOnChangedListener() {
        throw UnsupportedOperationException("FakeSettings.removeOnChangedListener")
    }
}