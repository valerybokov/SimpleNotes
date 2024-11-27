package prj.simplenotes.ui.settingsfragment

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    private var _isLoading = true

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

    val currentTextSizeIndex: Flow<Int> = settings.readFloat(TXT_SIZE_COEFF_KEY, TXT_SIZE_COEFF_UNSET)
        .map { txtSizeCoefficient ->
        coefficientToIndex(txtSizeCoefficient)
    }

    fun init(
        @ColorInt currentTextColor: Int,
        @ColorInt currentBackground: Int,
        baseTextSize: Float) {
        _isLoading = true
        _baseTextSize = baseTextSize

        viewModelScope.launch {
            val savedTextColor = settings.readInt(TXT_COLOR_KEY, currentTextColor)
            val savedBackground = settings.readInt(BACKGROUND_KEY, currentBackground)
            _textColor.value = savedTextColor
            _backgroundColor.value = savedBackground
            _isLoading = false
        }
    }

    @TextSize
    private fun coefficientToIndex(txtSizeCoefficient: Float): Int = when (txtSizeCoefficient) {
        1.0f -> TEXT_SIZE_DEFAULT
        1.2f -> TEXT_SIZE_BIG
        else -> TEXT_SIZE_BIGGEST
    }

    fun updateTextSizeIndex(value: Int) {
        val textSizeCoefficient = when (value) {
            TEXT_SIZE_DEFAULT -> 1.0f
            TEXT_SIZE_BIG -> 1.2f
            else -> 1.6f
        }

        val newValue = textSizeCoefficient * _baseTextSize

        if (!_isLoading && newValue != _textSize.value) {
            _textSize.value = newValue
            settings.write(TXT_SIZE_COEFF_KEY, textSizeCoefficient)
        }
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
