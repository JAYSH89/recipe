package nl.jaysh.recipe.helper.objects

import nl.jaysh.recipe.core.data.network.model.search.SearchResponseDTO
import nl.jaysh.recipe.core.data.network.model.search.SearchResultDTO
import nl.jaysh.recipe.core.data.network.model.search.toSearchResult
import nl.jaysh.recipe.core.domain.model.search.SearchResult

object SearchRecipeObjects {
    val testSearchResponseDTO = SearchResponseDTO(
        results = listOf(
            SearchResultDTO(
                id = 640864L,
                title = "Crock Pot Lasagna",
                summary = "Crock Pot Lasagna might be just the",
                image = "https://img.spoonacular.com/recipes/640864-312x231.jpg",
                readyInMinutes = 45,
            ),
            SearchResultDTO(
                id = 649293L,
                title = "Lasagne, Marietta-Style",
                summary = "Lasagne, Marietta-Style requires about",
                image = "https://img.spoonacular.com/recipes/649293-312x231.jpg",
                readyInMinutes = 45,
            ),
        ),
        offset = 0,
        number = 2,
        totalResults = 24,
    )

    val testSearchResults: List<SearchResult> = testSearchResponseDTO
        .results
        .map { it.toSearchResult() }
}