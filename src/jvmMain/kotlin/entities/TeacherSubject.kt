package entities

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TeacherSubjects : Table() {
    val teacher =
        reference("teacher", Teachers, onDelete = ReferenceOption.CASCADE)
    val subject =
        reference("subject", Subjects, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(teacher, subject)
}