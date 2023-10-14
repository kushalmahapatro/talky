import Screen.Details
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimator
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import platform.UIKit.UIViewController
import postDetails.PostDetails
import posts.ui.PostsViewModel
import posts.ui.components.PostList


actual fun getPlatformName(): String = "iOS"

fun MainViewController(): UIViewController {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)


    return ComposeUIViewController {
        CompositionLocalProvider(LocalScrollbarStyle provides defaultScrollbarStyle()) {
            ProvideComponentContext(rootComponentContext) {
                val navigation = remember { StackNavigation<Screen>() }

                PostScreen()


//                val navigator: NavigationViewModel<Screen> = getViewModel(Unit, viewModelFactory {
//                    NavigationViewModel(navigation)
//                })
//
//
//                val postsViewModel: PostsViewModel = getViewModel(Unit, viewModelFactory {
//                    PostsViewModel()
//                })
//
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                ) {
//                    NavigationScreen(
//                        navigationViewModel = navigator
//                    ) { screen ->
//                        when (screen) {
//                            is Screen.List -> {
//                                PostList(
//                                    postsViewModel,
//                                    onItemClick = {
//                                        navigator.push(Details(post = it))
//                                    },
//                                )
//                            }
//
//                            is Screen.Details -> {
//                                PostDetails(post = screen.post, onBack = navigator::pop)
//                            }
//                        }
//                    }
//
//                }
            }
        }
    }
}

fun onBackGesture() {
//    store.send(Action.OnBackPressed)
}


@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    LaunchedEffect(isEnabled) {
        store.events.collect {
            if (isEnabled) {
                onBack()
            }
        }
    }
}

@OptIn(ExperimentalDecomposeApi::class)
actual fun <C : Any, T : Any> backAnimation(
    backHandler: BackHandler,
    onBack: () -> Unit,
): StackAnimation<C, T> = predictiveBackAnimation(
    backHandler = backHandler,
    animation = stackAnimation(iosLikeSlide()),
    exitModifier = { progress, _ -> Modifier.slideExitModifier(progress = progress) },
    enterModifier = { progress, _ -> Modifier.slideEnterModifier(progress = progress) },
    onBack = onBack,
)

private fun iosLikeSlide(animationSpec: FiniteAnimationSpec<Float> = tween()): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, direction, content ->
        content(
            Modifier.then(if (direction.isFront) Modifier else Modifier.fade(factor + 1F))
                .offsetXFactor(factor = if (direction.isFront) factor else factor * 0.5F)
        )
    }

private fun Modifier.slideExitModifier(progress: Float): Modifier = offsetXFactor(progress)

private fun Modifier.slideEnterModifier(progress: Float): Modifier =
    fade(progress).offsetXFactor((progress - 1f) * 0.5f)

private fun Modifier.fade(factor: Float) = drawWithContent {
    drawContent()
    drawRect(color = Color(red = 0F, green = 0F, blue = 0F, alpha = 0F)/* (1F - factor) / 4F)*/)
}

private fun Modifier.offsetXFactor(factor: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x = (placeable.width.toFloat() * factor).toInt(), y = 0)
    }
}