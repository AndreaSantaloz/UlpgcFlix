package com.company.ulpgcflix.ui.servicios

import com.company.ulpgcflix.firebase.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SocialMediaService(
    private val firebaseService: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val CHANNELS_PATH = "channels"
    private val CHANNEL_MEMBERS_PATH = "channel_members"


    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("Usuario no autenticado. Inicie sesión.")
    }


    suspend fun createChannel(name: String, description: String, isPublic: Boolean): String =
        suspendCancellableCoroutine { continuation ->
            val userId = getUserId()
            val newChannel = mapOf(
                "name" to name,
                "description" to description,
                "ownerId" to userId,
                "isPublic" to isPublic,
                "createdAt" to System.currentTimeMillis()
            )

            firebaseService.createDocument(
                collectionPath = CHANNELS_PATH,
                data = newChannel,
                onSuccess = { docRef: DocumentReference ->
                    continuation.resume(docRef.id)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }


    suspend fun deleteChannel(channelId: String) {
        suspendCancellableCoroutine { continuation ->
            firebaseService.deleteDocument(
                collectionPath = CHANNELS_PATH,
                documentId = channelId,
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }


    suspend fun updateChannel(channelId: String, updates: Map<String, Any>) {
        suspendCancellableCoroutine { continuation ->
            firebaseService.updateDocument(
                collectionPath = CHANNELS_PATH,
                documentId = channelId,
                updates = updates,
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }


    suspend fun getChannel(channelId: String): Map<String, Any>? =
        suspendCancellableCoroutine { continuation ->
            firebaseService.readDocument(
                collectionPath = CHANNELS_PATH,
                documentId = channelId,
                onSuccess = { data ->
                    continuation.resume(data)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }


    suspend fun getChannels(): List<Map<String, Any>> =
        suspendCancellableCoroutine { continuation ->
            firebaseService.readCollection(
                collectionPath = CHANNELS_PATH,
                onSuccess = { querySnapshot ->
                    val channelsList = querySnapshot.documents.mapNotNull {
                        it.data?.plus("id" to it.id)
                    }
                    continuation.resume(channelsList)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }


    suspend fun searchChannels(query: String): List<Map<String, Any>> {
        val allChannels = getChannels()
        if (query.isBlank()) return allChannels

        return allChannels.filter { channel ->
            val name = channel["name"] as? String ?: ""
            name.contains(query, ignoreCase = true)
        }
    }


    suspend fun getFollowedChannelIds(): Set<String> =
        suspendCancellableCoroutine { continuation ->
            val userId = getUserId()
            firebaseService.queryCollection(
                collectionPath = CHANNEL_MEMBERS_PATH,
                query = { collectionRef -> collectionRef.whereEqualTo("userId", userId) },
                onSuccess = { querySnapshot ->
                    val followedIds = querySnapshot.documents
                        .mapNotNull { it.getString("channelId") }
                        .toSet()

                    continuation.resume(followedIds)
                },
                onFailure = { exception ->
                    continuation.resumeWithException(exception)
                }
            )
        }

    suspend fun followChannel(channelId: String) {
        val userId = getUserId()
        val membershipId = "${userId}_$channelId"
        val membershipData = mapOf(
            "userId" to userId,
            "channelId" to channelId,
            "followedAt" to System.currentTimeMillis()
        )

        suspendCancellableCoroutine { continuation ->
            firebaseService.createDocument(
                collectionPath = CHANNEL_MEMBERS_PATH,
                data = membershipData,
                documentId = membershipId,
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }


    suspend fun unfollowChannel(channelId: String) {
        val userId = getUserId()
        val membershipId = "${userId}_$channelId"

        suspendCancellableCoroutine { continuation ->
            firebaseService.deleteDocument(
                collectionPath = CHANNEL_MEMBERS_PATH,
                documentId = membershipId,
                onSuccess = { continuation.resume(Unit) },
                onFailure = { continuation.resumeWithException(it) }
            )
        }
    }
}