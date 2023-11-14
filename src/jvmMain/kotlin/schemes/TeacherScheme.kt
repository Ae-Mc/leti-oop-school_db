package schemes

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import java.util.*
import entities.Teacher as TeacherEntity

@Serializable
@XmlSerialName("Teacher")
data class Teacher(
    @JsonProperty("id") val id: String,
    @JsonProperty("fullName") val fullName: String,
    @JsonProperty("salary") val salary: Int
) {
    companion object {
        fun fromEntity(value: TeacherEntity): Teacher {
            return Teacher(
                id = value.id.value.toString(),
                fullName = value.fullName,
                salary = value.salary
            )
        }

        fun fromEntityList(value: List<TeacherEntity>): List<Teacher> {
            return value.map {
                Teacher(
                    id = it.id.value.toString(),
                    fullName = it.fullName,
                    salary = it.salary
                )
            }
        }
    }

    fun createEntity(): TeacherEntity {
        val it = this
        return TeacherEntity.new(UUID.fromString(id)) {
            salary = it.salary
            fullName = it.fullName
        }
    }
}