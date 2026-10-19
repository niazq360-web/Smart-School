package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ai.GeminiSchoolService
import com.example.data.SchoolRepository
import com.example.localization.StringsManager
import com.example.model.*
import com.example.ui.theme.SchoolBlue
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean, val timestamp: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsScreen(
    repository: SchoolRepository,
    currentLanguage: AppLanguage,
    currentRole: UserRole
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0: Dost AI Chat, 1: Paper Generator, 2: Syllabus, 3: OCR Data Entry

    val schoolProfile by repository.schoolProfile.collectAsState(initial = SchoolProfile())
    val students by repository.allStudents.collectAsState(initial = emptyList())
    val teachers by repository.allTeachers.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("ai_tools_screen")
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Dost AI") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Paper Maker") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Syllabus & Lesson") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("OCR Data Entry") })
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> DostAiChatTab(schoolProfile, students, teachers, currentLanguage)
            1 -> AiPaperGeneratorTab(currentLanguage)
            2 -> AiSyllabusAndLessonTab(currentLanguage)
            3 -> AiOcrDataEntryTab(repository)
        }
    }
}

@Composable
private fun DostAiChatTab(
    profile: SchoolProfile?,
    students: List<Student>,
    teachers: List<Teacher>,
    language: AppLanguage
) {
    val coroutineScope = rememberCoroutineScope()
    var promptInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("Assalam-o-Alaikum! I am 'Dost School AI'. How can I help you manage ${profile?.name ?: "your school"} today? You can ask in English, Urdu, or Sindhi.", false, "Just now")
        )
    }

    val contextInfo = remember(profile, students, teachers) {
        "School: ${profile?.name}, Students: ${students.size}, Teachers: ${teachers.size}, Classes: PlayGroup to Class 10."
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.isUser) SchoolBlue else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            if (isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dost AI is reasoning...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Quick query chips
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = false,
                onClick = { promptInput = "Generate Class 5 Math test syllabus" },
                label = { Text("Math Test", fontSize = 11.sp) }
            )
            FilterChip(
                selected = false,
                onClick = { promptInput = "احمد کی حاضری کی رپورٹ بتاؤ" },
                label = { Text("احمد حاضری (Urdu)", fontSize = 11.sp) }
            )
            FilterChip(
                selected = false,
                onClick = { promptInput = "سنڌي مضمون لاءِ ٽائيم ٽيبل ٺاهيو" },
                label = { Text("ٽائيم ٽيبل (Sindhi)", fontSize = 11.sp) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = { Text("Ask Dost AI in English, اردو or سنڌي...") },
                modifier = Modifier.weight(1f).testTag("dost_ai_input"),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (promptInput.isBlank() || isLoading) return@IconButton
                    val query = promptInput.trim()
                    promptInput = ""
                    chatMessages.add(ChatMessage(query, true, "Now"))
                    isLoading = true

                    coroutineScope.launch {
                        val answer = GeminiSchoolService.askDostAi(query, contextInfo, language)
                        chatMessages.add(ChatMessage(answer, false, "Now"))
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SchoolBlue)
                    .testTag("dost_ai_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
private fun AiPaperGeneratorTab(language: AppLanguage) {
    val coroutineScope = rememberCoroutineScope()
    var className by remember { mutableStateOf("Class 5") }
    var subject by remember { mutableStateOf("Mathematics") }
    var chapters by remember { mutableStateOf("Chapters 1 to 4 (Fractions & Decimals)") }
    var totalMarks by remember { mutableStateOf("50") }
    var timeAllowed by remember { mutableStateOf("2 Hours") }
    var difficulty by remember { mutableStateOf(DifficultyLevel.MEDIUM) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedPaper by remember { mutableStateOf<String?>(null) }
    var generatedAnswerKey by remember { mutableStateOf<String?>(null) }
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Question Paper, 1: Answer Key

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Examination & Test Generator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = chapters, onValueChange = { chapters = it }, label = { Text("Chapters / Syllabus Topics") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = totalMarks, onValueChange = { totalMarks = it }, label = { Text("Total Marks") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = timeAllowed, onValueChange = { timeAllowed = it }, label = { Text("Time Allowed") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Difficulty Level:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DifficultyLevel.values().forEach { diff ->
                        FilterChip(selected = difficulty == diff, onClick = { difficulty = diff }, label = { Text(diff.label) })
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        isGenerating = true
                        coroutineScope.launch {
                            val (paper, key) = GeminiSchoolService.generateExamPaper(
                                className = className,
                                subject = subject,
                                chapters = chapters,
                                totalMarks = totalMarks.toIntOrNull() ?: 50,
                                timeAllowed = timeAllowed,
                                difficulty = difficulty,
                                language = language
                            )
                            generatedPaper = paper
                            generatedAnswerKey = key
                            isGenerating = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue),
                    modifier = Modifier.fillMaxWidth().testTag("generate_paper_btn"),
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesizing Paper...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Question Paper + Answer Key")
                    }
                }
            }
        }

        generatedPaper?.let { paperText ->
            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedSubTab) {
                Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }, text = { Text("Question Paper") })
                Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }, text = { Text("Answer Key") })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedSubTab == 0) paperText else (generatedAnswerKey ?: ""),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AiSyllabusAndLessonTab(language: AppLanguage) {
    val coroutineScope = rememberCoroutineScope()
    var className by remember { mutableStateOf("Class 5") }
    var subject by remember { mutableStateOf("General Science") }
    var topic by remember { mutableStateOf("Photosynthesis & Plant Ecosystem") }
    var generatedContent by remember { mutableStateOf<String?>(null) }
    var isBusy by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Curriculum, Lesson Plans & Worksheets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Topic / Chapter") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            isBusy = true
                            coroutineScope.launch {
                                generatedContent = GeminiSchoolService.generateSyllabus(className, subject, 5, language)
                                isBusy = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolBlue)
                    ) {
                        Text("Syllabus", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            isBusy = true
                            coroutineScope.launch {
                                generatedContent = GeminiSchoolService.generateLessonPlan(className, subject, topic, 45, language)
                                isBusy = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                    ) {
                        Text("Lesson Plan", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            isBusy = true
                            coroutineScope.launch {
                                generatedContent = GeminiSchoolService.generateWorksheet(className, subject, topic, language)
                                isBusy = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        Text("Worksheet", fontSize = 12.sp)
                    }
                }
            }
        }

        generatedContent?.let { content ->
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(content, fontSize = 13.sp)
                }
            }
        }
    }
}

/**
 * OCR Document Scanner & Auto Data Entry with PREVIEW, EDIT & APPROVE controls!
 */
@Composable
private fun AiOcrDataEntryTab(repository: SchoolRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var sampleDocText by remember {
        mutableStateOf(
            """
            GOVT / CITIZENS HIGH SCHOOL HYDERABAD
            STUDENT ADMISSION RECORD SLIP
            Name: Daniyal Khoso
            Father: Ghulam Nabi Khoso
            Class: Class 5-A
            Roll No: 18
            B-Form: 41303-9988771-5
            Contact Phone: +92 300 9182736
            Address: Citizen Colony, Hyderabad
            Blood Group: A+
            """.trimIndent()
        )
    }

    var isExtracting by remember { mutableStateOf(false) }
    var parsedStudentData by remember { mutableStateOf<Map<String, String>?>(null) }
    var showPreviewApproveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Camera / Document OCR Auto Data Entry", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SchoolBlue)
                Text("Supports old English, Urdu, and Sindhi admission forms. AI extracts fields with mandatory Preview, Edit and Approve step.", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = sampleDocText,
                    onValueChange = { sampleDocText = it },
                    label = { Text("Scanned Document OCR Text") },
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isExtracting = true
                        coroutineScope.launch {
                            val map = GeminiSchoolService.parseDocumentToStudent(sampleDocText)
                            parsedStudentData = map
                            isExtracting = false
                            showPreviewApproveDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    modifier = Modifier.fillMaxWidth().testTag("ocr_extract_btn"),
                    enabled = !isExtracting
                ) {
                    if (isExtracting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extracting Fields with AI...")
                    } else {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Extract Fields & Open Preview")
                    }
                }
            }
        }
    }

    // MANDATORY PREVIEW, EDIT & APPROVE DIALOG BEFORE COMMITTING TO DATABASE
    if (showPreviewApproveDialog && parsedStudentData != null) {
        val data = parsedStudentData!!
        var editedName by remember { mutableStateOf(data["name"] ?: "Daniyal Khoso") }
        var editedFather by remember { mutableStateOf(data["fatherName"] ?: "Ghulam Nabi Khoso") }
        var editedClass by remember { mutableStateOf(data["className"] ?: "Class 5") }
        var editedRoll by remember { mutableStateOf(data["rollNo"] ?: "18") }
        var editedPhone by remember { mutableStateOf(data["phone"] ?: "+92 300 9182736") }
        var editedBForm by remember { mutableStateOf(data["bForm"] ?: "41303-9988771-5") }
        var editedAddress by remember { mutableStateOf(data["address"] ?: "Citizen Colony, Hyderabad") }

        Dialog(
            onDismissRequest = { showPreviewApproveDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f).clip(RoundedCornerShape(20.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())) {
                    Text("Preview, Edit & Approve Data Entry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                    Text("Verify all extracted fields before committing to school database.", fontSize = 12.sp, color = Color.Gray)
                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Student Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedFather, onValueChange = { editedFather = it }, label = { Text("Father's Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = editedClass, onValueChange = { editedClass = it }, label = { Text("Class") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = editedRoll, onValueChange = { editedRoll = it }, label = { Text("Roll #") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedPhone, onValueChange = { editedPhone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedBForm, onValueChange = { editedBForm = it }, label = { Text("B-Form #") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editedAddress, onValueChange = { editedAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showPreviewApproveDialog = false }) { Text("Reject / Discard") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val newStudent = Student(
                                        id = "STU-2026-${(200..999).random()}",
                                        admissionNo = "ADM-${(2000..9999).random()}",
                                        rollNo = editedRoll,
                                        name = editedName,
                                        fatherName = editedFather,
                                        dob = "2015-05-15",
                                        gender = "Male",
                                        bForm = editedBForm,
                                        address = editedAddress,
                                        fatherPhone = editedPhone,
                                        whatsappNo = editedPhone,
                                        emergencyContact = editedPhone,
                                        admissionDate = "2026-10-19",
                                        className = editedClass,
                                        section = "A"
                                    )
                                    repository.addStudent(newStudent, "OCR Auto Importer")
                                    Toast.makeText(context, "Student approved and imported to database!", Toast.LENGTH_SHORT).show()
                                    showPreviewApproveDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Approve & Save to Database")
                        }
                    }
                }
            }
        }
    }
}
