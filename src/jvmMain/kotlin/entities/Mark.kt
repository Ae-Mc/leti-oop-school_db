package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import java.util.*

object Marks : UUIDTable() {
    var mark = integer("mark")
    var student =
        reference("student", Students, onDelete = ReferenceOption.CASCADE)
    var teacher =
        reference("teacher", Teachers, onDelete = ReferenceOption.CASCADE)
    var subject =
        reference("subject", Subjects, onDelete = ReferenceOption.CASCADE)
    var date = date("date")
}

class Mark(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Mark>(Marks)

    var mark by Marks.mark
    var student by Student referencedOn Marks.student
    var teacher by Teacher referencedOn Marks.teacher
    var subject by Subject referencedOn Marks.subject
    var date by Marks.date
}