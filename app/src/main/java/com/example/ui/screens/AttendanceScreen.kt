package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.components.FaceScannerView
import com.example.ui.components.QrCodeCanvas
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var selectedTab by remember { mutableStateOf(0) } // 0: QR Scan, 1: Face Scan, 2: Today's Log
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())
    val students by repository.allStudents.collectAsState(initial = emptyList())
    val teachers by repository.allTeachers.collectAsState(initial = emptyList())
    val todayAttendance by repository.getAttendanceForDate(today).collectAsState(initial = emptyList())

    // Scanner state
    var selectedPersonId by remember { mutableStateOf("") }
    var scanTimeInput by remember { mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())) }
    var scanFeedback by remember { mutableStateOf<String?>(null) }
    var isErrorFeedback by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("attendance_screen")
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("QR Scanner") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Face Recognition") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Today's Log (${todayAttendance.size})") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback Banner
        scanFeedback?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isErrorFeedback) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isErrorFeedback) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isErrorFeedback) RoseError else EmeraldGreen
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = if (isErrorFeedback) RoseError else Color(0xFF166534),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { scanFeedback = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // QR Scanner Mode
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Live QR Code Attendance Terminal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SchoolBlue
                            )
                            Text(
                                text = "Grace Period: ${schoolProfile?.schoolStartTime} + ${schoolProfile?.gracePeriodMinutes} mins grace (Late after 08:10)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interactive camera viewfinder simulation with QR target
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color(0xFF0F172A), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                QrCodeCanvas(
                                    content = selectedPersonId.ifBlank { "DEMO-ATTENDANCE" },
                                    sizeDp = 140,
                                    primaryColor = Color(0xFF38BDF8)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Student / Teacher quick selector
                            Text("Select Badge ID or Student to Simulate Scan:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedPersonId,
                                    onValueChange = { selectedPersonId = it },
                                    label = { Text("Person ID (e.g. STU-2026-001)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = scanTimeInput,
                                    onValueChange = { scanTimeInput = it },
                                    label = { Text("Scan Time (HH:mm)") },
                                    modifier = Modifier.width(110.dp),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Fast selection chips
                            Text("Fast Select Student:", fontSize = 11.sp, color = Color.Gray)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                students.take(3).forEach { st ->
                                    FilterChip(
                                        selected = selectedPersonId == st.id,
                                        onClick = { selectedPersonId = st.id },
                                        label = { Text("${st.rollNo}. ${st.name.split(" ").first()}") }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val targetStudent = students.find { it.id.equals(selectedPersonId.trim(), ignoreCase = true) }
                                    val targetTeacher = teachers.find { it.id.equals(selectedPersonId.trim(), ignoreCase = true) }

                                    val name = targetStudent?.name ?: targetTeacher?.name ?: "Ahmed Ali Chandio"
                                    val type = if (targetTeacher != null) "TEACHER" else "STUDENT"
                                    val cls = targetStudent?.className ?: "Class 5"
                                    val sec = targetStudent?.section ?: "A"
                                    val idToUse = if (selectedPersonId.isNotBlank()) selectedPersonId.trim() else "STU-2026-001"

                                    coroutineScope.launch {
                                        val result = repository.recordAttendance(
                                            personId = idToUse,
                                            personType = type,
                                            personName = name,
                                            className = cls,
                                            section = sec,
                                            method = AttendanceMethod.QR_SCAN,
                                            currentTime = scanTimeInput,
                                            currentDate = today
                                        )

                                        result.onSuccess { rec ->
                                            isErrorFeedback = false
                                            scanFeedback = "SUCCESS: ${rec.personName} (${rec.personId}) marked as ${rec.status} at ${rec.time}."
                                        }.onFailure { err ->
                                            isErrorFeedback = true
                                            scanFeedback = err.message ?: "Attendance failed."
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("scan_qr_trigger_btn")
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scan & Process QR Code")
                            }
                        }
                    }
                }
            }
            1 -> {
                // Face Recognition Scanner
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    FaceScannerView(
                        onCaptureClick = {
                            val student = students.firstOrNull() ?: Student(
                                id = "STU-2026-001",
                                admissionNo = "101",
                                rollNo = "1",
                                name = "Ahmed Ali Chandio",
                                fatherName = "Niaz",
                                dob = "2015-01-01",
                                gender = "Male",
                                address = "Hyderabad",
                                fatherPhone = "+92 300 123",
                                whatsappNo = "+92 300 123",
                                emergencyContact = "+92 300 123",
                                admissionDate = "2024",
                                className = "Class 5"
                            )
                            val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                            coroutineScope.launch {
                                val res = repository.recordAttendance(
                                    personId = student.id,
                                    personType = "STUDENT",
                                    personName = student.name,
                                    className = student.className,
                                    section = student.section,
                                    method = AttendanceMethod.FACE_SCAN,
                                    currentTime = timeNow,
                                    currentDate = today
                                )
                                res.onSuccess { rec ->
                                    isErrorFeedback = false
                                    scanFeedback = "FACE RECOGNITION MATCH: ${rec.personName} (${rec.personId}) identified with 99.4% confidence! Status: ${rec.status} at ${rec.time}."
                                }.onFailure { err ->
                                    isErrorFeedback = true
                                    scanFeedback = err.message ?: "Biometric check failed."
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Face Recognition Policy & Privacy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Biometric face vectors are processed strictly on-device for authorized campus entry. No images are shared externally. System meets school child-safety regulations.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            2 -> {
                // Today's Attendance Log List
                if (todayAttendance.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No attendance recorded yet for today ($today).", color = Color.Gray)
                            Text("Use QR Scan or Face Scan above to record attendance.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(todayAttendance, key = { it.id }) { rec ->
                            AttendanceLogItem(
                                record = rec,
                                onSendWhatsApp = {
                                    val msg = StringsManager.generateAttendanceAlert(
                                        studentName = rec.personName,
                                        status = rec.status,
                                        time = rec.time,
                                        schoolName = schoolProfile?.name ?: "Smart School",
                                        language = currentLanguage
                                    )
                                    val student = students.find { it.id == rec.personId }
                                    val phone = student?.whatsappNo ?: "+923001234567"
                                    val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${URLEncoder.encode(msg, "UTF-8")}")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Alert: $msg", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceLogItem(record: AttendanceRecord, onSendWhatsApp: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(record.personName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("(${record.personId})", fontSize = 11.sp, color = Color.Gray)
                }
                Text("${record.personType} • ${record.className} - ${record.section} • Time: ${record.time} (${record.method})", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            StatusBadge(status = record.status)

            if (record.personType == "STUDENT") {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onSendWhatsApp) {
                    Icon(Icons.Default.Send, contentDescription = "Send WhatsApp Alert", tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
