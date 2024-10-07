package nl.jaysh.recipe.core.domain.model.detail

data class Instruction(
    val name: String,
    val steps: List<InstructionStep>,
)
