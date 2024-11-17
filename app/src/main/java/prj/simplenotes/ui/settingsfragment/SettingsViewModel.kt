package prj.simplenotes.ui.settingsfragment

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import prj.simplenotes.data.Settings

const val TXT_SIZE_COEFF_KEY = "TXT_SIZE_COEFF"
const val TXT_SIZE_COEFF_UNSET = 1f
const val TXT_COLOR_KEY = "TXT_COLOR"
const val BACKGROUND_KEY = "BACKGROUND_COLOR"
const val DEFAULT_BACKGROUND_KEY = "DEFAULT_BACKGROUND"
const val DEFAULT_BACKGROUND_VALUE = Color.WHITE

class SettingsViewModel(
    private val settings: Settings
) : ViewModel() {
    class Factory(
        private val settings: Settings,
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras): T {
            return SettingsViewModel(
                settings
            ) as T
        }
    }

    private val _textSize = MutableLiveData<Float>()
    val textSize: LiveData<Float> = _textSize

    private val _textColor = MutableLiveData<Int>()
    val textColor: LiveData<Int> = _textColor

    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private var _baseTextSize: Float = 0f

    fun updateTextColor(value: Int) {
        _textColor.value = value
        settings.write(TXT_COLOR_KEY, value)
    }

    fun updateBackground(value: Int) {
        _backgroundColor.value = value
        settings.write(BACKGROUND_KEY, value)
    }

    @TextSize
    var currentTextSizeIndex: Int = TEXT_SIZE_DEFAULT
        set(value) {
            if (field == value)
                return

            val textSizeCoefficient = when (value) {
                TEXT_SIZE_DEFAULT -> 1.0f
                TEXT_SIZE_BIG -> 1.2f
                else -> 1.6f
            }
            _textSize.value = textSizeCoefficient * _baseTextSize
            settings.write(TXT_SIZE_COEFF_KEY, textSizeCoefficient)
            field = value
        }

    fun init(
        @ColorInt currentTextColor: Int,
        @ColorInt currentBackground: Int,
        baseTextSize: Float) {
        _baseTextSize = baseTextSize

        val txtSizeCoefficient = settings.readFloat(TXT_SIZE_COEFF_KEY, TXT_SIZE_COEFF_UNSET)

        val index = coefficientToIndex(txtSizeCoefficient)
        val savedTextColor = settings.readInt(TXT_COLOR_KEY, currentTextColor)
        val savedBackground = settings.readInt(BACKGROUND_KEY, currentBackground)
        currentTextSizeIndex = index
        _textColor.value = savedTextColor
        _backgroundColor.value = savedBackground
    }

    @TextSize
    private fun coefficientToIndex(txtSizeCoefficient: Float): Int = when (txtSizeCoefficient) {
        1.0f -> TEXT_SIZE_DEFAULT
        1.2f -> TEXT_SIZE_BIG
        else -> TEXT_SIZE_BIGGEST
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    TEXT_SIZE_DEFAULT,
    TEXT_SIZE_BIG,
    TEXT_SIZE_BIGGEST,
)
annotation class TextSize

const val TEXT_SIZE_DEFAULT = 0
const val TEXT_SIZE_BIG = 1
const val TEXT_SIZE_BIGGEST = 2
