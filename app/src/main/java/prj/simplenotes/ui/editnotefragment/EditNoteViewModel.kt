package prj.simplenotes.ui.editnotefragment
//https://medium.com/deuk/intermediate-android-compose-todo-app-with-room-database-18fd97436c6d
//https://johncodeos.com/how-to-use-room-in-android-using-kotlin/
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import prj.simplenotes.data.Note
import prj.simplenotes.data.Settings
import prj.simplenotes.domain.NotesRepository
import prj.simplenotes.domain.NotesRepository.OnResultListener
import prj.simplenotes.ui.settingsfragment.BACKGROUND_KEY
import prj.simplenotes.ui.settingsfragment.DEFAULT_BACKGROUND_KEY
import prj.simplenotes.ui.settingsfragment.DEFAULT_BACKGROUND_VALUE
import prj.simplenotes.ui.settingsfragment.TXT_COLOR_KEY
import prj.simplenotes.ui.settingsfragment.TXT_SIZE_COEFF_KEY
import prj.simplenotes.ui.settingsfragment.TXT_SIZE_COEFF_UNSET


const val HANDLE_OPERATION_TYPE = "OPERATION_TYPE"
const val HANDLE_TEXT = "TEXT"
const val HANDLE_TITLE = "TITLE"
const val HANDLE_IS_COMPLETED = "IS_COMPLETED"
const val HANDLE_BACKGROUND = "BACKGROUND"

class EditNoteViewModel(
    private val _repo: NotesRepository,
    private val _settings: Settings,
    private val _handle: SavedStateHandle
) : ViewModel() {
    class Factory(
        private val repo: NotesRepository,
        private val settings: Settings,
        owner: SavedStateRegistryOwner,
    ): AbstractSavedStateViewModelFactory(owner, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return EditNoteViewModel(
                repo,
                settings,
                handle
            ) as T
        }
    }

    fun getTextColor(@ColorInt currentTextColor: Int): Int =
        _settings.readInt(TXT_COLOR_KEY, currentTextColor)

    /** Returns a note background color. It is the same for all notes.
     * @param defaultBackground this is default background for note */
    fun getBackgroundColor(@ColorInt defaultBackground: Int): Int =
        _settings.readInt(BACKGROUND_KEY, defaultBackground)

    fun getTextSize(baseTextSize: Float): Float {
        return _settings.readFloat(TXT_SIZE_COEFF_KEY, TXT_SIZE_COEFF_UNSET) * baseTextSize
    }

    fun getTitleSize(baseTitleSize: Float): Float {
        return _settings.readFloat(TXT_SIZE_COEFF_KEY, TXT_SIZE_COEFF_UNSET) * baseTitleSize
    }

    val maxSymbolsNumberForTitle: Int = 128
    val maxSymbolsNumberForBody: Int = 1024

    val text = MutableLiveData("")
    val title = MutableLiveData("")

    private val _isCompleted = MutableLiveData(false)
    val isCompleted: LiveData<Boolean> = _isCompleted

    @OperationType
    private var _operationType = OPERATION_NONE

    private var _note: Note? = null
    private var _isInitialised = false

    private val _textChangedObserver = Observer<String> {
        if (_isInitialised) {
            _handle[HANDLE_TEXT] = it

            updateOperationType(OPERATION_UPDATE)
        }
    }

    private val _titleChangedObserver = Observer<String> {
        if (_isInitialised) {
            _handle[HANDLE_TITLE] = it

            updateOperationType(OPERATION_UPDATE)
        }
    }

    init {
        val operationType = _handle.get<Int>(HANDLE_OPERATION_TYPE)

        if (operationType != null) {
            _isCompleted.value = _handle.get<Boolean>(HANDLE_IS_COMPLETED)
            title.value = _handle.get<String>(HANDLE_TITLE) ?: ""
            text.value = _handle.get<String>(HANDLE_TEXT) ?: ""
            _operationType = operationType
        }

        title.observeForever(_titleChangedObserver)
        text.observeForever(_textChangedObserver)
    }

    fun init(value: Note?) {
        _isInitialised = false
        _note = value
        val operationType = _handle.get<Int>(HANDLE_OPERATION_TYPE)

        if (operationType == null || operationType == OPERATION_NONE) {
            if (value == null) {
                updateOperationType(OPERATION_CREATE)
            }
            else {
                title.value = value.title
                text.value = value.text
                _isCompleted.value = value.isCompleted
                //OPERATION_UPDATE will be called via observer
            }
        }

        _handle[HANDLE_IS_COMPLETED] = _isCompleted.value
        _isInitialised = true
    }

    private fun updateOperationType(
        @OperationType type: Int) {
        _operationType = type
        _handle[HANDLE_OPERATION_TYPE] = type
    }

    fun addOnResultListener(value: OnResultListener) {
        _repo.addOnResultListener(value)
    }

    fun removeOnResultListener(value: OnResultListener) {
        _repo.removeOnResultListener(value)
    }

    val isEditing: Boolean
        get() = _note != null

    /** Save changes */
    fun save() {
        if (_operationType == OPERATION_DELETE) {
            _note = null
            text.value = ""
            return
        }
        else
        if (_operationType == OPERATION_NONE) {
            return
        }

        val text = text.value ?: ""
        val title = title.value ?: ""
        val nt = _note
        //create item
        if (nt == null) {
            if (text.isNotEmpty() || title.isNotEmpty()) {
                val background = backgroundToNotNull(_handle.get<Int>(HANDLE_BACKGROUND))
                _repo.addItem(
                    Note(id = 0,
                        background = background,
                        text = text,
                        title = title,
                        isCompleted = _isCompleted.value!!))
            }
        }
        //edit item
        else {
            val background = _handle.get<Int>(HANDLE_BACKGROUND)
            
            if (text != nt.text || title != nt.title ||
                nt.isCompleted != _isCompleted.value || background != null) {
                _repo.updateItem(
                    nt.copy(
                        background = backgroundToNotNull(background),
                        text = text,
                        title = title,
                        isCompleted = _isCompleted.value!!,
                ))
            }
        }
    }

    private fun backgroundToNotNull(background: Int?): Int {
        if (background == null) {
            return _settings.readInt(
                DEFAULT_BACKGROUND_KEY, DEFAULT_BACKGROUND_VALUE
            )
        }
        return background
    }

    /** Set note to completed or not completed */
    fun updateIsCompleted() {
        val value = !_isCompleted.value!!
        _isCompleted.value = value
        _handle[HANDLE_IS_COMPLETED] = value
        updateOperationType(OPERATION_UPDATE)
    }

    /** Delete note and go back form fragment */
    fun deleteNote() {
        _note?.let {
            _repo.deleteItem(it)
            updateOperationType(OPERATION_DELETE)
        }
    }

    /** This is the background of the item that user can
     * see viewing the all items list (MainFragment) */
    @setparam:ColorInt
    var itemBackground: Int
        get() {
            val temporaryBackground = _handle.get<Int>(HANDLE_BACKGROUND)
            if (temporaryBackground != null)
                return temporaryBackground

            val note = _note

            if (note == null) {
                return _settings.readInt(
                    DEFAULT_BACKGROUND_KEY, DEFAULT_BACKGROUND_VALUE)
            }
            else {
                return note.background
            }
        }
        set(value) {
            val prevBackground = _handle.get<Int>(HANDLE_BACKGROUND)
            if (value != prevBackground) {
                _handle[HANDLE_BACKGROUND] = value
                updateOperationType(OPERATION_UPDATE)
            }
        }

    override fun onCleared() {
        super.onCleared()

        title.removeObserver(_titleChangedObserver)
        text.removeObserver(_textChangedObserver)
    }
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    OPERATION_NONE,
    OPERATION_CREATE,
    OPERATION_UPDATE,
    OPERATION_DELETE,
)
annotation class OperationType

const val OPERATION_NONE = 0
const val OPERATION_CREATE = 1
const val OPERATION_UPDATE = 2
const val OPERATION_DELETE = 3