package prj.simplenotes.ui.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorInt

@get:ColorInt
val View.backgroundColor: Int
    get() {
        val backgroundDrawable = this.background as? ColorDrawable
        return backgroundDrawable?.color ?: Color.TRANSPARENT
    }
