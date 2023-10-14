package posts.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import posts.data.dto.Post

@Composable
fun PostItem(post: Post, onItemClick: (Post) -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onItemClick(post) }
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Id: ${post.id}")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "By: ${post.userId}")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = post.title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = post.body)
    }
}

