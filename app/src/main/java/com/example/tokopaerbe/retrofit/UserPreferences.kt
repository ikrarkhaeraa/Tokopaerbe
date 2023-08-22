package com.example.tokopaerbe.retrofit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val database: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val USERNAME_KEY = stringPreferencesKey("userName")
        private val USERIMAGE_KEY = stringPreferencesKey("userImage")
        private val ACCESSTOKEN_KEY = stringPreferencesKey("accessToken")
        private val REFRESHTOKEN_KEY = stringPreferencesKey("refreshToken")
        private val EXPIRESAT_KEY = longPreferencesKey("expiresAt")
        private val STATE_KEY = booleanPreferencesKey("state")
        private val INSTALL_KEY = booleanPreferencesKey("install")

        fun getInstance(database: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(database)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUserSession(): Flow<UserSession> {
        return database.data.map { preferences ->
            UserSession(
                preferences[USERNAME_KEY] ?: "",
                preferences[USERIMAGE_KEY] ?: "",
                preferences[ACCESSTOKEN_KEY] ?: "",
                preferences[REFRESHTOKEN_KEY] ?: "",
                preferences[EXPIRESAT_KEY] ?: 0L,
                preferences[STATE_KEY] ?: false,
                preferences[INSTALL_KEY] ?: true,
            )
        }
    }


    suspend fun saveUserSession(session: UserSession) {
        database.edit { preferences ->
            preferences[USERNAME_KEY] = session.userName
            preferences[USERIMAGE_KEY] = session.userImage
            preferences[ACCESSTOKEN_KEY] = session.accessToken
            preferences[REFRESHTOKEN_KEY] = session.refreshToken
            preferences[EXPIRESAT_KEY] = session.expiresAt
            preferences[STATE_KEY] = session.isLogin
            preferences[INSTALL_KEY] = session.isfirstInstall
        }
    }


    suspend fun login() {
        database.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    suspend fun install() {
        database.edit { preferences ->
            preferences[INSTALL_KEY] = false
        }
    }

    suspend fun logout() {
        database.edit { preferences ->
            preferences.clear()
        }
    }
}