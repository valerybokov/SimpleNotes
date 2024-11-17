package prj.simplenotes.ui.mainfragment.adapter

import android.widget.Filter
import androidx.lifecycle.Observer
import prj.simplenotes.data.Note

/**This class needed to filter items in RecyclerView.Adapter
 * */
class NotesFilter : Filter() {
    private var _currentList = listOf<Note>()
    private var _originalList = listOf<Note>()
    private var _resultsListener: Observer<List<Note>?>? = null

    /** This is the list what was put into
     *  the RecyclerView.Adapter from outside. */
    fun setOriginalList(value: List<Note>) {
        _originalList = value
        _currentList = value
    }

    fun setResultsListener(value: Observer<List<Note>?>) {
        _resultsListener = value
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()

        if (constraint != null && constraint.length > 0) {
            val filterList = ArrayList<Note>()
            val request = constraint.toString().uppercase()
            for (item in _currentList) {
                if (item.title.uppercase().contains(request) ||
                    item.text.uppercase().contains(request)) {
                    filterList.add(item)
                }
            }
            results.values = filterList
            results.count = filterList.size
        }
        else {
            results.values = _originalList
            results.count = _originalList.size
        }

        return results
    }

    override fun publishResults(
        constraint: CharSequence?, filterResults: FilterResults?) {
        val list = filterResults?.values as? List<Note>

        _resultsListener?.onChanged(list)
    }
}