package com.company.ulpgcflix.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FirebaseFirestore {
    private val database = Firebase.firestore

    fun createDocument(
        collectionPath: String,
        data: Map<String, Any>,
        documentId: String? = null,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val collectionRef = database.collection(collectionPath)
        val task = if (documentId != null) {
            collectionRef.document(documentId).set(data)
        } else {
            collectionRef.add(data)
        }

        task.addOnSuccessListener {
            val docRef = if (documentId != null) collectionRef.document(documentId) else it as? DocumentReference
            docRef?.let(onSuccess) ?: onFailure(Exception("No se pudo obtener la DocumentReference."))
        }.addOnFailureListener(onFailure)
    }
    fun queryCollection(
        collectionPath: String,
        query: (CollectionReference) -> Query, // La lambda recibe CollectionReference y debe retornar un objeto Query
        onSuccess: (QuerySnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val collectionRef = database.collection(collectionPath)

        // Aplica el filtro/ordenación definido en la lambda 'query' y luego ejecuta la lectura
        query(collectionRef).get()
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun readDocument(
        collectionPath: String,
        documentId: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.collection(collectionPath).document(documentId).get()
            .addOnSuccessListener { document ->
                onSuccess(document.data)
            }.addOnFailureListener(onFailure)
    }


    fun readCollection(
        collectionPath: String,
        onSuccess: (QuerySnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.collection(collectionPath).get()
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }


    fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.collection(collectionPath).document(documentId).set(updates, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener(onFailure)
    }


    fun deleteDocument(
        collectionPath: String,
        documentId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        database.collection(collectionPath).document(documentId).delete()
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener(onFailure)
    }
}