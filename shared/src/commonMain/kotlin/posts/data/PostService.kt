package posts.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import posts.data.dto.Post

interface PostService {

    suspend fun getPosts(): List<Post>

    fun close()

    companion object {
        fun <T : HttpClientEngineConfig> create(engineFactory: HttpClientEngineFactory<T>?): PostService {
            var httpClient = HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    level = LogLevel.ALL
                }
            }
            if (engineFactory != null) {
                httpClient = HttpClient(engineFactory) {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            prettyPrint = true
                            isLenient = true
                        })
                    }
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                }
            }

            return PostServiceImpl(
                client = httpClient
            )
        }


    }
}