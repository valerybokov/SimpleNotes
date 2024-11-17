package prj.simplenotes.ui.aboutfragment

import android.app.Activity
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import prj.simplenotes.R


class AboutAppFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_about_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.let {
            it.title = resources.getString(R.string.about)
        }

        val tvVersion = activity.findViewById<TextView>(R.id.tvVersion)
        tvVersion.text = getAppVersion(activity)

        activity.findViewById<Button>(R.id.bOk).setOnClickListener(this)
    }

    private fun getAppVersion(activity: Activity): String {
        try {
            return activity.packageManager.
                        getPackageInfo(activity.packageName, 0).versionName
        }
        catch (ex: NameNotFoundException) {
            /* no-op */
        }
        return "1.0"
    }

    override fun onClick(v: View) {
        findNavController().popBackStack()
    }
}