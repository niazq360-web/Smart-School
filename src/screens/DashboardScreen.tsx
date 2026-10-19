import React from 'react';
import { SchoolProfile, Student, Teacher, AttendanceRecord, FeePayment, ExpenseRecord, NotificationItem, AppLanguage, UserRole } from '../types';
import { t } from '../localization/translations';
import {
  Users,
  GraduationCap,
  CheckCircle2,
  XCircle,
  Clock,
  CreditCard,
  Receipt,
  TrendingDown,
  TrendingUp,
  Sparkles,
  QrCode,
  PlusCircle,
  Bell,
} from 'lucide-react';

interface DashboardProps {
  profile: SchoolProfile;
  students: Student[];
  teachers: Teacher[];
  attendance: AttendanceRecord[];
  payments: FeePayment[];
  expenses: ExpenseRecord[];
  notifications: NotificationItem[];
  lang: AppLanguage;
  role: UserRole;
  onNavigate: (screen: string) => void;
}

export const DashboardScreen: React.FC<DashboardProps> = ({
  profile,
  students,
  teachers,
  attendance,
  payments,
  expenses,
  notifications,
  lang,
  role,
  onNavigate,
}) => {
  const activeStudents = students.filter((s) => s.active).length;
  const presentStudents = attendance.filter((a) => a.personType === 'STUDENT' && a.status === 'PRESENT').length;
  const absentStudents = attendance.filter((a) => a.personType === 'STUDENT' && a.status === 'ABSENT').length;
  const lateStudents = attendance.filter((a) => a.personType === 'STUDENT' && a.status === 'LATE').length;

  const totalFeeCollected = payments.reduce((acc, p) => acc + p.paidAmount, 0);
  const totalPendingFees = payments.reduce((acc, p) => acc + p.remainingBalance, 0);
  const totalExpensesSum = expenses.reduce((acc, e) => acc + e.amount, 0);
  const netBalance = totalFeeCollected - totalExpensesSum;

  const totalAttendanceRecorded = Math.max(1, presentStudents + absentStudents + lateStudents);
  const presentPct = Math.round((presentStudents / totalAttendanceRecorded) * 100);
  const latePct = Math.round((lateStudents / totalAttendanceRecorded) * 100);
  const absentPct = Math.round((absentStudents / totalAttendanceRecorded) * 100);

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-900 to-indigo-900 rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <span className="px-3 py-1 bg-white/20 backdrop-blur-md rounded-full text-xs font-semibold tracking-wide uppercase">
            {profile.academicYear} • Role: {role}
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold mt-2 tracking-tight">{profile.name}</h1>
          <p className="text-blue-200 text-sm mt-1">{profile.address}</p>
        </div>
        <button
          onClick={() => onNavigate('ai_tools')}
          className="px-5 py-2.5 bg-amber-400 hover:bg-amber-300 text-blue-950 font-bold rounded-2xl flex items-center gap-2 shadow-lg transition-transform active:scale-95"
        >
          <Sparkles size={18} />
          <span>Dost School AI</span>
        </button>
      </div>

      {/* Quick Action Buttons */}
      <div>
        <h3 className="text-sm font-bold uppercase tracking-wider text-slate-500 mb-3">{t('quick_actions', lang)}</h3>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <button
            onClick={() => onNavigate('students')}
            className="p-4 bg-white rounded-2xl border border-slate-200 hover:border-blue-500 hover:shadow-md transition-all text-left flex items-center gap-3"
          >
            <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-900 flex items-center justify-center">
              <PlusCircle size={20} />
            </div>
            <div>
              <p className="text-xs text-slate-500">Manage</p>
              <p className="text-sm font-bold text-slate-800">{t('students', lang)}</p>
            </div>
          </button>

          <button
            onClick={() => onNavigate('attendance')}
            className="p-4 bg-white rounded-2xl border border-slate-200 hover:border-emerald-500 hover:shadow-md transition-all text-left flex items-center gap-3"
          >
            <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center">
              <QrCode size={20} />
            </div>
            <div>
              <p className="text-xs text-slate-500">Scan</p>
              <p className="text-sm font-bold text-slate-800">{t('attendance', lang)}</p>
            </div>
          </button>

          <button
            onClick={() => onNavigate('fees')}
            className="p-4 bg-white rounded-2xl border border-slate-200 hover:border-amber-500 hover:shadow-md transition-all text-left flex items-center gap-3"
          >
            <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-700 flex items-center justify-center">
              <CreditCard size={20} />
            </div>
            <div>
              <p className="text-xs text-slate-500">Collect</p>
              <p className="text-sm font-bold text-slate-800">{t('fees', lang)}</p>
            </div>
          </button>

          <button
            onClick={() => onNavigate('ai_tools')}
            className="p-4 bg-white rounded-2xl border border-slate-200 hover:border-purple-500 hover:shadow-md transition-all text-left flex items-center gap-3"
          >
            <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-700 flex items-center justify-center">
              <Sparkles size={20} />
            </div>
            <div>
              <p className="text-xs text-slate-500">Generate</p>
              <p className="text-sm font-bold text-slate-800">{t('ai_tools', lang)}</p>
            </div>
          </button>
        </div>
      </div>

      {/* Core Summary Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500">{t('total_students', lang)}</p>
            <h4 className="text-2xl font-black text-slate-800 mt-1">{students.length}</h4>
            <span className="text-[11px] text-emerald-600 font-bold">{activeStudents} Enrolled Active</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-blue-50 text-blue-900 flex items-center justify-center">
            <Users size={24} />
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500">{t('total_teachers', lang)}</p>
            <h4 className="text-2xl font-black text-slate-800 mt-1">{teachers.length}</h4>
            <span className="text-[11px] text-purple-600 font-bold">Faculty Members</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-purple-50 text-purple-900 flex items-center justify-center">
            <GraduationCap size={24} />
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500">{t('fee_collection', lang)}</p>
            <h4 className="text-2xl font-black text-emerald-700 mt-1">
              {profile.currencySymbol} {totalFeeCollected.toLocaleString()}
            </h4>
            <span className="text-[11px] text-slate-500">Collected to Date</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-700 flex items-center justify-center">
            <TrendingUp size={24} />
          </div>
        </div>

        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500">{t('pending_fees', lang)}</p>
            <h4 className="text-2xl font-black text-rose-600 mt-1">
              {profile.currencySymbol} {totalPendingFees.toLocaleString()}
            </h4>
            <span className="text-[11px] text-rose-500 font-medium">Unpaid Balances</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center">
            <Receipt size={24} />
          </div>
        </div>
      </div>

      {/* Attendance & Finance Ratio Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Attendance Ratio Bar */}
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-bold text-slate-800 text-base">Today's Campus Attendance Ratio</h3>
            <span className="text-xs font-semibold text-slate-500">{new Date().toLocaleDateString()}</span>
          </div>

          <div className="h-4 w-full bg-slate-100 rounded-full overflow-hidden flex">
            <div style={{ width: `${presentPct}%` }} className="bg-emerald-500 h-full transition-all" title="Present" />
            <div style={{ width: `${latePct}%` }} className="bg-amber-400 h-full transition-all" title="Late" />
            <div style={{ width: `${absentPct}%` }} className="bg-rose-500 h-full transition-all" title="Absent" />
          </div>

          <div className="grid grid-cols-3 gap-2 mt-4 text-center">
            <div className="p-2.5 rounded-xl bg-emerald-50 border border-emerald-100">
              <div className="flex items-center justify-center gap-1.5 text-emerald-700">
                <CheckCircle2 size={16} />
                <span className="text-xs font-bold">Present ({presentStudents})</span>
              </div>
              <p className="text-xs text-emerald-800 font-black mt-0.5">{presentPct}%</p>
            </div>

            <div className="p-2.5 rounded-xl bg-amber-50 border border-amber-100">
              <div className="flex items-center justify-center gap-1.5 text-amber-700">
                <Clock size={16} />
                <span className="text-xs font-bold">Late ({lateStudents})</span>
              </div>
              <p className="text-xs text-amber-800 font-black mt-0.5">{latePct}%</p>
            </div>

            <div className="p-2.5 rounded-xl bg-rose-50 border border-rose-100">
              <div className="flex items-center justify-center gap-1.5 text-rose-700">
                <XCircle size={16} />
                <span className="text-xs font-bold">Absent ({absentStudents})</span>
              </div>
              <p className="text-xs text-rose-800 font-black mt-0.5">{absentPct}%</p>
            </div>
          </div>
        </div>

        {/* Financial Balance Overview */}
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex justify-between items-center mb-3">
              <h3 className="font-bold text-slate-800 text-base">{t('net_balance', lang)}</h3>
              <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${netBalance >= 0 ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'}`}>
                {netBalance >= 0 ? 'Surplus Reserve' : 'Deficit Warning'}
              </span>
            </div>
            <div className="flex items-baseline gap-2">
              <span className="text-3xl font-black text-slate-900">
                {profile.currencySymbol} {netBalance.toLocaleString()}
              </span>
            </div>
            <p className="text-xs text-slate-500 mt-1">
              Calculated as Total Collections ({profile.currencySymbol} {totalFeeCollected.toLocaleString()}) minus Total Expenditures ({profile.currencySymbol} {totalExpensesSum.toLocaleString()}).
            </p>
          </div>

          <div className="pt-4 border-t border-slate-100 flex justify-between text-xs text-slate-600 mt-4">
            <div>
              <p className="text-slate-400">Total Inflow</p>
              <p className="font-bold text-emerald-700">{profile.currencySymbol} {totalFeeCollected.toLocaleString()}</p>
            </div>
            <div>
              <p className="text-slate-400">Total Outflow</p>
              <p className="font-bold text-rose-600">{profile.currencySymbol} {totalExpensesSum.toLocaleString()}</p>
            </div>
            <button onClick={() => onNavigate('finance')} className="text-blue-900 font-bold hover:underline self-end">
              View Ledger →
            </button>
          </div>
        </div>
      </div>

      {/* Announcements */}
      <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
        <div className="flex justify-between items-center mb-4">
          <div className="flex items-center gap-2">
            <Bell size={20} className="text-blue-900" />
            <h3 className="font-bold text-slate-800">School Notices & Announcements</h3>
          </div>
        </div>
        <div className="space-y-3">
          {notifications.map((n) => (
            <div key={n.id} className="p-3.5 rounded-2xl bg-slate-50 border border-slate-100 flex flex-col sm:flex-row justify-between sm:items-center gap-2">
              <div>
                <p className="font-bold text-sm text-slate-900">{n.title}</p>
                <p className="text-xs text-slate-600 mt-0.5">{n.message}</p>
              </div>
              <span className="text-[11px] font-semibold text-slate-400 shrink-0">{n.date}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
