import React, { useState } from 'react';
import { Teacher, StaffMember, SchoolProfile, AppLanguage, UserRole } from '../types';
import { QrCodeCanvas } from '../components/QrCodeCanvas';
import { Search, UserPlus, GraduationCap, X, Printer, Phone } from 'lucide-react';

interface TeachersScreenProps {
  teachers: Teacher[];
  staff: StaffMember[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onAddTeacher: (teacher: Teacher) => void;
}

export const TeachersScreen: React.FC<TeachersScreenProps> = ({
  teachers,
  staff,
  profile,
  onAddTeacher,
}) => {
  const [tab, setTab] = useState<'faculty' | 'staff'>('faculty');
  const [search, setSearch] = useState('');
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedTeacher, setSelectedTeacher] = useState<Teacher | null>(null);

  const filteredTeachers = teachers.filter(
    (t) =>
      t.name.toLowerCase().includes(search.toLowerCase()) ||
      t.designation.toLowerCase().includes(search.toLowerCase()) ||
      t.subjectsAssigned.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Top Bar */}
      <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex flex-col sm:flex-row gap-4 justify-between items-center">
        <div className="flex gap-2">
          <button
            onClick={() => setTab('faculty')}
            className={`px-4 py-2 text-xs font-bold rounded-xl transition-colors ${
              tab === 'faculty' ? 'bg-purple-900 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            Faculty ({teachers.length})
          </button>
          <button
            onClick={() => setTab('staff')}
            className={`px-4 py-2 text-xs font-bold rounded-xl transition-colors ${
              tab === 'staff' ? 'bg-purple-900 text-white' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            Administrative Staff ({staff.length})
          </button>
        </div>

        <div className="flex w-full sm:w-auto items-center gap-3">
          <div className="relative flex-1 sm:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
            <input
              type="text"
              placeholder="Search faculty..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-purple-900"
            />
          </div>

          <button
            onClick={() => setShowAddModal(true)}
            className="px-4 py-2 bg-purple-900 hover:bg-purple-800 text-white text-xs font-bold rounded-xl flex items-center gap-1.5 shrink-0"
          >
            <UserPlus size={16} />
            <span>Appoint Faculty</span>
          </button>
        </div>
      </div>

      {/* Teachers / Staff Cards */}
      {tab === 'faculty' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredTeachers.map((t) => (
            <div
              key={t.id}
              className="bg-white p-5 rounded-3xl border border-slate-200 hover:border-purple-300 shadow-sm transition-all flex flex-col justify-between"
            >
              <div>
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-2xl bg-purple-100 text-purple-900 flex items-center justify-center font-bold text-lg">
                      {t.name.charAt(0)}
                    </div>
                    <div>
                      <h4 className="font-bold text-slate-900 text-sm">{t.name}</h4>
                      <p className="text-xs text-purple-700 font-medium">{t.designation}</p>
                    </div>
                  </div>
                  <span className="px-2 py-0.5 bg-slate-100 text-slate-600 text-xs font-mono font-bold rounded-md">
                    {t.id}
                  </span>
                </div>

                <div className="my-3 space-y-1 text-xs text-slate-600 pt-3 border-t border-slate-100">
                  <p><span className="text-slate-400">Subjects:</span> <strong>{t.subjectsAssigned}</strong></p>
                  <p><span className="text-slate-400">Classes:</span> {t.classesAssigned}</p>
                  <p><span className="text-slate-400">Experience:</span> {t.experienceYears} Years • {t.qualification}</p>
                  <p><span className="text-slate-400">Salary:</span> <strong>{profile.currencySymbol} {t.basicSalary.toLocaleString()}</strong></p>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-100 flex justify-between items-center">
                <span className="text-[11px] text-slate-400 flex items-center gap-1">
                  <Phone size={12} /> {t.phone}
                </span>
                <button
                  onClick={() => setSelectedTeacher(t)}
                  className="px-3 py-1 bg-purple-50 hover:bg-purple-100 text-purple-900 font-bold text-xs rounded-lg"
                >
                  Badge & QR
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {staff.map((m) => (
            <div key={m.id} className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex items-center justify-between">
              <div>
                <h4 className="font-bold text-slate-900 text-sm">{m.name}</h4>
                <p className="text-xs text-slate-500">{m.designation}</p>
                <p className="text-xs text-slate-400 mt-1">Tel: {m.phone}</p>
              </div>
              <div className="text-right">
                <span className="text-xs font-bold text-slate-700 font-mono">{m.id}</span>
                <p className="text-sm font-bold text-emerald-700 mt-1">{profile.currencySymbol} {m.basicSalary.toLocaleString()}</p>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Teacher Profile Modal */}
      {selectedTeacher && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 text-center">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800">Faculty Badge & Attendance ID</h3>
              <button onClick={() => setSelectedTeacher(null)} className="p-1 hover:bg-slate-100 rounded-lg text-slate-500">
                <X size={18} />
              </button>
            </div>

            <div className="py-6 flex flex-col items-center">
              <div className="w-16 h-16 rounded-full bg-purple-900 text-white flex items-center justify-center font-bold text-2xl mb-2">
                {selectedTeacher.name.charAt(0)}
              </div>
              <h4 className="font-bold text-slate-900 text-base">{selectedTeacher.name}</h4>
              <p className="text-xs text-purple-700 font-semibold">{selectedTeacher.designation}</p>
              <p className="text-xs text-slate-400 mt-0.5">ID: {selectedTeacher.id}</p>

              <div className="mt-4">
                <QrCodeCanvas content={selectedTeacher.id} size={110} primaryColor="#581C87" />
              </div>

              <div className="text-xs text-left bg-slate-50 p-4 rounded-xl border border-slate-100 w-full mt-4 space-y-1">
                <p><span className="text-slate-400">CNIC:</span> {selectedTeacher.cnic}</p>
                <p><span className="text-slate-400">Qualification:</span> {selectedTeacher.qualification}</p>
                <p><span className="text-slate-400">Bank:</span> {selectedTeacher.bankDetails}</p>
                <p><span className="text-slate-400">Joined:</span> {selectedTeacher.joiningDate}</p>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-3 border-t border-slate-100">
              <button onClick={() => setSelectedTeacher(null)} className="px-4 py-2 text-xs bg-slate-100 rounded-lg">Close</button>
              <button onClick={() => window.print()} className="px-4 py-2 text-xs bg-purple-900 text-white font-bold rounded-lg flex items-center gap-1.5">
                <Printer size={14} /> Print Faculty Badge
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Add Teacher Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-lg w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-extrabold text-slate-800">Appoint Faculty Member</h3>
              <button onClick={() => setShowAddModal(false)} className="p-1 hover:bg-slate-100 rounded-lg text-slate-500">
                <X size={18} />
              </button>
            </div>
            <form
              onSubmit={(e) => {
                e.preventDefault();
                const form = e.target as any;
                onAddTeacher({
                  id: `TCH-${String(teachers.length + 1).padStart(3, '0')}`,
                  name: form.name.value,
                  fatherOrHusbandName: form.father.value,
                  cnic: form.cnic.value,
                  dob: '1990-01-01',
                  gender: 'Male',
                  phone: form.phone.value,
                  whatsappNo: form.phone.value,
                  address: 'Hyderabad',
                  qualification: form.qualification.value,
                  experienceYears: Number(form.exp.value) || 5,
                  joiningDate: new Date().toISOString().split('T')[0],
                  designation: form.designation.value,
                  subjectsAssigned: form.subjects.value,
                  classesAssigned: 'Class 5, Class 10',
                  basicSalary: Number(form.salary.value) || 50000,
                  bankDetails: 'HBL Account',
                  active: true,
                });
                setShowAddModal(false);
              }}
              className="py-4 space-y-3"
            >
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Faculty Full Name *</label>
                <input name="name" required className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Father/Husband Name</label>
                  <input name="father" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">CNIC</label>
                  <input name="cnic" defaultValue="41303-" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Designation</label>
                  <input name="designation" defaultValue="Subject Specialist" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Qualification</label>
                  <input name="qualification" defaultValue="M.Sc" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Subjects Assigned</label>
                <input name="subjects" defaultValue="Mathematics, Physics" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Phone</label>
                  <input name="phone" defaultValue="+92 300 " className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-700 mb-1">Basic Salary (PKR)</label>
                  <input name="salary" defaultValue="55000" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs" />
                </div>
              </div>
              <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
                <button type="button" onClick={() => setShowAddModal(false)} className="px-4 py-2 text-xs bg-slate-100 rounded-xl">Cancel</button>
                <button type="submit" className="px-4 py-2 text-xs font-bold text-white bg-purple-900 rounded-xl">Save Faculty</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
