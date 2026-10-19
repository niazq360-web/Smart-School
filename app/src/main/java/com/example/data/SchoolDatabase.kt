package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SchoolProfile::class,
        Student::class,
        Teacher::class,
        StaffMember::class,
        AttendanceRecord::class,
        FeeStructure::class,
        FeePayment::class,
        ExpenseRecord::class,
        SalarySlip::class,
        ClassSection::class,
        SubjectItem::class,
        TimetableSlot::class,
        ExamRecord::class,
        ExamResultItem::class,
        NotificationItem::class,
        LeaveRequest::class,
        LibraryBook::class,
        InventoryItem::class,
        TransportVehicle::class,
        AuditLog::class,
        UserAccount::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: SchoolDatabase? = null

        fun getInstance(context: Context): SchoolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolDatabase::class.java,
                    "smart_school_management.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseSeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseSeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    seedDatabase(database.schoolDao())
                }
            }
        }
    }
}

suspend fun seedDatabase(dao: SchoolDao) {
    // 1. School Profile
    dao.upsertSchoolProfile(
        SchoolProfile(
            id = 1,
            name = "The Citizens Public High School",
            address = "Main Education Boulevard, Block 4, Hyderabad, Sindh",
            phone = "+92 22 2781920",
            whatsapp = "+92 300 1234567",
            email = "info@citizenschool.edu.pk",
            website = "www.citizenschool.edu.pk",
            regNumber = "REG-SD-HYD-2024-8891",
            principalName = "Prof. Ghulam Mustafa Chandio",
            academicYear = "2026-2027",
            schoolStartTime = "08:00",
            schoolEndTime = "13:30",
            gracePeriodMinutes = 10,
            workingDays = "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday",
            currency = "PKR",
            currencySymbol = "Rs.",
            defaultLanguage = "en",
            autoMonthlyFeeEnabled = true
        )
    )

    // 2. Classes & Sections
    val classes = listOf(
        ClassSection("PlayGroup-A", "Play Group", "A", "TCH-006", "Madam Ayesha Noor", "Room 01"),
        ClassSection("Nursery-A", "Nursery", "A", "TCH-006", "Madam Ayesha Noor", "Room 02"),
        ClassSection("KG-A", "KG", "A", "TCH-006", "Madam Ayesha Noor", "Room 03"),
        ClassSection("Class 1-A", "Class 1", "A", "TCH-004", "Madam Farzana", "Room 101"),
        ClassSection("Class 2-A", "Class 2", "A", "TCH-004", "Madam Farzana", "Room 102"),
        ClassSection("Class 3-A", "Class 3", "A", "TCH-002", "Madam Samina Kausar", "Room 103"),
        ClassSection("Class 4-A", "Class 4", "A", "TCH-002", "Madam Samina Kausar", "Room 104"),
        ClassSection("Class 5-A", "Class 5", "A", "TCH-001", "Sir Abdul Rauf", "Room 201"),
        ClassSection("Class 6-A", "Class 6", "A", "TCH-003", "Sir Zulfiqar Ali", "Room 202"),
        ClassSection("Class 7-A", "Class 7", "A", "TCH-005", "Sir Naveed Ahmed", "Room 203"),
        ClassSection("Class 8-A", "Class 8", "A", "TCH-003", "Sir Zulfiqar Ali", "Room 301"),
        ClassSection("Class 9-A", "Class 9", "A", "TCH-001", "Sir Abdul Rauf", "Room 302"),
        ClassSection("Class 10-A", "Class 10", "A", "TCH-001", "Sir Abdul Rauf", "Room 303")
    )
    classes.forEach { dao.insertClass(it) }

    // 3. Subjects
    val subjects = listOf(
        SubjectItem("SUB-ENG-5", "English", "ENG", "Class 5", 100, 40),
        SubjectItem("SUB-URD-5", "Urdu", "URD", "Class 5", 100, 40),
        SubjectItem("SUB-SND-5", "Sindhi", "SND", "Class 5", 100, 40),
        SubjectItem("SUB-MAT-5", "Mathematics", "MAT", "Class 5", 100, 40),
        SubjectItem("SUB-SCI-5", "General Science", "SCI", "Class 5", 100, 40),
        SubjectItem("SUB-COM-5", "Computer Studies", "COM", "Class 5", 50, 20),
        SubjectItem("SUB-ISL-5", "Islamiat", "ISL", "Class 5", 50, 20),
        SubjectItem("SUB-SST-5", "Social Studies", "SST", "Class 5", 50, 20),
        SubjectItem("SUB-ENG-10", "English", "ENG", "Class 10", 100, 40),
        SubjectItem("SUB-URD-10", "Urdu", "URD", "Class 10", 100, 40),
        SubjectItem("SUB-SND-10", "Sindhi", "SND", "Class 10", 100, 40),
        SubjectItem("SUB-MAT-10", "Mathematics", "MAT", "Class 10", 100, 40),
        SubjectItem("SUB-PHY-10", "Physics", "PHY", "Class 10", 100, 40),
        SubjectItem("SUB-CHM-10", "Chemistry", "CHM", "Class 10", 100, 40),
        SubjectItem("SUB-BIO-10", "Biology / CS", "BIO", "Class 10", 100, 40)
    )
    subjects.forEach { dao.insertSubject(it) }

    // 4. Teachers
    val teachers = listOf(
        Teacher(
            id = "TCH-001",
            name = "Sir Abdul Rauf Memon",
            fatherOrHusbandName = "Ghulam Qadir Memon",
            cnic = "41303-1234567-1",
            dob = "1982-04-15",
            gender = "Male",
            phone = "+92 301 2345678",
            whatsappNo = "+92 301 2345678",
            address = "Latifabad No. 7, Hyderabad",
            qualification = "M.Sc Mathematics, B.Ed",
            experienceYears = 14,
            joiningDate = "2015-08-01",
            designation = "Senior Master & Head of Math",
            subjectsAssigned = "Mathematics, Physics",
            classesAssigned = "Class 5, Class 9, Class 10",
            basicSalary = 65000.0
        ),
        Teacher(
            id = "TCH-002",
            name = "Madam Samina Kausar",
            fatherOrHusbandName = "Tariq Mehmood",
            cnic = "41304-7654321-2",
            dob = "1988-11-20",
            gender = "Female",
            phone = "+92 333 9876543",
            whatsappNo = "+92 333 9876543",
            address = "Qasimabad Phase 1, Hyderabad",
            qualification = "M.A English Literature, M.Ed",
            experienceYears = 9,
            joiningDate = "2018-03-15",
            designation = "Senior English Teacher",
            subjectsAssigned = "English, Urdu",
            classesAssigned = "Class 3, Class 4, Class 5",
            basicSalary = 52000.0
        ),
        Teacher(
            id = "TCH-003",
            name = "Sir Zulfiqar Ali Soomro",
            fatherOrHusbandName = "Allah Bux Soomro",
            cnic = "41302-3456789-3",
            dob = "1985-06-10",
            gender = "Male",
            phone = "+92 312 4567890",
            whatsappNo = "+92 312 4567890",
            address = "Citizen Colony, Hyderabad",
            qualification = "M.Sc Chemistry",
            experienceYears = 11,
            joiningDate = "2016-09-01",
            designation = "Science Department Incharge",
            subjectsAssigned = "General Science, Chemistry",
            classesAssigned = "Class 6, Class 8, Class 10",
            basicSalary = 58000.0
        ),
        Teacher(
            id = "TCH-004",
            name = "Madam Farzana Solangi",
            fatherOrHusbandName = "Imtiaz Solangi",
            cnic = "41301-8901234-4",
            dob = "1991-02-28",
            gender = "Female",
            phone = "+92 305 6789012",
            whatsappNo = "+92 305 6789012",
            address = "Gari Khata, Hyderabad",
            qualification = "M.A Sindhi, B.Ed",
            experienceYears = 7,
            joiningDate = "2019-01-10",
            designation = "Sindhi & Social Studies Specialist",
            subjectsAssigned = "Sindhi, Social Studies",
            classesAssigned = "Class 1, Class 2, Class 5",
            basicSalary = 48000.0
        ),
        Teacher(
            id = "TCH-005",
            name = "Sir Naveed Ahmed Khan",
            fatherOrHusbandName = "Saeed Ahmed Khan",
            cnic = "41303-9012345-5",
            dob = "1994-08-14",
            gender = "Male",
            phone = "+92 345 7890123",
            whatsappNo = "+92 345 7890123",
            address = "Wadhu Wah Road, Qasimabad",
            qualification = "BS Computer Science",
            experienceYears = 5,
            joiningDate = "2021-02-01",
            designation = "IT Instructor & Lab Incharge",
            subjectsAssigned = "Computer Studies",
            classesAssigned = "Class 5, Class 7, Class 9, Class 10",
            basicSalary = 45000.0
        ),
        Teacher(
            id = "TCH-006",
            name = "Madam Ayesha Noor",
            fatherOrHusbandName = "Zahid Hussain",
            cnic = "41304-4567890-6",
            dob = "1995-12-05",
            gender = "Female",
            phone = "+92 321 8901234",
            whatsappNo = "+92 321 8901234",
            address = "Defence Colony, Hyderabad",
            qualification = "Montessori Certified, B.A",
            experienceYears = 6,
            joiningDate = "2020-08-15",
            designation = "Kindergarten Coordinator",
            subjectsAssigned = "Early Childhood Education",
            classesAssigned = "Play Group, Nursery, KG",
            basicSalary = 42000.0
        )
    )
    teachers.forEach { dao.insertTeacher(it) }

    // 5. Staff Members
    val staff = listOf(
        StaffMember("STF-001", "Muhammad Aslam", "Accountant & Bursar", "+92 300 9988771", "41303-1122334-1", 45000.0, "2017-04-01"),
        StaffMember("STF-002", "Rashid Mallah", "Transport Driver", "+92 313 8877662", "41301-2233445-3", 30000.0, "2019-06-01"),
        StaffMember("STF-003", "Babu Ram", "Campus Security Incharge", "+92 342 7766553", "41302-3344556-5", 26000.0, "2018-01-15")
    )
    staff.forEach { dao.insertStaff(it) }

    // 6. Fee Structures
    val feeStructures = listOf(
        FeeStructure("Play Group", 5000.0, 3000.0, 1000.0, 1500.0, 300.0, 200.0, 100.0),
        FeeStructure("Nursery", 5000.0, 3000.0, 1000.0, 1500.0, 300.0, 200.0, 100.0),
        FeeStructure("KG", 5000.0, 3200.0, 1000.0, 1500.0, 400.0, 200.0, 100.0),
        FeeStructure("Class 1", 6000.0, 3500.0, 1200.0, 1500.0, 500.0, 300.0, 200.0),
        FeeStructure("Class 5", 6000.0, 4000.0, 1500.0, 1500.0, 600.0, 300.0, 200.0),
        FeeStructure("Class 9", 7000.0, 4800.0, 2000.0, 1500.0, 800.0, 400.0, 300.0),
        FeeStructure("Class 10", 7000.0, 5000.0, 2000.0, 1500.0, 800.0, 400.0, 300.0)
    )
    feeStructures.forEach { dao.insertFeeStructure(it) }

    // 7. Students
    val students = listOf(
        Student(
            id = "STU-2026-001",
            admissionNo = "ADM-1082",
            rollNo = "01",
            name = "Ahmed Ali Chandio",
            fatherName = "Niaz Hussain Chandio",
            motherName = "Zainab Bibi",
            dob = "2015-05-12",
            gender = "Male",
            bForm = "41303-8891023-1",
            address = "Flat B-4, Al-Mustafa Heights, Latifabad 6",
            city = "Hyderabad",
            fatherPhone = "+92 300 2341122",
            motherPhone = "+92 333 4455667",
            whatsappNo = "+92 300 2341122",
            emergencyContact = "+92 300 2341122",
            bloodGroup = "O+",
            previousSchool = "Beacon Public Academy",
            admissionDate = "2021-04-05",
            className = "Class 5",
            section = "A",
            shift = "Morning",
            transportRoute = "Route 1",
            remarks = "Excellent student, Class Monitor",
            active = true
        ),
        Student(
            id = "STU-2026-002",
            admissionNo = "ADM-1083",
            rollNo = "02",
            name = "Fatima Zahra Memon",
            fatherName = "Muhammad Hashim Memon",
            motherName = "Tahira Begum",
            dob = "2015-09-22",
            gender = "Female",
            bForm = "41304-9912034-2",
            address = "House 45, Gulistan-e-Sajjad, Qasimabad",
            city = "Hyderabad",
            fatherPhone = "+92 301 9871234",
            whatsappNo = "+92 301 9871234",
            emergencyContact = "+92 301 9871234",
            bloodGroup = "B+",
            admissionDate = "2021-04-05",
            className = "Class 5",
            section = "A",
            shift = "Morning",
            transportRoute = "Route 2",
            remarks = "Top ranking student in Science",
            active = true
        ),
        Student(
            id = "STU-2026-003",
            admissionNo = "ADM-1084",
            rollNo = "03",
            name = "Bilal Tariq",
            fatherName = "Tariq Mehmood Siddiqui",
            dob = "2015-01-18",
            gender = "Male",
            bForm = "41302-3344112-3",
            address = "Near St. Bonaventure School, Saddar",
            city = "Hyderabad",
            fatherPhone = "+92 312 8877112",
            whatsappNo = "+92 312 8877112",
            emergencyContact = "+92 312 8877112",
            bloodGroup = "A+",
            admissionDate = "2022-03-10",
            className = "Class 5",
            section = "A",
            shift = "Morning",
            transportRoute = "Self",
            remarks = "Good athlete, debates captain",
            active = true
        ),
        Student(
            id = "STU-2026-004",
            admissionNo = "ADM-1085",
            rollNo = "04",
            name = "Dua Shah",
            fatherName = "Syed Mansoor Shah",
            dob = "2015-07-30",
            gender = "Female",
            bForm = "41301-5566778-4",
            address = "Bungalow 12, Citizen Colony",
            city = "Hyderabad",
            fatherPhone = "+92 345 3322119",
            whatsappNo = "+92 345 3322119",
            emergencyContact = "+92 345 3322119",
            bloodGroup = "AB+",
            admissionDate = "2021-04-05",
            className = "Class 5",
            section = "A",
            shift = "Morning",
            transportRoute = "Route 1",
            remarks = "Art and calligraphy prize winner",
            active = true
        ),
        Student(
            id = "STU-2026-005",
            admissionNo = "ADM-1086",
            rollNo = "05",
            name = "Zayan Ali Baloch",
            fatherName = "Mir Nadir Baloch",
            dob = "2015-11-04",
            gender = "Male",
            bForm = "41303-7788990-5",
            address = "Wadhu Wah Road, Hyderabad",
            city = "Hyderabad",
            fatherPhone = "+92 302 5566441",
            whatsappNo = "+92 302 5566441",
            emergencyContact = "+92 302 5566441",
            bloodGroup = "O-",
            admissionDate = "2022-08-01",
            className = "Class 5",
            section = "A",
            shift = "Morning",
            transportRoute = "Route 2",
            remarks = "Active in sports",
            active = true
        ),
        Student(
            id = "STU-2026-006",
            admissionNo = "ADM-0920",
            rollNo = "01",
            name = "Hassan Raza",
            fatherName = "Khadim Hussain",
            dob = "2010-02-14",
            gender = "Male",
            bForm = "41302-1122998-1",
            address = "Unit 8, Latifabad",
            city = "Hyderabad",
            fatherPhone = "+92 300 4455889",
            whatsappNo = "+92 300 4455889",
            emergencyContact = "+92 300 4455889",
            bloodGroup = "B+",
            admissionDate = "2018-04-01",
            className = "Class 10",
            section = "A",
            shift = "Morning",
            transportRoute = "Self",
            remarks = "Science group, preparing for Board Exams",
            active = true
        ),
        Student(
            id = "STU-2026-007",
            admissionNo = "ADM-0921",
            rollNo = "02",
            name = "Areeba Shaikh",
            fatherName = "Waseem Shaikh",
            dob = "2010-06-25",
            gender = "Female",
            bForm = "41303-6655443-2",
            address = "Prince Town, Qasimabad",
            city = "Hyderabad",
            fatherPhone = "+92 333 1122334",
            whatsappNo = "+92 333 1122334",
            emergencyContact = "+92 333 1122334",
            bloodGroup = "A+",
            admissionDate = "2018-04-01",
            className = "Class 10",
            section = "A",
            shift = "Morning",
            transportRoute = "Route 1",
            remarks = "High achiever, 92% in 9th Board",
            active = true
        ),
        Student(
            id = "STU-2026-008",
            admissionNo = "ADM-1201",
            rollNo = "01",
            name = "Muhammad Hamza",
            fatherName = "Muhammad Shahid",
            dob = "2019-08-10",
            gender = "Male",
            bForm = "41304-4433221-1",
            address = "Gidu Chowk, Hyderabad",
            city = "Hyderabad",
            fatherPhone = "+92 315 7766554",
            whatsappNo = "+92 315 7766554",
            emergencyContact = "+92 315 7766554",
            bloodGroup = "O+",
            admissionDate = "2024-04-01",
            className = "Class 1",
            section = "A",
            shift = "Morning",
            transportRoute = "Route 1",
            remarks = "Enthusiastic primary learner",
            active = true
        )
    )
    students.forEach { dao.insertStudent(it) }

    // 8. Timetable
    val timetable = listOf(
        TimetableSlot(dayOfWeek = "Monday", periodNo = 1, startTime = "08:00", endTime = "08:45", className = "Class 5", subjectName = "English", teacherName = "Madam Samina Kausar", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Monday", periodNo = 2, startTime = "08:45", endTime = "09:30", className = "Class 5", subjectName = "Mathematics", teacherName = "Sir Abdul Rauf Memon", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Monday", periodNo = 3, startTime = "09:30", endTime = "10:15", className = "Class 5", subjectName = "General Science", teacherName = "Sir Zulfiqar Ali Soomro", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Monday", periodNo = 4, startTime = "10:45", endTime = "11:30", className = "Class 5", subjectName = "Urdu", teacherName = "Madam Samina Kausar", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Monday", periodNo = 5, startTime = "11:30", endTime = "12:15", className = "Class 5", subjectName = "Sindhi", teacherName = "Madam Farzana Solangi", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Monday", periodNo = 6, startTime = "12:15", endTime = "13:00", className = "Class 5", subjectName = "Computer Studies", teacherName = "Sir Naveed Ahmed Khan", room = "IT Lab 1"),
        TimetableSlot(dayOfWeek = "Tuesday", periodNo = 1, startTime = "08:00", endTime = "08:45", className = "Class 5", subjectName = "Mathematics", teacherName = "Sir Abdul Rauf Memon", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Tuesday", periodNo = 2, startTime = "08:45", endTime = "09:30", className = "Class 5", subjectName = "General Science", teacherName = "Sir Zulfiqar Ali Soomro", room = "Room 201"),
        TimetableSlot(dayOfWeek = "Tuesday", periodNo = 3, startTime = "09:30", endTime = "10:15", className = "Class 5", subjectName = "English", teacherName = "Madam Samina Kausar", room = "Room 201")
    )
    timetable.forEach { dao.insertTimetableSlot(it) }

    // 9. Fee Payments
    val payments = listOf(
        FeePayment(
            receiptNo = "REC-2026-001",
            studentId = "STU-2026-001",
            studentName = "Ahmed Ali Chandio",
            className = "Class 5",
            month = "October 2026",
            monthlyFee = 4000.0,
            transportFee = 1500.0,
            computerFee = 600.0,
            discountAmount = 0.0,
            previousDue = 0.0,
            totalPayable = 6100.0,
            paidAmount = 6100.0,
            remainingBalance = 0.0,
            paymentMethod = "Cash",
            paymentRef = "CASH-781",
            paymentDate = "2026-10-15",
            cashierName = "Muhammad Aslam (Accountant)"
        ),
        FeePayment(
            receiptNo = "REC-2026-002",
            studentId = "STU-2026-002",
            studentName = "Fatima Zahra Memon",
            className = "Class 5",
            month = "October 2026",
            monthlyFee = 4000.0,
            transportFee = 1500.0,
            computerFee = 600.0,
            discountAmount = 500.0,
            previousDue = 0.0,
            totalPayable = 5600.0,
            paidAmount = 5600.0,
            remainingBalance = 0.0,
            paymentMethod = "JazzCash",
            paymentRef = "JC-99281726",
            paymentDate = "2026-10-16",
            cashierName = "Muhammad Aslam (Accountant)"
        ),
        FeePayment(
            receiptNo = "REC-2026-003",
            studentId = "STU-2026-003",
            studentName = "Bilal Tariq",
            className = "Class 5",
            month = "October 2026",
            monthlyFee = 4000.0,
            transportFee = 0.0,
            computerFee = 600.0,
            discountAmount = 0.0,
            previousDue = 2000.0,
            totalPayable = 6600.0,
            paidAmount = 4600.0,
            remainingBalance = 2000.0,
            paymentMethod = "EasyPaisa",
            paymentRef = "EP-44182901",
            paymentDate = "2026-10-18",
            cashierName = "Muhammad Aslam (Accountant)"
        )
    )
    payments.forEach { dao.insertFeePayment(it) }

    // 10. Expenses
    val expenses = listOf(
        ExpenseRecord(date = "2026-10-01", category = "Rent", amount = 120000.0, description = "School Main Campus Building Rent for October 2026"),
        ExpenseRecord(date = "2026-10-05", category = "Electricity", amount = 42500.0, description = "HESCO Electricity Bill"),
        ExpenseRecord(date = "2026-10-07", category = "Stationery", amount = 18400.0, description = "Exam papers, answer booklets, and printer ink"),
        ExpenseRecord(date = "2026-10-10", category = "Internet", amount = 6500.0, description = "PTCL Fiber Internet connection for IT Lab"),
        ExpenseRecord(date = "2026-10-12", category = "Maintenance", amount = 9800.0, description = "Water filter service & electrical repair")
    )
    expenses.forEach { dao.insertExpense(it) }

    // 11. Salary Slips
    val salarySlips = listOf(
        SalarySlip(slipNo = "SAL-2026-001", employeeId = "TCH-001", employeeName = "Sir Abdul Rauf Memon", designation = "Senior Master & Head of Math", month = "September 2026", basicSalary = 65000.0, allowances = 5000.0, bonus = 0.0, lateDeductions = 0.0, leaveDeductions = 0.0, netSalary = 70000.0, paymentDate = "2026-10-01", paymentMethod = "Bank Transfer", status = "PAID"),
        SalarySlip(slipNo = "SAL-2026-002", employeeId = "TCH-002", employeeName = "Madam Samina Kausar", designation = "Senior English Teacher", month = "September 2026", basicSalary = 52000.0, allowances = 3000.0, bonus = 0.0, lateDeductions = 0.0, leaveDeductions = 0.0, netSalary = 55000.0, paymentDate = "2026-10-01", paymentMethod = "Bank Transfer", status = "PAID")
    )
    salarySlips.forEach { dao.insertSalarySlip(it) }

    // 12. Exams & Results
    dao.insertExam(
        ExamRecord(
            id = "EXAM-2026-MID",
            title = "First Term Mid-Year Examination 2026",
            examType = "Mid Term",
            className = "Class 5",
            startDate = "2026-11-05",
            endDate = "2026-11-15",
            status = "Scheduled"
        )
    )

    dao.insertExamResult(
        ExamResultItem(
            examId = "EXAM-2026-MID",
            examTitle = "Monthly Assessment - September 2026",
            studentId = "STU-2026-001",
            studentName = "Ahmed Ali Chandio",
            rollNo = "01",
            className = "Class 5",
            subjectMarksJson = """[{"subject":"English","max":100,"obtained":92},{"subject":"Urdu","max":100,"obtained":88},{"subject":"Sindhi","max":100,"obtained":90},{"subject":"Mathematics","max":100,"obtained":96},{"subject":"General Science","max":100,"obtained":94}]""",
            totalMarks = 500,
            obtainedMarks = 460,
            percentage = 92.0,
            grade = "A+",
            position = "1st",
            remarks = "Outstanding performance in all academic subjects"
        )
    )

    // 13. Notifications
    val notifications = listOf(
        NotificationItem(
            title = "Mid-Term Examination Schedule Announced",
            message = "Mid-term examinations will commence from 5th November 2026. Date sheets are available at the front desk.",
            category = "ACADEMIC",
            targetAudience = "ALL",
            date = "2026-10-18"
        ),
        NotificationItem(
            title = "Monthly Fee Reminder - October 2026",
            message = "Parents are requested to clear October fees by 25th October to avoid late fee surcharge.",
            category = "FEES",
            targetAudience = "PARENTS",
            date = "2026-10-17"
        ),
        NotificationItem(
            title = "Sindhi Culture & Heritage Day Celebration",
            message = "Cultural day will be observed next Friday. Traditional attire is welcomed.",
            category = "GENERAL",
            targetAudience = "ALL",
            date = "2026-10-16"
        )
    )
    notifications.forEach { dao.insertNotification(it) }

    // 14. Library & Transport & Inventory
    val books = listOf(
        LibraryBook("BK-001", "Stories from Sindh History", "Dr. N.A. Baloch", "History", 5, 4, "Rack A-1"),
        LibraryBook("BK-002", "Oxford Modern English Course 5", "Nicholas Horsburgh", "Curriculum", 25, 22, "Rack B-3"),
        LibraryBook("BK-003", "Exploring Pakistan Geography", "Prof. M. Sharif", "Social Studies", 8, 7, "Rack C-2"),
        LibraryBook("BK-004", "Great Islamic Scientists & Inventors", "Ziauddin Sardar", "Biography", 10, 9, "Rack A-3")
    )
    books.forEach { dao.insertBook(it) }

    val vehicles = listOf(
        TransportVehicle("BUS-01", "Rashid Mallah", "+92 313 8877662", "Route 1: Latifabad Unit 1-12 to Campus", 32, 1500.0),
        TransportVehicle("VAN-02", "Khurram Shah", "+92 321 4455661", "Route 2: Qasimabad to Campus", 18, 1600.0)
    )
    vehicles.forEach { dao.insertVehicle(it) }

    val inventory = listOf(
        InventoryItem("INV-001", "Dell Core i5 Desktops (Lab 1)", "Computers", 25, "Available", "2024-03-10", 875000.0),
        InventoryItem("INV-002", "Classroom Student Wooden Benches", "Furniture", 150, "Available", "2023-08-01", 600000.0),
        InventoryItem("INV-003", "HP Laser Multi-function Printer", "Equipment", 3, "Available", "2024-01-15", 135000.0),
        InventoryItem("INV-004", "Science Lab Compound Microscopes", "Lab", 12, "Available", "2024-05-20", 180000.0)
    )
    inventory.forEach { dao.insertInventory(it) }

    // 15. User Accounts
    val users = listOf(
        UserAccount("USR-001", "admin", "Principal Office", "SUPER_ADMIN"),
        UserAccount("USR-002", "principal", "Prof. Ghulam Mustafa", "PRINCIPAL"),
        UserAccount("USR-003", "teacher1", "Sir Abdul Rauf", "TEACHER", "TCH-001"),
        UserAccount("USR-004", "accountant", "Muhammad Aslam", "ACCOUNTANT"),
        UserAccount("USR-005", "receptionist", "Fatima Reception", "RECEPTIONIST"),
        UserAccount("USR-006", "parent", "Niaz Hussain Chandio", "PARENT", "STU-2026-001")
    )
    users.forEach { dao.insertUser(it) }

    // 16. Audit Log
    dao.insertAuditLog(
        AuditLog(
            actor = "System",
            action = "INITIAL_SEED",
            details = "Initialized Smart School database with curriculum, classes, teachers, and records.",
            timestamp = "2026-10-19 08:00:00"
        )
    )
}
