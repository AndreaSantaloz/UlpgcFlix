package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.domain.model.Message
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import com.company.ulpgcflix.firebase.FirestoreRepository
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChannelDialogService(
        private val service: FirestoreRepository = FirestoreRepository(),
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    ) {
        fun getUserCurrently(): String {
            return auth.currentUser?.uid
                ?: throw Exception("Usuario no autenticado. Por favor, inicie sesión.")
        }

        suspend fun getNameChannel(id:String): String{
            return ((service.readDocumentSuspended("channels", id))?.get("name") as? String)
                ?: throw Exception("Nombre del canal no encontrado.")
        }

        suspend fun sendMessage(channelId: String, text: String): DocumentReference {
            val userId = getUserCurrently()

            val message = Message(
                id = "",
                idUser = userId,
                text = text,
                timestamp = System.currentTimeMillis()
            )

            return service.createDocumentSuspended(
                collectionPath = "channels/$channelId/messages",
                data = mapOf(
                    "idUser" to message.getIdUser(),
                    "text" to message.getText(),
                    "timestamp" to message.timestamp
                )
            )
        }


        fun receiveMessages(channelId: String): Flow<List<Message>> {
            val path = "channels/$channelId/messages"

            return service.getRealtimeCollectionUpdates(
                collectionPath = path,
                queryBuilder = { query ->
                    query.orderBy("timestamp", Query.Direction.ASCENDING)
                }
            ).map { snapshot ->
                snapshot.documents.map { document ->
                    Message(
                        id = document.id,
                        idUser = document.getString("idUser") ?: "",
                        text = document.getString("text") ?: "",
                        timestamp = document.getLong("timestamp") ?: 0L
                    )
                }
            }
        }
}