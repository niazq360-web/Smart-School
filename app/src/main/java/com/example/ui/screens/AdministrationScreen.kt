package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun AdministrationScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0: Certificates, 1: Leaves, 2: Transport & Library, 3: WhatsApp Hub
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())
    val students by repository.allStudents.collectAsState(initial = emptyList())
    val leaves by repository.allLeaveRequests.collectAsState(initial = emptyList())
    val books by repository.allBooks.collectAsState(initial = emptyList())
    val vehicles by repository.allVehicles.collectAsState(initial = emptyList())

    var showCertificateDialog by remember { mutableStateOf(false) }
    var selectedCertificateType by remember { mutableStateOf("Bonafide Certificate") }
    var selectedStudentForCert by remember { mutableStateOf(students.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_screen")
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Certificates") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Leaves") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Transport & Lib") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("WhatsApp Alerts") })
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // Certificates Generator
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Official School Certificates Generator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                            Text("Generates Bonafide, Character, and School Leaving Certificates with official crest and signature line.", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Select Certificate Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            listOf("Bonafide Certificate", "Character Certificate", "School Leaving Certificate", "Achievement Certificate").forEach { cert ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedCertificateType == cert, onClick = { selectedCertificateType = cert })
                                    Text(cert, fontSize = 13.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Select Student:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                students.take(4).forEach { st ->
                                    FilterChip(
                                        selected = selectedStudentForCert?.id == st.id,
                                        onClick = { selectedStudentForCert = st },
                                        label = { Text(st.name.split(" ").first()) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showCertificateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                                modifier = Modifier.fillMaxWidth().testTag("generate_certificate_btn")
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate & View Printable Certificate")
                            }
                        }
                    }
                }
            }
            1 -> {
                // Leave Requests
                if (leaves.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No pending leave applications.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(leaves, key = { it.id }) { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${req.applicantName} (${req.applicantType})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        StatusBadge(status = req.status)
                                    }
                                    Text("Dates: ${req.startDate} to ${req.endDate}", fontSize = 12.sp, color = SchoolBlue)
                                    Text("Reason: \"${req.reason}\"", fontSize = 12.sp, color = Color(0xFF475569))

                                    if (req.status == "PENDING" && currentRole != UserRole.PARENT) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            OutlinedButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        repository.updateLeaveStatus(req.id, LeaveStatus.REJECTED)
                                                    }
                                                }
                                            ) { Text("Reject") }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        repository.updateLeaveStatus(req.id, LeaveStatus.APPROVED)
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                            ) { Text("Approve") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Transport & Library
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("School Transport Vehicles", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SchoolBlue)
                    vehicles.forEach { v ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = CardDefaults.outlinedCardBorder()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${v.vehicleNo} - ${v.routeName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Driver: ${v.driverName} • Phone: ${v.driverPhone} • Capacity: ${v.capacity} seats", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Text("Library Catalog (${books.size} Books)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SchoolBlue)
                    books.forEach { b ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = CardDefaults.outlinedCardBorder()) {
                            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(b.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Author: ${b.author} • Location: ${b.rackLocation}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Text("${b.availableCopies}/${b.totalCopies} Avail", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF059669))
                            }
                        }
                    }
                }
            }
            3 -> {
                // WhatsApp & SMS Alerts Hub
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Text("Automated Parent WhatsApp & SMS Alerts Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                    Text("Trigger real-time messages in English, Urdu, or Sindhi directly to parents.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))

                    val st = selectedStudentForCert ?: students.firstOrNull()
                    st?.let { student ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = CardDefaults.outlinedCardBorder()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Selected Child: ${student.name} (Parent: ${student.fatherPhone})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(10.dp))

                                AlertButton("1. Send Attendance Arrival Notice", StringsManager.generateAttendanceAlert(student.name, "Present", "08:05 AM", schoolProfile?.name ?: "Smart School", currentLanguage), student.whatsappNo, context)
                                Spacer(modifier = Modifier.height(6.dp))
                                AlertButton("2. Send Absent Notice", "Dear Parent, your child ${student.name} is absent from school today without prior notice. Please contact administration.", student.whatsappNo, context)
                                Spacer(modifier = Modifier.height(6.dp))
                                AlertButton("3. Send Monthly Fee Due Reminder", StringsManager.generateFeeDueAlert(student.name, 4000.0, "October 2026", schoolProfile?.name ?: "Smart School", currentLanguage), student.whatsappNo, context)
                                Spacer(modifier = Modifier.height(6.dp))
                                AlertButton("4. Send Upcoming Examination Notice", StringsManager.generateExamAlert(student.name, "First Term Mid-Year Exams", "5th November 2026", schoolProfile?.name ?: "Smart School", currentLanguage), student.whatsappNo, context)
                            }
                        }
                    }
                }
            }
        }
    }

    // Certificate Preview Dialog
    if (showCertificateDialog && selectedStudentForCert != null) {
        val st = selectedStudentForCert!!
        val profile = schoolProfile ?: SchoolProfile()

        Dialog(
            onDismissRequest = { showCertificateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f).clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Printable Certificate", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showCertificateDialog = false }) { Icon(Icons.Default.Close, contentDescription = null) }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Certificate Parchment Box
                    Card(
                        modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFEFA)),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).border(4.dp, Color(0xFFB45309), RoundedCornerShape(4.dp)).padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(profile.name.uppercase(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SchoolBlue, textAlign = TextAlign.Center)
                            Text("Govt. Recognized Reg #: ${profile.regNumber}", fontSize = 11.sp, color = Color.Gray)
                            Text(profile.address, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = selectedCertificateType.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFFB45309),
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "This is to certify that ${st.name}, Son/Daughter of ${st.fatherName}, bearing Student ID ${st.id} and Roll Number ${st.rollNo}, is a bonafide student of this institution studying in ${st.className} (Academic Session ${profile.academicYear}).",
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "During their academic tenure here, their conduct and moral character have been found EXCELLENT. We wish them success in their future academic pursuits.",
                                fontSize = 13.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(36.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Date: 19th October 2026", fontSize = 11.sp)
                                    Text("Official School Seal", fontSize = 10.sp, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("_______________________________", fontSize = 10.sp, color = Color.Gray)
                                    Text("Prof. Ghulam Mustafa Chandio", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Principal / Head of Institution", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showCertificateDialog = false }) { Text("Dismiss") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { Toast.makeText(context, "Printing Certificate for ${st.name}...", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309))
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print Certificate")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertButton(title: String, message: String, phone: String, context: Context) {
    OutlinedButton(
        onClick = {
            val clean = phone.replace("+", "").replace(" ", "").replace("-", "")
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=${URLEncoder.encode(message, "UTF-8")}")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Alert: $message", Toast.LENGTH_LONG).show()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 12.sp)
            Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
        }
    }
}
