package me.rajesh.expensetracker

import android.app.ProgressDialog.show
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import me.rajesh.expensetracker.app.MyApplication
import me.rajesh.expensetracker.databinding.ActivityMainBinding
import me.rajesh.expensetracker.di.component.ActivityComponent
import me.rajesh.expensetracker.di.component.DaggerActivityComponent
import me.rajesh.expensetracker.di.module.ActivityModule
import kotlin.and

class MainActivity : AppCompatActivity() {

    lateinit var activityComponent: ActivityComponent

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFabIconAccordingly()
        setUpBottomNavigation()
        setEventListener()
    }

    private fun setUpBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        // Listen for navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> binding.changeTheme.visibility = View.VISIBLE
                else -> binding.changeTheme.visibility = View.GONE
            }
        }
    }

    private fun injectDependencies() {
        activityComponent = DaggerActivityComponent.builder()
            .applicationComponent((application as MyApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
        activityComponent.inject(this)
    }

    private fun isDarkThemeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setEventListener() {
        binding.changeTheme.setOnClickListener { changeTheme() }
    }

    private fun changeTheme() {
        if (isDarkThemeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        changeFabIconAccordingly()
    }

    private fun changeFabIconAccordingly() {
        if (isDarkThemeOn()) {
            binding.changeTheme.setImageResource(R.drawable.ic_light_mode)
        } else {
            binding.changeTheme.setImageResource(R.drawable.ic_dark_mode)
        }
    }
}