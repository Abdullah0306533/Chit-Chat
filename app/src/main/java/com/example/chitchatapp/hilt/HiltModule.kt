package com.example.chitchatapp.hilt

import com.example.chitchatapp.repository.SignInRepository
import com.example.chitchatapp.repository.UpdateDataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModule {
    @Provides
    @Singleton
    fun getFireBaseAuth():FirebaseAuth= Firebase.auth

    @Provides
    @Singleton
    fun getFireBaseFireStore():FirebaseFirestore=Firebase.firestore
    @Provides
    @Singleton
    fun getUpdateDataRepository(authentication: FirebaseAuth,
                                db: FirebaseFirestore):UpdateDataRepository{
        return UpdateDataRepository(authentication,db)
    }
    @Provides
    @Singleton
    fun getSignInRepository(authentication: FirebaseAuth,
                            db: FirebaseFirestore,updateDataRepository: UpdateDataRepository):SignInRepository{
        return SignInRepository(authentication,db,updateDataRepository)

    }
}