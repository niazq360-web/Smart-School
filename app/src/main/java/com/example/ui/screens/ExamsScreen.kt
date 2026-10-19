package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SchoolRepository
import com.example.model.*
import com.example.ui.print.ResultCardDialog
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch

@Composable
fun ExamsScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val exams by repository.allExams.collectAsState(initial = emptyList())
    val students by repository.allStudents.collectAsState(initial = emptyList())
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())

    var selectedExam by remember { mutableStateOf<ExamRecord?>(null) }
    val results by (selectedExam?.let { repository.getResultsForExam(it.id) }
        ?: repository.getResultsForExam("EXAM-2026-MID")).collectAsState(initial = emptyList())

    var showMarksEntryDialog by remember { mutableStateOf(false) }
    var selectedResultForPrint by remember { mutableStateOf<ExamResultItem?>(null) }

    Scaffold(
        floatingActionButton = {
            if (currentRole == UserRole.SUPER_ADMIN || currentRole == UserRole.TEACHER || currentRole == UserRole.PRINCIPAL) {
                FloatingActionButton(
                    onClick = { showMarksEntryDialog = true },
                    containerColor = SchoolBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("enter_marks_fab")
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = "Enter Marks")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Examinations & Result Cards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SchoolBlue)
            Spacer(modifier = Modifier.height(10.dp))

            // Active Exams Horizontal Chips
            Text("Select Examination Schedule:", fontSize = 12.sp, color = Color.Gray)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                exams.forEach { ex ->
                    FilterChip(
                        selected = (selectedExam?.id ?: "EXAM-2026-MID") == ex.id,
                        onClick = { selectedExam = ex },
                        label = { Text(ex.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Published Class Results (${results.size})", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (results.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No results recorded yet for this exam.", color = Color.Gray)
                        Text("Click '+' below to enter marks.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(results, key = { it.id }) { res ->
                        ExamResultCard(
                            result = res,
                            onPrintClick = { selectedResultForPrint = res }
                        )
                    }
                }
            }
        }
    }

    // Marks Entry Modal
    if (showMarksEntryDialog) {
        MarksEntryDialog(
            students = students,
            examId = selectedExam?.id ?: "EXAM-2026-MID",
            examTitle = selectedExam?.title ?: "First Term Mid-Year Examination 2026",
            onDismiss = { showMarksEntryDialog = false },
            onSave = { res ->
                coroutineScope.launch {
                    repository.addExamResult(res)
                    Toast.makeText(context, "Result saved for ${res.studentName} (${res.grade} - ${res.percentage}%)", Toast.LENGTH_SHORT).show()
                    showMarksEntryDialog = false
                    selectedResultForPrint = res
                }
            }
        )
    }

    // Result Card Printable View
    selectedResultForPrint?.let { res ->
        ResultCardDialog(
            result = res,
            profile = schoolProfile ?: SchoolProfile(),
            onDismiss = { selectedResultForPrint = null }
        )
    }
}

@Composable
private fun ExamResultCard(result: ExamResultItem, onPrintClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onPrintClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(result.studentName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Class: ${result.className} | Roll: ${result.rollNo} | ID: ${result.studentId}", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total: ${result.obtainedMarks}/${result.totalMarks}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(${result.percentage}%)", fontSize = 12.sp, color = SchoolBlue, fontWeight = FontWeight.Bold)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = when (result.grade) {
                        "A+", "A" -> Color(0xFFDCFCE7)
                        "B", "C" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Grade ${result.grade}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = when (result.grade) {
                            "A+", "A" -> Color(0xFF166534)
                            "B", "C" -> Color(0xFF92400E)
                            else -> Color(0xFF991B1B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Rank: ${result.position}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
            }
        }
    }
}

@Composable
private fun MarksEntryDialog(
    students: List<Student>,
    examId: String,
    examTitle: String,
    onDismiss: () -> Unit,
    onSave: (ExamResultItem) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var mathMarks by remember { mutableStateOf("94") }
    var scienceMarks by remember { mutableStateOf("90") }
    var englishMarks by remember { mutableStateOf("88") }
    var urduMarks by remember { mutableStateOf("86") }
    var sindhiMarks by remember { mutableStateOf("92") }
    var teacherRemarks by remember { mutableStateOf("Hardworking and disciplined student") }

    val math = mathMarks.toIntOrNull() ?: 0
    val sci = scienceMarks.toIntOrNull() ?: 0
    val eng = englishMarks.toIntOrNull() ?: 0
    val urd = urduMarks.toIntOrNull() ?: 0
    val snd = sindhiMarks.toIntOrNull() ?: 0

    val total = 500
    val obtained = math + sci + eng + urd + snd
    val percentage = (obtained.toDouble() / total) * 100.0
    val grade = when {
        percentage >= 80.0 -> "A+"
        percentage >= 70.0 -> "A"
        percentage >= 60.0 -> "B"
        percentage >= 50.0 -> "C"
        percentage >= 40.0 -> "D"
        else -> "F"
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.92f).clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Subject Marks Entry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SchoolBlue)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Select Student:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    students.take(4).forEach { st ->
                        FilterChip(
                            selected = selectedStudent?.id == st.id,
                            onClick = { selectedStudent = st },
                            label = { Text(st.name.split(" ").first()) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = englishMarks, onValueChange = { englishMarks = it }, label = { Text("English (100)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = urduMarks, onValueChange = { urduMarks = it }, label = { Text("Urdu (100)") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sindhiMarks, onValueChange = { sindhiMarks = it }, label = { Text("Sindhi (100)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = mathMarks, onValueChange = { mathMarks = it }, label = { Text("Math (100)") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = scienceMarks, onValueChange = { scienceMarks = it }, label = { Text("Science (100)") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(10.dp))

                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Result Summary: $obtained / $total (${String.format("%.1f", percentage)}%)", fontWeight = FontWeight.Bold)
                        Text("Calculated Grade: $grade • Position: 1st", color = SchoolBlue, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = teacherRemarks,
                    onValueChange = { teacherRemarks = it },
                    label = { Text("Teacher Remarks") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val st = selectedStudent ?: return@Button
                            val jsonMarks = """[{"subject":"English","max":100,"obtained":$eng},{"subject":"Urdu","max":100,"obtained":$urd},{"subject":"Sindhi","max":100,"obtained":$snd},{"subject":"Mathematics","max":100,"obtained":$math},{"subject":"Science","max":100,"obtained":$sci}]"""
                            val res = ExamResultItem(
                                examId = examId,
                                examTitle = examTitle,
                                studentId = st.id,
                                studentName = st.name,
                                rollNo = st.rollNo,
                                className = st.className,
                                subjectMarksJson = jsonMarks,
                                totalMarks = total,
                                obtainedMarks = obtained,
                                percentage = Math.round(percentage * 10.0) / 10.0,
                                grade = grade,
                                position = "1st",
                                remarks = teacherRemarks
                            )
                            onSave(res)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue)
                    ) {
                        Text("Save Result Card")
                    }
                }
            }
        }
    }
}
