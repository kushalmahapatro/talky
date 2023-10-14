import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.parcelable.Parcelable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NavigationUIState(
    //--Back Stack for navigation--
    val navigationBackStack: SnapshotStateList<Screen> = mutableStateListOf(Screen.List),

    //--Horizontal Draggable Screen offsets--
    val screenXOffset: Float = 0.0f,
    val topScreenXOffset: Float = 0.0f,
    val prevScreenXOffset: Float = 0.0f,

    //-- Index of previous screen, this is used to fine tune animations.--
    val prevScreenIndex: Int = -1,
)

class NavigationViewModel<C>(navigation: StackNavigation<Screen>) : ViewModel() {
    val navigator = navigation;
    private val _navigationState = MutableStateFlow(NavigationUIState())
    val navigationState = _navigationState.asStateFlow()

    //-- Set value of screenXOffset and prevScreenXOffset --
    fun setScreenXOffset(offset: Float) {
        _navigationState.update { state ->
            state.copy(
                screenXOffset = offset,
                prevScreenXOffset = -offset / 8
            )
        }
    }

    //-- Push new screen on stack --
    fun push(screenRoute: Screen) {
        _navigationState.value.navigationBackStack.add(screenRoute)
        navigator.push(screenRoute)
    }

    //-- Pop top of stack --
    fun pop() {
        val currentStack = _navigationState.value.navigationBackStack
        navigationState.value.navigationBackStack.removeAt(currentStack.lastIndex)
        navigator.pop()
    }

    //-- Update XOffset for top and previous Screen --
    fun updateTopScreenXOffset(delta: Float) {
        val topScreenXOffset = _navigationState.value.topScreenXOffset
        val prevXOffset = _navigationState.value.prevScreenXOffset
        if (topScreenXOffset + delta >= 0.0f) {
            _navigationState.update { state ->
                state.copy(
                    topScreenXOffset = topScreenXOffset + delta,
                    prevScreenXOffset = prevXOffset + delta / 8
                )
            }
        }
    }

    //-- Reset values  for Top and previous screen XOffsets --
    fun resetScreenXOffset() {
        val screenXOffset = _navigationState.value.screenXOffset
        _navigationState.update { state ->
            state.copy(
                topScreenXOffset = 0.0f,
                prevScreenXOffset = -screenXOffset / 8,
                prevScreenIndex = -1,
            )
        }
    }

    //-- Clean Up Top Screen XOffset when composition is destroyed --
    fun cleanUpXOffset() {
        _navigationState.update { state ->
            state.copy(
                topScreenXOffset = 0.0f,
            )
        }
    }

    //-- Set the Index of the prevScreen --
    private fun setPrevScreenIndex() {
        val nextPrevScreenIndex = _navigationState.value.navigationBackStack.lastIndex - 1
        _navigationState.update { state ->
            state.copy(
                prevScreenXOffset = 0.0f,
                prevScreenIndex = nextPrevScreenIndex
            )
        }
    }

    //-- CallBack for when a screen drag ends --
    fun horizontalScreenDragEnded(xBreakPoint: Float) {
        val screenXOffset = _navigationState.value.screenXOffset
        val topScreenXOffset = _navigationState.value.topScreenXOffset
        if (topScreenXOffset > xBreakPoint) {
            setPrevScreenIndex()
            pop()
        } else {
            _navigationState.update { state ->
                state.copy(
                    topScreenXOffset = 0.0f,
                    prevScreenXOffset = -screenXOffset / 8f,
                )
            }
        }
    }
}

@Composable
inline fun <reified C : Parcelable> NavigationScreen(
    navigationViewModel: NavigationViewModel<C>,
    noinline content: @Composable (Screen) -> Unit,
) {
    val navigationState = navigationViewModel.navigationState.collectAsState()
    val navigationBackStack = navigationState.value.navigationBackStack
    val screenXOffsetSet = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                //--Set screenXOffset if not set--
                if (!screenXOffsetSet.value) {
                    val rect = layoutCoordinates.boundsInRoot()
                    navigationViewModel.setScreenXOffset(rect.topRight.x)
                    screenXOffsetSet.value = true
                }
            }
    ) {
        ChildStack(
            source = navigationViewModel.navigator,
            initialStack = { listOf(Screen.List) },
            handleBackButton = true,
            animation = backAnimation(
                backHandler = BackDispatcher(),
                onBack = {}
            ),
        ) {
            for ((index, screen) in navigationBackStack.withIndex()) {
                HorizontalDraggableScreen(
                    screenStackIndex = index,
                    navigationViewModel = navigationViewModel
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colors.background)
                    ) {

                        AnimatedVisibility(
                            visible = index >= navigationBackStack.size - 2
                        ) {

                            content(screen)
                        }
                    }
                }
            }
        }
    }
}

