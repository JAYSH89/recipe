package nl.jaysh.recipe.core.domain.model.detail

data class InstructionStep(
    val number: Int,
    val step: String,
    val equipment: List<InstructionStepDetail>,
    val ingredients: List<InstructionStepDetail>,
)
