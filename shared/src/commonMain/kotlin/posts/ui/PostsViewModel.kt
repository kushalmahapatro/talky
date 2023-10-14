package posts.ui

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import posts.data.PostService
import posts.data.dto.Post

data class PostsUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class PostsViewModel(engineFactory: HttpClientEngineFactory<HttpClientEngineConfig>? = null) : ViewModel() {
    private val _uiState = MutableStateFlow(PostsUiState())
    val uiState = _uiState.asStateFlow()

    private val postService = PostService.create(engineFactory)

    init {
        updatePosts()
    }

    override fun onCleared() {
        postService.close()
    }

    private fun updatePosts() {
        viewModelScope.launch {
            kotlin.runCatching {
                getPosts()
            }.onSuccess { posts ->
                _uiState.update { state ->
                    state.copy(posts = posts, isLoading = false)
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = it.error
                    )
                }
            }
        }
    }

    private suspend fun getPosts(): List<Post> {
        return postService.getPosts()
    }
}