package prj.simplenotes.ui.common

import android.content.DialogInterface

class DialogClickListener(
    private val positiveButtonClick: () -> Unit,
    private val negativeButtonClick: () -> Unit,
    ): DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            positiveButtonClick()
        }
        else
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            negativeButtonClick()
        }
    }
}