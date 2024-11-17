package prj.simplenotes.ui

import android.app.Application
import prj.simplenotes.data.AndroidSettings
import prj.simplenotes.data.Settings
import prj.simplenotes.domain.NotesRepository
import prj.simplenotes.domain.NotesRepositoryImpl

class NotesApplication: Application() {
    lateinit var repo: NotesRepository
        private set
    lateinit var settings: Settings
        private set

    override fun onCreate() {
        super.onCreate()

        settings = AndroidSettings(
            getSharedPreferences("preferences", MODE_PRIVATE))
        repo = NotesRepositoryImpl(this)
    }
}