package nl.jaysh.recipe.core.utils

import arrow.core.Either

fun <L, R> List<Either<L, R>>.sequence(): Either<L, List<R>> {
    val accumulatedResults = mutableListOf<R>()

    for (either in this) {
        when (either) {
            is Either.Left -> return either
            is Either.Right -> accumulatedResults.add(either.value)
        }
    }

    return Either.Right(accumulatedResults)
}