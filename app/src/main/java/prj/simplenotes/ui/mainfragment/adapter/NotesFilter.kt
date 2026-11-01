package prj.simplenotes.ui.mainfragment.adapter

import android.widget.Filter
import androidx.lifecycle.Observer
import prj.simplenotes.data.Note

/**This class needed to filter items in RecyclerView.Adapter
 * */
class NotesFilter : Filter() {
    //read in performFiltering on the filter's worker thread
    @Volatile
    private var _originalList = listOf<Note>()
    private var _constraint: CharSequence? = null
    private var _resultsListener: Observer<List<Note>?>? = null

    /** This is the list what was put into
     *  the RecyclerView.Adapter from outside. */
    fun setOriginalList(value: List<Note>) {
        _originalList = value
    }

    fun setResultsListener(value: Observer<List<Note>?>) {
        _resultsListener = value
    }

    /** Filters items and remembers the request
     * so it can be applied again to a new list. */
    fun filterBy(constraint: CharSequence?) {
        _constraint = constraint
        filter(constraint)
    }

    /** Applies the last request to the current original list. */
    fun refilter() {
        filter(_constraint)
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        val originalList = _originalList

        if (constraint != null && constraint.length > 0) {
            val filterList = ArrayList<Note>()
            val request = constraint.toString().uppercase()
            for (item in originalList) {
                if (item.title.uppercase().contains(request) ||
                    item.text.uppercase().contains(request)) {
                    filterList.add(item)
                }
            }
            results.values = filterList
            results.count = filterList.size
        }
        else {
            results.values = originalList
            results.count = originalList.size
        }

        return results
    }

    override fun publishResults(
        constraint: CharSequence?, filterResults: FilterResults?) {
        val list = filterResults?.values as? List<Note>

        _resultsListener?.onChanged(list)
    }
}