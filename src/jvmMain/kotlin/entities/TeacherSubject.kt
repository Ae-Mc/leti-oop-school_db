package entities

import org.jetbrains.exposed.sql.Table

object TeacherSubjects : Table() {
    val teacher = reference("teacher", Teachers)
    val subject = reference("subject", Subjects)

    override val primaryKey = PrimaryKey(teacher, subject)
}