package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SchoolRepository(private val dao: SchoolDao) {

    val schoolProfile: Flow<SchoolProfile?> = dao.getSchoolProfile()
    val allStudents: Flow<List<Student>> = dao.getAllStudents()
    val allTeachers: Flow<List<Teacher>> = dao.getAllTeachers()
    val allStaff: Flow<List<StaffMember>> = dao.getAllStaff()
    val allFeeStructures: Flow<List<FeeStructure>> = dao.getAllFeeStructures()
    val allFeePayments: Flow<List<FeePayment>> = dao.getAllFeePayments()
    val allExpenses: Flow<List<ExpenseRecord>> = dao.getAllExpenses()
    val allSalarySlips: Flow<List<SalarySlip>> = dao.getAllSalarySlips()
    val allClasses: Flow<List<ClassSection>> = dao.getAllClasses()
    val allSubjects: Flow<List<SubjectItem>> = dao.getAllSubjects()
    val allTimetableSlots: Flow<List<TimetableSlot>> = dao.getAllTimetableSlots()
    val allExams: Flow<List<ExamRecord>> = dao.getAllExams()
    val allNotifications: Flow<List<NotificationItem>> = dao.getAllNotifications()
    val allLeaveRequests: Flow<List<LeaveRequest>> = dao.getAllLeaveRequests()
    val allBooks: Flow<List<LibraryBook>> = dao.getAllBooks()
    val allInventory: Flow<List<InventoryItem>> = dao.getAllInventory()
    val allVehicles: Flow<List<TransportVehicle>> = dao.getAllVehicles()
    val auditLogs: Flow<List<AuditLog>> = dao.getAuditLogs()
    val allUsers: Flow<List<UserAccount>> = dao.getAllUsers()

    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> = dao.getAttendanceByDate(date)

    fun getPresentStudentsCount(date: String): Flow<Int> = dao.getPresentStudentsCount(date)
    fun getAbsentStudentsCount(date: String): Flow<Int> = dao.getAbsentStudentsCount(date)
    fun getLateStudentsCount(date: String): Flow<Int> = dao.getLateStudentsCount(date)

    fun getPresentTeachersCount(date: String): Flow<Int> = dao.getPresentTeachersCount(date)
    fun getAbsentTeachersCount(date: String): Flow<Int> = dao.getAbsentTeachersCount(date)

    fun getTodayFeeCollection(date: String): Flow<Double> = dao.getTodayFeeCollection(date)
    fun getMonthlyFeeCollection(month: String): Flow<Double> = dao.getMonthlyFeeCollection(month)
    fun getTotalPendingFees(): Flow<Double> = dao.getTotalPendingFees()
    fun getTotalExpenses(): Flow<Double> = dao.getTotalExpenses()
    fun getMonthlySalaryTotal(month: String): Flow<Double> = dao.getMonthlySalaryTotal(month)

    fun getResultsForExam(examId: String): Flow<List<ExamResultItem>> = dao.getResultsForExam(examId)
    fun getResultsForStudent(studentId: String): Flow<List<ExamResultItem>> = dao.getResultsForStudent(studentId)

    suspend fun getStudentById(id: String): Student? = dao.getStudentById(id)
    suspend fun getTeacherById(id: String): Teacher? = dao.getTeacherById(id)

    suspend fun upsertSchoolProfile(profile: SchoolProfile, actor: String = "Admin") {
        dao.upsertSchoolProfile(profile)
        logAudit(actor, "UPDATE_PROFILE", "Updated school details for ${profile.name}")
    }

    suspend fun addStudent(student: Student, actor: String = "Admin") {
        dao.insertStudent(student)
        logAudit(actor, "ADD_STUDENT", "Enrolled ${student.name} (ID: ${student.id}) in ${student.className}")
    }

    suspend fun updateStudent(student: Student, actor: String = "Admin") {
        dao.updateStudent(student)
        logAudit(actor, "UPDATE_STUDENT", "Updated record for ${student.name} (${student.id})")
    }

    suspend fun deleteStudent(id: String, actor: String = "Admin") {
        dao.deleteStudent(id)
        logAudit(actor, "DELETE_STUDENT", "Deleted student record $id")
    }

    suspend fun addTeacher(teacher: Teacher, actor: String = "Admin") {
        dao.insertTeacher(teacher)
        logAudit(actor, "ADD_TEACHER", "Registered teacher ${teacher.name} (${teacher.id})")
    }

    suspend fun updateTeacher(teacher: Teacher, actor: String = "Admin") {
        dao.updateTeacher(teacher)
        logAudit(actor, "UPDATE_TEACHER", "Updated teacher ${teacher.name} (${teacher.id})")
    }

    suspend fun deleteTeacher(id: String, actor: String = "Admin") {
        dao.deleteTeacher(id)
        logAudit(actor, "DELETE_TEACHER", "Removed teacher record $id")
    }

    suspend fun addStaff(staff: StaffMember, actor: String = "Admin") {
        dao.insertStaff(staff)
        logAudit(actor, "ADD_STAFF", "Added staff member ${staff.name} (${staff.designation})")
    }

    // Smart Attendance Handler with Grace Period logic and duplicate prevention
    suspend fun recordAttendance(
        personId: String,
        personType: String,
        personName: String,
        className: String,
        section: String,
        method: AttendanceMethod,
        currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
        currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        overrideStatus: AttendanceStatus? = null,
        actor: String = "Attendance System"
    ): Result<AttendanceRecord> {
        // Check duplicate
        val existing = dao.getAttendanceForPersonOnDate(personId, currentDate)
        if (existing != null) {
            return Result.failure(Exception("Attendance already recorded today for $personName at ${existing.time} (${existing.status})"))
        }

        val profile = dao.getSchoolProfileSync() ?: SchoolProfile()
        val status = if (overrideStatus != null) {
            overrideStatus.name
        } else {
            // Evaluate present vs late based on school start time + grace period
            val startTimeParts = profile.schoolStartTime.split(":")
            val startMinutes = (startTimeParts.getOrNull(0)?.toIntOrNull() ?: 8) * 60 +
                    (startTimeParts.getOrNull(1)?.toIntOrNull() ?: 0)
            val thresholdMinutes = startMinutes + profile.gracePeriodMinutes

            val timeParts = currentTime.split(":")
            val scanMinutes = (timeParts.getOrNull(0)?.toIntOrNull() ?: 8) * 60 +
                    (timeParts.getOrNull(1)?.toIntOrNull() ?: 0)

            if (scanMinutes <= thresholdMinutes) {
                AttendanceStatus.PRESENT.name
            } else {
                AttendanceStatus.LATE.name
            }
        }

        val record = AttendanceRecord(
            personId = personId,
            personType = personType,
            personName = personName,
            className = className,
            section = section,
            date = currentDate,
            time = currentTime,
            status = status,
            method = method.name
        )
        val rowId = dao.insertAttendance(record)
        logAudit(actor, "RECORD_ATTENDANCE", "$personType $personName ($personId) marked as $status via $method at $currentTime")
        return Result.success(record.copy(id = rowId))
    }

    suspend fun collectFee(payment: FeePayment, actor: String = "Accountant") {
        dao.insertFeePayment(payment)
        logAudit(actor, "COLLECT_FEE", "Received fee ${payment.paidAmount} for ${payment.studentName} (${payment.receiptNo})")
    }

    suspend fun addExpense(expense: ExpenseRecord, actor: String = "Accountant") {
        dao.insertExpense(expense)
        logAudit(actor, "ADD_EXPENSE", "Recorded expense: ${expense.category} - Rs. ${expense.amount}")
    }

    suspend fun generateSalarySlip(slip: SalarySlip, actor: String = "Accountant") {
        dao.insertSalarySlip(slip)
        logAudit(actor, "SALARY_SLIP", "Generated salary slip for ${slip.employeeName}: Rs. ${slip.netSalary}")
    }

    suspend fun addNotification(notification: NotificationItem, actor: String = "Admin") {
        dao.insertNotification(notification)
        logAudit(actor, "POST_NOTICE", "Published notice: ${notification.title}")
    }

    suspend fun addExam(exam: ExamRecord, actor: String = "Admin") {
        dao.insertExam(exam)
        logAudit(actor, "ADD_EXAM", "Created exam: ${exam.title}")
    }

    suspend fun addExamResult(result: ExamResultItem, actor: String = "Teacher") {
        dao.insertExamResult(result)
        logAudit(actor, "ENTER_MARKS", "Recorded result for ${result.studentName} in ${result.examTitle}")
    }

    suspend fun updateLeaveStatus(id: Long, status: LeaveStatus, actor: String = "Admin") {
        dao.updateLeaveStatus(id, status.name)
        logAudit(actor, "LEAVE_STATUS", "Updated leave request #$id to $status")
    }

    suspend fun addLeaveRequest(request: LeaveRequest, actor: String = "User") {
        dao.insertLeaveRequest(request)
        logAudit(actor, "APPLY_LEAVE", "Leave requested by ${request.applicantName}")
    }

    suspend fun addLibraryBook(book: LibraryBook, actor: String = "Librarian") {
        dao.insertBook(book)
        logAudit(actor, "ADD_BOOK", "Catalogued book: ${book.title}")
    }

    suspend fun addInventory(item: InventoryItem, actor: String = "Admin") {
        dao.insertInventory(item)
        logAudit(actor, "ADD_INVENTORY", "Added inventory: ${item.name}")
    }

    suspend fun logAudit(actor: String, action: String, details: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        dao.insertAuditLog(AuditLog(actor = actor, action = action, details = details, timestamp = timestamp))
    }

    // Export Database to JSON String (for backup & download)
    suspend fun exportDataJson(): String {
        val root = JSONObject()
        val profile = dao.getSchoolProfileSync()
        profile?.let {
            root.put("schoolName", it.name)
            root.put("principal", it.principalName)
            root.put("academicYear", it.academicYear)
        }
        root.put("exportTimestamp", System.currentTimeMillis())
        root.put("system", "Smart School Management System")
        return root.toString(2)
    }
}
