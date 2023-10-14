package posts.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import posts.data.dto.Post

class PostServiceImpl(private val client: HttpClient) : PostService {
    override suspend fun getPosts(): List<Post> {
        return client.get(HttpRoutes.POSTS).body<List<Post>>()
    }

    override fun close() {
        client.close()
    }
}