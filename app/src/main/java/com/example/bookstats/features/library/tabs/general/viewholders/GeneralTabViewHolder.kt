package com.example.bookstats.features.library.tabs.general.viewholders

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bookstats.databinding.TabGeneralBinding
import com.example.bookstats.features.library.tabs.general.helpers.GeneralBookInfo

class GeneralTabViewHolder(
    private val binding: TabGeneralBinding,
    private val onStartSessionClick: () -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(generalBookInfo: GeneralBookInfo) {
        with(binding) {
            generalBookInfo.apply {
                generalTabBookName.text = bookName
                generalTabBookAuthor.text = bookAuthor
                generalAvgSessionTimeValue.text = avgReadingTime
                generalAvgMinPageValue.text = avgMinutesPerPage
                generalAvgPagesHourValue.text = avgPagesPerHour
                generalTotalReadTimeValue.text = totalReadTime
                binding.generalTabBookImage.load(image)
            }
            continueReading.setOnClickListener {
                onStartSessionClick()
            }
        }
    }
}
