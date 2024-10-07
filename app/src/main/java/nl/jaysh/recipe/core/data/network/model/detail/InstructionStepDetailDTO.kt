package nl.jaysh.recipe.core.data.network.model.detail

import kotlinx.serialization.Serializable
import nl.jaysh.recipe.core.domain.model.detail.InstructionStepDetail

@Serializable
data class InstructionStepDetailDTO(
    val id: Long,
    val name: String,
    val localizedName: String,
    val image: String,
)

fun InstructionStepDetailDTO.toInstructionStepDetail(): InstructionStepDetail {
    return InstructionStepDetail(
        id = id,
        name = name,
        localizedName = localizedName,
        image = image,
    )
}
