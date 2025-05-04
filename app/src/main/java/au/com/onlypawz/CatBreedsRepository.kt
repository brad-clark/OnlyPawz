package au.com.onlypawz

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CatBreedsRepository(val httpClient: HttpClient) {
    suspend fun getCatBreeds(): CatBreeds {
        val response: CatBreeds = httpClient.get(BASE_URL).body()
        return response
    }

    companion object {
        private const val BASE_URL =
            "https://firebasestorage.googleapis.com/v0/b/only-pawz.firebasestorage.app/o/cat_breeds.json?alt=media&token=8fa1149d-e633-4303-b5bc-fb5aa1008f3e"
    }
}