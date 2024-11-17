package prj.simplenotes.ui.mainfragment.adapter

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Supplier
import androidx.recyclerview.widget.RecyclerView
import prj.simplenotes.R
import prj.simplenotes.data.Note
import prj.simplenotes.data.NoteTextSize
import prj.simplenotes.ui.common.ItemOperationType
import prj.simplenotes.ui.common.OPERATION_ITEM_CLICK
import prj.simplenotes.ui.common.OPERATION_ITEM_DELETE


class ItemViewHolder(
    root: View,
    itemClickListener: prj.simplenotes.ui.common.ItemClickListener,
    backgroundRadius: Float,
    private val textSizeSupplier: Supplier<NoteTextSize>,
    ) : RecyclerView.ViewHolder(root) {
    private val parent: ConstraintLayout = root.findViewById(R.id.itemParent)
    private val tvTitle: TextView = root.findViewById(R.id.tvTitle)
    private val tvText: TextView = root.findViewById(R.id.tvText)
    private val bDelete: ImageButton = root.findViewById(R.id.bDelete)
    private val clickListener = ItemClickListener(itemClickListener, OPERATION_ITEM_CLICK)
    private val deleteClickListener = ItemClickListener(itemClickListener, OPERATION_ITEM_DELETE)
    private val background = GradientDrawable()

    init {
        background.cornerRadius = backgroundRadius
    }

    fun onAttach() {
        parent.setOnClickListener(clickListener)
        bDelete.setOnClickListener(deleteClickListener)
    }

    fun bind(note: Note) {
        clickListener.note = note
        deleteClickListener.note = note

        background.setColor(note.background)
        parent.background = background

        tvTitle.text = note.title
        val noteTextSize = textSizeSupplier.get()
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, noteTextSize.titleSize)

        tvText.text = note.text
        tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, noteTextSize.textSize)
    }

    fun onDetach() {
        parent.setOnClickListener(null)
        bDelete.setOnClickListener(null)
    }

    private class ItemClickListener(
        private val itemClickListener: prj.simplenotes.ui.common.ItemClickListener,
        @ItemOperationType
        private val operationType: Int
    ): View.OnClickListener {
        lateinit var note: Note

        override fun onClick(view: View) {
            itemClickListener.onClick(note, operationType)
        }
    }
}