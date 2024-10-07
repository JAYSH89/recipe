package nl.jaysh.recipe.core.data.network.model.detail

import kotlinx.serialization.Serializable
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail

@Serializable
data class RecipeDetailDTO(
    val id: Long,
    val title: String,
    val readyInMinutes: Int,
    val image: String,
    val sourceUrl: String,
    val instructions: String,
    val analyzedInstructions: List<InstructionDTO>,
    val extendedIngredients: List<IngredientDTO>,
)

fun RecipeDetailDTO.toRecipeDetail(): RecipeDetail = RecipeDetail(
    id = id,
    title = title,
    readyInMinutes = readyInMinutes,
    image = image,
    sourceUrl = sourceUrl,
    instructions = instructions,
    analyzedInstructions = analyzedInstructions.map { it.toInstruction() },
    extendedIngredients = extendedIngredients.map { it.toIngredient() }
)
