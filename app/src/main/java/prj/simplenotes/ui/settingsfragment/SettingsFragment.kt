package prj.simplenotes.ui.settingsfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import prj.simplenotes.R
import prj.simplenotes.ui.NotesApplication
import prj.simplenotes.ui.common.ColorDialogListener
import yuku.ambilwarna.AmbilWarnaDialog

class SettingsFragment: Fragment(), RadioGroup.OnCheckedChangeListener {
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(
            (requireActivity().application as NotesApplication).settings)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.let { actionBar ->
            actionBar.title = resources.getString(R.string.title_settings)
        }

        val tvExample = activity.findViewById<TextView>(R.id.tvExample)
        val initBackground = getBackgroundColor(tvExample)
        val rbDefaultTxt = activity.findViewById<RadioButton>(R.id.rbDefaultTxt)

        viewModel.init(
            currentTextColor = tvExample.currentTextColor,
            currentBackground = initBackground,
            baseTextSize = rbDefaultTxt.textSize)

        val bBackground = activity.findViewById<Button>(R.id.bBackground)
        val bTextColor = activity.findViewById<Button>(R.id.bTextColor)

        val owner = viewLifecycleOwner
        viewModel.textColor.observe(owner) { color ->
            bTextColor.setBackgroundColor(color)
            tvExample.setTextColor(color)
        }
        viewModel.backgroundColor.observe(owner) { color ->
            bBackground.setBackgroundColor(color)
            tvExample.setBackgroundColor(color)
        }
        bTextColor.setOnClickListener {
            val dialog = AmbilWarnaDialog(
                it.context,
                tvExample.currentTextColor,
                object: ColorDialogListener() {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    viewModel.updateTextColor(color)
                }
            })

            dialog.show()
        }
        bBackground.setOnClickListener {
            val background = getBackgroundColor(tvExample)

            val dialog = AmbilWarnaDialog(
                it.context,
                background,
                object: ColorDialogListener() {
                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                        viewModel.updateBackground(color)
                    }
                })

            dialog.show()
        }

        val radioGroup = activity.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.check(indexToId(viewModel.currentTextSizeIndex))
        radioGroup.setOnCheckedChangeListener(this)

        viewModel.textSize.observe(viewLifecycleOwner) { txtSize ->
            tvExample.setTextSize(
                android.util.TypedValue.COMPLEX_UNIT_PX,
                txtSize
            )
        }
    }

    private fun getBackgroundColor(tvExample: TextView): Int {
        val backgroundDrawable = tvExample.background as? ColorDrawable
        return backgroundDrawable?.color ?: Color.TRANSPARENT
    }

    private fun indexToId(currentTextSizeIndex: Int): Int = when(currentTextSizeIndex) {
        TEXT_SIZE_DEFAULT -> R.id.rbDefaultTxt
        TEXT_SIZE_BIG -> R.id.rbBigTxt
        else -> R.id.rbBiggestTxt
    }

    override fun onCheckedChanged(group: RadioGroup, radioButtonId: Int) {
        when(radioButtonId) {
            R.id.rbDefaultTxt -> viewModel.currentTextSizeIndex = TEXT_SIZE_DEFAULT
            R.id.rbBigTxt ->    viewModel.currentTextSizeIndex = TEXT_SIZE_BIG
            R.id.rbBiggestTxt -> viewModel.currentTextSizeIndex = TEXT_SIZE_BIGGEST
        }
    }
}