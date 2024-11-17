package prj.simplenotes.data

import androidx.annotation.ColorInt

data class Note(
    val id: Int,
    @ColorInt val background: Int,
    val title: String,
    val text: String,
    val isCompleted: Boolean,
): java.io.Serializable