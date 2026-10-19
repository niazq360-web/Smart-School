import React, { useState } from 'react';
import { SchoolProfile, AppLanguage, UserRole, AuditLog } from '../types';
import {
  Settings,
  Building,
  Globe,
  ShieldCheck,
  Database,
  History,
  Check,
  Download,
  Upload,
  RefreshCw,
  Save,
} from 'lucide-react';

interface SettingsScreenProps {
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  auditLogs: AuditLog[];
  onUpdateProfile: (newProfile: SchoolProfile) => void;
  onLanguageChange: (lang: AppLanguage) => void;
  onRoleChange: (role: UserRole) => void;
  onExportAllData: () => void;
  onResetData: () => void;
}

export const SettingsScreen: React.FC<SettingsScreenProps> = ({
  profile,
  lang,
  role,
  auditLogs,
  onUpdateProfile,
  onLanguageChange,
  onRoleChange,
  onExportAllData,
  onResetData,
}) => {
  const [tab, setTab] = useState<'profile' | 'language' | 'roles' | 'backup' | 'audit'>('profile');
  const [formData, setFormData] = useState<SchoolProfile>({ ...profile });
  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onUpdateProfile(formData);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 2500);
  };

  return (
    <div className="space-y-6">
      {/* Settings Navigation Bar */}
      <div className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm flex flex-wrap gap-2 justify-between items-center">
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setTab('profile')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'profile' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Building size={16} /> School Profile
          </button>
          <button
            onClick={() => setTab('language')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'language' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Globe size={16} /> Multilingual Settings
          </button>
          <button
            onClick={() => setTab('roles')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'roles' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <ShieldCheck size={16} /> User Roles & Access
          </button>
          <button
            onClick={() => setTab('backup')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'backup' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Database size={16} /> Data Backup & Restore
          </button>
          <button
            onClick={() => setTab('audit')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'audit' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <History size={16} /> Security Audit Trail
          </button>
        </div>
      </div>

      {/* TAB 1: SCHOOL PROFILE */}
      {tab === 'profile' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
          <div className="flex justify-between items-center pb-4 mb-6 border-b border-slate-200">
            <div>
              <h3 className="text-lg font-bold text-slate-800">School Institutional Profile</h3>
              <p className="text-xs text-slate-500">
                Official institution details used on fee receipts, student cards, report cards, and certificates
              </p>
            </div>
            {savedSuccess && (
              <span className="px-3 py-1 bg-emerald-100 text-emerald-800 text-xs font-bold rounded-xl flex items-center gap-1">
                <Check size={14} /> Profile Saved Successfully
              </span>
            )}
          </div>

          <form onSubmit={handleSubmit} className="space-y-5 text-xs">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="font-semibold text-slate-700">Official School Name</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold text-slate-900 text-sm"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">Registration / Affiliation Number</label>
                <input
                  type="text"
                  value={formData.regNumber}
                  onChange={(e) => setFormData({ ...formData, regNumber: e.target.value })}
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-mono"
                />
              </div>
            </div>

            <div>
              <label className="font-semibold text-slate-700">Campus Physical Address</label>
              <input
                type="text"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-medium"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="font-semibold text-slate-700">Official Landline Phone</label>
                <input
                  type="text"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-mono"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">School WhatsApp Broadcast Line</label>
                <input
                  type="text"
                  value={formData.whatsapp}
                  onChange={(e) => setFormData({ ...formData, whatsapp: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-mono text-emerald-700 font-bold"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">Official Email</label>
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="font-semibold text-slate-700">Principal / Headmaster Name</label>
                <input
                  type="text"
                  value={formData.principalName}
                  onChange={(e) => setFormData({ ...formData, principalName: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">Active Academic Year</label>
                <input
                  type="text"
                  value={formData.academicYear}
                  onChange={(e) => setFormData({ ...formData, academicYear: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold text-blue-900"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">Billing Currency & Symbol</label>
                <div className="flex gap-2 mt-1">
                  <input
                    type="text"
                    value={formData.currency}
                    onChange={(e) => setFormData({ ...formData, currency: e.target.value })}
                    className="w-1/2 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                  />
                  <input
                    type="text"
                    value={formData.currencySymbol}
                    onChange={(e) => setFormData({ ...formData, currencySymbol: e.target.value })}
                    className="w-1/2 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold text-emerald-700"
                  />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
              <div>
                <label className="font-semibold text-slate-700">School Morning Start</label>
                <input
                  type="time"
                  value={formData.schoolStartTime}
                  onChange={(e) => setFormData({ ...formData, schoolStartTime: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-mono"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">School Dismissal Time</label>
                <input
                  type="time"
                  value={formData.schoolEndTime}
                  onChange={(e) => setFormData({ ...formData, schoolEndTime: e.target.value })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-mono"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-700">Grace Period (Minutes)</label>
                <input
                  type="number"
                  value={formData.gracePeriodMinutes}
                  onChange={(e) => setFormData({ ...formData, gracePeriodMinutes: Number(e.target.value) })}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>
            </div>

            <div className="flex justify-end pt-4 border-t border-slate-200">
              <button
                type="submit"
                className="px-6 py-2.5 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl text-xs shadow-md flex items-center gap-2 transition-colors"
              >
                <Save size={16} /> Save Profile Changes
              </button>
            </div>
          </form>
        </div>
      )}

      {/* TAB 2: MULTILINGUAL SETTINGS */}
      {tab === 'language' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-6">
          <div>
            <h3 className="text-lg font-bold text-slate-800">Language & Internationalization</h3>
            <p className="text-xs text-slate-500">
              Select your preferred operating language. Supports English, Urdu, and Sindhi with automated right-to-left
              (RTL) layout switching and localized font glyphs.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div
              onClick={() => onLanguageChange('en')}
              className={`p-6 rounded-3xl cursor-pointer border-2 transition-all ${
                lang === 'en'
                  ? 'bg-blue-50/80 border-blue-900 shadow-md ring-2 ring-blue-900/20'
                  : 'bg-slate-50 hover:bg-slate-100 border-slate-200'
              }`}
            >
              <div className="flex justify-between items-center mb-2">
                <span className="text-xl">🇬🇧</span>
                {lang === 'en' && <Check size={20} className="text-blue-900" />}
              </div>
              <h4 className="font-bold text-slate-900 text-base">English</h4>
              <p className="text-xs text-slate-500 mt-1">Default International LTR Standard</p>
            </div>

            <div
              onClick={() => onLanguageChange('ur')}
              className={`p-6 rounded-3xl cursor-pointer border-2 transition-all ${
                lang === 'ur'
                  ? 'bg-blue-50/80 border-blue-900 shadow-md ring-2 ring-blue-900/20'
                  : 'bg-slate-50 hover:bg-slate-100 border-slate-200'
              }`}
            >
              <div className="flex justify-between items-center mb-2">
                <span className="text-xl">🇵🇰</span>
                {lang === 'ur' && <Check size={20} className="text-blue-900" />}
              </div>
              <h4 className="font-bold text-slate-900 text-base font-['Noto_Naskh_Arabic']">اردو (Urdu)</h4>
              <p className="text-xs text-slate-500 mt-1 font-['Noto_Naskh_Arabic']">پاکستان کی قومی زبان • دائیں سے بائیں (RTL)</p>
            </div>

            <div
              onClick={() => onLanguageChange('sd')}
              className={`p-6 rounded-3xl cursor-pointer border-2 transition-all ${
                lang === 'sd'
                  ? 'bg-blue-50/80 border-blue-900 shadow-md ring-2 ring-blue-900/20'
                  : 'bg-slate-50 hover:bg-slate-100 border-slate-200'
              }`}
            >
              <div className="flex justify-between items-center mb-2">
                <span className="text-xl">🏛️</span>
                {lang === 'sd' && <Check size={20} className="text-blue-900" />}
              </div>
              <h4 className="font-bold text-slate-900 text-base font-['Noto_Naskh_Arabic']">سنڌي (Sindhi)</h4>
              <p className="text-xs text-slate-500 mt-1 font-['Noto_Naskh_Arabic']">سنڌ جي صوبائي ۽ ادبي ٻولي • سڄي کان کٻي (RTL)</p>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: USER ROLES */}
      {tab === 'roles' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-6">
          <div>
            <h3 className="text-lg font-bold text-slate-800">Role-Based Access Control (RBAC)</h3>
            <p className="text-xs text-slate-500">
              Switch current operating user role to test security privileges and navigation visibility
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            {(
              [
                'Super Admin',
                'School Admin',
                'Principal',
                'Teacher',
                'Accountant',
                'Receptionist',
                'Parent',
              ] as UserRole[]
            ).map((r) => (
              <div
                key={r}
                onClick={() => onRoleChange(r)}
                className={`p-4 rounded-2xl cursor-pointer border transition-all text-center ${
                  role === r
                    ? 'bg-blue-900 text-white font-bold shadow-md'
                    : 'bg-slate-50 hover:bg-slate-100 text-slate-700 border-slate-200 font-semibold'
                }`}
              >
                <p className="text-xs">{r}</p>
              </div>
            ))}
          </div>

          <div className="border border-slate-200 rounded-2xl overflow-hidden mt-4">
            <div className="p-3 bg-slate-50 border-b border-slate-200 font-bold text-xs text-slate-700">
              Active Security Permissions for: <span className="text-blue-900">{role}</span>
            </div>
            <div className="p-4 text-xs space-y-2 text-slate-600">
              <p>• Student & Staff Directory: Full Read & Write</p>
              <p>• Fee Collection & Ledgers: {role === 'Teacher' || role === 'Parent' ? 'Restricted' : 'Granted'}</p>
              <p>• Exam Marks & Report Cards: Full Authoring & Publishing</p>
              <p>• AI Exam & Syllabus Tools: Unlimited Generation Access</p>
              <p>• Institutional Settings: {role === 'Super Admin' || role === 'School Admin' ? 'Master Access' : 'View Only'}</p>
            </div>
          </div>
        </div>
      )}

      {/* TAB 4: BACKUP & RESTORE */}
      {tab === 'backup' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-6">
          <div>
            <h3 className="text-lg font-bold text-slate-800">Data Portability & Complete Backup</h3>
            <p className="text-xs text-slate-500">
              Export and download complete database snapshot in standard JSON format for offline safe-keeping or migration.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-6 bg-slate-50 border border-slate-200 rounded-3xl space-y-3">
              <Download size={32} className="text-blue-900" />
              <h4 className="font-bold text-slate-900 text-sm">Download Complete Database Backup</h4>
              <p className="text-xs text-slate-500">
                Exports all students, teachers, fee receipts, salaries, timetable, exam grades, and library records into a
                portable JSON archive.
              </p>
              <button
                onClick={onExportAllData}
                className="px-5 py-2.5 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl text-xs flex items-center gap-2 shadow-sm transition-colors"
              >
                <Download size={15} /> Export JSON Snapshot
              </button>
            </div>

            <div className="p-6 bg-rose-50/50 border border-rose-200 rounded-3xl space-y-3">
              <RefreshCw size={32} className="text-rose-700" />
              <h4 className="font-bold text-rose-950 text-sm">Reset to Default Institutional Seed Data</h4>
              <p className="text-xs text-slate-600">
                Restores initial sample data for "The Citizens Public High School" including pre-filled students, teachers,
                and fee transactions.
              </p>
              <button
                onClick={onResetData}
                className="px-5 py-2.5 bg-rose-700 hover:bg-rose-800 text-white font-bold rounded-2xl text-xs flex items-center gap-2 shadow-sm transition-colors"
              >
                <RefreshCw size={15} /> Reset Seed Database
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TAB 5: AUDIT LOGS */}
      {tab === 'audit' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200">
            <h3 className="font-bold text-slate-800 text-sm">Chronological Security Audit Trail</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Log ID</th>
                  <th className="p-3.5">Actor</th>
                  <th className="p-3.5">Action Executed</th>
                  <th className="p-3.5">Transaction Details</th>
                  <th className="p-3.5 text-right">Timestamp</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-mono text-[11px]">
                {auditLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50/60">
                    <td className="p-3.5 text-slate-400">#{log.id}</td>
                    <td className="p-3.5 font-bold text-blue-900">{log.actor}</td>
                    <td className="p-3.5 font-semibold text-slate-800">{log.action}</td>
                    <td className="p-3.5 text-slate-600 max-w-sm">{log.details}</td>
                    <td className="p-3.5 text-slate-400 text-right">{log.timestamp}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};
