package com.example.bookstats.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.example.bookstats.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView() {
    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
        View.GONE
}

fun Fragment.showBottomNavigationView(){
    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
        View.VISIBLE
}