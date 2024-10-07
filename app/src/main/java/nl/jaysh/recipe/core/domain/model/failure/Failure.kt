package nl.jaysh.recipe.core.domain.model.failure

sealed interface Failure

enum class NetworkFailure : Failure {
    TIMEOUT,
    NO_INTERNET,
    UNAUTHORIZED,
    NOT_FOUND,
    PAYMENT_REQUIRED,
    UNKNOWN,
}

sealed interface StorageFailure : Failure {
    data object IO: StorageFailure
    data object NotFound: StorageFailure
}

sealed interface ParseFailure : Failure {
    data object JsonParse: ParseFailure
}

sealed interface UnknownFailure: Failure {
    data object Unspecified: UnknownFailure
}
