package prj.simplenotes.ui.common

import androidx.annotation.IntDef
import prj.simplenotes.data.Note

fun interface ItemClickListener {
    fun onClick(
        value: Note,
        @ItemOperationType operationType: Int)
}

@Retention(AnnotationRetention.SOURCE)
@IntDef(
    OPERATION_ITEM_CLICK,
    OPERATION_ITEM_DELETE,
)
annotation class ItemOperationType

const val OPERATION_ITEM_CLICK = 0
const val OPERATION_ITEM_DELETE = 1
