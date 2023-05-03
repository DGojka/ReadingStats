package com.example.bookstats.features.library.tabs.general.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookstats.R
import com.example.bookstats.databinding.TabGeneralBinding
import com.example.bookstats.features.library.tabs.general.helpers.GeneralBookInfo

class GeneralTabViewHolder(private val binding: TabGeneralBinding) :
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
                Glide.with(itemView).load(bookImage).placeholder(R.drawable.image_place_holder)
                    .into(generalTabBookImage)
            }
        }
    }
}
