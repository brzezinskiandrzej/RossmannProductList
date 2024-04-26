package com.andrzejbrzezinski.rossmannproductlist.apiProvider

import com.andrzejbrzezinski.rossmannproductlist.productApi.ProductApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create




object ApiProvider {


    val productApi: ProductApi by lazy{
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://v5stg.rossmann.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
      retrofit.create(ProductApi::class.java)}

}