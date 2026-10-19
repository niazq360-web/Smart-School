import React, { useState } from 'react';
import {
  ExamRecord,
  ExamResultItem,
  Student,
  SchoolProfile,
  AppLanguage,
  UserRole,
} from '../types';
import { ExamResultCardModal } from '../components/PrintModals';
import { generateWhatsAppAlert } from '../localization/translations';
import {
  Award,
  BookOpen,
  Calendar,
  Search,
  PlusCircle,
  Printer,
  Send,
  X,
  CheckCircle,
  TrendingUp,
} from 'lucide-react';

interface ExamsScreenProps {
  exams: ExamRecord[];
  results: ExamResultItem[];
  students: Student[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onAddExam: (exam: ExamRecord) => void;
  onAddResult: (result: ExamResultItem) => void;
  onUpdateResult: (result: ExamResultItem) => void;
}

export const ExamsScreen: React.FC<ExamsScreenProps> = ({
  exams,
  results,
  students,
  profile,
  lang,
  onAddExam,
  onAddResult,
}) => {
  const [selectedExamId, setSelectedExamId] = useState(exams[0]?.id || 'EXAM-2026-01');
  const [selectedClass, setSelectedClass] = useState('Class 5');
  const [search, setSearch] = useState('');

  // Modals
  const [showAddExamModal, setShowAddExamModal] = useState(false);
  const [showAddMarksModal, setShowAddMarksModal] = useState(false);
  const [selectedResultToPrint, setSelectedResultToPrint] = useState<ExamResultItem | null>(null);

  // New Exam Form
  const [newExamTitle, setNewExamTitle] = useState('');
  const [newExamType, setNewExamType] = useState('Mid Term');
  const [newExamClass, setNewExamClass] = useState('Class 5');
  const [newExamStart, setNewExamStart] = useState('2026-11-15');
  const [newExamEnd, setNewExamEnd] = useState('2026-11-25');

  // New Marks Form
  const [marksStudentId, setMarksStudentId] = useState(students[0]?.id || '');
  const [marksTotal, setMarksTotal] = useState(100);
  const [marksObtained, setMarksObtained] = useState('');
  const [marksRemarks, setMarksRemarks] = useState('Excellent comprehension and critical thinking');

  const filteredResults = results.filter(
    (r) =>
      r.examId === selectedExamId &&
      (selectedClass === 'All' || r.className === selectedClass) &&
      (r.studentName.toLowerCase().includes(search.toLowerCase()) || r.rollNo.includes(search))
  );

  const calculateGrade = (pct: number) => {
    if (pct >= 85) return 'A+';
    if (pct >= 75) return 'A';
    if (pct >= 65) return 'B';
    if (pct >= 50) return 'C';
    if (pct >= 40) return 'D';
    return 'F';
  };

  const handleAddExamSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newExamTitle) return;

    const newExam: ExamRecord = {
      id: `EXAM-2026-${String(exams.length + 1).padStart(2, '0')}`,
      title: newExamTitle,
      examType: newExamType,
      className: newExamClass,
      startDate: newExamStart,
      endDate: newExamEnd,
      status: 'SCHEDULED',
    };

    onAddExam(newExam);
    setSelectedExamId(newExam.id);
    setShowAddExamModal(false);
    setNewExamTitle('');
  };

  const handleAddMarksSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const st = students.find((s) => s.id === marksStudentId);
    if (!st) return;

    const obtainedNum = parseFloat(marksObtained);
    if (isNaN(obtainedNum)) return;

    const pct = (obtainedNum / marksTotal) * 100;
    const grade = calculateGrade(pct);
    const activeExam = exams.find((e) => e.id === selectedExamId);

    const newResult: ExamResultItem = {
      id: Date.now(),
      examId: selectedExamId,
      examTitle: activeExam?.title || 'Mid-Term Examination 2026',
      studentId: st.id,
      studentName: st.name,
      rollNo: st.rollNo,
      className: st.className,
      totalMarks: marksTotal,
      obtainedMarks: obtainedNum,
      percentage: pct,
      grade,
      position: pct >= 90 ? '1st' : pct >= 85 ? '2nd' : pct >= 80 ? '3rd' : '—',
      remarks: marksRemarks,
    };

    onAddResult(newResult);
    setShowAddMarksModal(false);
    setMarksObtained('');
  };

  const activeExam = exams.find((e) => e.id === selectedExamId);

  return (
    <div className="space-y-6">
      {/* Exams Schedule Carousel */}
      <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-4">
          <div>
            <h2 className="text-lg font-bold text-slate-800">School Examination Schedules</h2>
            <p className="text-xs text-slate-500">Term examinations, periodic unit assessments & progress reports</p>
          </div>
          <button
            onClick={() => setShowAddExamModal(true)}
            className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-2 shadow-sm transition-colors"
          >
            <PlusCircle size={16} /> Schedule New Exam
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {exams.map((exam) => {
            const isSelected = exam.id === selectedExamId;
            return (
              <div
                key={exam.id}
                onClick={() => setSelectedExamId(exam.id)}
                className={`p-4 rounded-2xl cursor-pointer border transition-all ${
                  isSelected
                    ? 'bg-blue-50/80 border-blue-900 shadow-md ring-2 ring-blue-900/20'
                    : 'bg-slate-50 hover:bg-slate-100/80 border-slate-200'
                }`}
              >
                <div className="flex justify-between items-start">
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase bg-blue-900 text-white">
                    {exam.examType}
                  </span>
                  <span className="text-[10px] font-mono text-slate-400 font-bold">{exam.id}</span>
                </div>
                <h4 className="font-bold text-slate-900 text-sm mt-2">{exam.title}</h4>
                <p className="text-xs text-slate-500 mt-1">Target: {exam.className}</p>
                <div className="flex items-center gap-1.5 text-[11px] text-slate-600 mt-3 pt-2 border-t border-slate-200">
                  <Calendar size={13} className="text-blue-900" />
                  <span>
                    {exam.startDate} to {exam.endDate}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Grade Book & Marks Sheet */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
        {/* Filter and Top Bar */}
        <div className="p-5 border-b border-slate-200 flex flex-col md:flex-row gap-4 justify-between items-center">
          <div>
            <h3 className="font-bold text-slate-800 text-base flex items-center gap-2">
              <Award className="text-amber-600" size={20} />
              {activeExam?.title || 'Examination'} — Student Grade Book
            </h3>
            <p className="text-xs text-slate-500">Official marks register, grades, positions, and report card generation</p>
          </div>

          <div className="flex flex-wrap items-center gap-2 w-full md:w-auto">
            <select
              value={selectedClass}
              onChange={(e) => setSelectedClass(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 border border-slate-300 rounded-xl font-bold text-slate-700"
            >
              <option value="All">All Classes</option>
              {['Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10'].map(
                (c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                )
              )}
            </select>

            <div className="relative">
              <Search size={14} className="absolute left-3 top-2.5 text-slate-400" />
              <input
                type="text"
                placeholder="Search student..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-8 pr-3 py-1.5 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-900"
              />
            </div>

            <button
              onClick={() => setShowAddMarksModal(true)}
              className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl flex items-center gap-1.5 shadow-sm transition-colors"
            >
              <PlusCircle size={15} /> Enter Marks
            </button>
          </div>
        </div>

        {/* Results Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
              <tr>
                <th className="p-3.5">Roll #</th>
                <th className="p-3.5">Student Details</th>
                <th className="p-3.5">Class</th>
                <th className="p-3.5 text-center">Total Marks</th>
                <th className="p-3.5 text-center">Obtained Marks</th>
                <th className="p-3.5 text-center">Percentage</th>
                <th className="p-3.5 text-center">Grade</th>
                <th className="p-3.5 text-center">Position</th>
                <th className="p-3.5 text-right">Result Card</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredResults.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50/60">
                  <td className="p-3.5 font-bold text-slate-900">#{r.rollNo}</td>
                  <td className="p-3.5">
                    <div className="font-bold text-slate-800">{r.studentName}</div>
                    <div className="text-[10px] text-slate-400 font-mono">{r.studentId}</div>
                  </td>
                  <td className="p-3.5 text-slate-700 font-medium">{r.className}</td>
                  <td className="p-3.5 text-center font-medium text-slate-500">{r.totalMarks}</td>
                  <td className="p-3.5 text-center font-extrabold text-blue-900 text-sm">{r.obtainedMarks}</td>
                  <td className="p-3.5 text-center font-bold text-slate-800">{r.percentage.toFixed(1)}%</td>
                  <td className="p-3.5 text-center">
                    <span
                      className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-black ${
                        r.grade.startsWith('A')
                          ? 'bg-emerald-100 text-emerald-800'
                          : r.grade === 'B'
                          ? 'bg-blue-100 text-blue-800'
                          : r.grade === 'C'
                          ? 'bg-amber-100 text-amber-800'
                          : 'bg-rose-100 text-rose-800'
                      }`}
                    >
                      {r.grade}
                    </span>
                  </td>
                  <td className="p-3.5 text-center font-black text-amber-700">
                    {r.position && r.position !== '—' ? (
                      <span className="px-2 py-0.5 rounded-md bg-amber-50 border border-amber-200">
                        {r.position}
                      </span>
                    ) : (
                      '—'
                    )}
                  </td>
                  <td className="p-3.5 text-right">
                    <div className="flex justify-end gap-2">
                      <button
                        onClick={() => setSelectedResultToPrint(r)}
                        className="px-3 py-1 bg-blue-50 text-blue-900 hover:bg-blue-100 rounded-lg font-bold text-[11px] flex items-center gap-1 transition-colors"
                      >
                        <Printer size={13} /> Result Card
                      </button>
                      <a
                        href={`https://wa.me/?text=${encodeURIComponent(
                          `Official Academic Report: ${r.studentName} scored ${r.obtainedMarks}/${r.totalMarks} (${r.percentage.toFixed(1)}%, Grade ${r.grade}, Position: ${r.position}) in ${r.examTitle} at ${profile.name}.`
                        )}`}
                        target="_blank"
                        rel="noreferrer"
                        className="p-1 bg-emerald-50 text-emerald-700 hover:bg-emerald-100 rounded-lg"
                        title="Send WhatsApp Notification"
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

      {/* SCHEDULE EXAM MODAL */}
      {showAddExamModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Schedule New Examination</h3>
              <button onClick={() => setShowAddExamModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddExamSubmit} className="space-y-4 mt-4 text-xs">
              <div>
                <label className="font-semibold text-slate-600">Exam Title</label>
                <input
                  type="text"
                  placeholder="e.g. Annual Final Examinations 2026-27"
                  value={newExamTitle}
                  onChange={(e) => setNewExamTitle(e.target.value)}
                  required
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Exam Type</label>
                  <select
                    value={newExamType}
                    onChange={(e) => setNewExamType(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  >
                    <option value="Mid Term">Mid Term</option>
                    <option value="Final Term">Final Term</option>
                    <option value="Monthly Test">Monthly Test</option>
                    <option value="Pre-Board Exam">Pre-Board Exam</option>
                  </select>
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Target Class</label>
                  <select
                    value={newExamClass}
                    onChange={(e) => setNewExamClass(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  >
                    {['All Classes', 'Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10'].map(
                      (c) => (
                        <option key={c} value={c}>
                          {c}
                        </option>
                      )
                    )}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Start Date</label>
                  <input
                    type="date"
                    value={newExamStart}
                    onChange={(e) => setNewExamStart(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">End Date</label>
                  <input
                    type="date"
                    value={newExamEnd}
                    onChange={(e) => setNewExamEnd(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddExamModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl shadow-sm"
                >
                  Save Exam Schedule
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ENTER MARKS MODAL */}
      {showAddMarksModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Enter Examination Marks</h3>
              <button onClick={() => setShowAddMarksModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddMarksSubmit} className="space-y-4 mt-4 text-xs">
              <div>
                <label className="font-semibold text-slate-600">Select Student</label>
                <select
                  value={marksStudentId}
                  onChange={(e) => setMarksStudentId(e.target.value)}
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                >
                  {students.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name} (Roll #{s.rollNo} - {s.className})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Maximum Marks</label>
                  <input
                    type="number"
                    value={marksTotal}
                    onChange={(e) => setMarksTotal(Number(e.target.value))}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Obtained Marks</label>
                  <input
                    type="number"
                    placeholder="e.g. 88"
                    value={marksObtained}
                    onChange={(e) => setMarksObtained(e.target.value)}
                    required
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold text-blue-900 text-sm"
                  />
                </div>
              </div>

              <div>
                <label className="font-semibold text-slate-600">Teacher Evaluation Comments</label>
                <input
                  type="text"
                  value={marksRemarks}
                  onChange={(e) => setMarksRemarks(e.target.value)}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                />
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddMarksModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow-sm"
                >
                  Record & Calculate
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* RESULT CARD PRINT MODAL */}
      {selectedResultToPrint && (
        <ExamResultCardModal
          result={selectedResultToPrint}
          profile={profile}
          onClose={() => setSelectedResultToPrint(null)}
        />
      )}
    </div>
  );
};
