package nl.jaysh.recipe.core.data.network.model.search

import kotlinx.serialization.Serializable
import nl.jaysh.recipe.core.domain.model.search.SearchResult

@Serializable
data class SearchResultDTO(
    val id: Long,
    val title: String,
    val summary: String,
    val image: String,
    val readyInMinutes: Int,
)

fun SearchResultDTO.toSearchResult(): SearchResult = SearchResult(
    id = id,
    title = title,
    summary = summary,
    image = image,
    readyInMinutes = readyInMinutes,
)
