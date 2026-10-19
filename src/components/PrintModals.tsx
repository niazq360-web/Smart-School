import React, { useState } from 'react';
import { FeePayment, SchoolProfile, Student, ExamResultItem } from '../types';
import { QrCodeCanvas } from './QrCodeCanvas';
import { Printer, X, CheckCircle } from 'lucide-react';

interface FeeReceiptModalProps {
  payment: FeePayment;
  profile: SchoolProfile;
  onClose: () => void;
}

export const PrintableFeeReceiptModal: React.FC<FeeReceiptModalProps> = ({ payment, profile, onClose }) => {
  const [isThermal, setIsThermal] = useState(false);

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6 max-h-[90vh] flex flex-col">
        {/* Controls */}
        <div className="flex items-center justify-between pb-4 border-b border-slate-200">
          <h2 className="text-xl font-bold text-slate-800">Official Fee Receipt</h2>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsThermal(false)}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-colors ${
                !isThermal ? 'bg-blue-900 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              }`}
            >
              Standard A4
            </button>
            <button
              onClick={() => setIsThermal(true)}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-colors ${
                isThermal ? 'bg-blue-900 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
              }`}
            >
              80mm Thermal Slip
            </button>
            <button onClick={onClose} className="p-1.5 hover:bg-slate-100 rounded-lg text-slate-500">
              <X size={20} />
            </button>
          </div>
        </div>

        {/* Scrollable Receipt Body */}
        <div className="flex-1 overflow-y-auto py-6 flex justify-center">
          {!isThermal ? (
            /* Standard A4 View */
            <div className="w-full max-w-xl bg-white border border-slate-300 rounded-xl p-8 shadow-sm text-slate-800">
              <div className="flex justify-between items-start border-b-2 border-blue-900 pb-4">
                <div>
                  <h1 className="text-2xl font-extrabold text-blue-900 tracking-tight">{profile.name}</h1>
                  <p className="text-xs text-slate-500 mt-0.5">{profile.address}</p>
                  <p className="text-xs text-slate-500">Tel: {profile.phone} | Reg #: {profile.regNumber}</p>
                </div>
                <QrCodeCanvas content={`${payment.receiptNo}:${payment.studentId}`} size={70} />
              </div>

              <div className="grid grid-cols-2 gap-4 my-6 text-sm">
                <div>
                  <p><span className="text-slate-500">Receipt No:</span> <strong className="text-blue-900">{payment.receiptNo}</strong></p>
                  <p><span className="text-slate-500">Student:</span> <strong>{payment.studentName}</strong></p>
                  <p><span className="text-slate-500">ID:</span> <strong>{payment.studentId}</strong></p>
                  <p><span className="text-slate-500">Class:</span> <strong>{payment.className} - {payment.section}</strong></p>
                </div>
                <div className="text-right">
                  <p><span className="text-slate-500">Date:</span> <strong>{payment.paymentDate}</strong></p>
                  <p><span className="text-slate-500">Month:</span> <strong className="text-emerald-700">{payment.month}</strong></p>
                  <p><span className="text-slate-500">Payment:</span> <strong>{payment.paymentMethod}</strong></p>
                  <p><span className="text-slate-500">Ref:</span> <strong className="font-mono text-xs">{payment.paymentRef}</strong></p>
                </div>
              </div>

              {/* Breakdown Table */}
              <table className="w-full text-left text-sm border-collapse mb-6">
                <thead>
                  <tr className="bg-slate-100 text-slate-700 border-b border-slate-200">
                    <th className="py-2.5 px-3">Fee Particulars</th>
                    <th className="py-2.5 px-3 text-right">Amount ({profile.currencySymbol})</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  <tr>
                    <td className="py-2 px-3">Monthly Tuition Fee</td>
                    <td className="py-2 px-3 text-right font-medium">{payment.monthlyFee.toLocaleString()}</td>
                  </tr>
                  {payment.transportFee > 0 && (
                    <tr>
                      <td className="py-2 px-3">Transport Bus Service</td>
                      <td className="py-2 px-3 text-right font-medium">{payment.transportFee.toLocaleString()}</td>
                    </tr>
                  )}
                  {payment.computerFee > 0 && (
                    <tr>
                      <td className="py-2 px-3">Computer / IT Laboratory</td>
                      <td className="py-2 px-3 text-right font-medium">{payment.computerFee.toLocaleString()}</td>
                    </tr>
                  )}
                  {payment.previousDue > 0 && (
                    <tr>
                      <td className="py-2 px-3 text-amber-700">Previous Arrears / Dues</td>
                      <td className="py-2 px-3 text-right font-medium text-amber-700">{payment.previousDue.toLocaleString()}</td>
                    </tr>
                  )}
                  {payment.discountAmount > 0 && (
                    <tr>
                      <td className="py-2 px-3 text-emerald-700">Scholarship / Fee Concession</td>
                      <td className="py-2 px-3 text-right font-medium text-emerald-700">-{payment.discountAmount.toLocaleString()}</td>
                    </tr>
                  )}
                </tbody>
              </table>

              <div className="border-t-2 border-slate-200 pt-3 flex flex-col gap-1.5 text-sm">
                <div className="flex justify-between font-semibold">
                  <span>Total Payable:</span>
                  <span>{profile.currencySymbol} {payment.totalPayable.toLocaleString()}</span>
                </div>
                <div className="flex justify-between font-bold text-base text-emerald-700">
                  <span>Amount Paid:</span>
                  <span>{profile.currencySymbol} {payment.paidAmount.toLocaleString()}</span>
                </div>
                <div className="flex justify-between font-semibold text-rose-600">
                  <span>Outstanding Balance:</span>
                  <span>{profile.currencySymbol} {payment.remainingBalance.toLocaleString()}</span>
                </div>
              </div>

              <div className="flex justify-between items-end mt-12 pt-6 border-t border-slate-200 text-xs text-slate-500">
                <div className="text-center">
                  <p className="border-t border-slate-400 w-36 pt-1">Cashier: {payment.cashierName}</p>
                </div>
                <div className="text-center">
                  <p className="border-t border-slate-400 w-36 pt-1 font-bold text-slate-800">Principal Signature</p>
                </div>
              </div>
            </div>
          ) : (
            /* 80mm Thermal Receipt View */
            <div className="w-72 bg-amber-50/40 border border-slate-300 rounded-lg p-5 font-mono text-xs shadow-sm text-slate-800">
              <div className="text-center border-b border-dashed border-slate-400 pb-3">
                <h3 className="font-bold text-sm tracking-wide">{profile.name.toUpperCase()}</h3>
                <p className="text-[10px] text-slate-500">Tel: {profile.phone}</p>
                <p className="text-[10px] mt-1 text-slate-700 font-bold">FEE RECEIPT</p>
                <p className="text-[10px] text-slate-500">REC: {payment.receiptNo} | {payment.paymentDate}</p>
              </div>

              <div className="my-3 space-y-1 text-[11px]">
                <p><strong>Student:</strong> {payment.studentName}</p>
                <p><strong>ID:</strong> {payment.studentId} ({payment.className})</p>
                <p><strong>Month:</strong> {payment.month}</p>
              </div>

              <div className="border-t border-b border-dashed border-slate-400 py-2 space-y-1">
                <div className="flex justify-between">
                  <span>Tuition:</span>
                  <span>{payment.monthlyFee}</span>
                </div>
                {payment.transportFee > 0 && (
                  <div className="flex justify-between">
                    <span>Transport:</span>
                    <span>{payment.transportFee}</span>
                  </div>
                )}
                {payment.previousDue > 0 && (
                  <div className="flex justify-between text-amber-800">
                    <span>Prev Due:</span>
                    <span>{payment.previousDue}</span>
                  </div>
                )}
                {payment.discountAmount > 0 && (
                  <div className="flex justify-between text-emerald-800">
                    <span>Discount:</span>
                    <span>-{payment.discountAmount}</span>
                  </div>
                )}
                <div className="flex justify-between font-bold border-t border-dashed border-slate-300 pt-1">
                  <span>TOTAL DUE:</span>
                  <span>{payment.totalPayable}</span>
                </div>
                <div className="flex justify-between font-bold text-emerald-700">
                  <span>PAID:</span>
                  <span>{payment.paidAmount}</span>
                </div>
                <div className="flex justify-between font-bold text-rose-700">
                  <span>BALANCE:</span>
                  <span>{payment.remainingBalance}</span>
                </div>
              </div>

              <div className="mt-4 flex flex-col items-center text-center">
                <QrCodeCanvas content={payment.receiptNo} size={65} />
                <p className="text-[10px] text-slate-500 mt-2">Thank you for timely fee payment!</p>
              </div>
            </div>
          )}
        </div>

        {/* Footer actions */}
        <div className="pt-4 border-t border-slate-200 flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-xl"
          >
            Close
          </button>
          <button
            onClick={handlePrint}
            className="px-5 py-2 text-sm font-semibold text-white bg-blue-900 hover:bg-blue-800 rounded-xl flex items-center gap-2 shadow-sm"
          >
            <Printer size={16} />
            Print / Save as PDF
          </button>
        </div>
      </div>
    </div>
  );
};

export const StudentIdCardModal: React.FC<{ student: Student; profile: SchoolProfile; onClose: () => void }> = ({
  student,
  profile,
  onClose,
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-2xl shadow-2xl max-w-lg w-full p-6">
        <div className="flex items-center justify-between pb-3 border-b border-slate-200">
          <h2 className="text-lg font-bold text-slate-800">Student Identity Card</h2>
          <button onClick={onClose} className="p-1.5 hover:bg-slate-100 rounded-lg text-slate-500">
            <X size={18} />
          </button>
        </div>

        <div className="py-6 flex flex-col items-center gap-6">
          {/* Card Front */}
          <div className="w-80 h-48 bg-white border border-slate-300 rounded-2xl overflow-hidden shadow-md flex flex-col">
            <div className="bg-blue-900 text-white text-center py-2 px-3">
              <h4 className="font-extrabold text-xs tracking-wider">{profile.name.toUpperCase()}</h4>
              <p className="text-[9px] text-blue-200">STUDENT IDENTITY CARD • {profile.academicYear}</p>
            </div>
            <div className="flex-1 p-3 flex items-center gap-3">
              <div className="w-16 h-20 bg-slate-200 rounded-xl flex items-center justify-center font-bold text-blue-900 text-2xl border border-slate-300">
                {student.name.charAt(0)}
              </div>
              <div className="flex-1 text-xs space-y-0.5">
                <p className="font-bold text-sm text-slate-900">{student.name}</p>
                <p className="text-[11px] text-slate-600">S/D/O: {student.fatherName}</p>
                <p className="text-[11px] font-semibold text-blue-900">ID: {student.id}</p>
                <p className="text-[10px] text-slate-500">Class: {student.className}-{student.section} | Roll: {student.rollNo}</p>
                <p className="text-[10px] text-rose-600 font-semibold">Emg: {student.emergencyContact}</p>
              </div>
              <QrCodeCanvas content={student.id} size={50} />
            </div>
          </div>

          {/* Card Back */}
          <div className="w-80 h-48 bg-slate-50 border border-slate-300 rounded-2xl p-4 shadow-md flex flex-col justify-between text-[10px] text-slate-600">
            <div>
              <p className="font-bold text-blue-900 text-xs mb-1">TERMS & REGULATIONS</p>
              <ul className="list-disc list-inside space-y-0.5">
                <li>Card must be visibly displayed at all times on campus.</li>
                <li>Used for QR attendance scanning and library checkout.</li>
                <li>Non-transferable. Loss must be reported immediately.</li>
              </ul>
              <p className="mt-2 text-slate-500">Address: {profile.address}</p>
              <p className="text-slate-500">Phone: {profile.phone}</p>
            </div>
            <div className="flex justify-between items-end border-t border-slate-200 pt-2 text-[9px]">
              <span>Valid Session: {profile.academicYear}</span>
              <span className="font-bold text-slate-800">Principal Signature</span>
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-3 border-t border-slate-200">
          <button onClick={onClose} className="px-4 py-2 text-sm bg-slate-100 hover:bg-slate-200 rounded-xl font-medium">Close</button>
          <button onClick={() => window.print()} className="px-5 py-2 text-sm font-semibold text-white bg-blue-900 hover:bg-blue-800 rounded-xl flex items-center gap-2">
            <Printer size={16} /> Print ID Card
          </button>
        </div>
      </div>
    </div>
  );
};

export const CertificateModal: React.FC<{
  type: string;
  student: Student;
  profile: SchoolProfile;
  onClose: () => void;
}> = ({ type, student, profile, onClose }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6 max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between pb-3 border-b border-slate-200">
          <h2 className="text-lg font-bold text-slate-800">{type} Preview</h2>
          <button onClick={onClose} className="p-1.5 hover:bg-slate-100 rounded-lg text-slate-500">
            <X size={18} />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto py-6 flex justify-center">
          <div className="w-full bg-amber-50/30 border-4 border-double border-amber-800 rounded-xl p-10 text-center text-slate-800 relative">
            <h2 className="text-2xl font-black text-blue-950 uppercase tracking-wider">{profile.name}</h2>
            <p className="text-xs text-slate-500 mt-1">{profile.address} • Reg #: {profile.regNumber}</p>
            <div className="w-24 h-1 bg-amber-700 mx-auto my-4 rounded-full" />
            <h3 className="text-xl font-bold font-serif text-amber-900 uppercase tracking-widest my-4">{type}</h3>

            <p className="text-sm leading-relaxed text-slate-700 mt-6 px-4">
              This is to officially certify that <strong className="text-blue-950">{student.name}</strong>, Son/Daughter of{' '}
              <strong className="text-blue-950">{student.fatherName}</strong>, bearing Student ID{' '}
              <span className="font-mono font-bold text-blue-900">{student.id}</span> and Roll Number{' '}
              <strong>{student.rollNo}</strong>, is a bonafide student of this institution studying in{' '}
              <strong>{student.className}</strong> during the academic session <strong>{profile.academicYear}</strong>.
            </p>

            <p className="text-sm leading-relaxed text-slate-700 mt-4 px-4">
              During their tenure with our school, their character, conduct, and dedication have been found to be{' '}
              <strong className="text-emerald-800">EXCELLENT</strong>. We wish them success in all future academic endeavors.
            </p>

            <div className="flex justify-between items-end mt-16 pt-8 text-xs text-slate-600">
              <div className="text-left">
                <p>Date: {new Date().toLocaleDateString()}</p>
                <p className="font-bold text-amber-900 mt-1">Institutional Seal</p>
              </div>
              <div className="text-right">
                <p className="border-t border-slate-600 w-48 font-bold text-slate-900 pt-1">{profile.principalName}</p>
                <p className="text-[11px] text-slate-500">Principal / Headmaster</p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-3 border-t border-slate-200">
          <button onClick={onClose} className="px-4 py-2 text-sm bg-slate-100 hover:bg-slate-200 rounded-xl font-medium">Close</button>
          <button onClick={() => window.print()} className="px-5 py-2 text-sm font-semibold text-white bg-amber-800 hover:bg-amber-900 rounded-xl flex items-center gap-2">
            <Printer size={16} /> Print Certificate
          </button>
        </div>
      </div>
    </div>
  );
};

export const ExamResultCardModal: React.FC<{
  result: ExamResultItem;
  profile: SchoolProfile;
  onClose: () => void;
}> = ({ result, profile, onClose }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-6 max-h-[90vh] flex flex-col">
        <div className="flex items-center justify-between pb-3 border-b border-slate-200">
          <h2 className="text-lg font-bold text-slate-800">Student Progress & Result Card</h2>
          <button onClick={onClose} className="p-1.5 hover:bg-slate-100 rounded-lg text-slate-500">
            <X size={18} />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto py-6 flex justify-center">
          <div className="w-full max-w-xl bg-white border-2 border-slate-800 rounded-2xl p-6 shadow-sm text-slate-800">
            {/* Header */}
            <div className="text-center border-b-2 border-slate-800 pb-4">
              <h1 className="text-2xl font-black text-blue-950 uppercase">{profile.name}</h1>
              <p className="text-xs text-slate-600">{profile.address} • Reg #: {profile.regNumber}</p>
              <div className="inline-block mt-2 px-4 py-1 bg-blue-900 text-white text-xs font-bold rounded-full uppercase tracking-wider">
                {result.examTitle} — Official Report Card
              </div>
            </div>

            {/* Student Info */}
            <div className="grid grid-cols-2 gap-3 my-4 bg-slate-50 p-4 rounded-xl border border-slate-200 text-xs">
              <div>
                <p className="text-slate-500">Student Name:</p>
                <p className="font-bold text-sm text-slate-900">{result.studentName}</p>
              </div>
              <div>
                <p className="text-slate-500">Student ID & Roll #:</p>
                <p className="font-bold text-sm text-slate-900">{result.studentId} | Roll #{result.rollNo}</p>
              </div>
              <div>
                <p className="text-slate-500">Class & Academic Year:</p>
                <p className="font-bold text-slate-800">{result.className} ({profile.academicYear})</p>
              </div>
              <div>
                <p className="text-slate-500">Issued On:</p>
                <p className="font-bold text-slate-800">{new Date().toLocaleDateString()}</p>
              </div>
            </div>

            {/* Marks Breakdown Table */}
            <table className="w-full text-xs border border-slate-300 rounded-lg overflow-hidden my-4">
              <thead className="bg-slate-100 text-slate-700 font-bold border-b border-slate-300">
                <tr>
                  <th className="p-2.5 text-left">Assessment Metric</th>
                  <th className="p-2.5 text-center">Score / Metric</th>
                  <th className="p-2.5 text-right">Performance Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                <tr>
                  <td className="p-2.5 font-medium">Maximum Marks</td>
                  <td className="p-2.5 text-center font-bold">{result.totalMarks}</td>
                  <td className="p-2.5 text-right text-slate-500">Standard Total</td>
                </tr>
                <tr className="bg-blue-50/50">
                  <td className="p-2.5 font-semibold text-blue-950">Obtained Marks</td>
                  <td className="p-2.5 text-center font-extrabold text-blue-900 text-sm">{result.obtainedMarks}</td>
                  <td className="p-2.5 text-right text-blue-900 font-semibold">{result.percentage.toFixed(1)}%</td>
                </tr>
                <tr>
                  <td className="p-2.5 font-medium">Letter Grade</td>
                  <td className="p-2.5 text-center font-black text-emerald-700 text-base">{result.grade}</td>
                  <td className="p-2.5 text-right font-medium text-emerald-700">Satisfactory Standing</td>
                </tr>
                <tr>
                  <td className="p-2.5 font-medium">Class Position</td>
                  <td className="p-2.5 text-center font-bold text-amber-700">{result.position || '—'}</td>
                  <td className="p-2.5 text-right text-slate-600">Rank in Class</td>
                </tr>
              </tbody>
            </table>

            {/* Remarks */}
            <div className="bg-amber-50/60 border border-amber-200 p-3 rounded-xl text-xs my-4">
              <span className="font-bold text-amber-900">Teacher's Evaluation: </span>
              <span className="text-amber-950 italic">{result.remarks || 'Shows good understanding of syllabus core topics. Keep up the high standard of work.'}</span>
            </div>

            {/* Signatures */}
            <div className="flex justify-between items-end pt-8 mt-6 border-t border-slate-300 text-[11px] text-slate-600">
              <div className="text-center">
                <div className="w-32 border-b border-slate-400 pb-1 font-semibold">Class Teacher</div>
                <p className="text-[10px] text-slate-400 mt-0.5">Signature</p>
              </div>
              <div className="text-center">
                <div className="w-20 h-10 border border-dashed border-amber-600 rounded-full flex items-center justify-center text-[9px] text-amber-800 font-bold uppercase">
                  Seal
                </div>
              </div>
              <div className="text-center">
                <div className="w-36 border-b border-slate-400 pb-1 font-semibold">{profile.principalName}</div>
                <p className="text-[10px] text-slate-400 mt-0.5">Principal</p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-3 border-t border-slate-200">
          <button onClick={onClose} className="px-4 py-2 text-sm bg-slate-100 hover:bg-slate-200 rounded-xl font-medium">Close</button>
          <button onClick={() => window.print()} className="px-5 py-2 text-sm font-semibold text-white bg-blue-900 hover:bg-blue-800 rounded-xl flex items-center gap-2">
            <Printer size={16} /> Print Result Card
          </button>
        </div>
      </div>
    </div>
  );
};

