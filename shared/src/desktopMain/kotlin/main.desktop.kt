
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.essenty.backhandler.BackHandler
import io.ktor.client.engine.cio.CIO
import posts.data.dto.Post
import posts.ui.components.PostItem

actual fun getPlatformName(): String = "Desktop"

//val netowrkClient = NetworkClient.client

@Composable
fun MainView() {
    Home()
}


@Composable
internal fun Home() {
    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.2f).background(Color.Gray)
        )
        Box(
            modifier = Modifier.fillMaxWidth(1f).background(Color.White)
        ) {
            PostScreen(CIO)
        }
    }

}

@Composable
@Preview
fun PostItemPreview(){
    PostItem(post = Post(userId = 1, id = 1, title = "jnjn", body = "mnjnjn"), onItemClick = {} )
}

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
}


@OptIn(ExperimentalDecomposeApi::class)
actual fun <C : Any, T : Any> backAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit,
): StackAnimation<C, T> =
    predictiveBackAnimation(
        backHandler = backHandler,
        animation = stackAnimation(fade() + scale()),
        onBack = onBack,
    )


