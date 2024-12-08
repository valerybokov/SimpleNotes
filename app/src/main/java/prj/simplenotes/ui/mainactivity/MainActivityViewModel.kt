package prj.simplenotes.ui.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineScope
import prj.simplenotes.data.AndroidSettings
import prj.simplenotes.data.Settings
import prj.simplenotes.domain.NotesRepository
import prj.simplenotes.domain.NotesRepository.OnResultListener
import prj.simplenotes.domain.NotesRepositoryImpl
import prj.simplenotes.ui.NotesApplication

class MainActivityViewModel(
    private val settings: Settings,
    private val repo: NotesRepository,
): ViewModel() {
        class Factory: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as NotesApplication
                return MainActivityViewModel(
                    application.settings,
                    application.repo,
                ) as T
            }
        }

    fun setScope(scope: CoroutineScope?) {
        (settings as AndroidSettings).setScope(scope)
        (repo as NotesRepositoryImpl).setScope(scope)
    }

    fun setOnExceptionListener(value: Settings.OnExceptionListener?) {
        settings.setOnExceptionListener(value)
    }

    fun setOnResultListener(value: OnResultListener) {
        repo.addOnResultListener(value)
    }

    fun removeOnResultListener(value: OnResultListener) {
        repo.removeOnResultListener(value)
    }
}
