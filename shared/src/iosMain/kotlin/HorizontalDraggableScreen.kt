import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/*
Allows Screen to be dragged horizontally (to the right)
 */

@Composable
inline fun <reified C : Parcelable> HorizontalDraggableScreen(
    screenStackIndex: Int = 0,
    navigationViewModel: NavigationViewModel<C>,
    content: @Composable (ColumnScope.() -> Unit),
    ){

    val navigationUiState = navigationViewModel.navigationState.collectAsState()
    val screenXOffset = navigationUiState.value.screenXOffset
    val topScreenXOffset = navigationUiState.value.topScreenXOffset
    val prevScreenXOffset = navigationUiState.value.prevScreenXOffset


    val prevScreenIndex = navigationUiState.value.prevScreenIndex
    val animatedTopXOffset = animateFloatAsState(targetValue = topScreenXOffset)
    val animatedPrevXOffset = animateFloatAsState(targetValue = prevScreenXOffset)
    val navigationBackStack = navigationUiState.value.navigationBackStack
    val isTopScreen = screenStackIndex == navigationBackStack.lastIndex && screenStackIndex != prevScreenIndex



    LaunchedEffect(key1 = navigationBackStack.size){
        if(prevScreenIndex == screenStackIndex){
            delay(500)
            navigationViewModel.resetScreenXOffset()
        }
    }

    DisposableEffect(key1 = Unit){
        onDispose {
            navigationViewModel.cleanUpXOffset()
        }
    }

    Column(
        content = content,
        modifier = Modifier
            .offset {
                if (isTopScreen) {
                    IntOffset(animatedTopXOffset.value.roundToInt(), 0)
                } else {
                    IntOffset(animatedPrevXOffset.value.roundToInt(), 0)
                }
            }
            .draggable(
                enabled = ((screenStackIndex != 0) && prevScreenIndex != screenStackIndex),
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    navigationViewModel.horizontalScreenDragEnded(xBreakPoint = screenXOffset / 2)

                },
                state = rememberDraggableState { delta ->
                    navigationViewModel.updateTopScreenXOffset(delta)
                }
            )
    )

}