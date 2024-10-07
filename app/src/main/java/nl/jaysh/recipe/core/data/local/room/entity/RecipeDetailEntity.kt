package nl.jaysh.recipe.core.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import nl.jaysh.recipe.core.data.local.room.typeconverter.LocalDateTimeTypeConverter
import nl.jaysh.recipe.core.domain.model.detail.Ingredient
import nl.jaysh.recipe.core.domain.model.detail.Instruction
import nl.jaysh.recipe.core.domain.model.detail.RecipeDetail
import java.time.LocalDateTime

@Entity(tableName = "recipe")
data class RecipeDetailEntity(
    @PrimaryKey val id: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "ready_in_minutes")
    val readyInMinutes: Int,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "source_url")
    val sourceUrl: String,

    @ColumnInfo(name = "instructions")
    val instructions: String,

    @ColumnInfo(name = "analyzed_instructions")
    val analyzedInstructions: String,

    @ColumnInfo(name = "extended_ingredients")
    val extendedIngredients: String,

    @ColumnInfo(name = "favourite")
    val favourite: Boolean,

    @ColumnInfo(name = "updated_at")
    @TypeConverters(LocalDateTimeTypeConverter::class)
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun fromRecipeDetail(
            recipeDetail: RecipeDetail,
            analyzedInstructions: String,
            extendedIngredients: String,
        ): RecipeDetailEntity {
            return RecipeDetailEntity(
                id = recipeDetail.id,
                title = recipeDetail.title,
                readyInMinutes = recipeDetail.readyInMinutes,
                image = recipeDetail.image,
                sourceUrl = recipeDetail.sourceUrl,
                instructions = recipeDetail.instructions,
                analyzedInstructions = analyzedInstructions,
                extendedIngredients = extendedIngredients,
                favourite = recipeDetail.favourite ?: false,
                updatedAt = LocalDateTime.now(),
            )
        }
    }
}

fun RecipeDetailEntity.toRecipeDetail(
    analyzedInstructions: List<Instruction>,
    extendedIngredients: List<Ingredient>,
): RecipeDetail = RecipeDetail(
    id = id,
    title = title,
    readyInMinutes = readyInMinutes,
    image = image,
    sourceUrl = sourceUrl,
    instructions = instructions,
    analyzedInstructions = analyzedInstructions,
    extendedIngredients = extendedIngredients,
    favourite = favourite,
)
