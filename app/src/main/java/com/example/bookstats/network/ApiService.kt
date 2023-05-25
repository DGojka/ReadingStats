package com.example.bookstats.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("volumes")
    suspend fun getBookByISBN(@Query("q") isbn: String): Response<ApiResponse<BookResponse>>
}
