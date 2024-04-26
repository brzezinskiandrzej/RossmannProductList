package com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Films
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Users
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginState

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: Films)
    @Query("SELECT * FROM Films")
    fun getAllFilms(): List<Films>

}
@Dao
interface UserDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: Users)
    @Query("SELECT * FROM Users")
    fun getAllUsers(): List<Users>
    @Query("SELECT username FROM users WHERE username = :username")
    fun getUsername(username: String): String?
    @Query("DELETE FROM users WHERE username = :username")
    fun deleteUser(username: String): Int
    @Query("SELECT liked FROM users WHERE username = :username")
    fun selectliked(username:String):String?
    @Query("UPDATE users SET liked = :liked WHERE username = :username ")
    fun updateliked(username: String, liked: String)
}