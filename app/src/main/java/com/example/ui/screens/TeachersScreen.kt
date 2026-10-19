package com.example.ui.screens

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
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch

@Composable
fun TeachersScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val teachers by repository.allTeachers.collectAsState(initial = emptyList())
    val staff by repository.allStaff.collectAsState(initial = emptyList())
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())

    var selectedTab by remember { mutableStateOf(0) } // 0: Teachers, 1: Non-teaching Staff
    var searchQuery by remember { mutableStateOf("") }
    var selectedTeacherDetail by remember { mutableStateOf<Teacher?>(null) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }

    val filteredTeachers = remember(teachers, searchQuery) {
        teachers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.subjectsAssigned.contains(searchQuery, ignoreCase = true) ||
                    it.designation.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (currentRole == UserRole.SUPER_ADMIN || currentRole == UserRole.SCHOOL_ADMIN || currentRole == UserRole.PRINCIPAL) {
                FloatingActionButton(
                    onClick = { showAddTeacherDialog = true },
                    containerColor = PurpleAccent,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_teacher_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Teacher")
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Teaching Faculty (${teachers.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Administrative Staff (${staff.size})") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search faculty by name, designation, subjects...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTeachers, key = { it.id }) { teacher ->
                        TeacherCard(
                            teacher = teacher,
                            currencySymbol = schoolProfile?.currencySymbol ?: "Rs.",
                            onClick = { selectedTeacherDetail = teacher }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(staff, key = { it.id }) { member ->
                        StaffCard(member = member, currencySymbol = schoolProfile?.currencySymbol ?: "Rs.")
                    }
                }
            }
        }
    }

    // Teacher Profile Modal
    selectedTeacherDetail?.let { teacher ->
        Dialog(
            onDismissRequest = { selectedTeacherDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.88f)
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
                        Text("Teacher Faculty Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PurpleAccent)
                        IconButton(onClick = { selectedTeacherDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PurpleAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(teacher.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(teacher.designation, color = Color.Gray, fontSize = 13.sp)
                            Text("ID: ${teacher.id} • ${teacher.experienceYears} Years Exp.", fontSize = 12.sp, color = PurpleAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Card for Attendance
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Teacher Biometric / QR Attendance ID", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Use at teacher lounge scanner", fontSize = 11.sp, color = Color.Gray)
                            }
                            QrCodeCanvas(content = teacher.id, sizeDp = 65, primaryColor = PurpleAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TeacherDetailItem("CNIC / NADRA", teacher.cnic)
                    TeacherDetailItem("Qualification", teacher.qualification)
                    TeacherDetailItem("Subjects Assigned", teacher.subjectsAssigned)
                    TeacherDetailItem("Classes Assigned", teacher.classesAssigned)
                    TeacherDetailItem("Contact Phone", teacher.phone)
                    TeacherDetailItem("WhatsApp", teacher.whatsappNo)
                    TeacherDetailItem("Basic Salary", "${schoolProfile?.currencySymbol ?: "Rs."} ${teacher.basicSalary.toInt()}")
                    TeacherDetailItem("Bank Details", teacher.bankDetails)
                    TeacherDetailItem("Joining Date", teacher.joiningDate)
                    TeacherDetailItem("Address", teacher.address)

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { selectedTeacherDetail = null }) { Text("Dismiss") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                Toast.makeText(context, "Printing Teacher Badge & QR Code for ${teacher.name}...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print Faculty Badge")
                        }
                    }
                }
            }
        }
    }

    // Add Teacher Modal
    if (showAddTeacherDialog) {
        AddTeacherDialog(
            nextId = "TCH-${String.format("%03d", teachers.size + 1)}",
            onDismiss = { showAddTeacherDialog = false },
            onSave = { newTeacher ->
                coroutineScope.launch {
                    repository.addTeacher(newTeacher)
                    Toast.makeText(context, "Teacher ${newTeacher.name} appointed successfully!", Toast.LENGTH_SHORT).show()
                    showAddTeacherDialog = false
                }
            }
        )
    }
}

@Composable
private fun TeacherCard(teacher: Teacher, currencySymbol: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("teacher_item_${teacher.id}"),
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
                    .background(Color(0xFFEDE9FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(teacher.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = PurpleAccent, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(teacher.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(teacher.designation, fontSize = 12.sp, color = Color.Gray)
                Text("Subjects: ${teacher.subjectsAssigned}", fontSize = 11.sp, color = PurpleAccent, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                    Text(teacher.id, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
private fun StaffCard(member: StaffMember, currencySymbol: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
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
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF475569))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(member.designation, fontSize = 12.sp, color = Color.Gray)
                Text("Phone: ${member.phone}", fontSize = 11.sp, color = Color.Gray)
            }
            Text("$currencySymbol ${member.basicSalary.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun TeacherDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value.ifBlank { "—" }, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
private fun AddTeacherDialog(
    nextId: String,
    onDismiss: () -> Unit,
    onSave: (Teacher) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var fatherOrHusband by remember { mutableStateOf("") }
    var cnic by remember { mutableStateOf("41303-") }
    var phone by remember { mutableStateOf("+92 300 ") }
    var qualification by remember { mutableStateOf("M.Sc") }
    var designation by remember { mutableStateOf("Subject Specialist") }
    var subjects by remember { mutableStateOf("Mathematics, Physics") }
    var classes by remember { mutableStateOf("Class 9, Class 10") }
    var salary by remember { mutableStateOf("55000") }

    Dialog(
        onDismissRequest = onDismiss,
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
                    Text("Appoint Faculty Member", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PurpleAccent)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Faculty Full Name *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = fatherOrHusband, onValueChange = { fatherOrHusband = it }, label = { Text("Father / Husband Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = cnic, onValueChange = { cnic = it }, label = { Text("CNIC Number") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = designation, onValueChange = { designation = it }, label = { Text("Designation") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = qualification, onValueChange = { qualification = it }, label = { Text("Qualification") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = subjects, onValueChange = { subjects = it }, label = { Text("Subjects Assigned") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = classes, onValueChange = { classes = it }, label = { Text("Classes Assigned") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = salary, onValueChange = { salary = it }, label = { Text("Basic Salary (PKR)") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) return@Button
                            val teacher = Teacher(
                                id = nextId,
                                name = name.trim(),
                                fatherOrHusbandName = fatherOrHusband.trim(),
                                cnic = cnic,
                                dob = "1990-01-01",
                                gender = "Male",
                                phone = phone,
                                whatsappNo = phone,
                                address = "Hyderabad, Sindh",
                                qualification = qualification,
                                experienceYears = 5,
                                joiningDate = "2026-10-19",
                                designation = designation,
                                subjectsAssigned = subjects,
                                classesAssigned = classes,
                                basicSalary = salary.toDoubleOrNull() ?: 50000.0
                            )
                            onSave(teacher)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        enabled = name.isNotBlank()
                    ) {
                        Text("Save & Register")
                    }
                }
            }
        }
    }
}
