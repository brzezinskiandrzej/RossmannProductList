package com.andrzejbrzezinski.rossmannproductlist
import android.text.Html
import java.io.Serializable
data class ProductResponse(val data:Data1)
data class Data1(val products: List<Product>)
data class Product(val name: String?,val price:String?, val caption:String?,val id: Int, val pictures: List<Pictures>):Serializable
data class Pictures(val medium: String):Serializable

data class ProductResponse2(val data:Data2)
data class Data2(val availableQuantity: String?)

data class ProductResponse3(val data:Data3)
data class Data3(val name: String?,val price:String?, val caption:String?,val id: Int, val pictures: List<Pictures>)

data class ProductResponse4(val data:Data4)
data class Data4(val html: String)