import com.example.tokopaerbe.retrofit.ApiService
import com.example.tokopaerbe.retrofit.UserPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call

class TokenAuthenticator(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val newToken = runBlocking {
                refreshAuthToken()
            }

            if (newToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
        }

        return null
    }

    private suspend fun refreshAuthToken(): String? {
        return try {
            val apiKey = "6f8856ed-9189-488f-9011-0ff4b6c08edc"
            val accessToken = userPreferences.getAccessToken().toString()

            val refreshResponse = apiService.uploadDataRefresh(apiKey, accessToken).execute()

            if (refreshResponse.isSuccessful) {
                // Extract and return the new token from the response
                val newToken = refreshResponse.body()?.data?.refreshToken
                newToken
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle any exceptions that occur during the token refresh request
            e.printStackTrace()
            null
        }
    }
}
