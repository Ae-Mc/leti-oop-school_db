package exceptions

class ValidationException(override val message: String? = null) :
    Exception(message)