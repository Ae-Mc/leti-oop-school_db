package pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import entities.Student
import entities.Subject
import entities.Teacher
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

@Composable
fun GenerateReportPage(database: Database, callback: () -> Unit) {
    var isOpen by remember { mutableStateOf(true) }
    var path by remember { mutableStateOf("") }
    var showFilePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    if (isOpen) {
        DirectoryPicker(
            show = showFilePicker,
        ) { selectedPath ->
            if (selectedPath != null) {
                path = selectedPath
            }
            showFilePicker = false
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    isOpen = false
                    callback()
                }) {
                    Text("Назад")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Путь к директории для сохранения отчёта")
                TextField(value = path,
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.clickable(onClick = {
                        showFilePicker = true
                    }),
                    onValueChange = {})
            }
            if (error != null) {
                Text(
                    error!!,
                    style = TextStyle(color = MaterialTheme.colors.error)
                )
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    if (path.isBlank()) {
                        error = "Не выбран путь для сохранения!"
                    } else {
                        error = null
                        var reportNumber = 0
                        var fullPath: Path
                        do {
                            fullPath =
                                Path(path).resolve("report${reportNumber}.pdf")
                                    .toAbsolutePath()
                            reportNumber++
                        } while (fullPath.exists())
                        generateReport(database, fullPath)
                        isOpen = false
                        callback()
                    }
                },
            ) {
                Text("Создать отчёт")
            }
        }
    }
}

fun generateReport(database: Database, path: Path) {
    val fontPath = Path("").resolve("arial.ttf").toAbsolutePath()
    val font = PdfFontFactory.createFont(fontPath.pathString)
    val writer = PdfWriter(path.pathString)
    val pdf = PdfDocument(writer)
    val document = Document(pdf, PageSize.A4)
    document.setFont(font)
    transaction(database) {
        run {
            document.add(Paragraph("Преподаватели"))
            val table =
                Table(UnitValue.createPercentArray(floatArrayOf(45f, 45f, 10f)))
            table.addCell("ID")
            table.addCell("ФИО")
            table.addCell("Зарплата")
            for (teacher in Teacher.all()) {
                table.addCell(teacher.id.toString())
                table.addCell(teacher.fullName)
                table.addCell(teacher.salary.toString())
            }
            document.add(table)
        }
        run {
            document.add(Paragraph("Список предметов"))
            val table =
                Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            table.addCell("ID")
            table.addCell("Название")
            for (subject in Subject.all()) {
                table.addCell(subject.id.toString())
                table.addCell(subject.name)
            }
            document.add(table)
        }
        run {
            document.add(Paragraph("Ученики"))
            val table =
                Table(UnitValue.createPercentArray(floatArrayOf(25f, 60f, 15f)))
            table.addCell("ID")
            table.addCell("ФИО")
            table.addCell("Класс")
            for (student in Student.all().with(Student::studentClass)) {
                table.addCell(student.id.toString())
                table.addCell(student.fullName)
                table.addCell(student.studentClass.toString())
            }
            document.add(table)
        }
    }
    document.close()
}