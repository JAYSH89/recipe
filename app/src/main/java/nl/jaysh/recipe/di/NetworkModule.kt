package nl.jaysh.recipe.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import nl.jaysh.recipe.core.data.network.KtorClient
import nl.jaysh.recipe.core.data.network.service.RecipeService
import nl.jaysh.recipe.core.data.network.service.RecipeServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesHttpClient(): HttpClient {
        val androidEngine = Android.create()
        return KtorClient.createHttpClient(androidEngine)
    }

    @Singleton
    @Provides
    fun providesRecipeService(httpClient: HttpClient): RecipeService {
        return RecipeServiceImpl(httpClient = httpClient)
    }
}
