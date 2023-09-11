import android.util.Log
import com.example.tokopaerbe.retrofit.ApiService
import com.example.tokopaerbe.retrofit.UserPreferences
import com.example.tokopaerbe.retrofit.response.RefreshResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.Semaphore

class Authenticator(
    private val apiService: ApiService,
    private val preferences: UserPreferences
) : Authenticator {

    val apiKey = "6f8856ed-9189-488f-9011-0ff4b6c08edc"
    val accessToken = preferences.getAccessToken().toString()

    override fun authenticate(route: Route?, response: Response): Request? {

        synchronized(this) {
            runBlocking {
                refreshAuthToken()
                return@runBlocking
            }

            Log.d("cekNewToken", response.request.newBuilder().toString())

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${response.request.newBuilder()}")
                .build()

        }
    }

    private suspend fun refreshAuthToken(): Any? {
        return try {

            val refreshResponse = getNewToken(accessToken).execute()

            if (refreshResponse.isSuccessful) {
                return refreshResponse.body()?.data?.refreshToken
            } else if (refreshResponse.code() == 401) {
                preferences.install()
                return preferences.logout()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getNewToken(refreshToken: String?): Call<RefreshResponse> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.20.235:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(ApiService::class.java)
        return service.uploadDataRefresh(apiKey, refreshToken.toString())
    }

}










