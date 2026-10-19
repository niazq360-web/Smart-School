export type AppLanguage = 'en' | 'ur' | 'sd';

export type UserRole =
  | 'Super Admin'
  | 'School Admin'
  | 'Principal'
  | 'Teacher'
  | 'Accountant'
  | 'Receptionist'
  | 'Parent';

export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'LEAVE' | 'HALF_DAY';

export interface SchoolProfile {
  name: string;
  address: string;
  phone: string;
  whatsapp: string;
  email: string;
  website: string;
  regNumber: string;
  principalName: string;
  academicYear: string;
  schoolStartTime: string;
  schoolEndTime: string;
  gracePeriodMinutes: number;
  currency: string;
  currencySymbol: string;
}

export interface Student {
  id: string; // STU-2026-001
  admissionNo: string;
  rollNo: string;
  name: string;
  fatherName: string;
  motherName?: string;
  dob: string;
  gender: string;
  bForm: string;
  address: string;
  city: string;
  fatherPhone: string;
  whatsappNo: string;
  emergencyContact: string;
  bloodGroup: string;
  className: string;
  section: string;
  shift: string;
  transportRoute: string;
  admissionDate: string;
  remarks: string;
  active: boolean;
}

export interface Teacher {
  id: string; // TCH-001
  name: string;
  fatherOrHusbandName: string;
  cnic: string;
  dob: string;
  gender: string;
  phone: string;
  whatsappNo: string;
  address: string;
  qualification: string;
  experienceYears: number;
  joiningDate: string;
  designation: string;
  subjectsAssigned: string;
  classesAssigned: string;
  basicSalary: number;
  bankDetails: string;
  active: boolean;
}

export interface StaffMember {
  id: string;
  name: string;
  designation: string;
  phone: string;
  cnic: string;
  basicSalary: number;
  joiningDate: string;
  active: boolean;
}

export interface AttendanceRecord {
  id: number;
  personId: string;
  personType: 'STUDENT' | 'TEACHER' | 'STAFF';
  personName: string;
  className: string;
  section: string;
  date: string;
  time: string;
  status: AttendanceStatus;
  method: 'QR_SCAN' | 'FACE_SCAN' | 'MANUAL';
  notes?: string;
}

export interface FeeStructure {
  className: string;
  admissionFee: number;
  monthlyFee: number;
  examFee: number;
  transportFee: number;
  computerFee: number;
  libraryFee: number;
  otherFee: number;
}

export interface FeePayment {
  receiptNo: string;
  studentId: string;
  studentName: string;
  className: string;
  section: string;
  month: string;
  monthlyFee: number;
  transportFee: number;
  computerFee: number;
  discountAmount: number;
  previousDue: number;
  totalPayable: number;
  paidAmount: number;
  remainingBalance: number;
  paymentMethod: string;
  paymentRef: string;
  paymentDate: string;
  cashierName: string;
}

export interface ExpenseRecord {
  id: number;
  date: string;
  category: string;
  amount: number;
  description: string;
  paidBy: string;
  paymentMethod: string;
}

export interface SalarySlip {
  slipNo: string;
  employeeId: string;
  employeeName: string;
  designation: string;
  month: string;
  basicSalary: number;
  allowances: number;
  lateDeductions: number;
  netSalary: number;
  paymentDate: string;
  paymentMethod: string;
  status: string;
}

export interface TimetableSlot {
  id: number;
  dayOfWeek: string;
  periodNo: number;
  startTime: string;
  endTime: string;
  className: string;
  section: string;
  subjectName: string;
  teacherName: string;
  room: string;
}

export interface ExamRecord {
  id: string;
  title: string;
  examType: string;
  className: string;
  startDate: string;
  endDate: string;
  status: string;
}

export interface ExamResultItem {
  id: number;
  examId: string;
  examTitle: string;
  studentId: string;
  studentName: string;
  rollNo: string;
  className: string;
  totalMarks: number;
  obtainedMarks: number;
  percentage: number;
  grade: string;
  position: string;
  remarks: string;
}

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  category: string;
  targetAudience: string;
  date: string;
}

export interface LeaveRequest {
  id: number;
  applicantId: string;
  applicantType: string;
  applicantName: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  appliedDate: string;
}

export interface LibraryBook {
  id: string;
  title: string;
  author: string;
  category: string;
  totalCopies: number;
  availableCopies: number;
  rackLocation: string;
}

export interface TransportVehicle {
  vehicleNo: string;
  driverName: string;
  driverPhone: string;
  routeName: string;
  capacity: number;
  monthlyFee: number;
}

export interface InventoryItem {
  id: string;
  name: string;
  category: string;
  quantity: number;
  status: string;
  purchaseDate: string;
  cost: number;
}

export interface AuditLog {
  id: number;
  actor: string;
  action: string;
  details: string;
  timestamp: string;
}
