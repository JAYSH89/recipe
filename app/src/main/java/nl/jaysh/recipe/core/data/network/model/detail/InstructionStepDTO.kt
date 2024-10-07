package nl.jaysh.recipe.core.data.network.model.detail

import kotlinx.serialization.Serializable
import nl.jaysh.recipe.core.domain.model.detail.InstructionStep

@Serializable
data class InstructionStepDTO(
    val number: Int,
    val step: String,
    val ingredients: List<InstructionStepDetailDTO>,
    val equipment: List<InstructionStepDetailDTO>,
)

fun InstructionStepDTO.toInstructionStep(): InstructionStep = InstructionStep(
    number = number,
    step = step,
    equipment = equipment.map { it.toInstructionStepDetail() },
    ingredients = ingredients.map { it.toInstructionStepDetail() },
)
