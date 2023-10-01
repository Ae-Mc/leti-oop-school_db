import entities.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


class DatabaseFactory {
    fun getDatabase(): Database {
        val databaseUrl = "jdbc:mariadb://192.168.1.60:3306/studies"
        val user = "root"
        val password = "123654789987456321"
        val config =
            DatabaseConfig { keepLoadedReferencesOutOfTransaction = true }
        val database =
            Database.connect(
                databaseUrl,
                user = user,
                password = password,
                databaseConfig = config
            )
        transaction(database) {
            SchemaUtils.create(
                Classes,
                Marks,
                Students,
                Subjects,
                Teachers,
                TeacherClass,
                TeacherSubjects
            )
        }
        return database
    }
}