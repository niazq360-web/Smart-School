package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole,
    onNavigateTo: (String) -> Unit
) {
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentMonth = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()) }

    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())
    val students by repository.allStudents.collectAsState(initial = emptyList())
    val teachers by repository.allTeachers.collectAsState(initial = emptyList())
    val attendanceToday by repository.getAttendanceForDate(today).collectAsState(initial = emptyList())

    val todayFeeCollection by repository.getTodayFeeCollection(today).collectAsState(initial = 0.0)
    val monthlyFeeCollection by repository.getMonthlyFeeCollection(currentMonth).collectAsState(initial = 0.0)
    val pendingFees by repository.getTotalPendingFees().collectAsState(initial = 0.0)
    val totalExpenses by repository.getTotalExpenses().collectAsState(initial = 0.0)
    val notifications by repository.allNotifications.collectAsState(initial = emptyList())
    val exams by repository.allExams.collectAsState(initial = emptyList())

    val presentStudents = attendanceToday.count { it.personType == "STUDENT" && it.status == "PRESENT" }
    val absentStudents = attendanceToday.count { it.personType == "STUDENT" && it.status == "ABSENT" }
    val lateStudents = attendanceToday.count { it.personType == "STUDENT" && it.status == "LATE" }
    val presentTeachers = attendanceToday.count { it.personType == "TEACHER" && it.status == "PRESENT" }
    val absentTeachers = attendanceToday.count { it.personType == "TEACHER" && it.status == "ABSENT" }

    val netBalance = monthlyFeeCollection - totalExpenses
    val currency = schoolProfile?.currencySymbol ?: "Rs."

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SchoolBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = schoolProfile?.name ?: "Smart School",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${schoolProfile?.academicYear} • Role: ${currentRole.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        IconButton(
                            onClick = { onNavigateTo("ai_tools") },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .testTag("ai_assistant_quick_btn")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Dost AI", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = StringsManager.get("quick_actions", currentLanguage),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onNavigateTo("students") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolBlueLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(StringsManager.get("students", currentLanguage), maxLines = 1, fontSize = 12.sp)
                }
                Button(
                    onClick = { onNavigateTo("attendance") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(StringsManager.get("attendance", currentLanguage), maxLines = 1, fontSize = 12.sp)
                }
                Button(
                    onClick = { onNavigateTo("fees") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(StringsManager.get("fees", currentLanguage), maxLines = 1, fontSize = 12.sp)
                }
            }
        }

        // Student & Teacher Metric Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = StringsManager.get("total_students", currentLanguage),
                    value = "${students.size}",
                    subtitle = "${students.count { it.active }} Active",
                    icon = Icons.Default.School,
                    containerColor = SchoolBlueContainer,
                    contentColor = SchoolBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("students") }
                )
                StatCard(
                    title = StringsManager.get("total_teachers", currentLanguage),
                    value = "${teachers.size}",
                    subtitle = "$presentTeachers Present Today",
                    icon = Icons.Default.CastForEducation,
                    containerColor = Color(0xFFE0E7FF),
                    contentColor = PurpleAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("teachers") }
                )
            }
        }

        // Today's Attendance Breakdown Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = StringsManager.get("present_today", currentLanguage),
                    value = "$presentStudents",
                    subtitle = "Students In Campus",
                    icon = Icons.Default.CheckCircle,
                    containerColor = EmeraldContainer,
                    contentColor = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("attendance") }
                )
                StatCard(
                    title = StringsManager.get("absent_today", currentLanguage),
                    value = "$absentStudents",
                    subtitle = "Unaccounted",
                    icon = Icons.Default.Cancel,
                    containerColor = RoseContainer,
                    contentColor = RoseError,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("attendance") }
                )
                StatCard(
                    title = StringsManager.get("late_today", currentLanguage),
                    value = "$lateStudents",
                    subtitle = "Grace exceeded",
                    icon = Icons.Default.AccessTime,
                    containerColor = AmberContainer,
                    contentColor = AmberWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("attendance") }
                )
            }
        }

        // Financial Overview Cards
        item {
            Text(
                text = StringsManager.get("finance", currentLanguage),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = StringsManager.get("fee_collection", currentLanguage),
                    value = "$currency ${todayFeeCollection.toInt()}",
                    subtitle = "Month: $currency ${monthlyFeeCollection.toInt()}",
                    icon = Icons.Default.AccountBalanceWallet,
                    containerColor = EmeraldContainer,
                    contentColor = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("fees") }
                )
                StatCard(
                    title = StringsManager.get("pending_fees", currentLanguage),
                    value = "$currency ${pendingFees.toInt()}",
                    subtitle = "Outstanding Dues",
                    icon = Icons.Default.ReceiptLong,
                    containerColor = AmberContainer,
                    contentColor = AmberWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTo("fees") }
                )
            }
        }

        item {
            StatCard(
                title = StringsManager.get("net_balance", currentLanguage),
                value = "$currency ${netBalance.toInt()}",
                subtitle = "Monthly Income ($currency ${monthlyFeeCollection.toInt()}) - Expenses ($currency ${totalExpenses.toInt()})",
                icon = Icons.Default.TrendingUp,
                containerColor = if (netBalance >= 0) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                contentColor = if (netBalance >= 0) EmeraldGreen else RoseError,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNavigateTo("finance") }
            )
        }

        // Attendance Progress Bar Chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's Attendance Ratio",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val totalRecorded = (presentStudents + absentStudents + lateStudents).coerceAtLeast(1)
                    val presentFraction = presentStudents.toFloat() / totalRecorded
                    val lateFraction = lateStudents.toFloat() / totalRecorded
                    val absentFraction = absentStudents.toFloat() / totalRecorded

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(7.dp))
                    ) {
                        if (presentFraction > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(presentFraction)
                                    .background(EmeraldGreen, RoundedCornerShape(topStart = 7.dp, bottomStart = 7.dp))
                            )
                        }
                        if (lateFraction > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(lateFraction)
                                    .background(AmberLight)
                            )
                        }
                        if (absentFraction > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(absentFraction)
                                    .background(RoseLight, RoundedCornerShape(topEnd = 7.dp, bottomEnd = 7.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Present ($presentStudents)", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("• Late ($lateStudents)", color = AmberWarning, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("• Absent ($absentStudents)", color = RoseError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Notifications & Upcoming Events
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "School Announcements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = SchoolBlue)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    if (notifications.isEmpty()) {
                        Text("No notices posted yet.", color = Color.Gray, fontSize = 13.sp)
                    } else {
                        notifications.take(3).forEach { item ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(item.date, fontSize = 11.sp, color = Color.Gray)
                                }
                                Text(item.message, fontSize = 12.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }
        }
    }
}
