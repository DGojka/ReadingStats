package com.example.bookstats.features.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.bookstats.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LibraryFragment : Fragment() {
    private lateinit var addButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addButton = requireView().findViewById(R.id.button_add_book)
        addButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_libraryFragment_to_bookCreationFragment)
        }
    }
}