package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.time.LocalDate
import java.util.*

object Students : UUIDTable() {
    var fullName = text("full_name").index()
    var studentClass = reference("student_class", Classes)
}

class Student(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Student>(Students)

    var fullName by Students.fullName
    var studentClass by Students.studentClass
    val marks by Mark backReferencedOn Marks.student

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