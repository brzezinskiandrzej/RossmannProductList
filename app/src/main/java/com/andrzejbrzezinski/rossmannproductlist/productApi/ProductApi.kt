package com.andrzejbrzezinski.rossmannproductlist.productApi

import com.andrzejbrzezinski.rossmannproductlist.ProductResponse
import com.andrzejbrzezinski.rossmannproductlist.ProductResponse2
import com.andrzejbrzezinski.rossmannproductlist.ProductResponse3
import com.andrzejbrzezinski.rossmannproductlist.ProductResponse4
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("products/v2/api/Products?ShopNumber=737&PageSize=15")
    suspend fun productList(@Query("Page")page: Int): Response<ProductResponse>

    @GET("stocks/api/shops/{shopNumber}/products/{productId}/stock")
    suspend fun productStock(@Path("shopNumber")shopnumber: Int, @Path("productId")productid: Int): Response<ProductResponse2>

    @GET("products/v2/api/Products/{id}?shopNumber=735")
    suspend fun productDetailsApi(@Path("id")productid: Int): Response<ProductResponse3>

    @GET("products/api/Products/{id}/additionals/richContent")
    suspend fun productWebView(@Path("id")productid: Int): Response<ProductResponse4>

}

