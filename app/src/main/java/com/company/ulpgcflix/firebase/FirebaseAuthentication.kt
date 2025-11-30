package com.company.ulpgcflix.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

interface AuthCallback {
    fun onSuccess(message: String)
    fun onFailure(errorMessage: String)
}

class FirebaseAuthentication {

    private val auth: FirebaseAuth = Firebase.auth
    private val TAG = "FirebaseAuthenticationClass"


    fun RegisterUser(email: String, password: String, callback: AuthCallback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmailAndPassword: success")
                    val user = auth.currentUser
                    callback.onSuccess("Registro exitoso para ${user?.email}")
                } else {
                    Log.w(TAG, "createUserWithEmailAndPassword: failure", task.exception)
                    callback.onFailure("Fallo en el registro: ${task.exception?.message}")
                }
            }
    }


    fun LoginUser(email: String, password: String, callback: AuthCallback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmailAndPassword: success")
                    val user = auth.currentUser
                    callback.onSuccess("Inicio de sesión exitoso. Usuario: ${user?.email}")
                } else {
                    Log.w(TAG, "signInWithEmailAndPassword: failure", task.exception)
                    callback.onFailure("Fallo en el inicio de sesión: ${task.exception?.message}")
                }
            }
    }

    fun getCurrentUserID(): String? {
        return auth.currentUser?.uid
    }
}