package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import util.validators.fullNameValidator
import java.time.LocalDate
import java.util.*

object Students : UUIDTable() {
    var fullName = text("full_name").index()
    var studentClass = reference("student_class", Classes)
}

class Student(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Student>(Students) {
        fun validateNew(fullName: String, studentClass: Class): Student {
            return Student.new {
                this.fullName = fullNameValidator(fullName)
                this.studentClassId = studentClass.id
            }
        }
    }

    var fullName by Students.fullName
    var studentClassId by Students.studentClass
    var studentClass by Class referencedOn Students.studentClass
    val marks by Mark referrersOn Marks.student

    fun addMark(teacher: Teacher, subject: Subject, mark: Int, date: LocalDate?): Mark {
        val student = this
        return Mark.new {
            this.subject = subject
            this.student = student
            this.teacher = teacher
            this.mark = mark
            this.date = date ?: LocalDate.now()
        }
    }
}