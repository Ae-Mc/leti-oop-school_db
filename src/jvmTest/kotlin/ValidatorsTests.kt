import exceptions.ValidationException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.row
import util.validators.fullNameValidator

class ValidatorsTests : FreeSpec({
    "fullNameValidator" - {
        val valid = listOf("А А Я", "Олег Олегович Олегов", "Test")
        forAll(row(""), row("       "), row("Te")) { fullName ->
            "$fullName must be invalid" {
                shouldThrowExactly<ValidationException> {
                    fullNameValidator(fullName)
                }
            }
        }
        valid.forEach {
            "$it must be valid" {
                shouldNotThrowAny {
                    fullNameValidator(it)
                }
            }
        }
    }
})