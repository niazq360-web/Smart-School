package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.example.model.AppLanguage
import com.example.model.TimetableSlot
import com.example.model.UserRole
import com.example.ui.theme.SchoolBlue

@Composable
fun TimetableScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val timetableSlots by repository.allTimetableSlots.collectAsState(initial = emptyList())
    var selectedDay by remember { mutableStateOf("Monday") }
    var selectedClass by remember { mutableStateOf("Class 5") }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val classes = listOf("Class 1", "Class 5", "Class 9", "Class 10")

    val filteredSlots = remember(timetableSlots, selectedDay, selectedClass) {
        timetableSlots.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) && it.className == selectedClass }
            .sortedBy { it.periodNo }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("timetable_screen")
    ) {
        Text("Weekly Class Timetable", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SchoolBlue)
        Spacer(modifier = Modifier.height(10.dp))

        // Days of week tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            daysOfWeek.take(4).forEach { day ->
                FilterChip(
                    selected = selectedDay == day,
                    onClick = { selectedDay = day },
                    label = { Text(day, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Class selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            classes.forEach { cls ->
                FilterChip(
                    selected = selectedClass == cls,
                    onClick = { selectedClass = cls },
                    label = { Text(cls, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredSlots.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No periods scheduled for $selectedClass on $selectedDay.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredSlots, key = { it.id }) { slot ->
                    TimetableSlotCard(slot = slot)
                }
            }
        }
    }
}

@Composable
private fun TimetableSlotCard(slot: TimetableSlot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(54.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("P${slot.periodNo}", fontWeight = FontWeight.Bold, color = SchoolBlue, fontSize = 16.sp)
                    Text("Period", fontSize = 9.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(slot.subjectName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Teacher: ${slot.teacherName}", fontSize = 12.sp, color = Color.Gray)
                Text("${slot.startTime} - ${slot.endTime} • ${slot.room}", fontSize = 11.sp, color = SchoolBlue)
            }
        }
    }
}
