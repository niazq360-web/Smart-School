import React, { useState } from 'react';
import { TimetableSlot, Teacher, SchoolProfile, AppLanguage, UserRole } from '../types';
import { Clock, PlusCircle, Printer, X, Calendar, BookOpen, Users, MapPin } from 'lucide-react';

interface TimetableScreenProps {
  slots: TimetableSlot[];
  teachers: Teacher[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onAddSlot: (slot: TimetableSlot) => void;
}

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
const CLASSES = ['Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10'];

export const TimetableScreen: React.FC<TimetableScreenProps> = ({
  slots,
  teachers,
  profile,
  onAddSlot,
}) => {
  const [viewMode, setViewMode] = useState<'class' | 'teacher'>('class');
  const [selectedClass, setSelectedClass] = useState('Class 5');
  const [selectedTeacherName, setSelectedTeacherName] = useState(teachers[0]?.name || '');
  const [showAddModal, setShowAddModal] = useState(false);

  // Form state
  const [slotDay, setSlotDay] = useState('Monday');
  const [slotPeriod, setSlotPeriod] = useState(1);
  const [slotSubject, setSlotSubject] = useState('Mathematics');
  const [slotTeacher, setSlotTeacher] = useState(teachers[0]?.name || 'Sir Tariq Memon');
  const [slotRoom, setSlotRoom] = useState('Room 105');
  const [slotStart, setSlotStart] = useState('08:00');
  const [slotEnd, setSlotEnd] = useState('08:45');

  const filteredSlots = slots.filter((s) => {
    if (viewMode === 'class') {
      return s.className === selectedClass;
    } else {
      return s.teacherName.toLowerCase().includes(selectedTeacherName.toLowerCase());
    }
  });

  const handleAddSlotSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const newSlot: TimetableSlot = {
      id: Date.now(),
      dayOfWeek: slotDay,
      periodNo: Number(slotPeriod),
      startTime: slotStart,
      endTime: slotEnd,
      className: selectedClass,
      section: 'A',
      subjectName: slotSubject,
      teacherName: slotTeacher,
      room: slotRoom,
    };

    onAddSlot(newSlot);
    setShowAddModal(false);
  };

  return (
    <div className="space-y-6">
      {/* Controls & Mode Header */}
      <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex flex-col md:flex-row gap-4 justify-between items-center">
        <div className="flex items-center gap-3">
          <div className="flex bg-slate-100 p-1 rounded-2xl">
            <button
              onClick={() => setViewMode('class')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                viewMode === 'class' ? 'bg-blue-900 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Class Schedule
            </button>
            <button
              onClick={() => setViewMode('teacher')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                viewMode === 'teacher' ? 'bg-blue-900 text-white shadow-sm' : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Teacher Workload
            </button>
          </div>

          {viewMode === 'class' ? (
            <select
              value={selectedClass}
              onChange={(e) => setSelectedClass(e.target.value)}
              className="px-3 py-2 text-xs bg-slate-50 border border-slate-300 rounded-xl font-bold text-slate-700"
            >
              {CLASSES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </select>
          ) : (
            <select
              value={selectedTeacherName}
              onChange={(e) => setSelectedTeacherName(e.target.value)}
              className="px-3 py-2 text-xs bg-slate-50 border border-slate-300 rounded-xl font-bold text-slate-700"
            >
              {teachers.map((t) => (
                <option key={t.id} value={t.name}>
                  {t.name} ({t.designation})
                </option>
              ))}
            </select>
          )}
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => window.print()}
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-2xl flex items-center gap-1.5 transition-colors"
          >
            <Printer size={15} /> Print Schedule
          </button>
          <button
            onClick={() => setShowAddModal(true)}
            className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-1.5 shadow-sm transition-colors"
          >
            <PlusCircle size={15} /> Assign Period
          </button>
        </div>
      </div>

      {/* Weekly Grid */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm p-6 overflow-x-auto">
        <h3 className="font-extrabold text-slate-800 text-base mb-4 flex items-center gap-2">
          <Calendar className="text-blue-900" size={18} />
          {viewMode === 'class' ? `${selectedClass} — Master Weekly Academic Schedule` : `${selectedTeacherName} — Assigned Lecture Slots`}
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-6 gap-4 min-w-[700px]">
          {DAYS.map((day) => {
            const daySlots = filteredSlots
              .filter((s) => s.dayOfWeek === day)
              .sort((a, b) => a.periodNo - b.periodNo);

            return (
              <div key={day} className="bg-slate-50 border border-slate-200 rounded-2xl p-3 flex flex-col">
                <div className="pb-2 mb-3 border-b border-slate-200 flex justify-between items-center">
                  <span className="font-black text-xs text-blue-950 uppercase">{day}</span>
                  <span className="text-[10px] bg-blue-100 text-blue-900 font-bold px-1.5 py-0.5 rounded-md">
                    {daySlots.length} Slots
                  </span>
                </div>

                <div className="space-y-2.5 flex-1">
                  {daySlots.length > 0 ? (
                    daySlots.map((slot) => (
                      <div
                        key={slot.id}
                        className="bg-white border border-slate-200 rounded-xl p-2.5 shadow-sm hover:border-blue-400 transition-all text-xs"
                      >
                        <div className="flex justify-between items-start">
                          <span className="font-extrabold text-blue-900">{slot.subjectName}</span>
                          <span className="text-[9px] font-mono bg-slate-100 px-1 py-0.5 rounded text-slate-500 font-bold">
                            P{slot.periodNo}
                          </span>
                        </div>
                        <div className="flex items-center gap-1 text-[10px] text-slate-500 mt-1">
                          <Clock size={11} /> {slot.startTime} - {slot.endTime}
                        </div>
                        <div className="flex items-center gap-1 text-[11px] font-medium text-slate-700 mt-1">
                          <Users size={12} className="text-slate-400" />
                          <span>{viewMode === 'class' ? slot.teacherName : slot.className}</span>
                        </div>
                        <div className="flex items-center gap-1 text-[10px] text-slate-400 mt-1">
                          <MapPin size={10} /> {slot.room}
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="h-28 flex items-center justify-center text-slate-400 text-[11px] italic">
                      No lectures scheduled
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* ADD PERIOD SLOT MODAL */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Assign Timetable Period</h3>
              <button onClick={() => setShowAddModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddSlotSubmit} className="space-y-4 mt-4 text-xs">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Day of Week</label>
                  <select
                    value={slotDay}
                    onChange={(e) => setSlotDay(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  >
                    {DAYS.map((d) => (
                      <option key={d} value={d}>
                        {d}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Period Number</label>
                  <select
                    value={slotPeriod}
                    onChange={(e) => setSlotPeriod(Number(e.target.value))}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  >
                    {[1, 2, 3, 4, 5, 6, 7].map((p) => (
                      <option key={p} value={p}>
                        Period {p}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="font-semibold text-slate-600">Subject Name</label>
                <input
                  type="text"
                  placeholder="e.g. Science / Biology / Computer Science"
                  value={slotSubject}
                  onChange={(e) => setSlotSubject(e.target.value)}
                  required
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>

              <div>
                <label className="font-semibold text-slate-600">Assigned Teacher</label>
                <select
                  value={slotTeacher}
                  onChange={(e) => setSlotTeacher(e.target.value)}
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                >
                  {teachers.map((t) => (
                    <option key={t.id} value={t.name}>
                      {t.name} ({t.designation})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-3 gap-2">
                <div>
                  <label className="font-semibold text-slate-600">Room</label>
                  <input
                    type="text"
                    value={slotRoom}
                    onChange={(e) => setSlotRoom(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Start</label>
                  <input
                    type="text"
                    value={slotStart}
                    onChange={(e) => setSlotStart(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">End</label>
                  <input
                    type="text"
                    value={slotEnd}
                    onChange={(e) => setSlotEnd(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl shadow-sm"
                >
                  Save Timetable Slot
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
