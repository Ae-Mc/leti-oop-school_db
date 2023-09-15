import entities.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager

class DatabaseFactory {
    fun getDatabase(): Database {
        val databaseUrl = "jdbc:h2:mem:test"
        val database = Database.connect(databaseUrl)
        val connection = DriverManager.getConnection(databaseUrl) // holds connection to the database
        transaction(database) {
            SchemaUtils.create(Classes, Marks, Students, Subjects, Teachers, TeacherClass, TeacherSubjects)
        }
        return database
    }
}