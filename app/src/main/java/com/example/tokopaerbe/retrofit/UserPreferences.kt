package com.example.tokopaerbe.retrofit

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tokopaerbe.retrofit.user.ErrorState
import com.example.tokopaerbe.retrofit.user.UserLogin
import com.example.tokopaerbe.retrofit.user.UserProfile
import com.example.tokopaerbe.retrofit.user.UserRegister
import com.example.tokopaerbe.retrofit.user.ValueBottomSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences (private val database: DataStore<Preferences>) {

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
        private val CODE_KEY = intPreferencesKey("code")
        private val FAVORITE_KEY = booleanPreferencesKey("favorite")
        private val SELECTEDTEXT1_KEY = stringPreferencesKey("selectedText1")
        private val SELECTEDTEXT2_KEY = stringPreferencesKey("selectedText2")
        private val LOWEST_KEY = stringPreferencesKey("lowest")
        private val HIGHEST_KEY = stringPreferencesKey("highest")
        private val DARKTHEME_KEY = booleanPreferencesKey("darkTheme")

        fun getInstance(database: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(database)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun saveCode(errorState: ErrorState) {
        database.edit { preferences ->
            preferences[CODE_KEY] = errorState.code
        }
    }

    fun getCode(): Flow<Int> {
        return database.data.map { preferences ->
            preferences[CODE_KEY] ?: 0
        }
    }

    suspend fun saveUserProfile(sessionProfile: UserProfile) {
        database.edit { preferences ->
            preferences[USERNAME_KEY] = sessionProfile.userName
            preferences[USERIMAGE_KEY] = sessionProfile.userImage
        }
    }


    suspend fun saveUserLogin(sessionLogin: UserLogin) {
        database.edit { preferences ->
            preferences[USERNAME_KEY] = sessionLogin.userName
            preferences[USERIMAGE_KEY] = sessionLogin.userImage
            preferences[ACCESSTOKEN_KEY] = sessionLogin.accessToken
            preferences[REFRESHTOKEN_KEY] = sessionLogin.refreshToken
            preferences[EXPIRESAT_KEY] = sessionLogin.expiresAt
        }
    }

    suspend fun saveUserRegister(sessionRegister: UserRegister) {
        database.edit { preferences ->
            preferences[ACCESSTOKEN_KEY] = sessionRegister.accessToken
            preferences[REFRESHTOKEN_KEY] = sessionRegister.refreshToken
            preferences[EXPIRESAT_KEY] = sessionRegister.expiresAt
        }
    }

    fun getValueBottomSheet(): Flow<String> {
        return database.data.map { preferences ->
            preferences[SELECTEDTEXT1_KEY] ?: ""
            preferences[SELECTEDTEXT2_KEY] ?: ""
            preferences[LOWEST_KEY] ?: ""
            preferences[HIGHEST_KEY] ?: ""
        }
    }

    suspend fun saveValueBottomSheet(valueBottomSheet: ValueBottomSheet) {
        database.edit { preferences ->
            preferences[SELECTEDTEXT1_KEY] ?: valueBottomSheet.sort
            preferences[SELECTEDTEXT2_KEY] ?: valueBottomSheet.brand
            preferences[LOWEST_KEY] ?: valueBottomSheet.lowest
            preferences[HIGHEST_KEY] ?: valueBottomSheet.highest
        }
    }

    fun getAccessToken(): Flow<String> {
        return database.data.map { preferences ->
            preferences[ACCESSTOKEN_KEY] ?: ""
        }
    }

    fun getRefreshToken(): Flow<String> {
        return database.data.map { preferences ->
            preferences[REFRESHTOKEN_KEY] ?: ""
        }
    }

    fun getUserName(): Flow<String> {
        return database.data.map {preferences ->
            preferences[USERNAME_KEY] ?: ""
        }
    }

    fun getUserFirstInstallState(): Flow<Boolean> {
        return database.data.map { preferences ->
            preferences[INSTALL_KEY] ?: true
        }
    }

    fun getFavoriteState(): Flow<Boolean> {
        return database.data.map { preferences ->
            preferences[FAVORITE_KEY] ?: true
        }
    }

    fun getUserLoginState(): Flow<Boolean> {
        return database.data.map { preferences ->
            preferences[STATE_KEY] ?: false
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

    suspend fun favoriteKey() {
        database.edit { preferences ->
            preferences[FAVORITE_KEY] = false
        }
    }

    suspend fun darkTheme(value: Boolean) {
        database.edit { preferences ->
            preferences[DARKTHEME_KEY] = value
        }
    }

    fun getIsDarkState(): Flow<Boolean> {
        return database.data.map { preferences ->
            preferences[DARKTHEME_KEY] ?: false
        }
    }

    suspend fun logout() {
        database.edit { preferences ->
            preferences.clear()
        }
    }
}