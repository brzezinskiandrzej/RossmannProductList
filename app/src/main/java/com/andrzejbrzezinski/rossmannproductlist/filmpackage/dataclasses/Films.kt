package com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "films")
data class Films(
    @PrimaryKey val filmid:Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "url") val url: String?,
    @ColumnInfo(name="owner") val owner: String?
)
@Entity(tableName = "users")
data class Users(
    @PrimaryKey val userid:Int,
    @ColumnInfo(name = "liked") val liked: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "username") val username: String?
)
