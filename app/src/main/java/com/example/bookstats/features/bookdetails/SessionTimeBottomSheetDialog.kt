package com.example.bookstats.features.bookdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookstats.R
import com.example.bookstats.databinding.HoursPickerBottomsheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SessionTimeBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: HoursPickerBottomsheetDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HoursPickerBottomsheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun getTheme(): Int {
//        return R.style.BottomSheetDialogStyle // Ustawienie nowego stylu dla dialogu
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can set up your button click listener here
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
}