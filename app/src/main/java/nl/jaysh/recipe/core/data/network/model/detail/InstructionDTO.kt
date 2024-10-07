package nl.jaysh.recipe.core.data.network.model.detail

import kotlinx.serialization.Serializable
import nl.jaysh.recipe.core.domain.model.detail.Instruction

@Serializable
data class InstructionDTO(
    val name: String,
    val steps: List<InstructionStepDTO>,
)

fun InstructionDTO.toInstruction(): Instruction = Instruction(
    name = name,
    steps = steps.map { it.toInstructionStep() },
)
