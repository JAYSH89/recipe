package nl.jaysh.recipe.core.data.network.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDTO(
    val results: List<SearchResultDTO>,
    val offset: Int,
    val number: Int,
    val totalResults: Int,
)
