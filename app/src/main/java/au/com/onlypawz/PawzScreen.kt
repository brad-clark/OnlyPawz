package au.com.onlypawz

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlin.math.roundToInt


@Composable
fun PawzScreen(
    viewModel: PawzViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    CatProfilePanel(uiState, { viewModel.removeProfile() }, { viewModel.removeProfile() })
}

@Composable

fun CatProfilePanel(uiState: UiState, onSwipeRight: () -> Unit = {}, onSwipeLeft: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (uiState) {
            is UiState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    LoadingScreen()
                    uiState.profiles.reversed().forEach { profile ->
                        SwipeCard(
                            modifier = Modifier.fillMaxSize(),
                            onSwipeLeft = onSwipeLeft,
                            onSwipeRight = onSwipeRight
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .shadow(
                                        elevation = 16.dp, shape = RoundedCornerShape(32.dp)
                                    )
                                    .background(Color.White, shape = RoundedCornerShape(32.dp))
                                    .clip(RoundedCornerShape(32.dp))
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Image(
                                    bitmap = profile.profileImage,
                                    contentDescription = "Full Screen Image",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MarkdownText(
                                        markdown = profile.profileText,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            is UiState.Loading, UiState.Initial -> LoadingScreen()
            is UiState.Error -> {
                Log.e("Error", uiState.errorMessage)
                Text(text = uiState.errorMessage)
            }
        }

        Row(
            modifier = Modifier
                .padding(50.dp)
                .height(100.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = onSwipeLeft, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(240, 90, 100), // Background of the Button
                    contentColor = Color.White // Color of the Icon
                ), modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = "No",
                )
            }
            Spacer(Modifier.size(50.dp))
            IconButton(
                onClick = onSwipeRight, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(50, 180, 240), // Background of the Button
                    contentColor = Color.White // Color of the Icon
                ), modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Yes",
                )
            }
        }
    }

}

@Composable
fun SwipeCard(
    modifier: Modifier = Modifier,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    swipeThreshold: Float = 800f,
    sensitivityFactor: Float = 3f,
    content: @Composable () -> Unit
) {
    val offset = remember { mutableStateOf(0f) }
    val dismiss = remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    Box(modifier = modifier
        .offset { IntOffset(offset.value.roundToInt(), 0) }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(onDragEnd = {
                offset.value = 0f
            }) { change, dragAmount ->

                offset.value += (dragAmount / density) * sensitivityFactor
                when {
                    offset.value > swipeThreshold -> {
                        dismiss.value = true
                        onSwipeLeft.invoke()
                    }

                    offset.value < -swipeThreshold -> {
                        dismiss.value = true
                        onSwipeRight.invoke()
                    }
                }
                if (change.positionChange() != Offset.Zero) change.consume()
            }
        }
        .graphicsLayer(
            alpha = 10f - animateFloatAsState(if (dismiss.value) 1f else 0f).value,
            rotationZ = animateFloatAsState(offset.value / 50).value
        )) {
        content()
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(text = "Loading")
        }
    }
}

@Preview
@Composable
fun CatProfilePanelPreview() {
    val uiState = UiState.Success(
        listOf(
            Profile(
                profileImage = ImageBitmap.imageResource(R.drawable.baked_goods_1),
                profileText = "This is a cat profile."
            ), Profile(
                profileImage = ImageBitmap.imageResource(R.drawable.baked_goods_2),
                profileText = "This is a cat profile."
            ), Profile(
                profileImage = ImageBitmap.imageResource(R.drawable.baked_goods_3),
                profileText = "This is a cat profile."
            )
        )
    )

    CatProfilePanel(uiState = uiState)
}

