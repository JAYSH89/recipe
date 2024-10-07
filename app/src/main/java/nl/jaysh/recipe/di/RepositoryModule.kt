package nl.jaysh.recipe.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import nl.jaysh.recipe.core.data.repository.RecipeRepositoryImpl
import nl.jaysh.recipe.core.domain.RecipeRepository

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindsRecipeRepository(recipeRepository: RecipeRepositoryImpl): RecipeRepository
}
