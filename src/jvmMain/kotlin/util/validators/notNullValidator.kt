package util.validators

import exceptions.ValidationException

fun <T> notNullValidator(value: T?): T {
    if (value == null) {
        throw ValidationException()
    }
    return value
}