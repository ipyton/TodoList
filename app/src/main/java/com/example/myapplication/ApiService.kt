package com.example.myapplication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("maps/api/place/autocomplete/json?")
    fun autoCompelete(@Query("location") location: String,
                    @Query("input") text:String, @Query("radius") radius:Int, @Query("key") key: String, @Query("types") type:String): Call<ResponseBody>


    @GET("maps/api/place/details/json?")
    fun getDetails(@Query("place_id") placeId:String, @Query("key") key: String): Call<ResponseBody>
}