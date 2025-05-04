package au.com.onlypawz

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val modules by lazy { listOf(networkModule, repositoryModule, viewModelModule) }

val networkModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }

        }
    }
}

val repositoryModule = module {
    single {
        CatBreedsRepository(get())
    }
}

val viewModelModule = module {
    viewModel { PawzViewModel(get()) }
}
