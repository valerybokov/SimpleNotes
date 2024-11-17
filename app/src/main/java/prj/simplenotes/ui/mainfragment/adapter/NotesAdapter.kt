package prj.simplenotes.ui.mainfragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.util.Supplier
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import prj.simplenotes.R
import prj.simplenotes.data.Note
import prj.simplenotes.data.NoteTextSize
import prj.simplenotes.ui.common.ItemClickListener
import java.util.concurrent.Executors


class NotesAdapter(
    private val _itemClickListener: ItemClickListener,
    private val _itemBackgroundRadius: Float,
    private val _itemTextSizeSupplier: Supplier<NoteTextSize>,
): ListAdapter<Note, ItemViewHolder>(
    AsyncDifferConfig.Builder(object: DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.text == newItem.text && oldItem.title == newItem.title &&
                    oldItem.background == newItem.background
        }
    })
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
), Filterable {
    private val filter = NotesFilter()

    init {
        filter.setResultsListener { list ->
            super@NotesAdapter.submitList(list)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return ItemViewHolder(
            root = view,
            itemClickListener = _itemClickListener,
            backgroundRadius = _itemBackgroundRadius,
            textSizeSupplier = _itemTextSizeSupplier)
    }

    override fun onViewAttachedToWindow(holder: ItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: ItemViewHolder) {
        holder.onDetach()
        super.onViewDetachedFromWindow(holder)
    }

    override fun submitList(list: List<Note>?) {
        filter.setOriginalList(list ?: listOf())

        super.submitList(list)
    }

    override fun getFilter(): Filter {
        return filter
    }
}
