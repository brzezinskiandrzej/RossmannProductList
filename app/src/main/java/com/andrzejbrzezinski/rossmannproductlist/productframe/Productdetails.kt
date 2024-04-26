package com.andrzejbrzezinski.rossmannproductlist

import android.graphics.Bitmap
import java.io.Serializable

data class Productdetails(
    val name: String?,
    val price: String?,
    val caption: String?,
    val id: Int,
    val image: String?


)

data class Productdetailstwo(
    val availableQuantity: String?
)

data class CombinedData(val productdetails:Productdetails, val productdetailstwo:Productdetailstwo)

data class Productdetailsvdwa(
    val name: String?,
    val price: String?,
    val caption: String?,
    val id: Int,
    val image: List<Pictures>


)

data class ProductHtml(
    val html: String?
)

data class CombinedProductDetails(val productdetailsvdwa:Productdetailsvdwa, val producthtml: ProductHtml?)

data class FilmsDetails(
    val url:String?,
    val name:String?,
    val owner: String?
)
data class FilmsDetailsWithThumbnail(
    val url:String?,
    val name:String?,
    val owner: String?,
    val thumbnail: Bitmap?
)
data class CommentDetails(
    val text:String?,
    val user:String?

)
data class CommentsQuantity(
    val quantity:Int
)
data class CombinedComments(val commentdetails: CommentDetails, val commentsquantity: CommentsQuantity)
data class CombinedFilmsDetails(val filmsDetails: List<FilmsDetails>,val combinedComments: List<CombinedComments>?, val username :String?)
data class FilmWithComments(val film:FilmsDetails,var comments:List<CombinedComments>?,val username: String?)
data class User(
    val username:String?,
    val password: String?,
    val liked: List<String>?
)
data class Film(
    val url:String,
    val name:String,
    val owner:String
)