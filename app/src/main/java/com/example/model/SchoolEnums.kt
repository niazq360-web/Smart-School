package com.example.model

enum class UserRole(val displayName: String) {
    SUPER_ADMIN("Super Admin"),
    SCHOOL_ADMIN("School Admin"),
    PRINCIPAL("Principal"),
    TEACHER("Teacher"),
    ACCOUNTANT("Accountant"),
    RECEPTIONIST("Receptionist"),
    PARENT("Parent")
}

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    LEAVE("Leave"),
    HALF_DAY("Half Day")
}

enum class AttendanceMethod {
    QR_SCAN,
    FACE_SCAN,
    MANUAL
}

enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    BANK("Bank Transfer"),
    JAZZCASH("JazzCash"),
    EASYPAISA("EasyPaisa"),
    ONLINE("Online Portal")
}

enum class ExpenseCategory(val label: String) {
    SALARIES("Salaries"),
    ELECTRICITY("Electricity"),
    GAS("Gas"),
    WATER("Water"),
    RENT("Rent"),
    STATIONERY("Stationery"),
    MAINTENANCE("Maintenance"),
    TRANSPORT("Transport"),
    INTERNET("Internet"),
    SOFTWARE("Software"),
    EVENTS("Events"),
    OTHER("Other")
}

enum class ExamType(val label: String) {
    MID_TERM("Mid Term"),
    FINAL("Final Examination"),
    MONTHLY_TEST("Monthly Test"),
    WEEKLY_TEST("Weekly Test")
}

enum class LeaveStatus(val label: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected")
}

enum class InventoryStatus(val label: String) {
    AVAILABLE("Available"),
    PURCHASED("Purchased"),
    DAMAGED("Damaged"),
    MAINTENANCE("In Maintenance")
}

enum class DifficultyLevel(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}
