package prj.simplenotes.ui.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.CoroutineScope
import prj.simplenotes.domain.NotesRepository
import prj.simplenotes.domain.NotesRepository.OnResultListener
import prj.simplenotes.domain.NotesRepositoryImpl
import prj.simplenotes.ui.NotesApplication

class MainActivityViewModel(
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
                    application.repo,
                ) as T
            }
        }

    fun setScope(scope: CoroutineScope?) {
        (repo as NotesRepositoryImpl).setScope(scope)
    }

    fun setOnResultListener(value: OnResultListener) {
        repo.addOnResultListener(value)
    }

    fun removeOnResultListener(value: OnResultListener) {
        repo.removeOnResultListener(value)
    }
}
