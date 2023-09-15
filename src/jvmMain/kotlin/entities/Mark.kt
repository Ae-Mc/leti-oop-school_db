package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date
import java.util.*

object Marks : UUIDTable() {
    var mark = integer("mark")
    var student = reference("student", Students)
    var teacher = reference("teacher", Teachers)
    var subject = reference("subject", Subjects)
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