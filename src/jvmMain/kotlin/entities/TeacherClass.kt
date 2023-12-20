package entities

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TeacherClass : Table() {
    val teacher =
        reference("teacher", Teachers, onDelete = ReferenceOption.CASCADE)
    val studentsClass =
        reference("class", Classes, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(teacher, studentsClass)
}