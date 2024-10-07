package nl.jaysh.recipe.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nl.jaysh.recipe.core.data.local.room.AppDatabase
import nl.jaysh.recipe.core.data.local.room.dao.RecipeDetailDao
import nl.jaysh.recipe.core.utils.Constants.DATABASE_NAME
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = DATABASE_NAME,
        )
            .fallbackToDestructiveMigration() // for dev purposes - migrate properly in prod
            .build()
    }

    @Provides
    @Singleton
    fun providesRecipeDetailDao(database: AppDatabase): RecipeDetailDao {
        return database.recipeDetailDao()
    }
}
