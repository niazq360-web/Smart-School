import React, { useState } from 'react';
import {
  FeePayment,
  ExpenseRecord,
  SalarySlip,
  Student,
  Teacher,
  StaffMember,
  FeeStructure,
  SchoolProfile,
  AppLanguage,
  UserRole,
} from '../types';
import { PrintableFeeReceiptModal } from '../components/PrintModals';
import { generateWhatsAppAlert } from '../localization/translations';
import {
  CreditCard,
  Receipt,
  TrendingDown,
  TrendingUp,
  PlusCircle,
  Search,
  Printer,
  DollarSign,
  Calendar,
  Send,
  X,
  Building,
  CheckCircle,
} from 'lucide-react';

interface FinanceScreenProps {
  payments: FeePayment[];
  expenses: ExpenseRecord[];
  salaries: SalarySlip[];
  students: Student[];
  teachers: Teacher[];
  staff: StaffMember[];
  feeStructures: FeeStructure[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onAddPayment: (payment: FeePayment) => void;
  onAddExpense: (expense: ExpenseRecord) => void;
  onAddSalarySlip: (slip: SalarySlip) => void;
}

export const FinanceScreen: React.FC<FinanceScreenProps> = ({
  payments,
  expenses,
  salaries,
  students,
  teachers,
  feeStructures,
  profile,
  lang,
  onAddPayment,
  onAddExpense,
  onAddSalarySlip,
}) => {
  const [tab, setTab] = useState<'fees' | 'salaries' | 'expenses' | 'structures'>('fees');
  const [search, setSearch] = useState('');
  const [selectedReceipt, setSelectedReceipt] = useState<FeePayment | null>(null);

  // Modals
  const [showCollectFeeModal, setShowCollectFeeModal] = useState(false);
  const [showAddExpenseModal, setShowAddExpenseModal] = useState(false);
  const [showPaySalaryModal, setShowPaySalaryModal] = useState(false);

  // Fee Form State
  const [feeStudentId, setFeeStudentId] = useState(students[0]?.id || '');
  const [feeMonth, setFeeMonth] = useState('October 2026');
  const [feeDiscount, setFeeDiscount] = useState(0);
  const [feeMethod, setFeeMethod] = useState('Cash');

  // Expense Form State
  const [expCategory, setExpCategory] = useState('Utilities');
  const [expAmount, setExpAmount] = useState('');
  const [expDesc, setExpDesc] = useState('');
  const [expPaidBy, setExpPaidBy] = useState('Accounts Office');

  // Salary Form State
  const [salTeacherId, setSalTeacherId] = useState(teachers[0]?.id || '');
  const [salMonth, setSalMonth] = useState('October 2026');

  // Calculate totals
  const totalFeesCollected = payments.reduce((acc, p) => acc + p.paidAmount, 0);
  const totalExpenses = expenses.reduce((acc, e) => acc + e.amount, 0);
  const totalSalaries = salaries.reduce((acc, s) => acc + s.netSalary, 0);
  const netBalance = totalFeesCollected - totalExpenses - totalSalaries;

  // Selected student for fee collection
  const targetStudent = students.find((s) => s.id === feeStudentId) || students[0];
  const targetFeeStructure =
    feeStructures.find((f) => f.className === targetStudent?.className) || {
      className: targetStudent?.className || 'Class 5',
      admissionFee: 0,
      monthlyFee: 3500,
      examFee: 500,
      transportFee: 1500,
      computerFee: 500,
      libraryFee: 200,
      otherFee: 0,
    };

  const calculatedTotalPayable = Math.max(
    0,
    targetFeeStructure.monthlyFee + targetFeeStructure.computerFee - feeDiscount
  );

  const handleCollectFeeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!targetStudent) return;

    const receiptNo = `REC-2026-${String(payments.length + 1).padStart(3, '0')}`;
    const newPayment: FeePayment = {
      receiptNo,
      studentId: targetStudent.id,
      studentName: targetStudent.name,
      className: targetStudent.className,
      section: targetStudent.section,
      month: feeMonth,
      monthlyFee: targetFeeStructure.monthlyFee,
      transportFee: 0,
      computerFee: targetFeeStructure.computerFee,
      discountAmount: Number(feeDiscount),
      previousDue: 0,
      totalPayable: calculatedTotalPayable,
      paidAmount: calculatedTotalPayable,
      remainingBalance: 0,
      paymentMethod: feeMethod,
      paymentRef: `${feeMethod}-TXN-${Math.floor(100000 + Math.random() * 900000)}`,
      paymentDate: new Date().toISOString().split('T')[0],
      cashierName: 'Muhammad Salman (Bursar)',
    };

    onAddPayment(newPayment);
    setShowCollectFeeModal(false);
    setSelectedReceipt(newPayment);
  };

  const handleExpenseSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const amountNum = parseFloat(expAmount);
    if (!amountNum || amountNum <= 0) return;

    const newExpense: ExpenseRecord = {
      id: Date.now(),
      date: new Date().toISOString().split('T')[0],
      category: expCategory,
      amount: amountNum,
      description: expDesc || `${expCategory} School Operating Cost`,
      paidBy: expPaidBy,
      paymentMethod: 'Cash / Bank',
    };

    onAddExpense(newExpense);
    setExpAmount('');
    setExpDesc('');
    setShowAddExpenseModal(false);
  };

  const handleSalarySubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const targetTeacher = teachers.find((t) => t.id === salTeacherId);
    if (!targetTeacher) return;

    const slipNo = `SAL-2026-${String(salaries.length + 1).padStart(3, '0')}`;
    const newSalary: SalarySlip = {
      slipNo,
      employeeId: targetTeacher.id,
      employeeName: targetTeacher.name,
      designation: targetTeacher.designation,
      month: salMonth,
      basicSalary: targetTeacher.basicSalary,
      allowances: 4000,
      lateDeductions: 0,
      netSalary: targetTeacher.basicSalary + 4000,
      paymentDate: new Date().toISOString().split('T')[0],
      paymentMethod: 'Online Bank Transfer (HBL)',
      status: 'PAID',
    };

    onAddSalarySlip(newSalary);
    setShowPaySalaryModal(false);
  };

  return (
    <div className="space-y-6">
      {/* Financial Overview Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase">Fee Inflow</p>
            <p className="text-2xl font-black text-emerald-800 mt-1">
              {profile.currencySymbol} {totalFeesCollected.toLocaleString()}
            </p>
            <span className="text-[11px] text-emerald-600 font-semibold flex items-center gap-1 mt-1">
              <TrendingUp size={13} /> {payments.length} Receipts Issued
            </span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <DollarSign size={24} />
          </div>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase">Teacher Payroll</p>
            <p className="text-2xl font-black text-blue-900 mt-1">
              {profile.currencySymbol} {totalSalaries.toLocaleString()}
            </p>
            <span className="text-[11px] text-slate-500 font-medium mt-1">Faculty & Staff</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-blue-50 text-blue-900 flex items-center justify-center">
            <CreditCard size={24} />
          </div>
        </div>

        <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase">Operating Costs</p>
            <p className="text-2xl font-black text-rose-700 mt-1">
              {profile.currencySymbol} {totalExpenses.toLocaleString()}
            </p>
            <span className="text-[11px] text-rose-600 font-semibold flex items-center gap-1 mt-1">
              <TrendingDown size={13} /> Utilities & Maintenance
            </span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center">
            <Receipt size={24} />
          </div>
        </div>

        <div className="bg-gradient-to-br from-blue-900 to-indigo-950 text-white p-5 rounded-3xl shadow-md flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-blue-200 uppercase">Net Surplus Balance</p>
            <p className="text-2xl font-black text-amber-300 mt-1">
              {profile.currencySymbol} {netBalance.toLocaleString()}
            </p>
            <span className="text-[11px] text-blue-200 font-medium mt-1">Operational Reserve</span>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-white/10 flex items-center justify-center">
            <Building size={24} />
          </div>
        </div>
      </div>

      {/* Tabs and Actions */}
      <div className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm flex flex-wrap gap-2 justify-between items-center">
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setTab('fees')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all ${
              tab === 'fees' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            Fee Collection Ledger
          </button>
          <button
            onClick={() => setTab('salaries')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all ${
              tab === 'salaries' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            Salary Payroll
          </button>
          <button
            onClick={() => setTab('expenses')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all ${
              tab === 'expenses' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            School Expenses
          </button>
          <button
            onClick={() => setTab('structures')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all ${
              tab === 'structures' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            Fee Structures
          </button>
        </div>

        <div className="flex items-center gap-2">
          {tab === 'fees' && (
            <button
              onClick={() => setShowCollectFeeModal(true)}
              className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-2xl flex items-center gap-2 shadow-sm transition-colors"
            >
              <PlusCircle size={16} /> Collect Student Fee
            </button>
          )}
          {tab === 'expenses' && (
            <button
              onClick={() => setShowAddExpenseModal(true)}
              className="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-2xl flex items-center gap-2 shadow-sm transition-colors"
            >
              <PlusCircle size={16} /> Record Expense
            </button>
          )}
          {tab === 'salaries' && (
            <button
              onClick={() => setShowPaySalaryModal(true)}
              className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-2 shadow-sm transition-colors"
            >
              <PlusCircle size={16} /> Pay Monthly Salary
            </button>
          )}
        </div>
      </div>

      {/* FEE COLLECTION TABLE */}
      {tab === 'fees' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">Official Fee Receipts History</h3>
            <div className="w-64 relative">
              <Search size={14} className="absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                placeholder="Search receipt or student..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="w-full pl-8 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-900"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Receipt #</th>
                  <th className="p-3.5">Student</th>
                  <th className="p-3.5">Billing Month</th>
                  <th className="p-3.5">Total Payable</th>
                  <th className="p-3.5">Amount Paid</th>
                  <th className="p-3.5">Payment Method</th>
                  <th className="p-3.5">Date</th>
                  <th className="p-3.5 text-right">Receipt & Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {payments
                  .filter(
                    (p) =>
                      p.receiptNo.toLowerCase().includes(search.toLowerCase()) ||
                      p.studentName.toLowerCase().includes(search.toLowerCase())
                  )
                  .map((p) => (
                    <tr key={p.receiptNo} className="hover:bg-slate-50/60">
                      <td className="p-3.5 font-mono font-bold text-blue-900">{p.receiptNo}</td>
                      <td className="p-3.5">
                        <div className="font-bold text-slate-800">{p.studentName}</div>
                        <div className="text-[10px] text-slate-400 font-mono">
                          {p.studentId} • {p.className}-{p.section}
                        </div>
                      </td>
                      <td className="p-3.5 font-medium text-slate-700">{p.month}</td>
                      <td className="p-3.5 font-medium text-slate-600">
                        {profile.currencySymbol} {p.totalPayable.toLocaleString()}
                      </td>
                      <td className="p-3.5 font-bold text-emerald-700">
                        {profile.currencySymbol} {p.paidAmount.toLocaleString()}
                      </td>
                      <td className="p-3.5">
                        <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700 font-mono text-[10px]">
                          {p.paymentMethod}
                        </span>
                      </td>
                      <td className="p-3.5 text-slate-500 font-mono">{p.paymentDate}</td>
                      <td className="p-3.5 text-right">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => setSelectedReceipt(p)}
                            className="px-2.5 py-1 bg-blue-50 text-blue-900 hover:bg-blue-100 rounded-lg font-bold text-[11px] flex items-center gap-1 transition-colors"
                          >
                            <Printer size={13} /> Print
                          </button>
                          <a
                            href={`https://wa.me/?text=${encodeURIComponent(
                              generateWhatsAppAlert(p.studentName, 'fee_paid', profile.name, `${p.paidAmount} PKR for ${p.month}`, lang)
                            )}`}
                            target="_blank"
                            rel="noreferrer"
                            className="p-1 bg-emerald-50 text-emerald-700 hover:bg-emerald-100 rounded-lg"
                            title="WhatsApp Receipt"
                          >
                            <Send size={14} />
                          </a>
                        </div>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* SALARIES TABLE */}
      {tab === 'salaries' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">Faculty Payroll Records</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Slip #</th>
                  <th className="p-3.5">Employee</th>
                  <th className="p-3.5">Designation</th>
                  <th className="p-3.5">Month</th>
                  <th className="p-3.5">Basic Pay</th>
                  <th className="p-3.5">Allowances</th>
                  <th className="p-3.5">Net Disbursed</th>
                  <th className="p-3.5">Payment Date</th>
                  <th className="p-3.5 text-right">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {salaries.map((s) => (
                  <tr key={s.slipNo} className="hover:bg-slate-50/60">
                    <td className="p-3.5 font-mono font-bold text-blue-900">{s.slipNo}</td>
                    <td className="p-3.5 font-bold text-slate-800">{s.employeeName}</td>
                    <td className="p-3.5 text-slate-600">{s.designation}</td>
                    <td className="p-3.5 font-medium">{s.month}</td>
                    <td className="p-3.5 text-slate-600">{profile.currencySymbol} {s.basicSalary.toLocaleString()}</td>
                    <td className="p-3.5 text-slate-600">+{profile.currencySymbol} {s.allowances.toLocaleString()}</td>
                    <td className="p-3.5 font-black text-blue-900">{profile.currencySymbol} {s.netSalary.toLocaleString()}</td>
                    <td className="p-3.5 text-slate-500 font-mono">{s.paymentDate}</td>
                    <td className="p-3.5 text-right">
                      <span className="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 font-bold text-[10px]">
                        PAID
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* EXPENSES TABLE */}
      {tab === 'expenses' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">School Operational Expenses</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Date</th>
                  <th className="p-3.5">Category</th>
                  <th className="p-3.5">Description</th>
                  <th className="p-3.5">Disbursed By</th>
                  <th className="p-3.5">Payment Method</th>
                  <th className="p-3.5 text-right">Amount</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {expenses.map((e) => (
                  <tr key={e.id} className="hover:bg-slate-50/60">
                    <td className="p-3.5 font-mono text-slate-500">{e.date}</td>
                    <td className="p-3.5">
                      <span className="px-2 py-0.5 rounded-full bg-rose-50 text-rose-700 font-bold text-[10px]">
                        {e.category}
                      </span>
                    </td>
                    <td className="p-3.5 text-slate-700 font-medium">{e.description}</td>
                    <td className="p-3.5 text-slate-600">{e.paidBy}</td>
                    <td className="p-3.5 text-slate-500">{e.paymentMethod}</td>
                    <td className="p-3.5 text-right font-black text-rose-700">
                      {profile.currencySymbol} {e.amount.toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* FEE STRUCTURES TABLE */}
      {tab === 'structures' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200">
            <h3 className="font-bold text-slate-800 text-sm">Class-wise Standard Monthly Fee Schedule</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Class Tier</th>
                  <th className="p-3.5">Tuition Fee</th>
                  <th className="p-3.5">Computer Lab</th>
                  <th className="p-3.5">Library Fund</th>
                  <th className="p-3.5">Exam Term Fee</th>
                  <th className="p-3.5">Admission Fee</th>
                  <th className="p-3.5 text-right">Standard Monthly Total</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {feeStructures.map((f) => (
                  <tr key={f.className} className="hover:bg-slate-50/60">
                    <td className="p-3.5 font-bold text-slate-800">{f.className}</td>
                    <td className="p-3.5 text-slate-700 font-medium">{profile.currencySymbol} {f.monthlyFee}</td>
                    <td className="p-3.5 text-slate-600">{profile.currencySymbol} {f.computerFee}</td>
                    <td className="p-3.5 text-slate-600">{profile.currencySymbol} {f.libraryFee}</td>
                    <td className="p-3.5 text-slate-600">{profile.currencySymbol} {f.examFee}</td>
                    <td className="p-3.5 text-slate-600">{profile.currencySymbol} {f.admissionFee}</td>
                    <td className="p-3.5 text-right font-black text-blue-900">
                      {profile.currencySymbol} {(f.monthlyFee + f.computerFee + f.libraryFee).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* COLLECT FEE MODAL */}
      {showCollectFeeModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-lg w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Collect Student Fee</h3>
              <button onClick={() => setShowCollectFeeModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleCollectFeeSubmit} className="space-y-4 mt-4">
              <div>
                <label className="text-xs font-semibold text-slate-600">Select Student</label>
                <select
                  value={feeStudentId}
                  onChange={(e) => setFeeStudentId(e.target.value)}
                  className="w-full mt-1 p-2.5 text-xs bg-slate-50 border border-slate-300 rounded-xl font-medium focus:ring-2 focus:ring-blue-900"
                >
                  {students.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name} ({s.id} - {s.className})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-600">Billing Month</label>
                  <input
                    type="text"
                    value={feeMonth}
                    onChange={(e) => setFeeMonth(e.target.value)}
                    className="w-full mt-1 p-2 text-xs bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-600">Payment Channel</label>
                  <select
                    value={feeMethod}
                    onChange={(e) => setFeeMethod(e.target.value)}
                    className="w-full mt-1 p-2 text-xs bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  >
                    <option value="Cash">Cash (Counter)</option>
                    <option value="JazzCash">JazzCash</option>
                    <option value="EasyPaisa">EasyPaisa</option>
                    <option value="Bank Transfer">Bank Transfer (HBL)</option>
                  </select>
                </div>
              </div>

              <div className="bg-slate-50 p-3.5 rounded-2xl border border-slate-200 text-xs space-y-1.5">
                <div className="flex justify-between text-slate-600">
                  <span>Base Tuition Fee:</span>
                  <span className="font-bold">{profile.currencySymbol} {targetFeeStructure.monthlyFee}</span>
                </div>
                <div className="flex justify-between text-slate-600">
                  <span>Computer / IT Fee:</span>
                  <span className="font-bold">{profile.currencySymbol} {targetFeeStructure.computerFee}</span>
                </div>
                <div className="flex justify-between items-center text-slate-600 pt-1">
                  <span>Scholarship / Concession:</span>
                  <input
                    type="number"
                    value={feeDiscount}
                    onChange={(e) => setFeeDiscount(Number(e.target.value))}
                    className="w-24 p-1 text-right text-xs bg-white border border-slate-300 rounded-lg font-bold"
                  />
                </div>
                <div className="flex justify-between font-black text-blue-900 border-t border-slate-200 pt-2 text-sm">
                  <span>Net Payable:</span>
                  <span>{profile.currencySymbol} {calculatedTotalPayable}</span>
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowCollectFeeModal(false)}
                  className="px-4 py-2 text-xs bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow-sm transition-colors"
                >
                  Confirm & Print Receipt
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* RECORD EXPENSE MODAL */}
      {showAddExpenseModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Record School Expense</h3>
              <button onClick={() => setShowAddExpenseModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleExpenseSubmit} className="space-y-4 mt-4 text-xs">
              <div>
                <label className="font-semibold text-slate-600">Expense Category</label>
                <select
                  value={expCategory}
                  onChange={(e) => setExpCategory(e.target.value)}
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                >
                  <option value="Electricity & Utilities">Electricity & Utilities</option>
                  <option value="Maintenance & Repairs">Maintenance & Repairs</option>
                  <option value="Stationary & Printing">Stationary & Printing</option>
                  <option value="Computer & Science Lab">Computer & Science Lab</option>
                  <option value="Sports & Events">Sports & Events</option>
                  <option value="Vehicle Fuel & Transport">Vehicle Fuel & Transport</option>
                </select>
              </div>

              <div>
                <label className="font-semibold text-slate-600">Amount ({profile.currencySymbol})</label>
                <input
                  type="number"
                  placeholder="e.g. 15000"
                  value={expAmount}
                  onChange={(e) => setExpAmount(e.target.value)}
                  required
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold text-sm"
                />
              </div>

              <div>
                <label className="font-semibold text-slate-600">Detailed Description / Invoice Info</label>
                <textarea
                  rows={3}
                  placeholder="e.g. Monthly high-speed fiber internet and power backup generator diesel"
                  value={expDesc}
                  onChange={(e) => setExpDesc(e.target.value)}
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddExpenseModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-rose-600 hover:bg-rose-700 rounded-xl shadow-sm"
                >
                  Save Expense Record
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* PAY SALARY MODAL */}
      {showPaySalaryModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Disburse Teacher Salary</h3>
              <button onClick={() => setShowPaySalaryModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleSalarySubmit} className="space-y-4 mt-4 text-xs">
              <div>
                <label className="font-semibold text-slate-600">Select Faculty Member</label>
                <select
                  value={salTeacherId}
                  onChange={(e) => setSalTeacherId(e.target.value)}
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                >
                  {teachers.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.name} ({t.designation} - Basic: {profile.currencySymbol} {t.basicSalary})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="font-semibold text-slate-600">Disbursement Month</label>
                <input
                  type="text"
                  value={salMonth}
                  onChange={(e) => setSalMonth(e.target.value)}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowPaySalaryModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl shadow-sm"
                >
                  Approve & Disburse
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* PRINT RECEIPT MODAL */}
      {selectedReceipt && (
        <PrintableFeeReceiptModal
          payment={selectedReceipt}
          profile={profile}
          onClose={() => setSelectedReceipt(null)}
        />
      )}
    </div>
  );
};
