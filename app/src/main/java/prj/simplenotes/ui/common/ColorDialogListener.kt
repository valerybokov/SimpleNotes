package prj.simplenotes.ui.common

import yuku.ambilwarna.AmbilWarnaDialog

abstract class ColorDialogListener: AmbilWarnaDialog.OnAmbilWarnaListener {
    override fun onCancel(dialog: AmbilWarnaDialog?) {
        // cancel was selected by the user
        /* no-op */
    }
}