import exceptions.ValidationException

fun intRangeValidator(num: Int?, min: Int? = null, max: Int? = null): Int {
    if (num == null) {
        throw ValidationException()
    }
    if (min != null) {
        if (num < min) {
            throw ValidationException()
        }
    }
    if (max != null) {
        if (num > max) {
            throw ValidationException()
        }
    }
    return num
}