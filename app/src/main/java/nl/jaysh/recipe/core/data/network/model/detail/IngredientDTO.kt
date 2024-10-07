package nl.jaysh.recipe.core.data.network.model.detail

import kotlinx.serialization.Serializable
import nl.jaysh.recipe.core.domain.model.detail.Ingredient

@Serializable
data class IngredientDTO(
    val id: Long,
    val original: String,
)

fun IngredientDTO.toIngredient(): Ingredient = Ingredient(
    id = id,
    original = original,
)
