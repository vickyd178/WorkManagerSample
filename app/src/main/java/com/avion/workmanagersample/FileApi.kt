package com.avion.workmanagersample

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

const val BaseUrl = "https://images4.alphacoders.com"

//const val BaseUrl = "https://user-images.githubusercontent.com"
//https://images4.alphacoders.com/103/1038322.jpg
//https://user-images.githubusercontent.com/44257172/167682389-58b2b3b6-8c51-4c00-8acc-38f050f2f585.jpg
interface FileApi {

    @GET("/103/1038322.jpg")
    suspend fun downloadImage(): Response<ResponseBody>

    companion object {
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl(BaseUrl)
                .build().create(FileApi::class.java)
        }
    }
}