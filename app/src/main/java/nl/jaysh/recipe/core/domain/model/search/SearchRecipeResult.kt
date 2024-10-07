package nl.jaysh.recipe.core.domain.model.search

data class SearchRecipeResult(
    val id: Long,
    val title: String,
    val summary: String,
    val image: String,
    val readyInMinutes: Int,
)
