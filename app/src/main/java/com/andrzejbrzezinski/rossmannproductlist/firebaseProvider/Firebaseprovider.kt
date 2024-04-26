package com.andrzejbrzezinski.rossmannproductlist.firebaseProvider

import com.andrzejbrzezinski.rossmannproductlist.objects.LoginState
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object Firebaseprovider {
   /* val firebase: DatabaseReference =
        FirebaseDatabase.getInstance("https://users-88640-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users/${LoginState.username}")*/
    val firebase: FirebaseDatabase = FirebaseDatabase.getInstance()
}