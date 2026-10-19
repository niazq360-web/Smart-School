package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.components.LanguageSwitcherRow
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0: School Setup, 1: Roles, 2: Audit Logs, 3: Backup
    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())
    val auditLogs by repository.auditLogs.collectAsState(initial = emptyList())

    // School Profile Edit State
    var schoolName by remember(schoolProfile) { mutableStateOf(schoolProfile?.name ?: "") }
    var address by remember(schoolProfile) { mutableStateOf(schoolProfile?.address ?: "") }
    var phone by remember(schoolProfile) { mutableStateOf(schoolProfile?.phone ?: "") }
    var whatsapp by remember(schoolProfile) { mutableStateOf(schoolProfile?.whatsapp ?: "") }
    var principal by remember(schoolProfile) { mutableStateOf(schoolProfile?.principalName ?: "") }
    var year by remember(schoolProfile) { mutableStateOf(schoolProfile?.academicYear ?: "") }
    var startTime by remember(schoolProfile) { mutableStateOf(schoolProfile?.schoolStartTime ?: "08:00") }
    var gracePeriod by remember(schoolProfile) { mutableStateOf("${schoolProfile?.gracePeriodMinutes ?: 10}") }

    var exportedJson by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("School Setup") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("User Roles") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Audit Logs") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Backup & Export") })
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // School Setup Wizard / Editor
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("System Language Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LanguageSwitcherRow(
                        currentLanguage = currentLanguage,
                        onLanguageSelected = onLanguageChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("School Profile Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                    Text("This information automatically prints on all receipts, ID cards, and certificates.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = schoolName, onValueChange = { schoolName = it }, label = { Text("School Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Campus Address") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Office Phone") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = { Text("Official WhatsApp") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = principal, onValueChange = { principal = it }, label = { Text("Principal Name") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Academic Year") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Start Time (HH:mm)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = gracePeriod, onValueChange = { gracePeriod = it }, label = { Text("Grace Period (Minutes)") }, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val updated = (schoolProfile ?: SchoolProfile()).copy(
                                    name = schoolName,
                                    address = address,
                                    phone = phone,
                                    whatsapp = whatsapp,
                                    principalName = principal,
                                    academicYear = year,
                                    schoolStartTime = startTime,
                                    gracePeriodMinutes = gracePeriod.toIntOrNull() ?: 10
                                )
                                repository.upsertSchoolProfile(updated)
                                Toast.makeText(context, "School profile updated successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                        modifier = Modifier.fillMaxWidth().testTag("save_school_profile_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save School Profile")
                    }
                }
            }
            1 -> {
                // Role-based access switcher
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Text("Role-Based Permissions & Simulation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                    Text("Switch between system roles to inspect views and access controls as specified in the PRD.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))

                    UserRole.values().forEach { role ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentRole == role) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        text = when (role) {
                                            UserRole.SUPER_ADMIN -> "Full access across all modules, settings & financial databases."
                                            UserRole.SCHOOL_ADMIN -> "Manage faculty, classes, exams, timetable, and campus setup."
                                            UserRole.PRINCIPAL -> "Executive reports, marks approvals, and academic certificates."
                                            UserRole.TEACHER -> "Record student marks, take attendance, view timetables."
                                            UserRole.ACCOUNTANT -> "Fee collection, receipts, staff salary slips, and expense ledger."
                                            UserRole.RECEPTIONIST -> "Student admissions, inquiry desk, and student profiles."
                                            UserRole.PARENT -> "Read-only access for child attendance, fees, marks, and timetables."
                                        },
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Button(
                                    onClick = {
                                        onRoleChange(role)
                                        Toast.makeText(context, "Active Role switched to ${role.displayName}", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentRole == role) SchoolBlue else Color(0xFFE2E8F0)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (currentRole == role) "Active" else "Switch",
                                        color = if (currentRole == role) Color.White else Color(0xFF334155),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Audit Logs
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("Security Audit Logs (${auditLogs.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                    Text("System-wide tracking of Who + What + When for every change.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(auditLogs, key = { it.id }) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(10.dp),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SchoolBlue)
                                        Text(log.timestamp, fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(log.details, fontSize = 12.sp)
                                    Text("By: ${log.actor}", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // Backup & Export
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    Text("Database Backup & Export", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                    Text("Export the full relational school database to structured JSON format.", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                exportedJson = repository.exportDataJson()
                                Toast.makeText(context, "Full school database exported successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                        modifier = Modifier.fillMaxWidth().testTag("export_database_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Database to JSON")
                    }

                    exportedJson?.let { jsonStr ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Exported JSON Payload Preview:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(jsonStr, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
