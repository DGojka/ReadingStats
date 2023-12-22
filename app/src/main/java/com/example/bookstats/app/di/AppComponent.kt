package com.example.bookstats.app.di

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment
import com.example.bookstats.activity.MainActivity
import com.example.bookstats.app.ReadingStatsApp
import com.example.bookstats.features.creation.BookCreationFragment
import com.example.bookstats.features.bookdetails.BookDetailsFragment
import com.example.bookstats.features.bookdetails.tabs.settings.BookSettingsFragment
import com.example.bookstats.features.bookdetails.tabs.general.GeneralBookDetailsFragment
import com.example.bookstats.features.bookdetails.tabs.sessions.BookSessionsFragment
import com.example.bookstats.features.library.ui.LibraryFragment
import com.example.bookstats.features.realtimesessions.timer.TimerService
import com.example.bookstats.features.realtimesessions.ui.RealTimeSessionFragment
import com.example.bookstats.features.realtimesessions.ui.SummaryFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(timerService: TimerService)

    fun inject(fragment: BookCreationFragment)

    fun inject(fragment: LibraryFragment)

    fun inject(fragment: BookDetailsFragment)

    fun inject(realTimeSessionFragment: RealTimeSessionFragment)

    fun inject(summaryFragment: SummaryFragment)

    fun inject(bookSettingsFragment: BookSettingsFragment)

    fun inject(bookSessionsFragment: BookSessionsFragment)

    fun inject(generalBookDetailsFragment: GeneralBookDetailsFragment)

    companion object {
        val Activity.appComponent: AppComponent
            get() = (application as ReadingStatsApp).appComponent

        val Service.appComponent: AppComponent
            get() = (application as ReadingStatsApp).appComponent

        val Fragment.appComponent: AppComponent
            get() = requireActivity().appComponent
    }
}