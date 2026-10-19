import React, { useState, useEffect } from 'react';
import {
  AppLanguage,
  UserRole,
  SchoolProfile,
  Student,
  Teacher,
  StaffMember,
  AttendanceRecord,
  FeePayment,
  ExpenseRecord,
  SalarySlip,
  TimetableSlot,
  ExamRecord,
  ExamResultItem,
  NotificationItem,
  LeaveRequest,
  LibraryBook,
  TransportVehicle,
  InventoryItem,
  AuditLog,
} from './types';
import {
  initialProfile,
  initialStudents,
  initialTeachers,
  initialStaff,
  initialAttendance,
  initialFeeStructures,
  initialPayments,
  initialExpenses,
  initialSalarySlips,
  initialTimetable,
  initialExams,
  initialResults,
  initialNotifications,
  initialLeaves,
  initialBooks,
  initialVehicles,
  initialInventory,
  initialAuditLogs,
} from './data/initialData';
import { t } from './localization/translations';

// Screens
import { DashboardScreen } from './screens/DashboardScreen';
import { StudentsScreen } from './screens/StudentsScreen';
import { TeachersScreen } from './screens/TeachersScreen';
import { AttendanceScreen } from './screens/AttendanceScreen';
import { FinanceScreen } from './screens/FinanceScreen';
import { ExamsScreen } from './screens/ExamsScreen';
import { TimetableScreen } from './screens/TimetableScreen';
import { AIToolsScreen } from './screens/AIToolsScreen';
import { AdministrationScreen } from './screens/AdministrationScreen';
import { ReportsScreen } from './screens/ReportsScreen';
import { SettingsScreen } from './screens/SettingsScreen';

import {
  LayoutDashboard,
  Users,
  GraduationCap,
  CalendarCheck,
  CreditCard,
  Award,
  Clock,
  Sparkles,
  Building2,
  BarChart3,
  Settings,
  Globe,
  Bell,
  Menu,
  X,
  Shield,
  ChevronDown,
} from 'lucide-react';

export const App: React.FC = () => {
  // Navigation State synced with Hash
  const getInitialScreen = () => {
    const hash = window.location.hash.replace('#/', '');
    const valid = [
      'dashboard',
      'students',
      'teachers',
      'attendance',
      'finance',
      'exams',
      'timetable',
      'ai_tools',
      'administration',
      'reports',
      'settings',
    ];
    return valid.includes(hash) ? hash : 'dashboard';
  };

  const [activeScreen, setActiveScreen] = useState<string>(getInitialScreen());
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  // Internationalization & User Role
  const [lang, setLang] = useState<AppLanguage>(() => {
    return (localStorage.getItem('smart_school_lang') as AppLanguage) || 'en';
  });
  const [role, setRole] = useState<UserRole>('School Admin');

  // Core Persistent State
  const [profile, setProfile] = useState<SchoolProfile>(() => {
    const saved = localStorage.getItem('smart_school_profile');
    return saved ? JSON.parse(saved) : initialProfile;
  });

  const [students, setStudents] = useState<Student[]>(() => {
    const saved = localStorage.getItem('smart_school_students');
    return saved ? JSON.parse(saved) : initialStudents;
  });

  const [teachers, setTeachers] = useState<Teacher[]>(() => {
    const saved = localStorage.getItem('smart_school_teachers');
    return saved ? JSON.parse(saved) : initialTeachers;
  });

  const [staff, setStaff] = useState<StaffMember[]>(initialStaff);

  const [attendance, setAttendance] = useState<AttendanceRecord[]>(() => {
    const saved = localStorage.getItem('smart_school_attendance');
    return saved ? JSON.parse(saved) : initialAttendance;
  });

  const [payments, setPayments] = useState<FeePayment[]>(() => {
    const saved = localStorage.getItem('smart_school_payments');
    return saved ? JSON.parse(saved) : initialPayments;
  });

  const [expenses, setExpenses] = useState<ExpenseRecord[]>(() => {
    const saved = localStorage.getItem('smart_school_expenses');
    return saved ? JSON.parse(saved) : initialExpenses;
  });

  const [salaries, setSalaries] = useState<SalarySlip[]>(initialSalarySlips);
  const [timetable, setTimetable] = useState<TimetableSlot[]>(initialTimetable);
  const [exams, setExams] = useState<ExamRecord[]>(initialExams);
  const [results, setResults] = useState<ExamResultItem[]>(initialResults);
  const [notifications] = useState<NotificationItem[]>(initialNotifications);
  const [leaves, setLeaves] = useState<LeaveRequest[]>(initialLeaves);
  const [libraryBooks, setLibraryBooks] = useState<LibraryBook[]>(initialBooks);
  const [transportVehicles, setTransportVehicles] = useState<TransportVehicle[]>(initialVehicles);
  const [inventory, setInventory] = useState<InventoryItem[]>(initialInventory);
  const [auditLogs, setAuditLogs] = useState<AuditLog[]>(initialAuditLogs);

  // Sync hash routing
  useEffect(() => {
    const handleHashChange = () => {
      const hash = window.location.hash.replace('#/', '');
      if (hash && hash !== activeScreen) {
        setActiveScreen(hash);
      }
    };
    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, [activeScreen]);

  const navigateTo = (screen: string) => {
    setActiveScreen(screen);
    window.location.hash = `#/${screen}`;
    setMobileMenuOpen(false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Sync Language and Direction
  useEffect(() => {
    localStorage.setItem('smart_school_lang', lang);
    if (lang === 'ur' || lang === 'sd') {
      document.documentElement.setAttribute('dir', 'rtl');
      document.documentElement.lang = lang;
    } else {
      document.documentElement.setAttribute('dir', 'ltr');
      document.documentElement.lang = 'en';
    }
  }, [lang]);

  // Persist to localStorage
  useEffect(() => {
    localStorage.setItem('smart_school_profile', JSON.stringify(profile));
  }, [profile]);

  useEffect(() => {
    localStorage.setItem('smart_school_students', JSON.stringify(students));
  }, [students]);

  useEffect(() => {
    localStorage.setItem('smart_school_attendance', JSON.stringify(attendance));
  }, [attendance]);

  useEffect(() => {
    localStorage.setItem('smart_school_payments', JSON.stringify(payments));
  }, [payments]);

  useEffect(() => {
    localStorage.setItem('smart_school_expenses', JSON.stringify(expenses));
  }, [expenses]);

  // Mutation Handlers
  const handleAddStudent = (s: Student) => {
    setStudents((prev) => [s, ...prev]);
    logAudit('Enrolled Student', `Enrolled ${s.name} (${s.id}) into ${s.className}`);
  };

  const handleAddTeacher = (t: Teacher) => {
    setTeachers((prev) => [t, ...prev]);
    logAudit('Appointed Faculty', `Appointed ${t.name} as ${t.designation}`);
  };

  const handleMarkAttendance = (rec: AttendanceRecord) => {
    setAttendance((prev) => [rec, ...prev]);
  };

  const handleBatchMarkAttendance = (records: AttendanceRecord[]) => {
    setAttendance((prev) => [...records, ...prev]);
    logAudit('Attendance Batch Marked', `Batch marked attendance for ${records.length} students`);
  };

  const handleAddPayment = (p: FeePayment) => {
    setPayments((prev) => [p, ...prev]);
    logAudit('Fee Receipt Generated', `Collected ${p.paidAmount} PKR from ${p.studentName} (${p.receiptNo})`);
  };

  const handleAddExpense = (e: ExpenseRecord) => {
    setExpenses((prev) => [e, ...prev]);
    logAudit('Expense Recorded', `Disbursed ${e.amount} PKR for ${e.category} - ${e.description}`);
  };

  const handleAddSalary = (s: SalarySlip) => {
    setSalaries((prev) => [s, ...prev]);
    logAudit('Salary Disbursed', `Disbursed ${s.netSalary} PKR to ${s.employeeName} (${s.slipNo})`);
  };

  const handleAddExam = (ex: ExamRecord) => {
    setExams((prev) => [ex, ...prev]);
    logAudit('Exam Scheduled', `Scheduled ${ex.title} for ${ex.className}`);
  };

  const handleAddResult = (res: ExamResultItem) => {
    setResults((prev) => [res, ...prev.filter((r) => !(r.examId === res.examId && r.studentId === res.studentId))]);
    logAudit('Marks Recorded', `Recorded score for ${res.studentName} in ${res.examTitle} (${res.obtainedMarks}/${res.totalMarks})`);
  };

  const handleAddTimetableSlot = (slot: TimetableSlot) => {
    setTimetable((prev) => [...prev, slot]);
  };

  const handleAddBook = (book: LibraryBook) => {
    setLibraryBooks((prev) => [book, ...prev]);
  };

  const handleAddTransport = (veh: TransportVehicle) => {
    setTransportVehicles((prev) => [veh, ...prev]);
  };

  const handleAddInventory = (inv: InventoryItem) => {
    setInventory((prev) => [inv, ...prev]);
  };

  const handleUpdateLeaveStatus = (id: number, status: 'APPROVED' | 'REJECTED') => {
    setLeaves((prev) => prev.map((l) => (l.id === id ? { ...l, status } : l)));
  };

  const logAudit = (action: string, details: string) => {
    const newLog: AuditLog = {
      id: Date.now(),
      actor: role,
      action,
      details,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };
    setAuditLogs((prev) => [newLog, ...prev.slice(0, 50)]);
  };

  const handleExportAllData = () => {
    const data = {
      profile,
      students,
      teachers,
      attendance,
      payments,
      expenses,
      salaries,
      exams,
      results,
      timetable,
      libraryBooks,
      transportVehicles,
      inventory,
      auditLogs,
      exportTimestamp: new Date().toISOString(),
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `SmartSchool_Complete_Database_${new Date().toISOString().split('T')[0]}.json`;
    a.click();
  };

  const handleResetData = () => {
    if (window.confirm('Reset all databases to default initial sample data?')) {
      localStorage.clear();
      window.location.reload();
    }
  };

  // Nav Items Definition
  const NAV_ITEMS = [
    { id: 'dashboard', label: t('dashboard', lang), icon: LayoutDashboard },
    { id: 'students', label: t('students', lang), icon: Users },
    { id: 'teachers', label: t('teachers', lang), icon: GraduationCap },
    { id: 'attendance', label: t('attendance', lang), icon: CalendarCheck },
    { id: 'finance', label: t('finance', lang), icon: CreditCard },
    { id: 'exams', label: t('exams', lang), icon: Award },
    { id: 'timetable', label: t('timetable', lang), icon: Clock },
    { id: 'ai_tools', label: t('ai_tools', lang), icon: Sparkles, badge: 'AI' },
    { id: 'administration', label: t('administration', lang), icon: Building2 },
    { id: 'reports', label: 'Reports', icon: BarChart3 },
    { id: 'settings', label: t('settings', lang), icon: Settings },
  ];

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col font-['Plus_Jakarta_Sans',sans-serif] text-slate-800 antialiased selection:bg-blue-900 selection:text-white">
      {/* Top Navbar */}
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200 px-4 sm:px-8 py-3 flex items-center justify-between shadow-xs">
        <div className="flex items-center gap-3">
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 rounded-xl text-slate-600 hover:bg-slate-100"
            aria-label="Toggle Navigation"
          >
            {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>

          {/* School Brand Crest */}
          <div
            onClick={() => navigateTo('dashboard')}
            className="flex items-center gap-2.5 cursor-pointer select-none"
          >
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-blue-950 via-blue-900 to-indigo-800 flex items-center justify-center text-amber-400 shadow-md shadow-blue-950/20 border border-blue-800">
              <GraduationCap size={22} />
            </div>
            <div>
              <span className="font-black text-sm tracking-tight text-blue-950 block leading-tight">
                {profile.name}
              </span>
              <span className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider block">
                Smart Management System • {profile.academicYear}
              </span>
            </div>
          </div>
        </div>

        {/* Right Action Controls: Role + Language Switcher + Bell */}
        <div className="flex items-center gap-2.5">
          {/* Active Role Selector */}
          <div className="hidden sm:flex items-center gap-1.5 bg-slate-50 border border-slate-200 px-2.5 py-1.5 rounded-xl text-xs">
            <Shield size={14} className="text-blue-900" />
            <select
              value={role}
              onChange={(e) => setRole(e.target.value as UserRole)}
              className="bg-transparent font-bold text-slate-700 text-xs focus:outline-none cursor-pointer"
            >
              <option value="Super Admin">Super Admin</option>
              <option value="School Admin">School Admin</option>
              <option value="Principal">Principal</option>
              <option value="Teacher">Teacher</option>
              <option value="Accountant">Accountant</option>
              <option value="Receptionist">Receptionist</option>
              <option value="Parent">Parent</option>
            </select>
          </div>

          {/* Language Switcher Buttons */}
          <div className="flex bg-slate-100 p-0.5 rounded-xl border border-slate-200 text-xs font-bold">
            <button
              onClick={() => setLang('en')}
              className={`px-2.5 py-1 rounded-lg transition-all ${
                lang === 'en' ? 'bg-white text-blue-950 shadow-xs' : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              EN
            </button>
            <button
              onClick={() => setLang('ur')}
              className={`px-2.5 py-1 rounded-lg font-['Noto_Naskh_Arabic'] transition-all ${
                lang === 'ur' ? 'bg-white text-blue-950 shadow-xs' : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              اردو
            </button>
            <button
              onClick={() => setLang('sd')}
              className={`px-2.5 py-1 rounded-lg font-['Noto_Naskh_Arabic'] transition-all ${
                lang === 'sd' ? 'bg-white text-blue-950 shadow-xs' : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              سنڌي
            </button>
          </div>

          {/* Notifications icon */}
          <button
            onClick={() => navigateTo('dashboard')}
            className="p-2 rounded-xl text-slate-500 hover:bg-slate-100 hover:text-slate-800 relative transition-colors"
            title="Notifications"
          >
            <Bell size={18} />
            <span className="w-2 h-2 rounded-full bg-rose-500 absolute top-2 right-2 ring-2 ring-white" />
          </button>
        </div>
      </header>

      {/* Main Container: Sidebar + Content */}
      <div className="flex-1 flex max-w-[1600px] w-full mx-auto">
        {/* Desktop Sidebar Navigation */}
        <aside className="hidden md:flex flex-col w-64 bg-white border-r border-slate-200 p-4 shrink-0 min-h-[calc(100vh-65px)]">
          <nav className="space-y-1">
            {NAV_ITEMS.map((item) => {
              const Icon = item.icon;
              const isActive = activeScreen === item.id;
              return (
                <button
                  key={item.id}
                  onClick={() => navigateTo(item.id)}
                  className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-2xl text-xs font-bold transition-all ${
                    isActive
                      ? 'bg-blue-900 text-white shadow-md shadow-blue-900/15'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <Icon size={17} className={isActive ? 'text-white' : 'text-slate-400'} />
                    <span>{item.label}</span>
                  </div>
                  {item.badge && (
                    <span className="px-1.5 py-0.5 text-[9px] font-black rounded-md bg-amber-400 text-slate-950 uppercase">
                      {item.badge}
                    </span>
                  )}
                </button>
              );
            })}
          </nav>

          {/* Institutional Badge in Sidebar */}
          <div className="mt-auto pt-6 border-t border-slate-100">
            <div className="p-3 bg-gradient-to-br from-blue-50 to-indigo-50/50 rounded-2xl border border-blue-100 text-xs">
              <p className="font-bold text-blue-950">{profile.principalName}</p>
              <p className="text-[10px] text-slate-500">Head of Institution</p>
              <div className="mt-2 pt-2 border-t border-blue-200/60 flex items-center justify-between text-[10px] text-blue-900 font-semibold font-mono">
                <span>{profile.currency}</span>
                <span>Reg: {profile.regNumber.split('-').slice(-2).join('-')}</span>
              </div>
            </div>
          </div>
        </aside>

        {/* Mobile Navigation Drawer */}
        {mobileMenuOpen && (
          <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm md:hidden flex">
            <div className="w-72 bg-white h-full p-5 flex flex-col justify-between shadow-2xl animate-in slide-in-from-left">
              <div>
                <div className="flex justify-between items-center pb-4 border-b border-slate-200 mb-4">
                  <div className="flex items-center gap-2">
                    <GraduationCap size={20} className="text-blue-900" />
                    <span className="font-black text-sm text-blue-950">Smart School</span>
                  </div>
                  <button onClick={() => setMobileMenuOpen(false)} className="p-1 text-slate-400">
                    <X size={20} />
                  </button>
                </div>

                <nav className="space-y-1">
                  {NAV_ITEMS.map((item) => {
                    const Icon = item.icon;
                    const isActive = activeScreen === item.id;
                    return (
                      <button
                        key={item.id}
                        onClick={() => navigateTo(item.id)}
                        className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-bold ${
                          isActive ? 'bg-blue-900 text-white' : 'text-slate-600 hover:bg-slate-50'
                        }`}
                      >
                        <div className="flex items-center gap-2.5">
                          <Icon size={16} />
                          <span>{item.label}</span>
                        </div>
                      </button>
                    );
                  })}
                </nav>
              </div>

              <div className="text-[11px] text-slate-400 text-center">
                Smart School System v1.0.0
              </div>
            </div>
            <div className="flex-1" onClick={() => setMobileMenuOpen(false)} />
          </div>
        )}

        {/* Main Content Area */}
        <main className="flex-1 p-4 sm:p-8 overflow-y-auto">
          {activeScreen === 'dashboard' && (
            <DashboardScreen
              profile={profile}
              students={students}
              teachers={teachers}
              attendance={attendance}
              payments={payments}
              expenses={expenses}
              notifications={notifications}
              lang={lang}
              role={role}
              onNavigate={navigateTo}
            />
          )}

          {activeScreen === 'students' && (
            <StudentsScreen
              students={students}
              profile={profile}
              lang={lang}
              role={role}
              onAddStudent={handleAddStudent}
            />
          )}

          {activeScreen === 'teachers' && (
            <TeachersScreen
              teachers={teachers}
              staff={staff}
              profile={profile}
              lang={lang}
              role={role}
              onAddTeacher={handleAddTeacher}
            />
          )}

          {activeScreen === 'attendance' && (
            <AttendanceScreen
              students={students}
              teachers={teachers}
              staff={staff}
              attendance={attendance}
              profile={profile}
              lang={lang}
              role={role}
              onMarkAttendance={handleMarkAttendance}
              onBatchMarkAttendance={handleBatchMarkAttendance}
            />
          )}

          {activeScreen === 'finance' && (
            <FinanceScreen
              payments={payments}
              expenses={expenses}
              salaries={salaries}
              students={students}
              teachers={teachers}
              staff={staff}
              feeStructures={initialFeeStructures}
              profile={profile}
              lang={lang}
              role={role}
              onAddPayment={handleAddPayment}
              onAddExpense={handleAddExpense}
              onAddSalarySlip={handleAddSalary}
            />
          )}

          {activeScreen === 'exams' && (
            <ExamsScreen
              exams={exams}
              results={results}
              students={students}
              profile={profile}
              lang={lang}
              role={role}
              onAddExam={handleAddExam}
              onAddResult={handleAddResult}
              onUpdateResult={handleAddResult}
            />
          )}

          {activeScreen === 'timetable' && (
            <TimetableScreen
              slots={timetable}
              teachers={teachers}
              profile={profile}
              lang={lang}
              role={role}
              onAddSlot={handleAddTimetableSlot}
            />
          )}

          {activeScreen === 'ai_tools' && (
            <AIToolsScreen
              profile={profile}
              students={students}
              lang={lang}
              role={role}
            />
          )}

          {activeScreen === 'administration' && (
            <AdministrationScreen
              students={students}
              libraryBooks={libraryBooks}
              transportVehicles={transportVehicles}
              inventory={inventory}
              leaves={leaves}
              profile={profile}
              lang={lang}
              role={role}
              onAddBook={handleAddBook}
              onAddTransport={handleAddTransport}
              onAddInventory={handleAddInventory}
              onUpdateLeaveStatus={handleUpdateLeaveStatus}
            />
          )}

          {activeScreen === 'reports' && (
            <ReportsScreen
              students={students}
              teachers={teachers}
              attendance={attendance}
              payments={payments}
              expenses={expenses}
              salaries={salaries}
              results={results}
              profile={profile}
              lang={lang}
              role={role}
            />
          )}

          {activeScreen === 'settings' && (
            <SettingsScreen
              profile={profile}
              lang={lang}
              role={role}
              auditLogs={auditLogs}
              onUpdateProfile={setProfile}
              onLanguageChange={setLang}
              onRoleChange={setRole}
              onExportAllData={handleExportAllData}
              onResetData={handleResetData}
            />
          )}
        </main>
      </div>
    </div>
  );
};
