package prj.simplenotes.ui.mainactivity

import android.app.Application
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import prj.simplenotes.R
import prj.simplenotes.domain.EXCEPTION_CANNOT_ADD
import prj.simplenotes.domain.EXCEPTION_CANNOT_DELETE
import prj.simplenotes.domain.EXCEPTION_CANNOT_READ
import prj.simplenotes.domain.EXCEPTION_CANNOT_UPDATE
import prj.simplenotes.domain.EventType
import prj.simplenotes.domain.NotesRepository

class RepositoryResultListener(
    private val scope: CoroutineScope,
    private val context: Application): NotesRepository.OnResultListener {
    override fun onError(@EventType value: Int) {
        when(value) {
            EXCEPTION_CANNOT_ADD ->
                scope.launch {
                    Toast.makeText(
                        context, R.string.cannot_add_note, Toast.LENGTH_SHORT).show()
                }
            EXCEPTION_CANNOT_UPDATE ->
                scope.launch {
                    Toast.makeText(
                        context, R.string.cannot_update_note, Toast.LENGTH_SHORT).show()
                }
            EXCEPTION_CANNOT_DELETE ->
                scope.launch {
                    Toast.makeText(
                        context, R.string.cannot_delete_note, Toast.LENGTH_SHORT).show()
                }
            EXCEPTION_CANNOT_READ ->
                scope.launch {
                    Toast.makeText(
                        context, R.string.cannot_read_notes, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDeleted() {
        /* no-op */
    }
}