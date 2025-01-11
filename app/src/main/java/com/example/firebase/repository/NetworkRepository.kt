package com.example.firebase.repository

import com.example.firebase.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NetworkRepositoryMhs(
    private val firestore: FirebaseFirestore
) : RepositoryMhs {
    override suspend fun insertMhs (mahasiswa: Mahasiswa) {
        try {
            firestore.collection ("Mahasiswa").add (mahasiswa) .await ()

        } catch (e: Exception) {
            throw Exception ("Gagal Menambahkan data Mahasiswa:  ${e.message}")
        }
    }

    override fun getAllMhs(): Flow<List<Mahasiswa>> = callbackFlow{
        val mhsCollection = firestore.collection("Mahasiswa")
            .orderBy("nim", Query.Direction.ASCENDING)
            .addSnapshotListener{
                    value, error ->
                if (value != null){
                    val mhsList = value.documents.mapNotNull {
                        //convert dari document firestore ke data class
                        it.toObject(Mahasiswa::class.java)
                    }
                    //untuk mengirim colection ke data class
                    trySend(mhsList)
                }
            }
        awaitClose {
            //menutup collection dari firestore
            mhsCollection.remove()
        }
    }

    override fun getMhs(nim: String): Flow<Mahasiswa> = callbackFlow {
        val mhsDocumented = firestore.collection("Mahasiswa")
            .document(nim)
            .addSnapshotListener{value, error ->
                if (value != null) {
                    val mhs = value.toObject(Mahasiswa::class.java)!!
                }
            }
        awaitClose{
            mhsDocumented.remove()
        }
    }

    override suspend fun deleteMhs(mahasiswa: Mahasiswa) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMhs(mahasiswa: Mahasiswa) {
        TODO("Not yet implemented")
    }

}