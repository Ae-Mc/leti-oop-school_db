package util.validators

import exceptions.ValidationException

fun fullNameValidator(fullName: String): String {
    if (fullName.isBlank() || fullName.length < 3) {
        throw ValidationException()
    }
    return fullName
}