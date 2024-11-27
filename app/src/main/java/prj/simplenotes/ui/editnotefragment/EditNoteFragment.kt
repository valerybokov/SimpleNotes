package prj.simplenotes.ui.editnotefragment

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import prj.simplenotes.R
import prj.simplenotes.data.Note
import prj.simplenotes.databinding.FragmentEditNoteBinding
import prj.simplenotes.domain.NotesRepository.OnResultListener
import prj.simplenotes.ui.NotesApplication
import prj.simplenotes.ui.common.ColorDialogListener
import prj.simplenotes.ui.common.DialogClickListener
import prj.simplenotes.ui.utils.backgroundColor
import yuku.ambilwarna.AmbilWarnaDialog


const val ARGUMENT_NOTE = "ARG_NOTE"

private const val DESTINATION_FRAGMENT_MAIN = "fragment_main"

class EditNoteFragment : Fragment(), MenuProvider {
    private val viewModel: EditNoteViewModel by viewModels {
        val application = requireActivity().application as NotesApplication
        EditNoteViewModel.Factory(
            application.repo,
            application.settings,
            this)
    }

    private lateinit var menu: Menu
    private lateinit var dialogClickListener: DialogInterface.OnClickListener

    private val resultListener = object: OnResultListener {
        override fun onDeleted() {
            lifecycleScope.launch {
                //go back after item deletion
                findNavController().popBackStack()
            }
        }

        override fun onError(value: Int) {
            //this will be handled in the MainActivity
        }
    }

    private val destinationChangedListener =
        OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.label == DESTINATION_FRAGMENT_MAIN) {
                viewModel.save()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @Suppress("DEPRECATION")
        val note: Note? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arguments?.getSerializable(ARGUMENT_NOTE, Note::class.java)
            else
                arguments?.getSerializable(ARGUMENT_NOTE) as Note?

        viewModel.init(note)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstState: Bundle?
    ): View {
        val dataBinding = DataBindingUtil.inflate<FragmentEditNoteBinding>(
            inflater,
            R.layout.fragment_edit_note,
            container,
            false
        )
        dataBinding.viewModel = viewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.let { actionBar ->
            if (viewModel.isEditing) {
                actionBar.title = resources.getString(R.string.noteEdit)
            }
            else {
                actionBar.title = resources.getString(R.string.noteNew)
            }
        }

        activity.addMenuProvider(this, viewLifecycleOwner)

        val itemParent = activity.findViewById<LinearLayout>(R.id.viewParent)
        val tbTitle = activity.findViewById<TextView>(R.id.tbTitle)
        val tbEdit = activity.findViewById<TextView>(R.id.tbEdit)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getTitleSize(tbTitle.textSize).collectLatest { titleSize ->
                    tbTitle.setTextSize(
                        android.util.TypedValue.COMPLEX_UNIT_PX,
                        titleSize
                    )
                }
            }
        }
        lifecycleScope.launch {
            viewModel.getTextSize(tbEdit.textSize).collect { textSize ->
                tbEdit.setTextSize(
                    android.util.TypedValue.COMPLEX_UNIT_PX,
                    textSize
                )
            }
        }
        lifecycleScope.launch {
            @ColorInt val background = tbEdit.backgroundColor
            @ColorInt val backgroundColor = viewModel.getBackgroundColor(background)
            @ColorInt val textColor = viewModel.getTextColor(tbEdit.currentTextColor)

            itemParent.setBackgroundColor(backgroundColor)
            tbTitle.setTextColor(textColor)
            tbEdit.setTextColor(textColor)
        }

        dialogClickListener = DialogClickListener(
            viewModel::deleteNote, ::notDeleteNote)
        viewModel.addOnResultListener(resultListener)
        viewModel.isCompleted.observe(viewLifecycleOwner) {
            if (!::menu.isInitialized) {
                return@observe
            }

            if (viewModel.isEditing) {
                val miECompleted = menu.findItem(R.id.miECompleted)
                setMenuCompleted(miECompleted, it)
            }
            else {
                val miNCompleted = menu.findItem(R.id.miNCompleted)
                setMenuCompleted(miNCompleted, it)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        findNavController().addOnDestinationChangedListener(
            destinationChangedListener)
    }

    override fun onDetach() {
        findNavController().removeOnDestinationChangedListener(
            destinationChangedListener)

        super.onDetach()
    }

    override fun onPrepareMenu(menu: Menu) {
        this.menu = menu

        if (viewModel.isEditing) {
            viewModel.isCompleted.value?.let {
                val miECompleted = menu.findItem(R.id.miECompleted)
                setMenuCompleted(miECompleted, it)
            }
        }
    }

    private fun setMenuCompleted(miCompleted: MenuItem, isCompleted: Boolean) {
        if (isCompleted) {
            miCompleted.setTitle(resources.getString(R.string.title_completed))
            miCompleted.setIcon(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked))
        }
        else {
            miCompleted.setTitle(resources.getString(R.string.title_not_completed))
            miCompleted.setIcon(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_not_checked))
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        @MenuRes val menuRes = if (viewModel.isEditing) R.menu.menu_edit else R.menu.menu_new
        menuInflater.inflate(menuRes, menu)
    }

    override fun onPause() {
        //hide keyboard
        val activity = this.activity
        activity?.currentFocus?.let { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        super.onPause()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.miExternalColor,
            R.id.miEExternalColor -> {
                val dialog = AmbilWarnaDialog(
                    requireContext(),
                    viewModel.itemBackground,
                    object: ColorDialogListener() {
                        override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                            viewModel.itemBackground = color
                        }
                    })

                dialog.show()
                return true
            }
            //set item has been completed for edit mode
            R.id.miECompleted,
            //set item has been completed for create mode
            R.id.miNCompleted -> {
                viewModel.updateIsCompleted()
                return true
            }
            R.id.miERemove -> {
                deleteNote()
                return true
            }
            else ->
                return false
        }
    }

    private fun deleteNote() {
        val dlg = AlertDialog.Builder(requireContext())
        dlg.setMessage(R.string.msgRemove)
            .setPositiveButton(android.R.string.ok, dialogClickListener)
            .setNegativeButton(android.R.string.cancel, dialogClickListener)
            .create().show()
    }

    private fun notDeleteNote() {
        /* no-op */
    }

    override fun onDestroyView() {
        viewModel.removeOnResultListener(resultListener)
        super.onDestroyView()
    }
}