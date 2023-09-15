package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
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
}