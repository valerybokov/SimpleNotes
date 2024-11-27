package prj.simplenotes.ui.mainfragment

import androidx.annotation.ColorInt
import androidx.core.util.Supplier
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import prj.simplenotes.data.Note
import prj.simplenotes.domain.NotesRepository
import prj.simplenotes.ui.NotesApplication
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import prj.simplenotes.data.NoteTextSize
import prj.simplenotes.data.Settings
import prj.simplenotes.ui.settingsfragment.DEFAULT_BACKGROUND_KEY
import prj.simplenotes.ui.settingsfragment.TXT_SIZE_COEFF_KEY
import prj.simplenotes.ui.settingsfragment.TXT_SIZE_COEFF_UNSET

class MainFragmentViewModel(
    private val _repo: NotesRepository,
    private val _settings: Settings,
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as NotesApplication

                return MainFragmentViewModel(
                    application.repo,
                    application.settings,
                ) as T
            }
        }
    }

    private var _baseTitleSize = 0f
    private var _baseTxtSize = 0f
    private var _noteTextSize = NoteTextSize(0f, 0f)

    fun init(
        baseTxtSize: Float,
        baseTitleSize: Float,
        @ColorInt itemDefaultBackground: Int) {
        if (_baseTxtSize == baseTxtSize &&
            _baseTitleSize == baseTitleSize)
            return

        _baseTitleSize = baseTitleSize
        _baseTxtSize = baseTxtSize

        _settings.write(DEFAULT_BACKGROUND_KEY, itemDefaultBackground)
    }

    val textSize: Flow<Unit> = _settings.readFloat(
        TXT_SIZE_COEFF_KEY, TXT_SIZE_COEFF_UNSET
    ).map { textSizeCoeff ->
        _noteTextSize = NoteTextSize(
            textSizeCoeff * _baseTitleSize,
            textSizeCoeff * _baseTxtSize
        )
    }

    fun getNoteTextSizeSupplier(): Supplier<NoteTextSize> {
        return object: Supplier<NoteTextSize> {
            override fun get(): NoteTextSize {
                return _noteTextSize
            }
        }
    }

    val items: LiveData<List<Note>> = _repo.getItems()

    private var _noteToDelete: Note? = null

    fun setItemToDelete(value: Note) {
        _noteToDelete = value
    }

    fun cancelDeleteItem() {
        _noteToDelete = null
    }

    fun deleteItem() {
        _noteToDelete?.let {
            _repo.deleteItem(it)
            _noteToDelete = null
        }
    }
}