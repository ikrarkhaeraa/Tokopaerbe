package com.example.tokopaerbe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tokopaerbe.retrofit.DataSource
import com.example.tokopaerbe.retrofit.UserSession
import com.example.tokopaerbe.retrofit.response.LoginResponse
import com.example.tokopaerbe.retrofit.response.ProfileResponse
import com.example.tokopaerbe.retrofit.response.RegisterResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ViewModel(private val data: DataSource) : ViewModel() {

    val profile: LiveData<ProfileResponse> = data.profile
    val signIn: LiveData<LoginResponse> = data.signIn
    val signUp: LiveData<RegisterResponse> = data.signUp

    fun getUserSession(): LiveData<UserSession> {
        return data.getUserSession()
    }

    fun userInstall() {
        viewModelScope.launch {
            data.userInstall()
        }
    }

    fun saveUserSession(session: UserSession) {
        viewModelScope.launch {
            data.saveSession(session)
        }
    }

    fun userLogin() {
        viewModelScope.launch {
            data.userLogin()
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            data.userLogout()
        }
    }

    fun postDataProfile(auth: String, userName: MultipartBody.Part, userImage: MultipartBody.Part) {
        viewModelScope.launch {
            data.uploadProfileData(auth, userName, userImage)
        }
    }


    fun postDataRegister(API_KEY:String, email:String, password:String, firebaseToken:String) {
        viewModelScope.launch {
            data.uploadRegisterData(API_KEY, email, password, firebaseToken)
        }
    }

    fun postDataLogin(API_KEY:String, email:String, password:String, firebaseToken:String) {
        viewModelScope.launch {
            data.uploadLoginData(API_KEY, email, password, firebaseToken)
        }
    }


}