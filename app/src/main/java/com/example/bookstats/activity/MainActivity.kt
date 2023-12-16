package com.example.bookstats.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bookstats.R
import com.example.bookstats.app.di.AppComponent.Companion.appComponent
import com.example.bookstats.databinding.ActivityMainBinding
import com.example.bookstats.extensions.viewBinding
import com.example.bookstats.features.realtimesessions.timer.helpers.TimerServiceHelper
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var helper: TimerServiceHelper

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(binding.root)
        setupNavControllers()
        navigateToSessionFragmentIfServiceIsRunning()
    }

    private fun setupNavControllers(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun navigateToSessionFragmentIfServiceIsRunning() {
        if (helper.isServiceRunning()) {
            val navController = (supportFragmentManager.findFragmentById(R.id.fragmentView) as NavHostFragment).navController
            navController.navigate(R.id.sessionFragment)
        }
    }
}