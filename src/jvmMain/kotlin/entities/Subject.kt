package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.insert
import java.util.*

object Subjects : UUIDTable() {
    val name = text("subject_name").uniqueIndex()
}

class Subject(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Subject>(Subjects)

    var name by Subjects.name
    var teachers by Teacher via TeacherSubjects

    fun addTeacher(newTeacher: Teacher) {
        TeacherSubjects.insert {
            it[teacher] = newTeacher.id
            it[subject] = id
        }
    }

    override fun toString(): String {
        return name
    }
}