package postDetails

import BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import posts.data.dto.Post

@Composable
fun PostDetails(post: Post, onBack: () -> Unit) {

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = post.body)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text(text = "Back")
        }

        BackHandler(isEnabled = true, onBack = onBack)
    }
}