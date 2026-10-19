import React, { useState } from 'react';
import { Student, SchoolProfile, AppLanguage, UserRole } from '../types';
import { t, generateWhatsAppAlert } from '../localization/translations';
import { QrCodeCanvas } from '../components/QrCodeCanvas';
import { StudentIdCardModal, CertificateModal } from '../components/PrintModals';
import {
  Search,
  UserPlus,
  Send,
  Printer,
  Award,
  X,
  Phone,
  MapPin,
  Calendar,
  AlertCircle,
  Eye,
} from 'lucide-react';

interface StudentsScreenProps {
  students: Student[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onAddStudent: (student: Student) => void;
}

export const StudentsScreen: React.FC<StudentsScreenProps> = ({
  students,
  profile,
  lang,
  role,
  onAddStudent,
}) => {
  const [search, setSearch] = useState('');
  const [selectedClass, setSelectedClass] = useState('All');
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showIdCard, setShowIdCard] = useState<Student | null>(null);
  const [showCert, setShowCert] = useState<{ student: Student; type: string } | null>(null);

  const classes = ['All', ...Array.from(new Set(students.map((s) => s.className)))];

  const filteredStudents = students.filter((s) => {
    const matchesSearch =
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.id.toLowerCase().includes(search.toLowerCase()) ||
      s.fatherName.toLowerCase().includes(search.toLowerCase()) ||
      s.rollNo.includes(search);
    const matchesClass = selectedClass === 'All' || s.className === selectedClass;
    return matchesSearch && matchesClass;
  });

  const handleSendWhatsApp = (student: Student) => {
    const text = generateWhatsAppAlert(
      student.name,
      'present',
      profile.name,
      '',
      lang
    );
    const cleanPhone = student.whatsappNo.replace(/[^0-9]/g, '');
    window.open(`https://api.whatsapp.com/send?phone=${cleanPhone}&text=${encodeURIComponent(text)}`, '_blank');
  };

  return (
    <div className="space-y-6">
      {/* Top Filter and Action Bar */}
      <div className="bg-white p-5 rounded-3xl border border-slate-200 shadow-sm flex flex-col md:flex-row gap-4 justify-between items-center">
        <div className="flex-1 w-full flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input
              type="text"
              placeholder={t('search_placeholder', lang)}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-2xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-900 focus:bg-white transition-all"
            />
          </div>

          <div className="flex gap-2 overflow-x-auto pb-1 sm:pb-0">
            {classes.slice(0, 5).map((cls) => (
              <button
                key={cls}
                onClick={() => setSelectedClass(cls)}
                className={`px-3.5 py-2 text-xs font-bold rounded-xl whitespace-nowrap transition-colors ${
                  selectedClass === cls
                    ? 'bg-blue-900 text-white'
                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                }`}
              >
                {cls}
              </button>
            ))}
          </div>
        </div>

        {role !== 'Parent' && (
          <button
            onClick={() => setShowAddModal(true)}
            className="w-full sm:w-auto px-5 py-2.5 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl flex items-center justify-center gap-2 shadow-sm transition-all text-sm shrink-0"
          >
            <UserPlus size={18} />
            <span>Enroll Student</span>
          </button>
        )}
      </div>

      {/* Student Cards List */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        {filteredStudents.map((st) => (
          <div
            key={st.id}
            className="bg-white p-5 rounded-3xl border border-slate-200 hover:border-blue-400 shadow-sm hover:shadow-md transition-all flex flex-col justify-between"
          >
            <div>
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-2xl bg-blue-100 text-blue-950 flex items-center justify-center font-bold text-lg">
                    {st.name.charAt(0)}
                  </div>
                  <div>
                    <h4 className="font-extrabold text-slate-900 text-base">{st.name}</h4>
                    <p className="text-xs text-slate-500">Father: {st.fatherName}</p>
                  </div>
                </div>
                <span className="px-2.5 py-1 bg-slate-100 text-slate-700 text-xs font-bold rounded-lg font-mono">
                  {st.id}
                </span>
              </div>

              <div className="grid grid-cols-2 gap-2 text-xs my-4 pt-3 border-t border-slate-100">
                <div>
                  <span className="text-slate-400">Class:</span>
                  <p className="font-bold text-slate-800">{st.className} - {st.section}</p>
                </div>
                <div>
                  <span className="text-slate-400">Roll Number:</span>
                  <p className="font-bold text-slate-800">#{st.rollNo}</p>
                </div>
                <div>
                  <span className="text-slate-400">Contact:</span>
                  <p className="font-medium text-slate-700">{st.fatherPhone}</p>
                </div>
                <div>
                  <span className="text-slate-400">Blood Group:</span>
                  <p className="font-medium text-rose-600 font-bold">{st.bloodGroup}</p>
                </div>
              </div>
            </div>

            <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
              <button
                onClick={() => setSelectedStudent(st)}
                className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl flex items-center gap-1.5 transition-colors"
              >
                <Eye size={14} />
                Profile
              </button>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => setShowIdCard(st)}
                  className="p-2 hover:bg-blue-50 text-blue-900 rounded-xl"
                  title="Print ID Card"
                >
                  <Printer size={16} />
                </button>
                <button
                  onClick={() => setShowCert({ student: st, type: 'Bonafide Certificate' })}
                  className="p-2 hover:bg-amber-50 text-amber-700 rounded-xl"
                  title="Generate Certificate"
                >
                  <Award size={16} />
                </button>
                <button
                  onClick={() => handleSendWhatsApp(st)}
                  className="p-2 hover:bg-emerald-50 text-emerald-600 rounded-xl"
                  title="Send WhatsApp Notice"
                >
                  <Send size={16} />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Student Detailed Profile Modal */}
      {selectedStudent && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-xl w-full p-6">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-extrabold text-lg text-slate-800">Student Comprehensive Record</h3>
              <button onClick={() => setSelectedStudent(null)} className="p-1.5 hover:bg-slate-100 rounded-xl text-slate-500">
                <X size={20} />
              </button>
            </div>

            <div className="py-4 space-y-4">
              <div className="flex items-center gap-4">
                <div className="w-16 h-16 rounded-2xl bg-blue-900 text-white flex items-center justify-center font-bold text-2xl">
                  {selectedStudent.name.charAt(0)}
                </div>
                <div className="flex-1">
                  <h4 className="text-xl font-black text-slate-900">{selectedStudent.name}</h4>
                  <p className="text-xs text-slate-500">ID: {selectedStudent.id} • Roll: {selectedStudent.rollNo}</p>
                  <span className="inline-block px-2.5 py-0.5 bg-blue-100 text-blue-900 rounded-md text-xs font-bold mt-1">
                    {selectedStudent.className} - {selectedStudent.section} ({selectedStudent.shift})
                  </span>
                </div>
                <QrCodeCanvas content={selectedStudent.id} size={65} />
              </div>

              <div className="grid grid-cols-2 gap-3 text-xs bg-slate-50 p-4 rounded-2xl border border-slate-100">
                <div><span className="text-slate-400">Father's Name:</span> <strong className="text-slate-800">{selectedStudent.fatherName}</strong></div>
                <div><span className="text-slate-400">Date of Birth:</span> <strong className="text-slate-800">{selectedStudent.dob}</strong></div>
                <div><span className="text-slate-400">Gender:</span> <strong className="text-slate-800">{selectedStudent.gender}</strong></div>
                <div><span className="text-slate-400">B-Form / NADRA:</span> <strong className="text-slate-800 font-mono">{selectedStudent.bForm}</strong></div>
                <div><span className="text-slate-400">Phone:</span> <strong className="text-slate-800">{selectedStudent.fatherPhone}</strong></div>
                <div><span className="text-slate-400">WhatsApp:</span> <strong className="text-slate-800">{selectedStudent.whatsappNo}</strong></div>
                <div><span className="text-slate-400">Address:</span> <strong className="text-slate-800">{selectedStudent.address}</strong></div>
                <div><span className="text-slate-400">Transport:</span> <strong className="text-slate-800">{selectedStudent.transportRoute}</strong></div>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => {
                    setShowIdCard(selectedStudent);
                    setSelectedStudent(null);
                  }}
                  className="flex-1 py-2.5 bg-blue-900 hover:bg-blue-800 text-white font-bold text-xs rounded-xl flex items-center justify-center gap-2"
                >
                  <Printer size={16} /> Print ID Card
                </button>
                <button
                  onClick={() => {
                    setShowCert({ student: selectedStudent, type: 'Bonafide Certificate' });
                    setSelectedStudent(null);
                  }}
                  className="flex-1 py-2.5 bg-amber-700 hover:bg-amber-800 text-white font-bold text-xs rounded-xl flex items-center justify-center gap-2"
                >
                  <Award size={16} /> Bonafide Cert
                </button>
                <button
                  onClick={() => handleSendWhatsApp(selectedStudent)}
                  className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs rounded-xl flex items-center gap-1.5"
                >
                  <Send size={16} /> WhatsApp
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Enroll Student Modal */}
      {showAddModal && (
        <AddStudentModal
          nextId={`STU-2026-${String(students.length + 1).padStart(3, '0')}`}
          onClose={() => setShowAddModal(false)}
          onSave={(newStudent) => {
            onAddStudent(newStudent);
            setShowAddModal(false);
          }}
        />
      )}

      {/* ID Card Modal */}
      {showIdCard && (
        <StudentIdCardModal
          student={showIdCard}
          profile={profile}
          onClose={() => setShowIdCard(null)}
        />
      )}

      {/* Certificate Modal */}
      {showCert && (
        <CertificateModal
          student={showCert.student}
          type={showCert.type}
          profile={profile}
          onClose={() => setShowCert(null)}
        />
      )}
    </div>
  );
};

const AddStudentModal: React.FC<{
  nextId: string;
  onClose: () => void;
  onSave: (student: Student) => void;
}> = ({ nextId, onClose, onSave }) => {
  const [name, setName] = useState('');
  const [fatherName, setFatherName] = useState('');
  const [className, setClassName] = useState('Class 5');
  const [section, setSection] = useState('A');
  const [rollNo, setRollNo] = useState('15');
  const [phone, setPhone] = useState('+92 300 ');
  const [bForm, setBForm] = useState('');
  const [dob, setDob] = useState('2015-06-15');
  const [address, setAddress] = useState('Hyderabad, Sindh');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !fatherName) return;
    onSave({
      id: nextId,
      admissionNo: `ADM-${Math.floor(1000 + Math.random() * 9000)}`,
      rollNo,
      name,
      fatherName,
      dob,
      gender: 'Male',
      bForm,
      address,
      city: 'Hyderabad',
      fatherPhone: phone,
      whatsappNo: phone,
      emergencyContact: phone,
      bloodGroup: 'B+',
      className,
      section,
      shift: 'Morning',
      transportRoute: 'Route 1',
      admissionDate: new Date().toISOString().split('T')[0],
      remarks: 'Regular Student',
      active: true,
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-3xl shadow-2xl max-w-lg w-full p-6">
        <div className="flex justify-between items-center pb-3 border-b border-slate-200">
          <h3 className="font-extrabold text-lg text-slate-800">Enroll New Student</h3>
          <button onClick={onClose} className="p-1.5 hover:bg-slate-100 rounded-xl text-slate-500">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="py-4 space-y-3">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Student Full Name *</label>
            <input
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-900"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Father's Name *</label>
            <input
              type="text"
              required
              value={fatherName}
              onChange={(e) => setFatherName(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-900"
            />
          </div>

          <div className="grid grid-cols-3 gap-2">
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Class</label>
              <input
                type="text"
                value={className}
                onChange={(e) => setClassName(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Section</label>
              <input
                type="text"
                value={section}
                onChange={(e) => setSection(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Roll #</label>
              <input
                type="text"
                value={rollNo}
                onChange={(e) => setRollNo(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">Phone</label>
              <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">B-Form #</label>
              <input
                type="text"
                value={bForm}
                onChange={(e) => setBForm(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Address</label>
            <input
              type="text"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              className="w-full px-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm"
            />
          </div>

          <div className="pt-3 flex justify-end gap-2 border-t border-slate-100">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 text-sm font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl"
            >
              Save Student
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
