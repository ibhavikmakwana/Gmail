package com.ibhavikmakwana.gmail.network

import com.ibhavikmakwana.gmail.model.Message

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @get:GET("inbox.json")
    val inbox: Call<List<Message>>
}