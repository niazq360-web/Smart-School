package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    // School Profile
    @Query("SELECT * FROM school_profile WHERE id = 1 LIMIT 1")
    fun getSchoolProfile(): Flow<SchoolProfile?>

    @Query("SELECT * FROM school_profile WHERE id = 1 LIMIT 1")
    suspend fun getSchoolProfileSync(): SchoolProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchoolProfile(profile: SchoolProfile)

    // Students
    @Query("SELECT * FROM students ORDER BY className, rollNo")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE active = 1 ORDER BY className, rollNo")
    fun getActiveStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: String): Student?

    @Query("SELECT * FROM students WHERE className = :className ORDER BY rollNo")
    fun getStudentsByClass(className: String): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students WHERE active = 1")
    fun getActiveStudentsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudent(id: String)

    // Teachers
    @Query("SELECT * FROM teachers ORDER BY name")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE active = 1 ORDER BY name")
    fun getActiveTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
    suspend fun getTeacherById(id: String): Teacher?

    @Query("SELECT COUNT(*) FROM teachers WHERE active = 1")
    fun getActiveTeachersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher)

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Query("DELETE FROM teachers WHERE id = :id")
    suspend fun deleteTeacher(id: String)

    // Staff
    @Query("SELECT * FROM staff_members ORDER BY name")
    fun getAllStaff(): Flow<List<StaffMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffMember)

    // Attendance
    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY time DESC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE personId = :personId ORDER BY date DESC, time DESC")
    fun getAttendanceForPerson(personId: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE personId = :personId AND date = :date LIMIT 1")
    suspend fun getAttendanceForPersonOnDate(personId: String, date: String): AttendanceRecord?

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND personType = 'STUDENT' AND status = 'PRESENT'")
    fun getPresentStudentsCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND personType = 'STUDENT' AND status = 'ABSENT'")
    fun getAbsentStudentsCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND personType = 'STUDENT' AND status = 'LATE'")
    fun getLateStudentsCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND personType = 'TEACHER' AND status = 'PRESENT'")
    fun getPresentTeachersCount(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND personType = 'TEACHER' AND status = 'ABSENT'")
    fun getAbsentTeachersCount(date: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    // Fee Structures
    @Query("SELECT * FROM fee_structures ORDER BY className")
    fun getAllFeeStructures(): Flow<List<FeeStructure>>

    @Query("SELECT * FROM fee_structures WHERE className = :className LIMIT 1")
    suspend fun getFeeStructureForClass(className: String): FeeStructure?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeStructure(structure: FeeStructure)

    // Fee Payments
    @Query("SELECT * FROM fee_payments ORDER BY paymentDate DESC")
    fun getAllFeePayments(): Flow<List<FeePayment>>

    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY paymentDate DESC")
    fun getFeePaymentsForStudent(studentId: String): Flow<List<FeePayment>>

    @Query("SELECT COALESCE(SUM(paidAmount), 0.0) FROM fee_payments WHERE paymentDate = :date")
    fun getTodayFeeCollection(date: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(paidAmount), 0.0) FROM fee_payments WHERE month = :month")
    fun getMonthlyFeeCollection(month: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(remainingBalance), 0.0) FROM fee_payments")
    fun getTotalPendingFees(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeePayment(payment: FeePayment)

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseRecord>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses")
    fun getTotalExpenses(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseRecord): Long

    // Salary Slips
    @Query("SELECT * FROM salary_slips ORDER BY paymentDate DESC")
    fun getAllSalarySlips(): Flow<List<SalarySlip>>

    @Query("SELECT COALESCE(SUM(netSalary), 0.0) FROM salary_slips WHERE month = :month")
    fun getMonthlySalaryTotal(month: String): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalarySlip(slip: SalarySlip)

    // Classes & Sections
    @Query("SELECT * FROM classes ORDER BY className, section")
    fun getAllClasses(): Flow<List<ClassSection>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(classSection: ClassSection)

    // Subjects
    @Query("SELECT * FROM subjects ORDER BY className, name")
    fun getAllSubjects(): Flow<List<SubjectItem>>

    @Query("SELECT * FROM subjects WHERE className = :className ORDER BY name")
    fun getSubjectsForClass(className: String): Flow<List<SubjectItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectItem)

    // Timetable
    @Query("SELECT * FROM timetable_slots ORDER BY dayOfWeek, periodNo")
    fun getAllTimetableSlots(): Flow<List<TimetableSlot>>

    @Query("SELECT * FROM timetable_slots WHERE className = :className ORDER BY periodNo")
    fun getTimetableForClass(className: String): Flow<List<TimetableSlot>>

    @Query("SELECT * FROM timetable_slots WHERE teacherName LIKE '%' || :teacherName || '%' ORDER BY dayOfWeek, periodNo")
    fun getTimetableForTeacher(teacherName: String): Flow<List<TimetableSlot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableSlot(slot: TimetableSlot)

    // Exams & Results
    @Query("SELECT * FROM exams ORDER BY startDate DESC")
    fun getAllExams(): Flow<List<ExamRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamRecord)

    @Query("SELECT * FROM exam_results WHERE examId = :examId ORDER BY percentage DESC")
    fun getResultsForExam(examId: String): Flow<List<ExamResultItem>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY id DESC")
    fun getResultsForStudent(studentId: String): Flow<List<ExamResultItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultItem): Long

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY date DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    // Leave Requests
    @Query("SELECT * FROM leave_requests ORDER BY appliedDate DESC")
    fun getAllLeaveRequests(): Flow<List<LeaveRequest>>

    @Query("UPDATE leave_requests SET status = :status WHERE id = :id")
    suspend fun updateLeaveStatus(id: Long, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(request: LeaveRequest): Long

    // Library
    @Query("SELECT * FROM library_books ORDER BY title")
    fun getAllBooks(): Flow<List<LibraryBook>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: LibraryBook)

    // Inventory
    @Query("SELECT * FROM inventory_items ORDER BY category, name")
    fun getAllInventory(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(item: InventoryItem)

    // Transport
    @Query("SELECT * FROM transport_vehicles ORDER BY vehicleNo")
    fun getAllVehicles(): Flow<List<TransportVehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: TransportVehicle)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 150")
    fun getAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)

    // User Accounts
    @Query("SELECT * FROM user_accounts ORDER BY role")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)
}
