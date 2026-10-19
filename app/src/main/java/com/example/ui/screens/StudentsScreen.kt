package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.components.QrCodeCanvas
import com.example.ui.print.ResultCardDialog
import com.example.ui.print.StudentIdCardDialog
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val students by repository.allStudents.collectAsState(initial = emptyList())
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())

    var searchQuery by remember { mutableStateOf("") }
    var selectedClassFilter by remember { mutableStateOf("All") }

    var selectedStudentForDetail by remember { mutableStateOf<Student?>(null) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showIdCardStudent by remember { mutableStateOf<Student?>(null) }
    var showResultCardStudent by remember { mutableStateOf<Student?>(null) }
    var sampleResultForStudent by remember { mutableStateOf<ExamResultItem?>(null) }

    val classesList = remember(students) {
        listOf("All") + students.map { it.className }.distinct()
    }

    val filteredStudents = remember(students, searchQuery, selectedClassFilter) {
        students.filter { student ->
            val matchesQuery = student.name.contains(searchQuery, ignoreCase = true) ||
                    student.id.contains(searchQuery, ignoreCase = true) ||
                    student.fatherName.contains(searchQuery, ignoreCase = true) ||
                    student.rollNo.contains(searchQuery, ignoreCase = true)
            val matchesClass = selectedClassFilter == "All" || student.className == selectedClassFilter
            matchesQuery && matchesClass
        }
    }

    Scaffold(
        floatingActionButton = {
            if (currentRole != UserRole.PARENT) {
                FloatingActionButton(
                    onClick = { showAddStudentDialog = true },
                    containerColor = SchoolBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_student_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Student")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(StringsManager.get("search_placeholder", currentLanguage)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Class Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classesList.take(6).forEach { cls ->
                    FilterChip(
                        selected = selectedClassFilter == cls,
                        onClick = { selectedClassFilter = cls },
                        label = { Text(cls) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${filteredStudents.size} Students Found",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Students List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredStudents, key = { it.id }) { student ->
                    StudentItemCard(
                        student = student,
                        onClick = { selectedStudentForDetail = student }
                    )
                }
            }
        }
    }

    // Student Detail / Profile Dialog
    selectedStudentForDetail?.let { student ->
        Dialog(
            onDismissRequest = { selectedStudentForDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student Profile",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = SchoolBlue
                        )
                        IconButton(onClick = { selectedStudentForDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(SchoolBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SchoolBlue, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(student.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("ID: ${student.id} | Roll No: ${student.rollNo}", color = Color.Gray, fontSize = 13.sp)
                            Text("${student.className} - Section ${student.section} (${student.shift})", fontWeight = FontWeight.SemiBold, color = SchoolBlue, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code View
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Student Unique QR Code", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Scan at campus entrance for touchless attendance", fontSize = 11.sp, color = Color.Gray)
                            }
                            QrCodeCanvas(content = student.id, sizeDp = 70)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Information details
                    DetailRow("Father's Name", student.fatherName)
                    DetailRow("Date of Birth", student.dob)
                    DetailRow("Gender", student.gender)
                    DetailRow("B-Form #", student.bForm)
                    DetailRow("Blood Group", student.bloodGroup)
                    DetailRow("Father Contact", student.fatherPhone)
                    DetailRow("WhatsApp #", student.whatsappNo)
                    DetailRow("Emergency Phone", student.emergencyContact)
                    DetailRow("Address", student.address)
                    DetailRow("Transport Route", student.transportRoute)
                    DetailRow("Admission Date", student.admissionDate)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                showIdCardStudent = student
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print ID Card", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                sampleResultForStudent = ExamResultItem(
                                    examId = "EXAM-2026-MID",
                                    examTitle = "Mid-Term Examination 2026",
                                    studentId = student.id,
                                    studentName = student.name,
                                    rollNo = student.rollNo,
                                    className = student.className,
                                    subjectMarksJson = "[]",
                                    totalMarks = 500,
                                    obtainedMarks = 452,
                                    percentage = 90.4,
                                    grade = "A+",
                                    position = "1st",
                                    remarks = student.remarks
                                )
                                showResultCardStudent = student
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Result Card", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // WhatsApp Alert Button
                    OutlinedButton(
                        onClick = {
                            val alertMsg = StringsManager.generateAttendanceAlert(
                                studentName = student.name,
                                status = "Present",
                                time = "08:05 AM",
                                schoolName = schoolProfile?.name ?: "Smart School",
                                language = currentLanguage
                            )
                            val cleanPhone = student.whatsappNo.replace("+", "").replace(" ", "").replace("-", "")
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${URLEncoder.encode(alertMsg, "UTF-8")}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp Alert: $alertMsg", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send WhatsApp Attendance Alert to Parent", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Add Student Modal Dialog
    if (showAddStudentDialog) {
        AddStudentDialog(
            nextId = "STU-2026-${String.format("%03d", students.size + 1)}",
            onDismiss = { showAddStudentDialog = false },
            onSave = { newStudent ->
                coroutineScope.launch {
                    repository.addStudent(newStudent)
                    Toast.makeText(context, "Student ${newStudent.name} registered successfully!", Toast.LENGTH_SHORT).show()
                    showAddStudentDialog = false
                }
            }
        )
    }

    // ID Card Preview Dialog
    showIdCardStudent?.let { student ->
        StudentIdCardDialog(
            student = student,
            profile = schoolProfile ?: SchoolProfile(),
            onDismiss = { showIdCardStudent = null }
        )
    }

    // Result Card Dialog
    sampleResultForStudent?.let { result ->
        ResultCardDialog(
            result = result,
            profile = schoolProfile ?: SchoolProfile(),
            onDismiss = { sampleResultForStudent = null }
        )
    }
}

@Composable
private fun StudentItemCard(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_item_${student.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = SchoolBlue,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Father: ${student.fatherName}", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${student.className} - ${student.section}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = SchoolBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Roll #${student.rollNo}", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = student.id,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value.ifBlank { "—" }, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
private fun AddStudentDialog(
    nextId: String,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Class 5") }
    var section by remember { mutableStateOf("A") }
    var rollNo by remember { mutableStateOf("15") }
    var phone by remember { mutableStateOf("+92 300 ") }
    var whatsappNo by remember { mutableStateOf("+92 300 ") }
    var bForm by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Hyderabad") }
    var dob by remember { mutableStateOf("2015-06-15") }
    var gender by remember { mutableStateOf("Male") }
    var bloodGroup by remember { mutableStateOf("B+") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Register New Student", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SchoolBlue)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text("Father's Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Section") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rollNo,
                        onValueChange = { rollNo = it },
                        label = { Text("Roll #") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Father Phone") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = whatsappNo,
                        onValueChange = { whatsappNo = it },
                        label = { Text("WhatsApp #") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bForm,
                    onValueChange = { bForm = it },
                    label = { Text("B-Form / NADRA Reg #") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Residential Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank() || fatherName.isBlank()) return@Button
                            val student = Student(
                                id = nextId,
                                admissionNo = "ADM-${(1100..1999).random()}",
                                rollNo = rollNo,
                                name = name.trim(),
                                fatherName = fatherName.trim(),
                                dob = dob,
                                gender = gender,
                                bForm = bForm,
                                address = address,
                                fatherPhone = phone,
                                whatsappNo = whatsappNo,
                                emergencyContact = phone,
                                bloodGroup = bloodGroup,
                                admissionDate = "2026-10-19",
                                className = className,
                                section = section
                            )
                            onSave(student)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                        enabled = name.isNotBlank() && fatherName.isNotBlank()
                    ) {
                        Text("Save & Enroll")
                    }
                }
            }
        }
    }
}
