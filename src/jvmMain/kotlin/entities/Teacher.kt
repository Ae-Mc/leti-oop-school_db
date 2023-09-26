package entities

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.insert
import java.util.*

object Teachers : UUIDTable() {
    val fullName = text("full_name").index()
    val salary = integer("salary")
}

class Teacher(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Teacher>(Teachers)

    var fullName by Teachers.fullName
    var salary by Teachers.salary
    var subjects by Subject via TeacherSubjects
    val classroomClasses by Class optionalReferrersOn Classes.classroomTeacher
    val studentsClasses by Class via TeacherClass

    fun addSubject(newSubject: Subject) {
        TeacherSubjects.insert {
            it[teacher] = id
            it[subject] = newSubject.id
        }
    }

    override fun toString(): String {
        val classesString = classroomClasses.joinToString(", ")
        val subjectsString = subjects.joinToString(", ")

        return "$fullName, Salary: $salary, Classroom teacher of [$classesString], Subjects: [$subjectsString]"
    }
}