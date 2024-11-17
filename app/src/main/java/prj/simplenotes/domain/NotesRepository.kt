package prj.simplenotes.domain

import androidx.annotation.IntDef
import androidx.lifecycle.LiveData
import prj.simplenotes.data.Note


@Retention(AnnotationRetention.SOURCE)
@IntDef(
    EXCEPTION_CANNOT_ADD,
    EXCEPTION_CANNOT_UPDATE,
    EXCEPTION_CANNOT_DELETE,
    EXCEPTION_CANNOT_READ)
annotation class EventType

const val EXCEPTION_CANNOT_ADD = 1
const val EXCEPTION_CANNOT_UPDATE = 2
const val EXCEPTION_CANNOT_DELETE = 3
const val EXCEPTION_CANNOT_READ = 4

interface NotesRepository {
    fun addItem(note: Note)

    fun getItems(): LiveData<List<Note>>

    fun updateItem(note: Note)

    fun deleteItem(note: Note)

    fun addOnResultListener(value: OnResultListener)

    fun removeOnResultListener(value: OnResultListener)

    interface OnResultListener {
        fun onError(@EventType value: Int)
        fun onDeleted()
    }
}