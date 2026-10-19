import React, { useState } from 'react';
import {
  Student,
  SchoolProfile,
  LibraryBook,
  TransportVehicle,
  InventoryItem,
  LeaveRequest,
  AppLanguage,
  UserRole,
} from '../types';
import { CertificateModal } from '../components/PrintModals';
import {
  Award,
  Book,
  Bus,
  Package,
  CalendarCheck,
  PlusCircle,
  Printer,
  X,
  Search,
  CheckCircle,
  XCircle,
  Phone,
} from 'lucide-react';

interface AdministrationScreenProps {
  students: Student[];
  libraryBooks: LibraryBook[];
  transportVehicles: TransportVehicle[];
  inventory: InventoryItem[];
  leaves: LeaveRequest[];
  profile: SchoolProfile;
  lang: AppLanguage;
  role: UserRole;
  onAddBook: (book: LibraryBook) => void;
  onAddTransport: (vehicle: TransportVehicle) => void;
  onAddInventory: (item: InventoryItem) => void;
  onUpdateLeaveStatus: (id: number, status: 'APPROVED' | 'REJECTED') => void;
}

export const AdministrationScreen: React.FC<AdministrationScreenProps> = ({
  students,
  libraryBooks,
  transportVehicles,
  inventory,
  leaves,
  profile,
  onAddBook,
  onAddTransport,
  onAddInventory,
  onUpdateLeaveStatus,
}) => {
  const [tab, setTab] = useState<'certificates' | 'library' | 'transport' | 'inventory' | 'leaves'>('certificates');

  // Certificate Modal State
  const [certStudentId, setCertStudentId] = useState(students[0]?.id || '');
  const [certType, setCertType] = useState('Bonafide Student Certificate');
  const [showCertModal, setShowCertModal] = useState<{ student: Student; type: string } | null>(null);

  // Library Modal State
  const [showAddBookModal, setShowAddBookModal] = useState(false);
  const [bookTitle, setBookTitle] = useState('');
  const [bookAuthor, setBookAuthor] = useState('');
  const [bookCategory, setBookCategory] = useState('Science');
  const [bookCopies, setBookCopies] = useState(10);
  const [bookRack, setBookRack] = useState('Shelf A-3');

  // Transport Modal State
  const [showAddVehicleModal, setShowAddVehicleModal] = useState(false);
  const [vehNo, setVehNo] = useState('');
  const [driverName, setDriverName] = useState('');
  const [driverPhone, setDriverPhone] = useState('');
  const [routeName, setRouteName] = useState('');
  const [vehCapacity, setVehCapacity] = useState(30);
  const [monthlyFee, setMonthlyFee] = useState(2500);

  // Inventory Modal State
  const [showAddInventoryModal, setShowAddInventoryModal] = useState(false);
  const [invName, setInvName] = useState('');
  const [invCategory, setInvCategory] = useState('Electronics');
  const [invQty, setInvQty] = useState(10);
  const [invCost, setInvCost] = useState(50000);

  const selectedCertStudent = students.find((s) => s.id === certStudentId) || students[0];

  const handleBookSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!bookTitle) return;
    const newBook: LibraryBook = {
      id: `LIB-2026-${String(libraryBooks.length + 1).padStart(3, '0')}`,
      title: bookTitle,
      author: bookAuthor || 'Academic Faculty',
      category: bookCategory,
      totalCopies: Number(bookCopies),
      availableCopies: Number(bookCopies),
      rackLocation: bookRack,
    };
    onAddBook(newBook);
    setShowAddBookModal(false);
    setBookTitle('');
  };

  const handleTransportSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!vehNo) return;
    const newVeh: TransportVehicle = {
      vehicleNo: vehNo,
      driverName,
      driverPhone,
      routeName,
      capacity: Number(vehCapacity),
      monthlyFee: Number(monthlyFee),
    };
    onAddTransport(newVeh);
    setShowAddVehicleModal(false);
    setVehNo('');
  };

  const handleInventorySubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!invName) return;
    const newItem: InventoryItem = {
      id: `INV-${String(inventory.length + 1).padStart(3, '0')}`,
      name: invName,
      category: invCategory,
      quantity: Number(invQty),
      status: 'OPERATIONAL',
      purchaseDate: new Date().toISOString().split('T')[0],
      cost: Number(invCost),
    };
    onAddInventory(newItem);
    setShowAddInventoryModal(false);
    setInvName('');
  };

  return (
    <div className="space-y-6">
      {/* Navigation Tabs */}
      <div className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm flex flex-wrap gap-2 justify-between items-center">
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setTab('certificates')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'certificates' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Award size={16} /> Official Certificates
          </button>
          <button
            onClick={() => setTab('library')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'library' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Book size={16} /> School Library
          </button>
          <button
            onClick={() => setTab('transport')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'transport' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Bus size={16} /> Transport Fleet
          </button>
          <button
            onClick={() => setTab('inventory')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'inventory' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <Package size={16} /> Assets & Inventory
          </button>
          <button
            onClick={() => setTab('leaves')}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
              tab === 'leaves' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
            }`}
          >
            <CalendarCheck size={16} /> Leave Approvals
          </button>
        </div>

        <div>
          {tab === 'library' && (
            <button
              onClick={() => setShowAddBookModal(true)}
              className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-1.5 shadow-sm transition-colors"
            >
              <PlusCircle size={15} /> Add Book
            </button>
          )}
          {tab === 'transport' && (
            <button
              onClick={() => setShowAddVehicleModal(true)}
              className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-1.5 shadow-sm transition-colors"
            >
              <PlusCircle size={15} /> Add Vehicle / Route
            </button>
          )}
          {tab === 'inventory' && (
            <button
              onClick={() => setShowAddInventoryModal(true)}
              className="px-4 py-2 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-2xl flex items-center gap-1.5 shadow-sm transition-colors"
            >
              <PlusCircle size={15} /> Register Asset
            </button>
          )}
        </div>
      </div>

      {/* TAB 1: CERTIFICATES */}
      {tab === 'certificates' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-4 text-xs">
            <h3 className="font-bold text-slate-800 text-base flex items-center gap-2">
              <Award className="text-amber-600" size={18} /> Certificate Generator
            </h3>
            <p className="text-slate-500">
              Generate officially certified student documents with school insignia, registration number, and seal.
            </p>

            <div>
              <label className="font-semibold text-slate-600">Document Type</label>
              <select
                value={certType}
                onChange={(e) => setCertType(e.target.value)}
                className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold text-slate-800"
              >
                <option value="Bonafide Student Certificate">Bonafide Student Certificate</option>
                <option value="School Leaving Certificate (SLC)">School Leaving Certificate (SLC)</option>
                <option value="Character Certificate">Character & Conduct Certificate</option>
                <option value="Merit & Distinction Certificate">Academic Merit Certificate</option>
              </select>
            </div>

            <div>
              <label className="font-semibold text-slate-600">Select Student</label>
              <select
                value={certStudentId}
                onChange={(e) => setCertStudentId(e.target.value)}
                className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-medium"
              >
                {students.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name} ({s.id} - {s.className})
                  </option>
                ))}
              </select>
            </div>

            <button
              onClick={() => {
                if (selectedCertStudent) {
                  setShowCertModal({ student: selectedCertStudent, type: certType });
                }
              }}
              className="w-full py-3 bg-amber-800 hover:bg-amber-900 text-white font-bold rounded-2xl shadow-md transition-colors flex items-center justify-center gap-2 mt-4"
            >
              <Printer size={16} /> Preview & Print Certificate
            </button>
          </div>

          <div className="md:col-span-2 bg-gradient-to-br from-amber-50/40 to-orange-50/20 p-8 rounded-3xl border-2 border-dashed border-amber-300 flex flex-col items-center justify-center text-center">
            <Award size={64} className="text-amber-800 mb-3 opacity-90" />
            <h4 className="text-xl font-black text-slate-900">Institutional Certification Desk</h4>
            <p className="text-xs text-slate-600 max-w-md mt-2">
              All generated certificates strictly comply with standard Board of Intermediate & Secondary Education
              (BISE) guidelines, containing student B-Form, parental info, and institutional verification codes.
            </p>
            <div className="mt-4 flex gap-3">
              <span className="px-3 py-1 bg-white rounded-xl border border-amber-200 text-[11px] font-bold text-amber-900">
                Seal Protected
              </span>
              <span className="px-3 py-1 bg-white rounded-xl border border-amber-200 text-[11px] font-bold text-amber-900">
                Auto Serial Number
              </span>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: LIBRARY */}
      {tab === 'library' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">School Library Catalog</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Book ID</th>
                  <th className="p-3.5">Title & Author</th>
                  <th className="p-3.5">Category</th>
                  <th className="p-3.5">Rack Location</th>
                  <th className="p-3.5">Total Copies</th>
                  <th className="p-3.5">Available Copies</th>
                  <th className="p-3.5 text-right">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {libraryBooks.map((b) => (
                  <tr key={b.id} className="hover:bg-slate-50/60">
                    <td className="p-3.5 font-mono font-bold text-blue-900">{b.id}</td>
                    <td className="p-3.5">
                      <div className="font-bold text-slate-800">{b.title}</div>
                      <div className="text-[10px] text-slate-400">By {b.author}</div>
                    </td>
                    <td className="p-3.5 text-slate-600 font-medium">{b.category}</td>
                    <td className="p-3.5 font-mono text-slate-500">{b.rackLocation}</td>
                    <td className="p-3.5 font-bold text-slate-700">{b.totalCopies}</td>
                    <td className="p-3.5 font-black text-emerald-700">{b.availableCopies}</td>
                    <td className="p-3.5 text-right">
                      <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800">
                        IN STOCK
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: TRANSPORT */}
      {tab === 'transport' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">School Transport Fleet & Bus Routes</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Vehicle #</th>
                  <th className="p-3.5">Route Path</th>
                  <th className="p-3.5">Assigned Driver</th>
                  <th className="p-3.5">Emergency Contact</th>
                  <th className="p-3.5">Seating Capacity</th>
                  <th className="p-3.5 text-right">Monthly Transport Fee</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {transportVehicles.map((v) => (
                  <tr key={v.vehicleNo} className="hover:bg-slate-50/60">
                    <td className="p-3.5 font-mono font-bold text-blue-900">{v.vehicleNo}</td>
                    <td className="p-3.5 font-bold text-slate-800">{v.routeName}</td>
                    <td className="p-3.5 text-slate-700 font-medium">{v.driverName}</td>
                    <td className="p-3.5 text-slate-600 font-mono flex items-center gap-1">
                      <Phone size={12} className="text-emerald-600" /> {v.driverPhone}
                    </td>
                    <td className="p-3.5 text-slate-600 font-bold">{v.capacity} Passengers</td>
                    <td className="p-3.5 text-right font-black text-emerald-800">
                      {profile.currencySymbol} {v.monthlyFee.toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: INVENTORY */}
      {tab === 'inventory' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">School Physical Assets & Equipment</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Asset Code</th>
                  <th className="p-3.5">Equipment Name</th>
                  <th className="p-3.5">Category</th>
                  <th className="p-3.5">Total Quantity</th>
                  <th className="p-3.5">Purchase Date</th>
                  <th className="p-3.5">Condition Status</th>
                  <th className="p-3.5 text-right">Valuation</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {inventory.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/60">
                    <td className="p-3.5 font-mono font-bold text-blue-900">{item.id}</td>
                    <td className="p-3.5 font-bold text-slate-800">{item.name}</td>
                    <td className="p-3.5 text-slate-600">{item.category}</td>
                    <td className="p-3.5 font-bold text-slate-700">{item.quantity} Units</td>
                    <td className="p-3.5 font-mono text-slate-500">{item.purchaseDate}</td>
                    <td className="p-3.5">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800">
                        {item.status}
                      </span>
                    </td>
                    <td className="p-3.5 text-right font-black text-blue-900">
                      {profile.currencySymbol} {item.cost.toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 5: LEAVES */}
      {tab === 'leaves' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 flex justify-between items-center">
            <h3 className="font-bold text-slate-800 text-sm">Pending Staff & Student Leave Applications</h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-600 font-bold border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Applicant</th>
                  <th className="p-3.5">Type</th>
                  <th className="p-3.5">Dates Duration</th>
                  <th className="p-3.5">Reason for Absence</th>
                  <th className="p-3.5">Status</th>
                  <th className="p-3.5 text-right">Administrative Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {leaves.map((leave) => (
                  <tr key={leave.id} className="hover:bg-slate-50/60">
                    <td className="p-3.5">
                      <div className="font-bold text-slate-800">{leave.applicantName}</div>
                      <div className="text-[10px] text-slate-400 font-mono">{leave.applicantId}</div>
                    </td>
                    <td className="p-3.5">
                      <span className="px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 text-[10px] font-bold">
                        {leave.applicantType}
                      </span>
                    </td>
                    <td className="p-3.5 font-medium text-slate-600">
                      {leave.startDate} to {leave.endDate}
                    </td>
                    <td className="p-3.5 text-slate-700 italic max-w-xs">{leave.reason}</td>
                    <td className="p-3.5">
                      <span
                        className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold ${
                          leave.status === 'APPROVED'
                            ? 'bg-emerald-100 text-emerald-800'
                            : leave.status === 'REJECTED'
                            ? 'bg-rose-100 text-rose-800'
                            : 'bg-amber-100 text-amber-800'
                        }`}
                      >
                        {leave.status}
                      </span>
                    </td>
                    <td className="p-3.5 text-right">
                      {leave.status === 'PENDING' ? (
                        <div className="flex justify-end gap-1.5">
                          <button
                            onClick={() => onUpdateLeaveStatus(leave.id, 'APPROVED')}
                            className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg font-bold text-[10px]"
                          >
                            Approve
                          </button>
                          <button
                            onClick={() => onUpdateLeaveStatus(leave.id, 'REJECTED')}
                            className="px-2.5 py-1 bg-rose-100 text-rose-800 hover:bg-rose-200 rounded-lg font-bold text-[10px]"
                          >
                            Reject
                          </button>
                        </div>
                      ) : (
                        <span className="text-slate-400 text-[11px] font-medium">Processed</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* CERTIFICATE PREVIEW MODAL */}
      {showCertModal && (
        <CertificateModal
          type={showCertModal.type}
          student={showCertModal.student}
          profile={profile}
          onClose={() => setShowCertModal(null)}
        />
      )}

      {/* ADD BOOK MODAL */}
      {showAddBookModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 text-xs">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Add Library Book</h3>
              <button onClick={() => setShowAddBookModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleBookSubmit} className="space-y-4 mt-4">
              <div>
                <label className="font-semibold text-slate-600">Book Title</label>
                <input
                  type="text"
                  value={bookTitle}
                  onChange={(e) => setBookTitle(e.target.value)}
                  required
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-600">Author</label>
                <input
                  type="text"
                  value={bookAuthor}
                  onChange={(e) => setBookAuthor(e.target.value)}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Total Copies</label>
                  <input
                    type="number"
                    value={bookCopies}
                    onChange={(e) => setBookCopies(Number(e.target.value))}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Rack / Shelf</label>
                  <input
                    type="text"
                    value={bookRack}
                    onChange={(e) => setBookRack(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl"
                  />
                </div>
              </div>
              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddBookModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl shadow-sm"
                >
                  Save Book
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ADD VEHICLE MODAL */}
      {showAddVehicleModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 text-xs">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Add Transport Vehicle</h3>
              <button onClick={() => setShowAddVehicleModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleTransportSubmit} className="space-y-4 mt-4">
              <div>
                <label className="font-semibold text-slate-600">Vehicle Number</label>
                <input
                  type="text"
                  placeholder="e.g. HYD-BUS-402"
                  value={vehNo}
                  onChange={(e) => setVehNo(e.target.value)}
                  required
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>
              <div>
                <label className="font-semibold text-slate-600">Route Description</label>
                <input
                  type="text"
                  placeholder="e.g. Route 3 (Qasimabad to Latifabad Unit 7)"
                  value={routeName}
                  onChange={(e) => setRouteName(e.target.value)}
                  required
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Driver Name</label>
                  <input
                    type="text"
                    value={driverName}
                    onChange={(e) => setDriverName(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Driver Phone</label>
                  <input
                    type="text"
                    value={driverPhone}
                    onChange={(e) => setDriverPhone(e.target.value)}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-mono"
                  />
                </div>
              </div>
              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddVehicleModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl shadow-sm"
                >
                  Save Vehicle
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ADD ASSET MODAL */}
      {showAddInventoryModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-6 text-xs">
            <div className="flex justify-between items-center pb-3 border-b border-slate-200">
              <h3 className="font-bold text-slate-800 text-lg">Register Physical Asset</h3>
              <button onClick={() => setShowAddInventoryModal(false)} className="p-1 hover:bg-slate-100 rounded-lg">
                <X size={18} />
              </button>
            </div>
            <form onSubmit={handleInventorySubmit} className="space-y-4 mt-4">
              <div>
                <label className="font-semibold text-slate-600">Asset Name</label>
                <input
                  type="text"
                  placeholder="e.g. Lenovo Core i5 Desktop Computers"
                  value={invName}
                  onChange={(e) => setInvName(e.target.value)}
                  required
                  className="w-full mt-1 p-2.5 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-slate-600">Quantity</label>
                  <input
                    type="number"
                    value={invQty}
                    onChange={(e) => setInvQty(Number(e.target.value))}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                  />
                </div>
                <div>
                  <label className="font-semibold text-slate-600">Total Valuation ({profile.currencySymbol})</label>
                  <input
                    type="number"
                    value={invCost}
                    onChange={(e) => setInvCost(Number(e.target.value))}
                    className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                  />
                </div>
              </div>
              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddInventoryModal(false)}
                  className="px-4 py-2 bg-slate-100 hover:bg-slate-200 rounded-xl font-medium"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 font-bold text-white bg-blue-900 hover:bg-blue-800 rounded-xl shadow-sm"
                >
                  Register Asset
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
