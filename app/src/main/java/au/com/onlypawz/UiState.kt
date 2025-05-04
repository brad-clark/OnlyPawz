package au.com.onlypawz

import androidx.compose.ui.graphics.ImageBitmap

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    object Loading : UiState


    data class Success(
        val profiles: List<Profile> = emptyList(),
    ) : UiState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState
}

data class Profile(
    val profileImage: ImageBitmap,
    val profileText: String
)