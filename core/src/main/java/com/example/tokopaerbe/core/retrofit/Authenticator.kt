import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.tokopaerbe.core.retrofit.ApiService
import com.example.tokopaerbe.core.retrofit.RefreshRequestBody
import com.example.tokopaerbe.core.retrofit.UserPreferences
import com.example.tokopaerbe.core.retrofit.response.RefreshResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class Authenticator @Inject constructor(val preferences: UserPreferences, val chucker: Context) :
    Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            return runBlocking {
                val refreshToken = refreshAuthToken()
                Log.d("cekNewToken", refreshToken.toString())
                if (refreshToken != null) {
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $refreshToken")
                        .build()
                } else {
                    null
                }
            }
        }
    }

    private suspend fun refreshAuthToken(): String? {
        val apiKey = "6f8856ed-9189-488f-9011-0ff4b6c08edc"
        val refreshToken = preferences.getRefreshToken().first().toString()

        Log.d("cekToken", refreshToken)

        return try {
            val refreshResponse = getNewToken(apiKey, refreshToken).execute()

            if (refreshResponse.isSuccessful) {
                refreshResponse.body()?.data?.accessToken
            } else if (refreshResponse.code() == 401) {
                preferences.logout()
                preferences.install()
                "401"
            } else {
                "error"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("cekE", e.toString())
            null
        }
    }

    private fun getNewToken(apiKey: String, token: String): Call<RefreshResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val chuckerInterceptor = ChuckerInterceptor.Builder(chucker)
            .build()

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(chuckerInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.20.10.10:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(ApiService::class.java)
        val requestBody = RefreshRequestBody(token)
        return service.uploadDataRefresh(apiKey, requestBody)
    }
}
