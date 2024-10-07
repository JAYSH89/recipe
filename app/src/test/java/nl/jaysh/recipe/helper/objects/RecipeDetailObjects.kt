package nl.jaysh.recipe.helper.objects

import nl.jaysh.recipe.core.data.local.room.entity.RecipeDetailEntity
import nl.jaysh.recipe.core.data.network.model.detail.RecipeDetailDTO
import nl.jaysh.recipe.core.domain.model.detail.Instruction
import nl.jaysh.recipe.core.domain.model.detail.InstructionStep
import nl.jaysh.recipe.core.domain.model.detail.InstructionStepDetail
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import java.time.LocalDateTime

object RecipeDetailObjects {
    private val fryingPan = InstructionStepDetail(
        id = 404645L,
        name = "frying pan",
        localizedName = "frying pan",
        image = "https://spoonacular.com/cdn/equipment_100x100/pan.png"
    )

    private val water = InstructionStepDetail(
        id = 14412L,
        name = "water",
        localizedName = "water",
        image = "water.png"
    )

    val recipeDetail = RecipeDetail(
        id = 640864L,
        title = "Crock Pot Lasagna",
        readyInMinutes = 45,
        image = "https://img.spoonacular.com/recipes/640864-556x370.jpg",
        sourceUrl = "https://www.foodista.com/recipe/QTRKQVWX/crock-pot-lasagna",
        instructions = "instructions",
        analyzedInstructions = listOf(
            Instruction(
                name = "",
                steps = listOf(
                    InstructionStep(
                        number = 1,
                        step = "Brown the ground beef",
                        equipment = listOf(fryingPan),
                        ingredients = listOf(water),
                    ),
                    InstructionStep(
                        number = 2,
                        step = "Place a layer of meat",
                        equipment = listOf(),
                        ingredients = listOf(),
                    ),
                    InstructionStep(
                        number = 3,
                        step = "Add another layer of meat",
                        equipment = listOf(),
                        ingredients = listOf(),
                    ),
                )
            )
        ),
        extendedIngredients = emptyList(),
    )

    val recipeDetailEntity = RecipeDetailEntity(
        id = 640864L,
        title = "Crock Pot Lasagna",
        readyInMinutes = 45,
        image = "https://img.spoonacular.com/recipes/640864-556x370.jpg",
        sourceUrl = "https://www.foodista.com/recipe/QTRKQVWX/crock-pot-lasagna",
        instructions = "instructions",
        analyzedInstructions = "[]",
        extendedIngredients = "[]",
        favourite = false,
        updatedAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0),
    )

    val testRecipeDetailDTO = RecipeDetailDTO(
        id = 640864L,
        title = "Crock Pot Lasagna",
        readyInMinutes = 45,
        image = "https://img.spoonacular.com/recipes/640864-556x370.jpg",
        sourceUrl = "https://www.foodista.com/recipe/QTRKQVWX/crock-pot-lasagna",
        instructions = "instructions",
        analyzedInstructions = emptyList(),
        extendedIngredients = emptyList(),
    )
}