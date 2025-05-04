package au.com.onlypawz

import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.FirebaseOptions
import com.google.firebase.vertexai.type.ImagenAspectRatio
import com.google.firebase.vertexai.type.ImagenGenerationConfig
import com.google.firebase.vertexai.type.ImagenImageFormat
import com.google.firebase.vertexai.type.PublicPreviewAPI
import com.google.firebase.vertexai.type.QuotaExceededException
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(PublicPreviewAPI::class)
class PawzViewModel(private val catBreedsRepository: CatBreedsRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()
    val config = ImagenGenerationConfig(
        numberOfImages = 1,
        aspectRatio = ImagenAspectRatio.PORTRAIT_3x4,
        imageFormat = ImagenImageFormat.jpeg(compressionQuality = 50),
        addWatermark = false
    )
    val imagenModel = Firebase.vertexAI.imagenModel("imagen-3.0-generate-002", config)
    val generativeModel = Firebase.vertexAI.generativeModel("gemini-2.0-flash-001")

    private var timerJob: Job? = null


    init {
        _uiState.value = UiState.Loading
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                addProfile()
                delay(30_000)
            }

        }
    }

    fun removeProfile() {
        when(val state = uiState.value) {
            is UiState.Success -> {
                val profiles = state.profiles.toMutableStateList()
                profiles.removeAt(0)
                _uiState.value = UiState.Success(profiles)
            }
            else -> {}
        }
        addProfile()
    }

    fun addProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val randomBreed = catBreedsRepository.getCatBreeds().randomeCatBreed()
                val breedName = randomBreed.breed
                val activityLevel = randomBreed.activityLevel
                val coatLength = randomBreed.coatLength
                val color = randomBreed.color
                val groomingNeeds = randomBreed.groomingNeeds
                val age = randomBreed.age
                val origin = randomBreed.origin
                val pattern = randomBreed.pattern
                val size = randomBreed.size
                val personality = randomBreed.personality

                val breedPrompt = "$color $pattern $coatLength $breedName cat. "
                val profileBasePrompt = "$breedPrompt. Are a $activityLevel engery, $age year old,with. Is from $origin, and" +
                        "has a $personality personality"

                val profilePrompt = "You are a cat with these features; $profileBasePrompt. Give yourself a name, and single sentence summary of yourself in markdown. Your answer will be your profile."
                val imagePrompt = "Create a text prompt for AI of a realistic photo of a $breedPrompt"

                val generatedContent = generativeModel.generateContent(profilePrompt)
                val generatedImages = imagenModel.generateImages(imagePrompt)
                val profileText= generatedContent.text.orEmpty()
                val profileImage = generatedImages.images.first().asBitmap().asImageBitmap()
                val profiles = ((_uiState.value as? UiState.Success)?.profiles ?: emptyList()).toMutableStateList()
                profiles.add(Profile(profileImage, profileText))
                _uiState.value = UiState.Success(profiles)
            } catch (e: Exception) {
                if (e is QuotaExceededException) {
                    return@launch
                }
                _uiState.value = UiState.Error(e.localizedMessage.orEmpty())
            }
        }
    }

}