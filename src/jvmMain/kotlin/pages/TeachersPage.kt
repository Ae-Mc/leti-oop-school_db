package pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import entities.Teacher
import entities.TeacherClass
import entities.TeacherSubjects
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import widgets.TableCell
import java.io.File
import javax.xml.stream.XMLOutputFactory

// List of teachers
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun TeachersPage(database: Database, callback: () -> Unit) {
    // Used to close window
    var isOpen by remember { mutableStateOf(true) }

    var teachers by remember { mutableStateOf(emptyList<Teacher>()) }
    val weights = floatArrayOf(0.2f, 1.3f, 1f, 0.2f, 1f, 0.4f, 0.4f)
    val columnState = LazyListState()
    var showAddTeacherPage by remember { mutableStateOf(false) }
    var editingTeacher by remember { mutableStateOf<Teacher?>(null) }
    val teacher: Teacher? = editingTeacher

    fun reloadTeachers() {
        teachers = transaction(database) {
            Teacher.all().with(Teacher::subjects, Teacher::classroomClasses)
                .toList()
        }
    }

    reloadTeachers()

    if (isOpen) {
        if (showAddTeacherPage) {
            // Shows add teacher page
            AddTeacherPage(
                database,
                callback = {
                    showAddTeacherPage = false
                    reloadTeachers()
                },
            )
        } else if (teacher is Teacher) {
            // Shows edit teacher page
            EditTeacherPage(database = database, teacher = teacher, callback = {
                editingTeacher = null
                reloadTeachers()
            })
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(onClick = {
                        isOpen = false
                        callback()
                    }) {
                        Text("Назад")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                        dump(database)
                    }) {
                        Text("Дамп")
                    }
                    Button(onClick = {
                        load(database)
                        reloadTeachers()
                    }) {
                        Text("Загрузка")
                    }
                }
                // Table
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    state = columnState,
                    userScrollEnabled = true,
                ) {
                    // Here is the header
                    item {
                        Row(Modifier.background(Color(0xff9efd38))) {
                            TableCell(
                                text = "№",
                                weight = weights[0],
                            )
                            TableCell(text = "id", weight = weights[1])
                            TableCell(text = "ФИО", weight = weights[2])
                            TableCell(text = "Зарплата", weight = weights[3])
                            TableCell(text = "Предметы", weight = weights[4])
                            TableCell(text = "Классрук", weight = weights[5])
                            TableCell(text = "", weight = weights[6])
                        }
                    }
                    // Here are all the lines of your table.
                    this.itemsIndexed(
                        items = teachers,
                        contentType = { _, _ -> },
                        key = { _, teacher -> teacher.id },
                    ) { index, teacher ->
                        Row(
                            modifier = Modifier.onClick {
                                editingTeacher = teacher
                            },
                        ) {
                            TableCell(
                                text = index.toString(),
                                weight = weights[0]
                            )
                            TableCell(
                                text = teacher.id.toString(),
                                weight = weights[1],
                                textStyle = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                            TableCell(
                                text = teacher.fullName,
                                weight = weights[2]
                            )
                            TableCell(
                                text = teacher.salary.toString(),
                                weight = weights[3]
                            )
                            TableCell(
                                text = teacher.subjects.joinToString(", ") { subject -> subject.name },
                                weight = weights[4]
                            )
                            TableCell(
                                text = teacher.classroomClasses.joinToString(", "),
                                weight = weights[5]
                            )
                            TableCell(
                                text = "Удалить",
                                weight = weights[6],
                                onClick = {
                                    transaction(database) {
                                        for (classRoom in teacher.classroomClasses) {
                                            classRoom.classroomTeacher = null
                                        }
                                        TeacherClass.deleteWhere {
                                            TeacherClass.teacher eq teacher.id
                                        }
                                        TeacherSubjects.deleteWhere {
                                            TeacherSubjects.teacher eq teacher.id
                                        }
                                        teacher.delete()
                                    }
                                    reloadTeachers()
                                },
                            )
                        }

                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Button(
                        onClick = {
                            showAddTeacherPage = true
                        },
                    ) {
                        Text(
                            "Добавить учителя",
                        )
                    }
                }
            }
        }
    }

}

fun dump(database: Database) {
    val mapper = XmlMapper.builder().defaultUseWrapper(false).build()
    val out = File("teachers.xml").printWriter()
    val outputFactory = XMLOutputFactory.newFactory()
    val xml = outputFactory.createXMLStreamWriter(out)
    lateinit var teachers: List<schemes.Teacher>
    transaction(database) {
        teachers = schemes.Teacher.fromEntityList(Teacher.all().toList())
    }
    mapper.writeValue(xml, teachers)
    xml.close()
    out.close()
}

fun load(database: Database) {
    val mapper = XmlMapper.builder().defaultUseWrapper(false).build()
    val inputFile = File("teachers.xml")
    var teachers: List<Teacher> =
        mapper.readValue<List<Teacher>>(inputFile)
    transaction(database) {
        val dbTeachers = Teacher.all()
        for (teacher in teachers) {
            var found = false
            for (dbTeacher in dbTeachers) {
                found = (dbTeacher.id == teacher.id)
                if (found) {
                    break
                }
            }
            if (!found) {
                // TODO: create teacher
                teacher
            }
        }
    }
}