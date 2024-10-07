package nl.jaysh.recipe.core.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nl.jaysh.recipe.core.data.local.room.entity.RecipeDetailEntity

@Dao
interface RecipeDetailDao {
    @Query("SELECT * FROM recipe WHERE id = :id")
    fun getById(id: Long): Flow<RecipeDetailEntity?>

    @Query("SELECT * FROM recipe WHERE favourite = :isFavourite")
    fun getFavourites(isFavourite: Boolean = true): Flow<List<RecipeDetailEntity>>

    @Query("UPDATE recipe SET favourite = :isFavourite WHERE id = :recipeId")
    suspend fun updateFavouriteStatus(recipeId: Long, isFavourite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(recipe: RecipeDetailEntity)

    @Delete
    fun delete(recipe: RecipeDetailEntity)
}
