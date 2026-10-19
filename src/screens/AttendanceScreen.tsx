import React, { useState } from 'react';
import {
  AttendanceRecord,
  Student,
  Teacher,
  StaffMember,
  SchoolProfile,
  AppLanguage,
  UserRole,
  AttendanceStatus,
} from '../types';
import { t, generateWhatsAppAlert } from '../localization/translations';
import { QrCodeCanvas } from '../components/QrCodeCanvas';
import {
  CheckCircle2,
  XCircle,
  Clock,
  Calendar,
  Search,
  QrCode,
  Scan,
  Users,
  Send,
  Sparkles,
  Camera,
  Check,
  RefreshCw,
} from 'lucide-react';

interface AttendanceScreenProps {
  students: Student[];
  teachers: Teacher[];
  staff: StaffMember[];
  attendance: AttendanceRecord[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onMarkAttendance: (record: AttendanceRecord) => void;
  onBatchMarkAttendance: (records: AttendanceRecord[]) => void;
}

export const AttendanceScreen: React.FC<AttendanceScreenProps> = ({
  students,
  teachers,
  staff,
  attendance,
  profile,
  lang,
  onMarkAttendance,
  onBatchMarkAttendance,
}) => {
  const [activeTab, setActiveTab] = useState<'students' | 'teachers' | 'qr_scanner' | 'face_scanner'>('students');
  const [selectedClass, setSelectedClass] = useState('Class 5');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [search, setSearch] = useState('');

  // Scanner states
  const [qrInput, setQrInput] = useState('');
  const [scanFeedback, setScanFeedback] = useState<{
    success: boolean;
    name: string;
    id: string;
    status: AttendanceStatus;
    time: string;
  } | null>(null);

  // Face scanner state
  const [faceScanning, setFaceScanning] = useState(false);
  const [faceDetected, setFaceDetected] = useState(false);

  // Filter students for attendance sheet
  const classStudents = students.filter(
    (s) => (selectedClass === 'All' || s.className === selectedClass) &&
      (s.name.toLowerCase().includes(search.toLowerCase()) || s.rollNo.includes(search))
  );

  // Map today's attendance for fast lookup
  const attendanceMap = new Map<string, AttendanceRecord>();
  attendance
    .filter((a) => a.date === selectedDate)
    .forEach((a) => attendanceMap.set(a.personId, a));

  const handleMark = (
    personId: string,
    personType: 'STUDENT' | 'TEACHER' | 'STAFF',
    personName: string,
    className: string,
    section: string,
    status: AttendanceStatus,
    method: 'QR_SCAN' | 'FACE_SCAN' | 'MANUAL' = 'MANUAL'
  ) => {
    const record: AttendanceRecord = {
      id: Date.now() + Math.floor(Math.random() * 1000),
      personId,
      personType,
      personName,
      className,
      section,
      date: selectedDate,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      status,
      method,
    };
    onMarkAttendance(record);
  };

  const markAllClass = (status: AttendanceStatus) => {
    const newRecords: AttendanceRecord[] = classStudents.map((s, idx) => ({
      id: Date.now() + idx,
      personId: s.id,
      personType: 'STUDENT',
      personName: s.name,
      className: s.className,
      section: s.section,
      date: selectedDate,
      time: '08:00',
      status,
      method: 'MANUAL',
    }));
    onBatchMarkAttendance(newRecords);
  };

  const handleQrScanSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    const idToSearch = qrInput.trim().toUpperCase();
    if (!idToSearch) return;

    const student = students.find((s) => s.id === idToSearch || s.rollNo === idToSearch);
    const teacher = teachers.find((t) => t.id === idToSearch);

    if (student) {
      const now = new Date();
      const timeStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      const [curH, curM] = [now.getHours(), now.getMinutes()];
      const isLate = curH > 8 || (curH === 8 && curM > 10);
      const status: AttendanceStatus = isLate ? 'LATE' : 'PRESENT';

      handleMark(student.id, 'STUDENT', student.name, student.className, student.section, status, 'QR_SCAN');
      setScanFeedback({
        success: true,
        name: student.name,
        id: student.id,
        status,
        time: timeStr,
      });
      setQrInput('');
    } else if (teacher) {
      const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      handleMark(teacher.id, 'TEACHER', teacher.name, 'Staff', 'Faculty', 'PRESENT', 'QR_SCAN');
      setScanFeedback({
        success: true,
        name: teacher.name,
        id: teacher.id,
        status: 'PRESENT',
        time: timeStr,
      });
      setQrInput('');
    } else {
      setScanFeedback({
        success: false,
        name: 'Unrecognized ID Code',
        id: idToSearch,
        status: 'ABSENT',
        time: '--:--',
      });
    }
  };

  const triggerFaceRecognition = () => {
    setFaceScanning(true);
    setFaceDetected(false);
    setTimeout(() => {
      setFaceDetected(true);
      const randomStudent = students[Math.floor(Math.random() * students.length)];
      setTimeout(() => {
        setFaceScanning(false);
        const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        handleMark(randomStudent.id, 'STUDENT', randomStudent.name, randomStudent.className, randomStudent.section, 'PRESENT', 'FACE_SCAN');
        setScanFeedback({
          success: true,
          name: randomStudent.name,
          id: randomStudent.id,
          status: 'PRESENT',
          time: timeStr,
        });
      }, 900);
    }, 1200);
  };

  // Stats calculation
  const totalInClass = classStudents.length;
  let presentCount = 0;
  let absentCount = 0;
  let lateCount = 0;

  classStudents.forEach((s) => {
    const rec = attendanceMap.get(s.id);
    if (!rec || rec.status === 'PRESENT') presentCount++;
    else if (rec.status === 'ABSENT') absentCount++;
    else if (rec.status === 'LATE') lateCount++;
  });

  const attendancePercent = totalInClass > 0 ? Math.round((presentCount / totalInClass) * 100) : 100;

  return (
    <div className="space-y-6">
      {/* Tab Navigation */}
      <div className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm flex flex-wrap gap-2 justify-between items-center">
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setActiveTab('students')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              activeTab === 'students'
                ? 'bg-blue-900 text-white shadow-md shadow-blue-900/20'
                : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Users size={16} /> Student Attendance
          </button>
          <button
            onClick={() => setActiveTab('teachers')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              activeTab === 'teachers'
                ? 'bg-blue-900 text-white shadow-md shadow-blue-900/20'
                : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Users size={16} /> Teachers & Staff
          </button>
          <button
            onClick={() => setActiveTab('qr_scanner')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              activeTab === 'qr_scanner'
                ? 'bg-blue-900 text-white shadow-md shadow-blue-900/20'
                : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <QrCode size={16} /> QR Code Terminal
          </button>
          <button
            onClick={() => setActiveTab('face_scanner')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              activeTab === 'face_scanner'
                ? 'bg-blue-900 text-white shadow-md shadow-blue-900/20'
                : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Scan size={16} /> Face Recognition AI
          </button>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1 text-xs text-slate-500 font-medium">
            <Calendar size={14} />
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="bg-slate-50 border border-slate-300 rounded-xl px-2.5 py-1 text-xs font-semibold text-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-900"
            />
          </div>
        </div>
      </div>

      {/* QR SCANNER TERMINAL */}
      {activeTab === 'qr_scanner' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-slate-900 text-white p-8 rounded-3xl shadow-xl flex flex-col items-center justify-center relative overflow-hidden border border-slate-800">
            <div className="absolute top-4 left-4 flex items-center gap-2 text-xs font-mono text-emerald-400">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping" />
              CAMERA OPTICAL SCANNER ACTIVE
            </div>

            {/* Viewfinder Graphic */}
            <div className="w-64 h-64 border-2 border-emerald-400/80 rounded-2xl relative my-8 flex items-center justify-center bg-slate-950/60 backdrop-blur-sm shadow-inner">
              <div className="absolute top-0 left-0 w-8 h-8 border-t-4 border-l-4 border-emerald-400 -mt-1 -ml-1 rounded-tl-lg" />
              <div className="absolute top-0 right-0 w-8 h-8 border-t-4 border-r-4 border-emerald-400 -mt-1 -mr-1 rounded-tr-lg" />
              <div className="absolute bottom-0 left-0 w-8 h-8 border-b-4 border-l-4 border-emerald-400 -mb-1 -ml-1 rounded-bl-lg" />
              <div className="absolute bottom-0 right-0 w-8 h-8 border-b-4 border-r-4 border-emerald-400 -mb-1 -mr-1 rounded-br-lg" />
              
              {/* Laser Line */}
              <div className="w-full h-0.5 bg-gradient-to-r from-transparent via-emerald-400 to-transparent shadow-[0_0_12px_#34d399] animate-bounce" />

              <div className="text-center">
                <QrCode size={56} className="text-emerald-400/60 mx-auto mb-2" />
                <p className="text-xs text-slate-400 font-mono">Present Student ID Card</p>
              </div>
            </div>

            {/* Quick Demo Simulator Buttons */}
            <p className="text-xs text-slate-400 mb-2 font-mono">Or test scan a student card:</p>
            <div className="flex flex-wrap gap-2 justify-center max-w-md">
              {students.slice(0, 4).map((s) => (
                <button
                  key={s.id}
                  onClick={() => {
                    setQrInput(s.id);
                    setTimeout(() => {
                      const now = new Date();
                      const timeStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                      handleMark(s.id, 'STUDENT', s.name, s.className, s.section, 'PRESENT', 'QR_SCAN');
                      setScanFeedback({
                        success: true,
                        name: s.name,
                        id: s.id,
                        status: 'PRESENT',
                        time: timeStr,
                      });
                    }, 100);
                  }}
                  className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-emerald-300 text-xs rounded-xl font-mono border border-slate-700 transition-all"
                >
                  Scan {s.name.split(' ')[0]} ({s.id})
                </button>
              ))}
            </div>

            {/* Manual QR Input */}
            <form onSubmit={handleQrScanSubmit} className="w-full max-w-sm mt-6 flex gap-2">
              <input
                type="text"
                placeholder="Enter Student ID / Barcode..."
                value={qrInput}
                onChange={(e) => setQrInput(e.target.value)}
                className="flex-1 bg-slate-800 border border-slate-700 text-white rounded-xl px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-400"
              />
              <button
                type="submit"
                className="px-5 py-2 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-xl text-sm transition-colors"
              >
                Punch In
              </button>
            </form>
          </div>

          {/* Feedback & Recent Scans */}
          <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm flex flex-col justify-between">
            <div>
              <h3 className="text-lg font-bold text-slate-800 mb-4 flex items-center gap-2">
                <CheckCircle2 className="text-emerald-600" size={20} /> Real-Time Attendance Verification
              </h3>

              {scanFeedback ? (
                <div
                  className={`p-5 rounded-2xl border ${
                    scanFeedback.success
                      ? 'bg-emerald-50 border-emerald-200 text-emerald-950'
                      : 'bg-rose-50 border-rose-200 text-rose-950'
                  } animate-in fade-in`}
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <span
                        className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-bold uppercase mb-2 ${
                          scanFeedback.status === 'PRESENT'
                            ? 'bg-emerald-600 text-white'
                            : scanFeedback.status === 'LATE'
                            ? 'bg-amber-600 text-white'
                            : 'bg-rose-600 text-white'
                        }`}
                      >
                        {scanFeedback.status}
                      </span>
                      <h4 className="text-xl font-black">{scanFeedback.name}</h4>
                      <p className="text-xs text-slate-600 mt-1">ID Code: {scanFeedback.id} | Timestamp: {scanFeedback.time}</p>
                    </div>
                    <QrCodeCanvas content={scanFeedback.id} size={60} />
                  </div>

                  {/* Instant WhatsApp Action */}
                  {scanFeedback.success && (
                    <div className="mt-4 pt-3 border-t border-emerald-200 flex items-center justify-between">
                      <span className="text-xs text-emerald-800">Parent Alert Ready:</span>
                      <a
                        href={`https://wa.me/?text=${encodeURIComponent(
                          generateWhatsAppAlert(
                            scanFeedback.name,
                            scanFeedback.status === 'LATE' ? 'late' : 'present',
                            profile.name,
                            scanFeedback.time,
                            lang
                          )
                        )}`}
                        target="_blank"
                        rel="noreferrer"
                        className="px-3 py-1.5 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold rounded-xl flex items-center gap-1.5 transition-colors"
                      >
                        <Send size={14} /> Send WhatsApp Alert
                      </a>
                    </div>
                  )}
                </div>
              ) : (
                <div className="text-center py-12 text-slate-400 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                  <Scan size={48} className="mx-auto mb-2 opacity-40 text-blue-900" />
                  <p className="font-semibold text-slate-600">Terminal Ready</p>
                  <p className="text-xs">Scan any student QR card or use quick buttons above.</p>
                </div>
              )}
            </div>

            {/* Recent Scans list */}
            <div className="mt-6">
              <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Today's Live Punches</h4>
              <div className="space-y-2 max-h-56 overflow-y-auto">
                {attendance
                  .filter((a) => a.date === selectedDate)
                  .slice(-5)
                  .reverse()
                  .map((a) => (
                    <div key={a.id} className="flex items-center justify-between p-3 bg-slate-50 rounded-xl text-xs">
                      <div>
                        <span className="font-bold text-slate-800">{a.personName}</span>
                        <span className="text-slate-400 ml-2">({a.className})</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="text-slate-500 font-mono">{a.time}</span>
                        <span
                          className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                            a.status === 'PRESENT'
                              ? 'bg-emerald-100 text-emerald-700'
                              : a.status === 'LATE'
                              ? 'bg-amber-100 text-amber-700'
                              : 'bg-rose-100 text-rose-700'
                          }`}
                        >
                          {a.status}
                        </span>
                      </div>
                    </div>
                  ))}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* FACE RECOGNITION SIMULATOR */}
      {activeTab === 'face_scanner' && (
        <div className="bg-slate-950 text-white p-8 rounded-3xl shadow-xl border border-slate-800 flex flex-col items-center">
          <div className="max-w-xl w-full text-center space-y-4">
            <div className="flex items-center justify-center gap-2 text-cyan-400 font-mono text-sm">
              <Sparkles size={18} />
              <span>AI BIOMETRIC FACIAL ATTENDANCE SYSTEM</span>
            </div>
            <p className="text-xs text-slate-400">
              Integrated with front-entrance optical camera. Detects students walking through the main school gate and
              logs attendance with biometric verification.
            </p>

            {/* Video Viewport Mock */}
            <div className="w-full h-80 bg-slate-900 border-2 border-cyan-500/50 rounded-3xl relative overflow-hidden flex items-center justify-center shadow-2xl">
              <Camera size={64} className="text-slate-700" />

              {faceScanning && (
                <div className="absolute inset-0 flex flex-col items-center justify-center bg-cyan-950/30 backdrop-blur-[2px]">
                  {/* Bounding Box */}
                  <div className="w-48 h-56 border-2 border-dashed border-cyan-400 rounded-3xl relative animate-pulse flex items-center justify-center">
                    <span className="absolute -top-3 bg-cyan-400 text-slate-950 text-[10px] font-bold px-2 py-0.5 rounded-full">
                      MATCHING 99.4%
                    </span>
                    <div className="w-16 h-16 rounded-full border border-cyan-300 animate-spin" />
                  </div>
                  <p className="mt-4 font-mono text-xs text-cyan-300">Extracting facial landmarks...</p>
                </div>
              )}

              {faceDetected && (
                <div className="absolute inset-0 bg-emerald-950/40 flex flex-col items-center justify-center animate-in zoom-in-95">
                  <div className="w-16 h-16 bg-emerald-500 rounded-full flex items-center justify-center text-slate-950 shadow-lg">
                    <Check size={36} strokeWidth={3} />
                  </div>
                  <p className="mt-3 text-lg font-black text-emerald-300">Identity Verified & Punched</p>
                </div>
              )}
            </div>

            <button
              onClick={triggerFaceRecognition}
              disabled={faceScanning}
              className="px-8 py-3.5 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 text-slate-950 font-black rounded-2xl text-sm transition-all shadow-lg shadow-cyan-500/20 flex items-center justify-center gap-2 mx-auto disabled:opacity-50"
            >
              <Scan size={18} /> {faceScanning ? 'Recognizing Face...' : 'Simulate Facial Scan Punch'}
            </button>
          </div>
        </div>
      )}

      {/* STUDENT ATTENDANCE REGISTER */}
      {activeTab === 'students' && (
        <div className="space-y-6">
          {/* Controls Bar */}
          <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex flex-col md:flex-row gap-4 justify-between items-center">
            <div className="flex flex-wrap items-center gap-3 w-full md:w-auto">
              <span className="text-xs font-bold text-slate-500 uppercase">Class:</span>
              {['Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10'].map(
                (c) => (
                  <button
                    key={c}
                    onClick={() => setSelectedClass(c)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${
                      selectedClass === c
                        ? 'bg-blue-900 text-white shadow-sm'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {c}
                  </button>
                )
              )}
            </div>

            {/* Quick Bulk Actions */}
            <div className="flex items-center gap-2 w-full md:w-auto justify-end">
              <button
                onClick={() => markAllClass('PRESENT')}
                className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl flex items-center gap-1.5 transition-colors shadow-sm"
              >
                <CheckCircle2 size={15} /> Mark All Present
              </button>
              <button
                onClick={() => markAllClass('ABSENT')}
                className="px-4 py-2 bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-bold rounded-xl flex items-center gap-1.5 transition-colors"
              >
                <XCircle size={15} /> Clear
              </button>
            </div>
          </div>

          {/* Stats Bar */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
              <p className="text-xs text-slate-500 font-medium">Class Attendance Rate</p>
              <p className="text-2xl font-black text-blue-900 mt-1">{attendancePercent}%</p>
            </div>
            <div className="bg-emerald-50 p-4 rounded-2xl border border-emerald-200 shadow-sm">
              <p className="text-xs text-emerald-800 font-medium">Present Today</p>
              <p className="text-2xl font-black text-emerald-900 mt-1">{presentCount}</p>
            </div>
            <div className="bg-rose-50 p-4 rounded-2xl border border-rose-200 shadow-sm">
              <p className="text-xs text-rose-800 font-medium">Absent Today</p>
              <p className="text-2xl font-black text-rose-900 mt-1">{absentCount}</p>
            </div>
            <div className="bg-amber-50 p-4 rounded-2xl border border-amber-200 shadow-sm">
              <p className="text-xs text-amber-800 font-medium">Late Arrivals</p>
              <p className="text-2xl font-black text-amber-900 mt-1">{lateCount}</p>
            </div>
          </div>

          {/* Attendance Table */}
          <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-4 border-b border-slate-200 flex justify-between items-center">
              <h3 className="font-bold text-slate-800 text-sm">
                {selectedClass} — Attendance Sheet ({classStudents.length} Students Enrolled)
              </h3>
              <div className="w-64 relative">
                <Search size={14} className="absolute left-3 top-2.5 text-slate-400" />
                <input
                  type="text"
                  placeholder="Search student..."
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
                    <th className="p-3.5">Roll #</th>
                    <th className="p-3.5">Student Details</th>
                    <th className="p-3.5">Father's Name</th>
                    <th className="p-3.5">Punch Method</th>
                    <th className="p-3.5">Status Selector</th>
                    <th className="p-3.5 text-right">Instant Alert</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {classStudents.map((s) => {
                    const record = attendanceMap.get(s.id);
                    const currentStatus = record ? record.status : 'PRESENT';
                    const method = record?.method || 'MANUAL';

                    return (
                      <tr key={s.id} className="hover:bg-slate-50/60 transition-colors">
                        <td className="p-3.5 font-bold text-slate-900">#{s.rollNo}</td>
                        <td className="p-3.5">
                          <div className="font-bold text-slate-800">{s.name}</div>
                          <div className="text-[10px] text-slate-400 font-mono">{s.id}</div>
                        </td>
                        <td className="p-3.5 text-slate-600">{s.fatherName}</td>
                        <td className="p-3.5">
                          <span className="px-2 py-0.5 rounded-md bg-slate-100 text-slate-600 text-[10px] font-mono font-medium">
                            {method}
                          </span>
                        </td>
                        <td className="p-3.5">
                          <div className="flex items-center gap-1.5">
                            {(['PRESENT', 'ABSENT', 'LATE', 'LEAVE'] as AttendanceStatus[]).map((st) => (
                              <button
                                key={st}
                                onClick={() => handleMark(s.id, 'STUDENT', s.name, s.className, s.section, st)}
                                className={`px-2.5 py-1 rounded-lg text-[10px] font-bold transition-all ${
                                  currentStatus === st
                                    ? st === 'PRESENT'
                                      ? 'bg-emerald-600 text-white shadow-sm'
                                      : st === 'ABSENT'
                                      ? 'bg-rose-600 text-white shadow-sm'
                                      : st === 'LATE'
                                      ? 'bg-amber-600 text-white shadow-sm'
                                      : 'bg-blue-600 text-white shadow-sm'
                                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                                }`}
                              >
                                {st}
                              </button>
                            ))}
                          </div>
                        </td>
                        <td className="p-3.5 text-right">
                          <a
                            href={`https://wa.me/${s.whatsappNo.replace(/[^0-9]/g, '')}?text=${encodeURIComponent(
                              generateWhatsAppAlert(
                                s.name,
                                currentStatus === 'ABSENT' ? 'absent' : currentStatus === 'LATE' ? 'late' : 'present',
                                profile.name,
                                selectedDate,
                                lang
                              )
                            )}`}
                            target="_blank"
                            rel="noreferrer"
                            className="p-1.5 bg-emerald-50 text-emerald-700 hover:bg-emerald-100 rounded-lg inline-flex items-center gap-1 font-semibold text-[11px] transition-colors"
                          >
                            <Send size={12} /> WhatsApp
                          </a>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TEACHERS & STAFF ATTENDANCE */}
      {activeTab === 'teachers' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">
              Faculty & Staff Daily Attendance ({teachers.length + staff.length} Personnel)
            </h3>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Emp ID</th>
                  <th className="p-3.5">Staff Name</th>
                  <th className="p-3.5">Designation</th>
                  <th className="p-3.5">Status</th>
                  <th className="p-3.5">Punch In Time</th>
                  <th className="p-3.5 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {teachers.map((t) => {
                  const record = attendanceMap.get(t.id);
                  const currentStatus = record ? record.status : 'PRESENT';

                  return (
                    <tr key={t.id} className="hover:bg-slate-50/60">
                      <td className="p-3.5 font-mono font-bold text-blue-900">{t.id}</td>
                      <td className="p-3.5 font-bold text-slate-800">{t.name}</td>
                      <td className="p-3.5 text-slate-600">{t.designation}</td>
                      <td className="p-3.5">
                        <span
                          className={`px-2.5 py-1 rounded-full text-[10px] font-bold ${
                            currentStatus === 'PRESENT'
                              ? 'bg-emerald-100 text-emerald-700'
                              : 'bg-rose-100 text-rose-700'
                          }`}
                        >
                          {currentStatus}
                        </span>
                      </td>
                      <td className="p-3.5 font-mono text-slate-500">{record?.time || '07:50 AM'}</td>
                      <td className="p-3.5 text-right">
                        <div className="flex justify-end gap-1.5">
                          <button
                            onClick={() => handleMark(t.id, 'TEACHER', t.name, 'Faculty', 'Staff', 'PRESENT')}
                            className="px-2.5 py-1 rounded-lg text-[10px] font-bold bg-emerald-100 text-emerald-800 hover:bg-emerald-200"
                          >
                            Present
                          </button>
                          <button
                            onClick={() => handleMark(t.id, 'TEACHER', t.name, 'Faculty', 'Staff', 'ABSENT')}
                            className="px-2.5 py-1 rounded-lg text-[10px] font-bold bg-rose-100 text-rose-800 hover:bg-rose-200"
                          >
                            Absent
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
