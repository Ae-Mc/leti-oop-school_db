import exceptions.ValidationException

fun positiveInt(num: Int?): Int {
    if (num == null || num < 0) {
        throw ValidationException()
    }
    return num
}