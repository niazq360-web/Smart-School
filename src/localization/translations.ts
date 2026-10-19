import { AppLanguage } from '../types';

export const translations: Record<string, Record<AppLanguage, string>> = {
  app_name: {
    en: 'Smart School Management System',
    ur: 'اسمارٹ اسکول مینجمنٹ سسٹم',
    sd: 'سمارٽ اسڪول مئنيجمينٽ سسٽم',
  },
  dashboard: { en: 'Dashboard', ur: 'ڈیش بورڈ', sd: 'ڊيش بورڊ' },
  students: { en: 'Students', ur: 'طلباء', sd: 'شاگرد' },
  teachers: { en: 'Teachers', ur: 'اساتذہ', sd: 'استاد' },
  attendance: { en: 'Attendance', ur: 'حاضری', sd: 'حاضري' },
  fees: { en: 'Fees', ur: 'فیس مینجمنٹ', sd: 'فيون مئنيجمينٽ' },
  finance: { en: 'Finance', ur: 'مالیات اور اخراجات', sd: 'ماليات ۽ خرچ' },
  exams: { en: 'Exams & Results', ur: 'امتحانات اور نتائج', sd: 'امتحان ۽ نتيجا' },
  timetable: { en: 'Timetable', ur: 'ٹائم ٹیبل', sd: 'ٽائيم ٽيبل' },
  ai_tools: { en: 'AI Tools', ur: 'اے آئی اوزار', sd: 'اي آءِ اوزار' },
  administration: { en: 'Administration', ur: 'انتظامیہ', sd: 'انتظاميه' },
  settings: { en: 'Settings', ur: 'ترتیبات', sd: 'سيٽنگس' },

  total_students: { en: 'Total Students', ur: 'کل طلباء', sd: 'سمورا شاگرد' },
  active_students: { en: 'Active Students', ur: 'فعال طلباء', sd: 'فعال شاگرد' },
  total_teachers: { en: 'Total Teachers', ur: 'کل اساتذہ', sd: 'سمورا استاد' },
  present_today: { en: 'Present Today', ur: 'آج حاضر', sd: 'اڄ حاضر' },
  absent_today: { en: 'Absent Today', ur: 'آج غیر حاضر', sd: 'اڄ غير حاضر' },
  late_today: { en: 'Late Today', ur: 'آج لیٹ', sd: 'اڄ دير سان' },
  fee_collection: { en: 'Fee Collection', ur: 'فیس وصولی', sd: 'في وصولي' },
  pending_fees: { en: 'Pending Fees', ur: 'بقایا فیس', sd: 'بقايا فيون' },
  total_expenses: { en: 'Total Expenses', ur: 'کل اخراجات', sd: 'سمورا خرچ' },
  net_balance: { en: 'Net Financial Balance', ur: 'خالص بچت / بیلنس', sd: 'صاف بچت / بيلنس' },

  qr_attendance: { en: 'QR Attendance Scanner', ur: 'کیو آر کوڈ حاضری', sd: 'ڪيو آر ڪوڊ حاضري' },
  face_attendance: { en: 'Face Recognition Attendance', ur: 'چہرہ شناسی حاضری', sd: 'چهري جي سڃاڻپ سان حاضري' },
  collect_fee: { en: 'Collect Fee', ur: 'فیس جمع کریں', sd: 'في جمع ڪريو' },
  fee_receipt: { en: 'Print Receipt', ur: 'رسید پرنٹ کریں', sd: 'رسيد پرنٽ ڪريو' },
  dost_ai: { en: 'Dost School AI Assistant', ur: 'دوست اسکول اے آئی', sd: 'دوست اسڪول اي آءِ' },
  paper_generator: { en: 'AI Paper Generator', ur: 'امتحانی پرچہ جنریٹر', sd: 'امتحاني پيپر جنريٽر' },
  syllabus_generator: { en: 'AI Syllabus Generator', ur: 'نصاب جنریٹر', sd: 'نصاب جنريٽر' },
  ocr_scanner: { en: 'OCR Document Auto Entry', ur: 'دستاویز اسکین و اندراج', sd: 'دستاويز اسڪين ۽ داخلا' },
  certificates: { en: 'Certificates', ur: 'سرٹیفکیٹس', sd: 'سرٽيفڪيٽ' },
  search_placeholder: {
    en: 'Search by student name, ID, roll #, father...',
    ur: 'نام، آئی ڈی، والد کا نام یا رول نمبر سے تلاش کریں...',
    sd: 'شاگرد جو نالو، آءِ ڊي، يا رول نمبر سان ڳوليو...',
  },
  quick_actions: { en: 'Quick Actions', ur: 'فوری کارروائیاں', sd: 'فوري ڪارروايون' },
};

export function t(key: string, lang: AppLanguage): string {
  return translations[key]?.[lang] || translations[key]?.['en'] || key;
}

export function generateWhatsAppAlert(
  studentName: string,
  type: 'present' | 'absent' | 'late' | 'fee_due' | 'fee_paid' | 'exam',
  schoolName: string,
  extra: string,
  lang: AppLanguage
): string {
  if (lang === 'ur') {
    switch (type) {
      case 'present':
        return `محترم والدین، آپ کا بچہ ${studentName} آج وقت پر اسکول حاضر ہوا ہے۔ ${schoolName}`;
      case 'absent':
        return `محترم والدین، آپ کا بچہ ${studentName} آج اسکول سے غیر حاضر ہے۔ برائے مہربانی وجہ مطلع فرمائیں۔ ${schoolName}`;
      case 'late':
        return `محترم والدین، آپ کا بچہ ${studentName} آج دیر سے اسکول پہنچا ہے۔ ${schoolName}`;
      case 'fee_due':
        return `محترم والدین، ${studentName} کی ماہانہ فیس ${extra} واجب الادا ہے۔ برائے مہربانی بروقت جمع فرمائیں۔ ${schoolName}`;
      case 'fee_paid':
        return `محترم والدین، ${studentName} کی فیس ${extra} وصول کر لی گئی ہے۔ رسید محفوظ رکھیں۔ شکریہ، ${schoolName}`;
      case 'exam':
        return `محترم والدین، ${studentName} کا امتحان ${extra} کو شیڈول ہے۔ تیاری کروائیں۔ ${schoolName}`;
    }
  }

  if (lang === 'sd') {
    switch (type) {
      case 'present':
        return `محترم والدين، اوهان جو ٻار ${studentName} اڄ اسڪول ۾ حاضر ٿيو آهي. ${schoolName}`;
      case 'absent':
        return `محترم والدين، اوهان جو ٻار ${studentName} اڄ اسڪول مان غير حاضر آهي. مهرباني ڪري سبب ٻڌايو. ${schoolName}`;
      case 'late':
        return `محترم والدين، اوهان جو ٻار ${studentName} اڄ دير سان اسڪول پهتو آهي. ${schoolName}`;
      case 'fee_due':
        return `محترم والدين، ${studentName} جي مهيني جي في ${extra} واجب الادا آهي. بروقت ادا ڪريو. ${schoolName}`;
      case 'fee_paid':
        return `محترم والدين، ${studentName} جي في ${extra} وصول ٿي چڪي آهي. مهرباني، ${schoolName}`;
      case 'exam':
        return `محترم والدين، ${studentName} جو امتحان ${extra} تي رکيل آهي. تياري ڪرايو. ${schoolName}`;
    }
  }

  // English
  switch (type) {
    case 'present':
      return `Dear Parent, your child ${studentName} was marked PRESENT at ${schoolName}.`;
    case 'absent':
      return `Dear Parent, your child ${studentName} is ABSENT today. Please notify administration. ${schoolName}`;
    case 'late':
      return `Dear Parent, your child ${studentName} arrived LATE at school today. ${schoolName}`;
    case 'fee_due':
      return `Dear Parent, monthly school fee of ${extra} for ${studentName} is due. ${schoolName}`;
    case 'fee_paid':
      return `Dear Parent, fee payment of ${extra} for ${studentName} has been received. Thank you! ${schoolName}`;
    case 'exam':
      return `Dear Parent, ${extra} for ${studentName} is scheduled. Kindly ensure regular revision. ${schoolName}`;
  }
}
