package prj.simplenotes.ui

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import prj.simplenotes.data.AndroidSettings
import prj.simplenotes.data.Settings
import prj.simplenotes.domain.NotesRepository
import prj.simplenotes.domain.NotesRepositoryImpl

class NotesApplication: Application() {
    lateinit var repo: NotesRepository
        private set
    lateinit var settings: Settings
        private set

    /** Lives as long as the process, so writes are not cancelled
     * when an activity is paused or recreated. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        settings = AndroidSettings(this, appScope)
        repo = NotesRepositoryImpl(this, appScope)
    }
}