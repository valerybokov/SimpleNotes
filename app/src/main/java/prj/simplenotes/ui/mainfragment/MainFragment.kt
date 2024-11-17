package prj.simplenotes.ui.mainfragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import prj.simplenotes.R
import prj.simplenotes.data.Note
import prj.simplenotes.ui.common.ItemClickListener
import prj.simplenotes.ui.common.DialogClickListener
import prj.simplenotes.ui.common.ItemOperationType
import prj.simplenotes.ui.common.OPERATION_ITEM_CLICK
import prj.simplenotes.ui.editnotefragment.ARGUMENT_NOTE
import prj.simplenotes.ui.mainfragment.adapter.NotesAdapter


class MainFragment : Fragment(), MenuProvider {
    private val viewModel: MainFragmentViewModel by viewModels {
        MainFragmentViewModel.Factory
    }

    private lateinit var rwNotes: RecyclerView
    private lateinit var imEmpty: ImageView
    private lateinit var dialogClickListener: DialogInterface.OnClickListener
    private var adapter: NotesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        val res = resources
        activity.supportActionBar?.let {
            it.title = res.getString(R.string.app_name)
        }

        val titleSize = res.getDimension(R.dimen.title_size)
        val textSize = res.getDimension(R.dimen.text_size)
        val itemBackgroundRadius = res.getDimension(R.dimen.item_note_corner_radius)
        viewModel.init(
            baseTitleSize = titleSize,
            baseTxtSize = textSize,
            itemDefaultBackground = ContextCompat.getColor(activity, R.color.itemDefaultBackground))

        adapter = NotesAdapter(
            _itemClickListener = object : ItemClickListener {
                override fun onClick(value: Note, @ItemOperationType operationType: Int) {
                    if (operationType == OPERATION_ITEM_CLICK)
                        editNote(value)
                    else
                        deleteNote(value)
                }
            },
            _itemBackgroundRadius = itemBackgroundRadius,
            _itemTextSizeSupplier = viewModel.getNoteTextSizeSupplier())
        val owner = viewLifecycleOwner
        viewModel.noteTextSizeChanged.observe(owner) {
            adapter?.notifyDataSetChanged()
        }
        viewModel.items.observe(owner) { list ->
            //show/hide the empty view
            if (list.isEmpty()) {
                imEmpty.visibility = View.VISIBLE
                rwNotes.visibility = View.GONE
            }
            else {
                imEmpty.visibility = View.GONE
                rwNotes.visibility = View.VISIBLE
            }

            adapter?.submitList(list)
        }
        dialogClickListener = DialogClickListener(
            positiveButtonClick = viewModel::deleteItem,
            negativeButtonClick = viewModel::cancelDeleteItem)
        val fab: FloatingActionButton = activity.findViewById(R.id.fab)
        fab.setOnClickListener {
            createNote()
        }

        rwNotes = activity.findViewById(R.id.rwNotes)
        imEmpty = activity.findViewById(R.id.imEmpty)
        rwNotes.adapter = adapter

        activity.addMenuProvider(this, viewLifecycleOwner)
    }

    private fun deleteNote(value: Note) {
        viewModel.setItemToDelete(value)

        val dlg = AlertDialog.Builder(requireContext())
        dlg.setMessage(R.string.msgRemove)
            .setPositiveButton(android.R.string.ok, dialogClickListener)
            .setNegativeButton(android.R.string.cancel, dialogClickListener)
            .create().show()
    }

    private fun createNote() {
        findNavController().navigate(R.id.editNoteFragment)
    }

    private fun editNote(value: Note) {
        findNavController().navigate(
            R.id.editNoteFragment,
            bundleOf(ARGUMENT_NOTE to value))
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
    }

    override fun onPrepareMenu(menu: Menu) {
        val miSearch = menu.findItem(R.id.miSearch)
        val searchView = miSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.let {
                    it.filter.filter(newText)
                }
                return false
            }
        })
        super.onPrepareMenu(menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.miSettings -> {
                findNavController().navigate(R.id.settingsFragment)
                true
            }
            R.id.miAbout -> {
                findNavController().navigate(R.id.aboutAppFragment)
                true
            }
            else -> false
        }
    }
}