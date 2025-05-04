package au.com.onlypawz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CatBreeds(
    @SerialName("catBreeds")
    val catBreeds: List<Breed>
) {
    fun randomeCatBreed(): Breed {
        return catBreeds.random()
    }
}

@Serializable
data class Breed(
    @SerialName("activity_level")
    val activityLevel: String,
    @SerialName("breed")
    val breed: String,
    @SerialName("coat_length")
    val coatLength: String,
    @SerialName("colors")
    val colors: List<String>,
    @SerialName("grooming_needs")
    val groomingNeeds: String,
    @SerialName("lifespan_years")
    val lifespanYears: String,
    @SerialName("origin")
    val origin: String,
    @SerialName("patterns")
    val patterns: List<String>,
    @SerialName("size")
    val size: String,
    @SerialName("temperament")
    val temperament: List<String>
) {
    val color = colors.random()
    val pattern = patterns.random()
    val personality = temperament.random()
    val maxAge = lifespanYears.split("-").random().toInt()
    val age = (1..maxAge).random()
}