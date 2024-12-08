package prj.simplenotes.ui.mainactivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import prj.simplenotes.R
import prj.simplenotes.domain.NotesRepository

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory()
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var resultListener: NotesRepository.OnResultListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        viewModel.setScope(lifecycleScope)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onResume() {
        super.onResume()
        val scope = lifecycleScope
        resultListener = RepositoryResultListener(scope, application)
        viewModel.setOnExceptionListener(SettingsExceptionListener(scope, application))
        viewModel.setOnResultListener(resultListener)
        viewModel.setScope(scope)
    }

    override fun onPause() {
        viewModel.setOnExceptionListener(null)
        viewModel.setScope(null)
        viewModel.removeOnResultListener(resultListener)
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}