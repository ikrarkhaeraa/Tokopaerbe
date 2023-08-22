package com.example.tokopaerbe.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.tokopaerbe.retrofit.response.LoginResponse
import com.example.tokopaerbe.retrofit.response.ProfileResponse
import com.example.tokopaerbe.retrofit.response.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataSource(private val pref: UserPreferences) {

    companion object {
        @Volatile
        private var instance: DataSource? = null
        fun getInstance(preferences: UserPreferences): DataSource =
            instance ?: synchronized(this) {
                instance ?: DataSource(preferences)
            }.also { instance = it }
    }

    private val _signUp = MutableLiveData<RegisterResponse>()
    val signUp: LiveData<RegisterResponse> = _signUp

    private val _signIn = MutableLiveData<LoginResponse>()
    val signIn: LiveData<LoginResponse> = _signIn

    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile


    fun uploadRegisterData(API_KEY: String, email:String, password:String, firebaseToken: String) {
        val requestBody = RegisterRequestBody(email, password, firebaseToken)
        val client = ApiConfig.getApiService().uploadDataRegister(API_KEY, requestBody)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("signUpResponse", "onResponse: ${response.message()}")
                    _signUp.value = response.body()
                } else {
                    Log.e("signUp", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("signUpFailure", "onFailure: ${t.message}")
            }
        })
    }


    fun uploadLoginData(API_KEY: String, email:String, password:String, firebaseToken: String) {
        val requestBody = LoginRequestBody(email, password, firebaseToken)
        val client = ApiConfig.getApiService().uploadDataLogin(API_KEY, requestBody)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("signInResponse", "onResponse: ${response.message()}")
                    _signIn.value = response.body()
                } else {
                    Log.e("signIn", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("signInFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun uploadProfileData(auth: String, userName:MultipartBody.Part, userImage: MultipartBody.Part) {
//        val requestBody = ProfileRequestBody(userName, userImage)
        val client = ApiConfig.getApiService().uploadDataProfile(auth, userName, userImage)
        client.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("profileResponse", "onResponse: ${response.message()}")
                    _profile.value = response.body()
                } else {
                    Log.e("profile", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("profileFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun getUserSession(): LiveData<UserSession> {
        return pref.getUserSession().asLiveData()
    }

    suspend fun saveSession(session: UserSession) {
        pref.saveUserSession(session)
    }

    suspend fun userLogin() {
        pref.login()
    }

    suspend fun userInstall() {
        pref.install()
    }

    suspend fun userLogout() {
        pref.logout()
    }

}