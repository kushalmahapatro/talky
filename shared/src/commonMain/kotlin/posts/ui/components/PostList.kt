package posts.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import posts.data.dto.Post
import posts.ui.PostsViewModel

@Composable
fun PostList(viewModel: PostsViewModel, onItemClick: (Post) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colors.primary,
            )
        }
    else
        LazyColumn {
            items(uiState.posts) { post ->
                PostItem(post = post, onItemClick = onItemClick)
            }
        }
}