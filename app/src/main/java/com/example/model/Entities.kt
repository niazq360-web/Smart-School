package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school_profile")
data class SchoolProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "The Citizens Public High School",
    val logoUri: String = "",
    val address: String = "Main Education Boulevard, Block 4, Hyderabad, Sindh",
    val phone: String = "+92 22 2781920",
    val whatsapp: String = "+92 300 1234567",
    val email: String = "info@citizenschool.edu.pk",
    val website: String = "www.citizenschool.edu.pk",
    val regNumber: String = "REG-SD-HYD-2024-8891",
    val principalName: String = "Prof. Ghulam Mustafa Chandio",
    val academicYear: String = "2026-2027",
    val schoolStartTime: String = "08:00",
    val schoolEndTime: String = "13:30",
    val gracePeriodMinutes: Int = 10,
    val workingDays: String = "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday",
    val currency: String = "PKR",
    val currencySymbol: String = "Rs.",
    val defaultLanguage: String = "en",
    val autoMonthlyFeeEnabled: Boolean = true
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: String, // STU-2026-001
    val admissionNo: String,
    val rollNo: String,
    val name: String,
    val fatherName: String,
    val motherName: String = "",
    val dob: String,
    val gender: String, // Male, Female
    val bForm: String = "",
    val photoUri: String = "",
    val address: String,
    val city: String = "Hyderabad",
    val fatherPhone: String,
    val motherPhone: String = "",
    val whatsappNo: String,
    val emergencyContact: String,
    val bloodGroup: String = "B+",
    val previousSchool: String = "",
    val admissionDate: String,
    val className: String, // Class 1, Class 5, Class 10
    val section: String = "A",
    val shift: String = "Morning",
    val transportRoute: String = "Route 1",
    val remarks: String = "Regular student",
    val active: Boolean = true,
    val fixedDiscount: Double = 0.0,
    val percentDiscount: Double = 0.0
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey val id: String, // TCH-2026-001
    val name: String,
    val fatherOrHusbandName: String,
    val cnic: String,
    val dob: String,
    val gender: String,
    val photoUri: String = "",
    val phone: String,
    val whatsappNo: String,
    val address: String,
    val qualification: String, // M.Sc Mathematics, B.Ed
    val experienceYears: Int,
    val joiningDate: String,
    val designation: String, // Senior Teacher, Science Head
    val subjectsAssigned: String, // Mathematics, Physics
    val classesAssigned: String, // Class 9, Class 10
    val basicSalary: Double,
    val bankDetails: String = "HBL - Account # 1042-7890123",
    val emergencyContact: String = "",
    val active: Boolean = true
)

@Entity(tableName = "staff_members")
data class StaffMember(
    @PrimaryKey val id: String, // STF-2026-001
    val name: String,
    val designation: String, // Accountant, Clerk, Peon, Security, Driver
    val phone: String,
    val cnic: String,
    val basicSalary: Double,
    val joiningDate: String,
    val active: Boolean = true
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: String, // STU-xxx or TCH-xxx or STF-xxx
    val personType: String, // STUDENT, TEACHER, STAFF
    val personName: String,
    val className: String = "",
    val section: String = "",
    val date: String, // YYYY-MM-DD
    val time: String, // HH:MM
    val status: String, // PRESENT, ABSENT, LATE, LEAVE, HALF_DAY
    val method: String = "MANUAL", // QR_SCAN, FACE_SCAN, MANUAL
    val notes: String = ""
)

@Entity(tableName = "fee_structures")
data class FeeStructure(
    @PrimaryKey val className: String,
    val admissionFee: Double = 5000.0,
    val monthlyFee: Double = 3500.0,
    val examFee: Double = 1000.0,
    val transportFee: Double = 1500.0,
    val computerFee: Double = 500.0,
    val libraryFee: Double = 300.0,
    val otherFee: Double = 200.0
)

@Entity(tableName = "fee_payments")
data class FeePayment(
    @PrimaryKey val receiptNo: String, // REC-2026-001
    val studentId: String,
    val studentName: String,
    val className: String,
    val section: String = "A",
    val month: String, // October 2026
    val monthlyFee: Double,
    val transportFee: Double = 0.0,
    val examFee: Double = 0.0,
    val computerFee: Double = 0.0,
    val otherFee: Double = 0.0,
    val discountAmount: Double = 0.0,
    val previousDue: Double = 0.0,
    val totalPayable: Double,
    val paidAmount: Double,
    val remainingBalance: Double,
    val paymentMethod: String = "Cash", // Cash, Bank, JazzCash, EasyPaisa
    val paymentRef: String = "TXN-001",
    val paymentDate: String,
    val cashierName: String = "Accountant Office",
    val notes: String = "Monthly fee settled"
)

@Entity(tableName = "expenses")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val category: String, // Salaries, Electricity, Gas, Rent, Stationery, Maintenance, Transport, Internet, Events, Other
    val amount: Double,
    val description: String,
    val paidBy: String = "Admin Office",
    val paymentMethod: String = "Cash",
    val receiptUrl: String = ""
)

@Entity(tableName = "salary_slips")
data class SalarySlip(
    @PrimaryKey val slipNo: String, // SAL-2026-001
    val employeeId: String,
    val employeeName: String,
    val designation: String,
    val month: String,
    val basicSalary: Double,
    val allowances: Double = 0.0,
    val bonus: Double = 0.0,
    val advance: Double = 0.0,
    val lateDeductions: Double = 0.0,
    val leaveDeductions: Double = 0.0,
    val netSalary: Double,
    val paymentDate: String,
    val paymentMethod: String = "Bank",
    val status: String = "PAID"
)

@Entity(tableName = "classes")
data class ClassSection(
    @PrimaryKey val id: String, // Class 5-A
    val className: String, // Class 5
    val section: String = "A",
    val classTeacherId: String = "",
    val classTeacherName: String = "",
    val roomNumber: String = "Room 102",
    val capacity: Int = 40
)

@Entity(tableName = "subjects")
data class SubjectItem(
    @PrimaryKey val id: String, // SUB-MAT-5
    val name: String,
    val code: String,
    val className: String,
    val totalMarks: Int = 100,
    val passingMarks: Int = 40
)

@Entity(tableName = "timetable_slots")
data class TimetableSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    val periodNo: Int, // 1 to 7
    val startTime: String, // 08:00
    val endTime: String, // 08:45
    val className: String,
    val section: String = "A",
    val subjectName: String,
    val teacherName: String,
    val room: String = "Room 101"
)

@Entity(tableName = "exams")
data class ExamRecord(
    @PrimaryKey val id: String, // EXAM-2026-MID
    val title: String, // Mid Term Examination 2026
    val examType: String, // Mid Term, Final, Monthly Test
    val className: String,
    val startDate: String,
    val endDate: String,
    val status: String = "Scheduled"
)

@Entity(tableName = "exam_results")
data class ExamResultItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: String,
    val examTitle: String,
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val className: String,
    val subjectMarksJson: String, // JSON array of {"subject":"Math","max":100,"obtained":88}
    val totalMarks: Int,
    val obtainedMarks: Int,
    val percentage: Double,
    val grade: String, // A+, A, B, C, D, F
    val position: String = "1st",
    val remarks: String = "Excellent academic performance",
    val principalSigned: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // ATTENDANCE, FEES, ACADEMIC, GENERAL, HOLIDAY
    val targetAudience: String = "ALL", // ALL, STUDENTS, TEACHERS, PARENTS
    val date: String,
    val scheduledFor: String = "",
    val sent: Boolean = true
)

@Entity(tableName = "leave_requests")
data class LeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val applicantId: String,
    val applicantType: String, // STUDENT, TEACHER
    val applicantName: String,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val appliedDate: String
)

@Entity(tableName = "library_books")
data class LibraryBook(
    @PrimaryKey val id: String, // BK-001
    val title: String,
    val author: String,
    val category: String,
    val totalCopies: Int = 5,
    val availableCopies: Int = 4,
    val rackLocation: String = "A-2"
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey val id: String, // INV-001
    val name: String,
    val category: String, // Furniture, Computers, Lab, Sports
    val quantity: Int,
    val status: String = "Available",
    val purchaseDate: String,
    val cost: Double
)

@Entity(tableName = "transport_vehicles")
data class TransportVehicle(
    @PrimaryKey val vehicleNo: String, // BUS-01
    val driverName: String,
    val driverPhone: String,
    val routeName: String,
    val capacity: Int = 30,
    val monthlyFee: Double = 1500.0
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actor: String,
    val action: String,
    val details: String,
    val timestamp: String
)

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val role: String, // SUPER_ADMIN, SCHOOL_ADMIN, PRINCIPAL, TEACHER, ACCOUNTANT, RECEPTIONIST, PARENT
    val linkedId: String = "" // studentId or teacherId
)
