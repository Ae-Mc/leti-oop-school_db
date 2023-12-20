package entities

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.insert
import positiveIntValidator
import util.validators.fullNameValidator
import java.util.*


object Teachers : UUIDTable() {
    val fullName = text("full_name").index()
    val salary = integer("salary")
}

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
)
class Teacher(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Teacher>(Teachers) {
        fun validateNew(
            salary: String,
            fullName: String,
            subjects: List<Subject>,
        ): Teacher {
            val salaryInt = positiveIntValidator(salary.toIntOrNull())
            return super.new {
                this.salary = salaryInt
                this.fullName = fullNameValidator(fullName)
                this.subjects = SizedCollection(subjects)
            }
        }
    }

    @get:JsonProperty("id")
    val jsonId get() = id

    @get:JsonProperty("fullName")
    var fullName by Teachers.fullName

    @get:JsonProperty("salary")
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