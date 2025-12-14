package com.company.ulpgcflix.firebase

import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreRepository {
    private val database = Firebase.firestore

    fun getRealtimeCollectionUpdates(
        collectionPath: String,
        queryBuilder: (Query) -> Query = { it }
    ): Flow<QuerySnapshot> = callbackFlow {
        val collectionRef = database.collection(collectionPath)
        val query = queryBuilder(collectionRef)
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot)
            }
        }
        awaitClose {
            registration.remove()
        }
    }
    suspend fun deleteDocumentAndDependents(
        rootCollectionPath: String,
        documentId: String,
        membersCollectionPath: String,
        memberFieldName: String
    ) {
        // PASO 1: Encontrar todos los documentos dependientes
        val membersSnapshot = database.collection(membersCollectionPath)
            .whereEqualTo(memberFieldName, documentId)
            .get()
            .await() // Usa await() para suspender la llamada

        // PASO 2: Crear el Batch Write
        val batch = database.batch()

        // 2a. Añadir el borrado de CADA documento dependiente al lote
        for (document in membersSnapshot.documents) {
            batch.delete(document.reference)
        }

        // 2b. Añadir el borrado del documento principal al lote
        val rootDocumentRef = database.collection(rootCollectionPath).document(documentId)
        batch.delete(rootDocumentRef)

        // PASO 3: Ejecutar el Batch Write de forma suspendida
        try {
            batch.commit().await()
        } catch (e: Exception) {
            // Relanzar la excepción si el commit falla
            throw e
        }
    }
    suspend fun readDocumentsByFieldSuspended(
        collectionPath: String,
        fieldName: String,
        fieldValue: Any
    ): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val querySnapshot = database.collection(collectionPath)
                .whereEqualTo(fieldName, fieldValue)
                .get()
                .await()
            querySnapshot.documents.mapNotNull { doc ->
                doc.data?.plus("id" to doc.id)
            }
        } catch (e: Exception) {
            println("Error al buscar documentos en $collectionPath por $fieldName=$fieldValue: ${e.message}")
            emptyList()
        }
    }
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
        query: (CollectionReference) -> Query,
        onSuccess: (QuerySnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val collectionRef = database.collection(collectionPath)
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

    suspend fun readDocumentSuspended(
        collectionPath: String,
        documentId: String
    ): Map<String, Any>? = suspendCoroutine { continuation ->
        database.collection(collectionPath).document(documentId).get()
            .addOnSuccessListener { document ->
                continuation.resume(document.data)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    suspend fun createDocumentSuspended(
        collectionPath: String,
        data: Map<String, Any>,
        documentId: String? = null
    ): DocumentReference = suspendCoroutine { continuation ->

        val collectionRef = database.collection(collectionPath)
        val task = if (documentId != null) {
            // Si hay ID, usa .set() para sobrescribir o crear con ID específico
            collectionRef.document(documentId).set(data)
        } else {
            // Si no hay ID, usa .add() para crear un nuevo documento con ID autogenerado
            collectionRef.add(data)
        }

        task.addOnSuccessListener { result ->
            val docRef = if (documentId != null) {
                // Si usamos .set(), la referencia es la que definimos
                collectionRef.document(documentId)
            } else {
                // Si usamos .add(), el resultado es la DocumentReference
                result as? DocumentReference
            }

            docRef?.let {
                continuation.resume(it) // Devuelve la referencia del documento
            } ?: continuation.resumeWithException(
                IllegalStateException("Firebase add/set succeeded but DocumentReference was null.")
            )

        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception) // Devuelve el error
        }

    }


}