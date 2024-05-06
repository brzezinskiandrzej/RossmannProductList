package com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces

import androidx.lifecycle.MutableLiveData
import com.andrzejbrzezinski.rossmannproductlist.CombinedComments
import com.andrzejbrzezinski.rossmannproductlist.CommentDetails
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetailsWithThumbnail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import org.w3c.dom.Comment

interface ILoadData {
    suspend fun loginAnonymously(auth: FirebaseAuth):Boolean
    suspend fun loginAndGetUserData(username: String, password: String, liked: List<String>): Boolean
    suspend fun downloadVideo(): MutableList<FilmsDetails>
    suspend fun downloadUserVideo(username: String?): MutableList<FilmsDetailsWithThumbnail>
    suspend fun downloadUserLikedVideo(username:String?): MutableList<FilmsDetailsWithThumbnail>
    suspend fun downloadExactVideo(videoNumer: String?): FilmsDetailsWithThumbnail
    suspend fun deleteVideo(film:FilmsDetailsWithThumbnail):Boolean
    //suspend fun downloadComments(filmnumber: Int):List<Comment>
    suspend fun getUserName(): String?

    suspend fun deleteUser(): Boolean

    suspend fun likeVideo(filmnumber: Int, add: Boolean): Boolean
    fun getDataAsList(dataSnapshot: DataSnapshot): List<String>
    suspend fun addComment(filmnumber: Int, username: String, comment:String):Boolean
    suspend fun downloadComments(filmnumber: Int): MutableList<CombinedComments>
    suspend fun addNewFilmToTest(newFilm: String, filmTitle: String, it: String):Boolean
    suspend fun addFilmToStorage(
        url: String?,
        username: String,
        filmTitle: String,
        progressListener: (Float) -> Unit,
        isCancelled: () -> Boolean
    ): String
    suspend fun incrementViewCount(videoUrl: String?):Boolean
}