package com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.andrzejbrzezinski.rossmannproductlist.CombinedComments
import com.andrzejbrzezinski.rossmannproductlist.CommentDetails
import com.andrzejbrzezinski.rossmannproductlist.CommentsQuantity
import com.andrzejbrzezinski.rossmannproductlist.Film
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetailsWithThumbnail
import com.andrzejbrzezinski.rossmannproductlist.User
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities.LoginUserActivity
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine





class LoadFirebaseData @Inject constructor(private val firebaseDatabase: FirebaseDatabase, private var loginStateService: LoginStateService) : ILoadData {
    override suspend fun loginAnonymously(auth: FirebaseAuth): Boolean = suspendCoroutine { cont ->
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInAnonymously:success")
                cont.resume(true)
            } else {
                Log.w(TAG, "signInAnonymously:failure", task.exception)
                cont.resume(false)
            }
        }
    }


    override suspend fun loginAndGetUserData(
        username: String,
        password: String,
        liked: List<String>
    ) =
        suspendCoroutine<Boolean> { cont ->
            val connection = firebaseDatabase.getReference("users")
            connection.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //val userlist = mutableListOf<User>()
                    var userExist = false
                    for (userSnapshot in snapshot.children) {
                        if (userSnapshot.child("username")
                                .getValue(String::class.java) == username
                        ) {

                            //loginStateService.setUser(userSnapshot.key.toString(),true)
                            loginStateService.setUser(userSnapshot.key.toString(), true)
//                        loginStateService.username=
//                        loginStateService.account=true
                            //LoginState.username = userSnapshot.key.toString()
                            userExist = true
                            break
                        }
                    }
                    if (!userExist) {
                        val usersCount = snapshot.childrenCount
                        val nextUserId = usersCount + 1
                        val newUser = User(username, password, liked)
                        connection.child("User$nextUserId").setValue(newUser)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    //loginStateService.setUser("User$nextUserId",true)
//                            loginStateService.username="User$nextUserId"
//                            loginStateService.account=true
                                    loginStateService.setUser("User$nextUserId", true)
                                    //LoginState.username = "User$nextUserId"
                                    cont.resume(true)
                                } else {
                                    cont.resumeWithException(
                                        it.exception
                                            ?: Exception("Firebase task failed without exception")
                                    )
                                }
                            }
                    } else {
                        cont.resume(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    cont.resumeWithException(error.toException())

                }
            })

        }

    override suspend fun downloadVideo() = suspendCoroutine { cont ->
        /* firebaseDatabase.getReference("test").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val filmlist = mutableListOf<FilmsDetails>()
                for (userSnapshot in dataSnapshot.children) {

                    val name = userSnapshot.child("name").getValue(String::class.java)

                    val url = userSnapshot.child("url").getValue(String::class.java)

                    filmlist.add(FilmsDetails(url = url, name = name))


                }

               cont.resume(filmlist)

            }

            override fun onCancelled(databaseError: DatabaseError) {


                cont.resumeWithException(databaseError.toException())
            }
        })

    }*/

        firebaseDatabase.getReference("test").get()
            .addOnCompleteListener(object : OnCompleteListener<DataSnapshot> {
                override fun onComplete(p0: Task<DataSnapshot>) {
                    if (p0.isSuccessful) {
                        val filmlist = mutableListOf<FilmsDetails>()
                        for (userSnapshot in p0.result.children) {

                            val name = userSnapshot.child("name").getValue(String::class.java)

                            val url = userSnapshot.child("url").getValue(String::class.java)
                            val owner= userSnapshot.child("owner").getValue(String::class.java)
                            filmlist.add(FilmsDetails(url = url, name = name,owner=owner))


                        }
                        cont.resume(filmlist)
                    } else {
                        cont.resumeWithException(
                            p0.exception ?: Exception("Error with film data download")
                        )
                    }
                }
            })


    }

    override suspend fun downloadUserVideo(username: String?)= suspendCoroutine { cont ->
        firebaseDatabase.getReference("test").get()
            .addOnCompleteListener(object : OnCompleteListener<DataSnapshot> {
                override fun onComplete(p0: Task<DataSnapshot>) {
                    if (p0.isSuccessful) {
                        val filmlist = mutableListOf<FilmsDetailsWithThumbnail>()
                        for (userSnapshot in p0.result.children) {

                            val name = userSnapshot.child("name").getValue(String::class.java)

                            val url = userSnapshot.child("url").getValue(String::class.java)
                            val owner= userSnapshot.child("owner").getValue(String::class.java)
                            if(owner==username)
                                filmlist.add(FilmsDetailsWithThumbnail(url = url, name = name,owner=owner,thumbnail = null))


                        }
                        cont.resume(filmlist)
                    } else {
                        cont.resumeWithException(
                            p0.exception ?: Exception("Error with film data download")
                        )
                    }
                }
            })


    }

    override suspend fun downloadUserLikedVideo(username: String?) = suspendCoroutine{ cont ->
        firebaseDatabase.getReference("users/${loginStateService.username}/liked").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val likedFilms = task.result.children.mapNotNull { snapshot ->
                        snapshot.value.toString().also {
                            Log.d("FirebaseDebug", "Liked film key: $it")
                        }
                    }

                    val filmList = mutableListOf<FilmsDetailsWithThumbnail>()

                    if (likedFilms.isEmpty()) {
                        cont.resume(filmList)
                    } else {

                        val filmTasks = likedFilms.map { filmId ->
                            firebaseDatabase.getReference("test/$filmId").get()
                        }

                        Tasks.whenAllSuccess<DataSnapshot>(filmTasks).addOnSuccessListener { filmSnapshots ->
                            filmSnapshots.forEach { dataSnapshot ->
                                val name = dataSnapshot.child("name").getValue(String::class.java)
                                val url = dataSnapshot.child("url").getValue(String::class.java)
                                val owner = dataSnapshot.child("owner").getValue(String::class.java)
                                filmList.add(FilmsDetailsWithThumbnail(url = url, name = name, owner = owner, thumbnail = null))
                            }
                            cont.resume(filmList)
                        }.addOnFailureListener { exception ->
                            cont.resumeWithException(exception)
                        }
                    }
                } else {
                    cont.resumeWithException(task.exception ?: Exception("Error with liked films data download"))
                }
            }
    }


    override suspend fun downloadExactVideo(videoNumer: String?)= suspendCoroutine { cont ->
        firebaseDatabase.getReference("test/${videoNumer}").get()
            .addOnCompleteListener(object : OnCompleteListener<DataSnapshot> {
                override fun onComplete(p0: Task<DataSnapshot>) {
                    if (p0.isSuccessful) {



                            val name = p0.result.child("name").getValue(String::class.java)

                            val url = p0.result.child("url").getValue(String::class.java)
                            val owner= p0.result.child("owner").getValue(String::class.java)

                            cont.resume(FilmsDetailsWithThumbnail(url = url, name = name,owner=owner,thumbnail = null))




                    } else {
                        cont.resumeWithException(
                            p0.exception ?: Exception("Error with exact film data download")
                        )
                    }
                }
            })


    }

    override suspend fun deleteVideo(film: FilmsDetailsWithThumbnail) = suspendCoroutine{cont->
        val filmsRef = firebaseDatabase.getReference("test")
            filmsRef.get().addOnSuccessListener { dataSnapshot ->
                        for (userSnapshot in dataSnapshot.children) {
                            val temp = "${userSnapshot.key}"
                            if(userSnapshot.child("url").getValue((String::class.java))==film.url)
                            {
                                filmsRef.child(temp).removeValue()
                                Timber.d("Movie disliked succesfully")
                                cont.resume(true)
                                break
                            }



                        }
                    }.addOnFailureListener { exception ->
                cont.resumeWithException(Exception("Error while data read:$exception"))

            }
                }





    override suspend fun getUserName() = suspendCoroutine { cont ->
        val activeuser =
            firebaseDatabase.getReference("users/${loginStateService.username}").child("username")
        activeuser.get().addOnSuccessListener { snapshot ->


            val name = snapshot.getValue(String::class.java)
            cont.resume(name)

        }

            .addOnFailureListener {
                cont.resumeWithException(Exception("Error on downloading user name"))

            }

    }

    override suspend fun deleteUser() = suspendCoroutine { cont ->
        val activeuser = firebaseDatabase.getReference("users").child(loginStateService.username)
        activeuser.removeValue()
            .addOnSuccessListener {
                Timber.i("User deleted successfully")
                cont.resume(true)

            }
            .addOnFailureListener { e ->
                cont.resumeWithException(Exception("Error on deleting user"))

            }
    }

    override suspend fun likeVideo(filmnumber: Int, add: Boolean) = suspendCoroutine { cont ->
        val likedRef =
            firebaseDatabase.getReference("users/${loginStateService.username}").child("liked")

        likedRef.get().addOnSuccessListener { dataSnapshot ->
            val currentLikedMovies = getDataAsList(dataSnapshot)
            val newLikedMovie = "Film$filmnumber"
            if (!add) {
                if (currentLikedMovies.contains(newLikedMovie)) {
                    var i = 0
                    for (userSnapshot in dataSnapshot.children) {

                        i = userSnapshot.getKey()?.toInt() ?: 0
                        val temp = "${userSnapshot.getValue(String::class.java)}"
                        //userSnapshot.child("$i").getValue(String::class.java)?.let { Log.w(TAG, it) }
                        if (temp == newLikedMovie) {
                            likedRef.child("$i").removeValue()
                            Timber.d("Movie disliked succesfully")
                            cont.resume(true)
                            break
                        }
                    }/*
                    likedRef.setValue(currentLikedMovies2).addOnSuccessListener {
                        Log.d("Firebase", "Movie disliked succesfully")
                    }.addOnFailureListener { exception ->
                        Log.e("Firebase", "Error while data update: ", exception)
                    }*/
                } else {
                    cont.resumeWithException(Exception("Movie isn't on liked list of this user"))

                }
            } else {
                val updatedLikedMovies = currentLikedMovies.toMutableList().apply {
                    add(newLikedMovie)
                }

                likedRef.setValue(updatedLikedMovies).addOnSuccessListener {
                    Timber.d("Movie liked succesfully")
                    cont.resume(true)
                }.addOnFailureListener { exception ->
                    cont.resumeWithException(Exception("Error while data update:${exception}"))

                }
            }
        }.addOnFailureListener { exception ->
            cont.resumeWithException(Exception("Error while data read:$exception"))

        }
    }

    override fun getDataAsList(dataSnapshot: DataSnapshot): List<String> {
        return if (dataSnapshot.value is Map<*, *>) {
            (dataSnapshot.value as Map<*, String>).values.toList()
        } else {
            dataSnapshot.getValue<List<String>>() ?: listOf()
        }
    }

    override suspend fun addComment(filmnumber: Int, username: String, comment: String) =
        suspendCoroutine { cont ->

            val connection =
                firebaseDatabase.getReference("test/Film${filmnumber}").child("comments")
            connection.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val commentsCount = snapshot.childrenCount
                    val nextcomment = commentsCount + 1
                    val newcomment = CommentDetails(comment, username)
                    connection.child("comment$nextcomment").setValue(newcomment)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Timber.d("Comment added succesfully")
                                cont.resume(true)
                            } else {
                                cont.resumeWithException(
                                    it.exception ?: Exception("Error while comment update")
                                )
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {

                    cont.resumeWithException(error.toException())

                }
            })


        }

    override suspend fun downloadComments(filmnumber: Int): MutableList<CombinedComments> =
        suspendCoroutine { cont ->
            /*firebaseDatabase.getReference("test/Film${filmnumber}/comments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commentslist = mutableListOf<CommentDetails>()
                var i=0
                for (userSnapshot in dataSnapshot.children) {

                    val text = userSnapshot.child("text").getValue(String::class.java)

                    val user = userSnapshot.child("user").getValue(String::class.java)

                    commentslist.add(CommentDetails(text = text, user = user))

                    i++
                }
                val result= commentslist.map{comment->
                    CombinedComments(comment, CommentsQuantity(i))
                }


                cont.resume(result.toMutableList())

            }

            override fun onCancelled(databaseError: DatabaseError) {


                cont.resumeWithException(databaseError.toException())
            }
        })*/
            firebaseDatabase.getReference("test/Film${filmnumber}/comments").get()
                .addOnCompleteListener(object : OnCompleteListener<DataSnapshot> {
                    override fun onComplete(p0: Task<DataSnapshot>) {
                        if (p0.isSuccessful) {
                            val commentslist = mutableListOf<CommentDetails>()
                            var i = 0
                            for (userSnapshot in p0.result.children) {

                                val text = userSnapshot.child("text").getValue(String::class.java)

                                val user = userSnapshot.child("user").getValue(String::class.java)

                                commentslist.add(CommentDetails(text = text, user = user))

                                i++
                            }
                            val result = commentslist.map { comment ->
                                CombinedComments(comment, CommentsQuantity(i))
                            }


                            cont.resume(result.toMutableList())
                        } else {
                            cont.resumeWithException(
                                p0.exception ?: Exception("Error during comment download")
                            )
                        }
                    }
                })
        }


    override suspend fun addFilmToStorage(
        url: String?,
        username: String,
        filmTitle: String,
        progressListener: (Float) -> Unit,
        isStopped: () -> Boolean
    ): String = suspendCoroutine { cont ->

        val file = Uri.parse(url)

        val storageRef = Firebase.storage.reference.child("${filmTitle}")
        val uploadTask = storageRef.putFile(file)
            uploadTask.addOnProgressListener {
                if (isStopped()) {
                    uploadTask.cancel()

                }
                progressListener.invoke(it.bytesTransferred.toFloat()/it.totalByteCount.toFloat())

            }
        uploadTask.addOnFailureListener {exception->
            if (exception is CancellationException) {
                cont.resumeWithException(exception)
            } else {
                cont.resumeWithException(Exception("Error during film upload"))
            }

        }.addOnSuccessListener { taskSnapshot ->
            if(!isStopped()) {
                Timber.d("Film added succesfully")
                val uploadedFileRef = taskSnapshot.metadata?.reference
                uploadedFileRef?.getDownloadUrl()?.addOnSuccessListener { downloadUri ->

                    val downloadUrl = downloadUri.toString()

                    cont.resume(downloadUrl)
                }
            }

        }

    }

    override suspend fun addNewFilmToTest(newFilm: String, filmTitle: String, it: String): Boolean = suspendCoroutine<Boolean> { cont ->
        val connection = firebaseDatabase.getReference("test")
        connection.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var filmExist=false
                for (userSnapshot in snapshot.children) {
                    if (userSnapshot.child("url").getValue(String::class.java) == newFilm) {
                        filmExist = true
                        break
                    }
                }
                if (!filmExist) {
                    val filmCount = snapshot.childrenCount
                    val nextFilmId = filmCount + 1
                    val newFilm = Film(newFilm, filmTitle, it)
                    connection.child("Film$nextFilmId").setValue(newFilm)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                cont.resume(true)
                            } else {
                                cont.resumeWithException(
                                    it.exception
                                        ?: Exception("Firebase task failed without exception")
                                )
                            }
                        }
                } else {
                    Timber.i("This Film already exists in database")
                    cont.resume(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                cont.resumeWithException(error.toException())

            }
        })
    }
}