package com.example.bookstats.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("volumes")
    suspend fun getBookByISBN(@Query("q") isbn: String): Response<ApiResponse<BookResponse>>
}

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
