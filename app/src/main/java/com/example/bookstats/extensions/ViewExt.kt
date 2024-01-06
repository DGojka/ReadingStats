package com.example.bookstats.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.example.bookstats.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

fun Fragment.hideBottomNavigationView() {
    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
        View.GONE
}

fun Fragment.showBottomNavigationView() {
    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
        View.VISIBLE
}

fun View.visibleOrInvisible(isVisible: Boolean) {
    val newVisibility = when (isVisible) {
        true -> View.VISIBLE
        false -> View.INVISIBLE
    }
    visibility = newVisibility
}

fun View.visibleOrGone(isVisible: Boolean) {
    val newVisibility = when (isVisible) {
        true -> View.VISIBLE
        false -> View.GONE
    }
    visibility = newVisibility
}

fun TextInputEditText.getString(): String? = this.text.toString().ifBlank { null }
