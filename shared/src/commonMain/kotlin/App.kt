
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import postDetails.PostDetails
import posts.ui.PostsViewModel
import posts.ui.components.PostList
import states.createStore


val store = CoroutineScope(SupervisorJob()).createStore()
@OptIn(ExperimentalResourceApi::class)
@Composable
 fun App() {
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello, World!") }
        var showImage by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = CenterHorizontally) {
            Button(onClick = {
                greetingText = "Hello, ${getPlatformName()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    contentDescription = "Compose Multiplatform icon"
                )
            }
        }
    }

}

@Composable
internal fun PostScreen(engineFactory: HttpClientEngineFactory<HttpClientEngineConfig>? = null) {
    val navigation = remember { StackNavigation<Screen>() }

    val postsViewModel: PostsViewModel = getViewModel(Unit, viewModelFactory {
        PostsViewModel(engineFactory)
    })
    ChildStack(
        source = navigation,
        initialStack = { listOf(Screen.List) },
        handleBackButton = true,
        animation = backAnimation(
            backHandler = BackDispatcher(),
            onBack =  navigation::pop
        ),
    ) { screen ->
        when (screen) {
            is Screen.List -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    PostList(
                        postsViewModel,
                        onItemClick = { navigation.push(Screen.Details(post = it)) })
                }
            }
            is Screen.Details -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colors.background)
                ) {
                    PostDetails(post = screen.post, onBack = navigation::pop)
                }
            }
        }
    }
}

@Composable
internal fun ObscureIcon(
    onClick: () -> Unit,
    obscureIcon: ImageVector = Icons.Outlined.Lock,
    nonObscureIcon: ImageVector = Icons.Outlined.Check,
) {
    var obscure by remember { mutableStateOf(true) }

    IconButton(
        onClick = {
            obscure = !obscure
            onClick()
        },
    ) {
        Icon(
            imageVector = if (obscure) obscureIcon else nonObscureIcon,
            contentDescription = "",
            tint = if (obscure) Color.Black else Color.Red
        )
    }
}


expect fun getPlatformName(): String

expect fun <C : Any, T : Any> backAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit,
): StackAnimation<C, T>
