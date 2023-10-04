package com.example.tokopaerbe.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.core.app.ApplicationProvider
import com.example.tokopaerbe.core.retrofit.UserPreferences
import com.example.tokopaerbe.core.retrofit.user.ErrorState
import com.example.tokopaerbe.core.retrofit.user.UserLogin
import com.example.tokopaerbe.core.retrofit.user.UserProfile
import com.example.tokopaerbe.core.retrofit.user.UserRegister
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UserPreferencesTest {

    private lateinit var userPreferences: UserPreferences
    private lateinit var context: Context
    private val Context.database: DataStore<Preferences> by preferencesDataStore("token")

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        userPreferences = UserPreferences(context.database)
    }

    @Test
    fun testSaveCode() = runBlocking {
        val code = 200
        userPreferences.saveCode(ErrorState(code))
        val getCode = userPreferences.getCode().first()
        assertEquals(200, getCode)
    }

    @Test
    fun testSaveUserProfile() = runBlocking {
        val userName = "Username"
        val userImage = "UserImage"
        userPreferences.saveUserProfile(UserProfile(userName, userImage))
        val getUserName = userPreferences.getUserName().first()
        assertEquals(userName, getUserName)
    }

    @Test
    fun testSaveUserLogin() = runBlocking {
        val userName = "username"
        val userImage = "userImage"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val expiresAt = 0L
        userPreferences.saveUserLogin(
            UserLogin(
                userName,
                userImage,
                accessToken,
                refreshToken,
                expiresAt
            )
        )
        val getUserName = userPreferences.getUserName().first()
        val getAccessToken = userPreferences.getAccessToken().first()
        val getRefreshToken = userPreferences.getRefreshToken().first()
        assertEquals(userName, getUserName)
        assertEquals(accessToken, getAccessToken)
        assertEquals(refreshToken, getRefreshToken)
    }

    @Test
    fun testSaveUserRegister() = runBlocking {
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val expiresAt = 0L
        userPreferences.saveUserRegister(UserRegister(accessToken, refreshToken, expiresAt))
        val getAccessToken = userPreferences.getAccessToken().first()
        val getRefreshToken = userPreferences.getRefreshToken().first()
        assertEquals(accessToken, getAccessToken)
        assertEquals(refreshToken, getRefreshToken)
    }

    @Test
    fun testFirstInstall() = runBlocking {
        val firstInstall = false
        userPreferences.install()
        val userInstall = userPreferences.getUserFirstInstallState().first()
        assertEquals(firstInstall, userInstall)
    }

    @Test
    fun testSaveAndGetRefreshResponseCode() = runBlocking {
        val code = 200
        userPreferences.saveRefreshResponseCode(code)
        val getCode = userPreferences.getRefreshResponseCode().first()
        assertEquals(code, getCode)
    }

    @Test
    fun testLoginState() = runBlocking {
        val loginState = true
        userPreferences.login()
        val userLogin = userPreferences.getUserLoginState().first()
        assertEquals(loginState, userLogin)
    }

    @Test
    fun testDarkTheme() = runBlocking {
        val darkTheme = true
        userPreferences.darkTheme(darkTheme)
        val userDarkTheme = userPreferences.getIsDarkState().first()
        assertEquals(darkTheme, userDarkTheme)
    }

    @Test
    fun testLogout() = runBlocking {
        userPreferences.logout()
        val userLogin = userPreferences.getUserLoginState().first()
        val getAccessToken = userPreferences.getAccessToken().first()
        val getRefreshToken = userPreferences.getRefreshToken().first()
        assertEquals(false, userLogin)
        assertEquals("", getAccessToken)
        assertEquals("", getRefreshToken)
    }
}
