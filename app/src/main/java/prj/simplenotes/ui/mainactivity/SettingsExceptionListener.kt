package prj.simplenotes.ui.mainactivity

import android.app.Application
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import prj.simplenotes.R
import prj.simplenotes.data.Settings

class SettingsExceptionListener(
    private val scope: CoroutineScope,
    private val context: Application): Settings.OnExceptionListener {
    override fun onException(value: Throwable, isWriting: Boolean) {
        val message =
            if (isWriting) R.string.cannot_update_settings
            else R.string.cannot_read_settings
        scope.launch {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}