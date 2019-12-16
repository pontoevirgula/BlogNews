package com.chsltutorials.blognews.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtils {

    fun getFirebaseAuth() = FirebaseAuth.getInstance()

    fun getFirebaseUser() = getFirebaseAuth().currentUser

    fun getFirebaseStorageInstance() = FirebaseStorage.getInstance()

    fun getFirebaseStorageReference(pathString : String) = getFirebaseStorageInstance().reference.child(pathString)

    fun getFirebaseDatabaseInstance() = FirebaseDatabase.getInstance()

    fun getFirebaseDatabaseReference(path : String) = getFirebaseDatabaseInstance().getReference(path)
}