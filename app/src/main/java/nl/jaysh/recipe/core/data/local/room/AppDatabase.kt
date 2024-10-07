package nl.jaysh.recipe.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nl.jaysh.recipe.core.data.local.room.dao.RecipeDetailDao
import nl.jaysh.recipe.core.data.local.room.entity.RecipeDetailEntity
import nl.jaysh.recipe.core.data.local.room.typeconverter.LocalDateTimeTypeConverter

@Database(
    entities = [RecipeDetailEntity::class],
    version = 1,
    exportSchema = false,
)

@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDetailDao(): RecipeDetailDao
}
