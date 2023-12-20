package entities

import exceptions.ValidationException
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import positiveIntValidator
import java.util.*
import kotlin.math.min

object Classes : UUIDTable() {
    var admissionYear = integer("admission_year")
    var letter = varchar("letter", 10)
    var classroomTeacher = reference(
        "classroom_teacher",
        Teachers,
        onDelete = ReferenceOption.SET_NULL
    ).nullable()

    val unique = uniqueIndex(admissionYear, letter)
}

class Class(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Class>(Classes) {
        fun validateNew(
            admissionYear: String,
            letter: String,
            teacher: Teacher?,
        ): Class {
            if (letter.length > 1) {
                throw ValidationException()
            }
            return Class.new {
                this.admissionYear =
                    positiveIntValidator(admissionYear.toIntOrNull())
                this.letter = letter
                this.classroomTeacher = teacher
            }
        }
    }

    var admissionYear by Classes.admissionYear
    var letter by Classes.letter
    var classroomTeacher by Teacher optionalReferencedOn Classes.classroomTeacher
    val students by Student referrersOn Students.studentClass
    val teachers by Teacher via TeacherClass

    val name: String
        get() {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val isNextStudyingYearStarted = currentMonth > 7
            val yearCorrection = if (isNextStudyingYearStarted) 1 else 0
            val classNumber =
                min(currentYear - admissionYear + yearCorrection, 11)


            return "${classNumber}$letter"
        }

    override fun toString(): String {
        return "$name ($admissionYear)"
    }
}