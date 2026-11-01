package prj.simplenotes.domain

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import prj.simplenotes.data.Note
import prj.simplenotes.data.db.NotesDatabase
import java.util.concurrent.CopyOnWriteArrayList

class NotesRepositoryImpl(
    app: Application): NotesRepository {
    private val _notesDao = NotesDatabase.getInstance(app).notesDao()
    private val _mapper = NoteMapper()
    //listeners are notified on Dispatchers.IO while added/removed on the main thread
    private val _eventListeners = CopyOnWriteArrayList<NotesRepository.OnResultListener>()

    private var _scope: CoroutineScope? = null

    fun setScope(value: CoroutineScope?) {
        _scope = value
    }

    override fun addOnResultListener(value: NotesRepository.OnResultListener) {
        _eventListeners.addIfAbsent(value)
    }

    override fun removeOnResultListener(value: NotesRepository.OnResultListener) {
        _eventListeners.remove(value)
    }

    override fun addItem(note: Note) {
        _scope?.launch(Dispatchers.IO) {
            try {
                _notesDao.insert(_mapper.toEntity(note))
            }
            catch (x: Exception) {
                _eventListeners.forEach {
                    it.onError(EXCEPTION_CANNOT_ADD)
                }
            }
        }
    }

    override fun updateItem(note: Note) {
        _scope?.launch(Dispatchers.IO) {
            try {
                _notesDao.update(_mapper.toEntity(note))
            }
            catch (x: Exception) {
                _eventListeners.forEach {
                    it.onError(EXCEPTION_CANNOT_UPDATE)
                }
            }
        }
    }

    override fun deleteItem(note: Note) {
        _scope?.launch(Dispatchers.IO) {
            try {
                _notesDao.delete(_mapper.toEntity(note))
                _eventListeners.forEach {
                    it.onDeleted()
                }
            }
            catch (x: Exception) {
                _eventListeners.forEach {
                    it.onError(EXCEPTION_CANNOT_DELETE)
                }
            }
        }
    }

    override fun getItems(): LiveData<List<Note>> {
        try {
            return _notesDao.getAll().map {
                _mapper.toNotes(it)
            }
        }
        catch (x: Exception) {
            _eventListeners.forEach {
                it.onError(EXCEPTION_CANNOT_READ)
            }

            return MutableLiveData()
        }
    }
}