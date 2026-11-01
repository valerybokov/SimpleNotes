package prj.simplenotes.fakes

import androidx.lifecycle.LiveData
import prj.simplenotes.data.Note
import prj.simplenotes.domain.NotesRepository

class FakeNotesRepository: NotesRepository {
    var updatedNote: Note? = null
        private set

    override fun addItem(note: Note) {
        /* noop */
    }

    override fun getItems(): LiveData<List<Note>> {
        throw UnsupportedOperationException("FakeNotesRepository.getItems")
    }

    override fun updateItem(note: Note) {
        updatedNote = note
    }

    override fun deleteItem(note: Note) {
        /* noop */
    }

    override fun addOnResultListener(value: NotesRepository.OnResultListener) {
        /* noop */
    }

    override fun removeOnResultListener(value: NotesRepository.OnResultListener) {
        /* noop */
    }

}