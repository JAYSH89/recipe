package nl.jaysh.recipe.core.data.local.room.typeconverter

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class LocalDateTimeTypeConverterTest {

    private val typeConverter = LocalDateTimeTypeConverter()

    @Test
    fun shouldConvertLocalDateTimeToString() {
        val localDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0)
        val result = typeConverter.fromLocalDateTime(localDateTime)

        val expected = "2024-01-01T00:00:00"
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun shouldConvertStringToLocalDateTime() {
        val localDateTimeString = "2024-01-01T00:00:00"
        val result = typeConverter.toLocalDateTime(localDateTimeString)

        val expected = LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun shouldThrowExceptionInvalidDateString() {
        val malformedString = "invalid"
        assertThrows<DateTimeParseException> {
            typeConverter.toLocalDateTime(malformedString)
        }
    }
}