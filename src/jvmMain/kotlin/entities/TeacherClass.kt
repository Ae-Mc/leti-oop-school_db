package entities

import org.jetbrains.exposed.sql.Table

object TeacherClass : Table() {
    val teacher = reference("teacher", Teachers)
    val studentsClass = reference("class", Classes)

    override val primaryKey = PrimaryKey(teacher, studentsClass)
}