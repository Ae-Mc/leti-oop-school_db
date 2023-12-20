import exceptions.ValidationException

fun positiveIntValidator(num: Int?): Int {
    if (num == null || num < 0) {
        throw ValidationException()
    }
    return num
}