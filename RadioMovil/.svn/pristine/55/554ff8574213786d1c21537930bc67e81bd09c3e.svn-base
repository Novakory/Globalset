package com.example.globalapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreLogin(private val context:Context) {
    companion object{
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("loginStore")
        val USER_STORE = stringPreferencesKey("user_store")
        val PASSWORD_STORE = stringPreferencesKey("password_store")

    }

    val getUserStore: Flow<String> = context.dataStore.data
        .map{ preferences -> preferences[USER_STORE] ?: ""}

    val getPasswordStore: Flow<String> = context.dataStore.data
        .map{ preferences -> preferences[PASSWORD_STORE] ?: ""}

    suspend fun saveUserStore(user:String){
        context.dataStore.edit { preferences-> preferences[USER_STORE] = user }
    }
    suspend fun savePasswordStore(password:String){
        context.dataStore.edit { preferences-> preferences[PASSWORD_STORE] = password }
    }
}