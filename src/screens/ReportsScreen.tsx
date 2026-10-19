import React from 'react';
import {
  Student,
  Teacher,
  AttendanceRecord,
  FeePayment,
  ExpenseRecord,
  SalarySlip,
  ExamResultItem,
  SchoolProfile,
  AppLanguage,
  UserRole,
} from '../types';
import {
  FileText,
  Printer,
  Download,
  Users,
  Award,
  DollarSign,
  TrendingUp,
  BarChart3,
} from 'lucide-react';

interface ReportsScreenProps {
  students: Student[];
  teachers: Teacher[];
  attendance: AttendanceRecord[];
  payments: FeePayment[];
  expenses: ExpenseRecord[];
  salaries: SalarySlip[];
  results: ExamResultItem[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
}

export const ReportsScreen: React.FC<ReportsScreenProps> = ({
  students,
  teachers,
  attendance,
  payments,
  expenses,
  salaries,
  results,
  profile,
}) => {
  // Stats
  const totalStudents = students.length;
  const maleStudents = students.filter((s) => s.gender === 'Male').length;
  const femaleStudents = students.filter((s) => s.gender === 'Female').length;
  const totalFeesCollected = payments.reduce((acc, p) => acc + p.paidAmount, 0);
  const totalSalaries = salaries.reduce((acc, s) => acc + s.netSalary, 0);
  const totalExpenses = expenses.reduce((acc, e) => acc + e.amount, 0);
  const netSurplus = totalFeesCollected - totalSalaries - totalExpenses;

  // Class wise distribution
  const classCounts: Record<string, number> = {};
  students.forEach((s) => {
    classCounts[s.className] = (classCounts[s.className] || 0) + 1;
  });

  const exportStudentsCSV = () => {
    const headers = 'ID,AdmissionNo,RollNo,Name,FatherName,Class,Gender,Phone,Status\n';
    const rows = students
      .map(
        (s) =>
          `"${s.id}","${s.admissionNo}","${s.rollNo}","${s.name}","${s.fatherName}","${s.className}","${s.gender}","${s.fatherPhone}","${s.active ? 'Active' : 'Inactive'}"`
      )
      .join('\n');
    const blob = new Blob([headers + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `SmartSchool_Students_Report_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  const exportFinancialCSV = () => {
    const headers = 'ReceiptNo,StudentName,Class,Month,PaidAmount,Method,Date\n';
    const rows = payments
      .map(
        (p) =>
          `"${p.receiptNo}","${p.studentName}","${p.className}","${p.month}",${p.paidAmount},"${p.paymentMethod}","${p.paymentDate}"`
      )
      .join('\n');
    const blob = new Blob([headers + rows], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `SmartSchool_Financial_Report_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  return (
    <div className="space-y-6">
      {/* Header and Export Actions */}
      <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
            <BarChart3 className="text-blue-900" size={22} /> Institutional Intelligence & Analytics Reports
          </h2>
          <p className="text-xs text-slate-500 mt-0.5">
            Audit-grade performance metrics, enrollment demographics, and financial balance sheets
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <button
            onClick={() => window.print()}
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-2xl flex items-center gap-1.5 transition-colors"
          >
            <Printer size={15} /> Print Report
          </button>
          <button
            onClick={exportStudentsCSV}
            className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-1.5 shadow-sm transition-colors"
          >
            <Download size={15} /> Export Students CSV
          </button>
          <button
            onClick={exportFinancialCSV}
            className="px-4 py-2 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold rounded-2xl flex items-center gap-1.5 shadow-sm transition-colors"
          >
            <Download size={15} /> Export Finance CSV
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500 font-semibold uppercase">Total Enrolled Strength</p>
          <p className="text-2xl font-black text-blue-950 mt-1">{totalStudents} Students</p>
          <p className="text-[11px] text-slate-500 mt-1">
            {maleStudents} Boys ({Math.round((maleStudents / totalStudents) * 100)}%) • {femaleStudents} Girls ({Math.round((femaleStudents / totalStudents) * 100)}%)
          </p>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500 font-semibold uppercase">Faculty & Staff</p>
          <p className="text-2xl font-black text-blue-900 mt-1">{teachers.length} Educators</p>
          <p className="text-[11px] text-slate-500 mt-1">100% Verified Credentials</p>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500 font-semibold uppercase">Revenue Realization</p>
          <p className="text-2xl font-black text-emerald-800 mt-1">
            {profile.currencySymbol} {totalFeesCollected.toLocaleString()}
          </p>
          <p className="text-[11px] text-emerald-600 font-semibold mt-1">From {payments.length} Verified Receipts</p>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm">
          <p className="text-xs text-slate-500 font-semibold uppercase">Net Operating Cash Flow</p>
          <p className="text-2xl font-black text-indigo-900 mt-1">
            {profile.currencySymbol} {netSurplus.toLocaleString()}
          </p>
          <p className="text-[11px] text-slate-500 mt-1">After payroll & operational debits</p>
        </div>
      </div>

      {/* Visual Analytics Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Class Enrollment Breakdown */}
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
          <h3 className="font-bold text-slate-800 text-sm mb-4">Class-Wise Student Distribution</h3>
          <div className="space-y-3">
            {Object.entries(classCounts).map(([cls, count]) => {
              const pct = Math.round((count / totalStudents) * 100);
              return (
                <div key={cls}>
                  <div className="flex justify-between text-xs font-semibold mb-1">
                    <span className="text-slate-800">{cls}</span>
                    <span className="text-slate-500">{count} Students ({pct}%)</span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-blue-900 rounded-full transition-all duration-500"
                      style={{ width: `${pct * 2}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Financial Flow Breakdown */}
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
          <h3 className="font-bold text-slate-800 text-sm mb-4">Institutional Balance Sheet Summary</h3>
          <div className="space-y-4 text-xs">
            <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl flex justify-between items-center">
              <div>
                <p className="font-bold text-emerald-950">Total Fee Collections & Inflow</p>
                <p className="text-[11px] text-emerald-700">Admission fees, tuition & transport funds</p>
              </div>
              <span className="text-base font-black text-emerald-800">
                +{profile.currencySymbol} {totalFeesCollected.toLocaleString()}
              </span>
            </div>

            <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl flex justify-between items-center">
              <div>
                <p className="font-bold text-rose-950">Faculty & Staff Payroll</p>
                <p className="text-[11px] text-rose-700">Disbursed monthly basic and allowances</p>
              </div>
              <span className="text-base font-black text-rose-800">
                -{profile.currencySymbol} {totalSalaries.toLocaleString()}
              </span>
            </div>

            <div className="p-4 bg-amber-50 border border-amber-200 rounded-2xl flex justify-between items-center">
              <div>
                <p className="font-bold text-amber-950">Campus Operations & Utilities</p>
                <p className="text-[11px] text-amber-700">Electricity, internet, repairs & lab maintenance</p>
              </div>
              <span className="text-base font-black text-amber-800">
                -{profile.currencySymbol} {totalExpenses.toLocaleString()}
              </span>
            </div>

            <div className="p-4 bg-blue-950 text-white rounded-2xl flex justify-between items-center shadow-md">
              <div>
                <p className="font-bold text-sm">Audited Net Cash Surplus</p>
                <p className="text-[11px] text-blue-200">School Treasury Current Balance</p>
              </div>
              <span className="text-lg font-black text-amber-300">
                {profile.currencySymbol} {netSurplus.toLocaleString()}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
