package com.example.bookstats.network


data class ApiResponse<T>(
    val items: List<T>
)

data class BookResponse(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val pageCount: Int,
    val imageLinks: ImageLinks?
)
data class ImageLinks(
    val smallThumbnail: String?,
    val thumbnail: String?
)