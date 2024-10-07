package nl.jaysh.recipe.core.domain.model.detail

data class RecipeDetail(
    val id: Long,
    val title: String,
    val readyInMinutes: Int,
    val image: String,
    val sourceUrl: String,
    val instructions: String,
    val analyzedInstructions: List<Instruction>,
    val extendedIngredients: List<Ingredient>,
    val favourite: Boolean? = null,
)
