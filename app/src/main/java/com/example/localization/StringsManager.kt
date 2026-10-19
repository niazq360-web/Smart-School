package com.example.localization

import com.example.model.AppLanguage

object StringsManager {

    private val translations: Map<String, Map<AppLanguage, String>> = mapOf(
        "app_title" to mapOf(
            AppLanguage.ENGLISH to "Smart School Management System",
            AppLanguage.URDU to "اسمارٹ اسکول مینجمنٹ سسٹم",
            AppLanguage.SINDHI to "سمارٽ اسڪول مئنيجمينٽ سسٽم"
        ),
        "dashboard" to mapOf(
            AppLanguage.ENGLISH to "Dashboard",
            AppLanguage.URDU to "ڈیش بورڈ",
            AppLanguage.SINDHI to "ڊيش بورڊ"
        ),
        "students" to mapOf(
            AppLanguage.ENGLISH to "Students",
            AppLanguage.URDU to "طلباء",
            AppLanguage.SINDHI to "شاگرد"
        ),
        "teachers" to mapOf(
            AppLanguage.ENGLISH to "Teachers",
            AppLanguage.URDU to "اساتذہ",
            AppLanguage.SINDHI to "استاد"
        ),
        "attendance" to mapOf(
            AppLanguage.ENGLISH to "Attendance",
            AppLanguage.URDU to "حاضری",
            AppLanguage.SINDHI to "حاضري"
        ),
        "fees" to mapOf(
            AppLanguage.ENGLISH to "Fees",
            AppLanguage.URDU to "فیس مینجمنٹ",
            AppLanguage.SINDHI to "فيون مئنيجمينٽ"
        ),
        "exams" to mapOf(
            AppLanguage.ENGLISH to "Exams & Results",
            AppLanguage.URDU to "امتحانات اور نتائج",
            AppLanguage.SINDHI to "امتحان ۽ نتيجا"
        ),
        "ai_tools" to mapOf(
            AppLanguage.ENGLISH to "AI Tools",
            AppLanguage.URDU to "اے آئی اوزار",
            AppLanguage.SINDHI to "اي آءِ اوزار"
        ),
        "timetable" to mapOf(
            AppLanguage.ENGLISH to "Timetable",
            AppLanguage.URDU to "ٹائم ٹیبل",
            AppLanguage.SINDHI to "ٽائيم ٽيبل"
        ),
        "finance" to mapOf(
            AppLanguage.ENGLISH to "Finance",
            AppLanguage.URDU to "مالیات اور اخراجات",
            AppLanguage.SINDHI to "ماليات ۽ خرچ"
        ),
        "administration" to mapOf(
            AppLanguage.ENGLISH to "Administration",
            AppLanguage.URDU to "انتظامیہ",
            AppLanguage.SINDHI to "انتظاميه"
        ),
        "settings" to mapOf(
            AppLanguage.ENGLISH to "Settings",
            AppLanguage.URDU to "ترتیبات",
            AppLanguage.SINDHI to "سيٽنگس"
        ),
        "total_students" to mapOf(
            AppLanguage.ENGLISH to "Total Students",
            AppLanguage.URDU to "کل طلباء",
            AppLanguage.SINDHI to "سمورا شاگرد"
        ),
        "active_students" to mapOf(
            AppLanguage.ENGLISH to "Active Students",
            AppLanguage.URDU to "فعال طلباء",
            AppLanguage.SINDHI to "فعال شاگرد"
        ),
        "total_teachers" to mapOf(
            AppLanguage.ENGLISH to "Total Teachers",
            AppLanguage.URDU to "کل اساتذہ",
            AppLanguage.SINDHI to "سمورا استاد"
        ),
        "present_today" to mapOf(
            AppLanguage.ENGLISH to "Present Today",
            AppLanguage.URDU to "آج حاضر",
            AppLanguage.SINDHI to "اڄ حاضر"
        ),
        "absent_today" to mapOf(
            AppLanguage.ENGLISH to "Absent Today",
            AppLanguage.URDU to "آج غیر حاضر",
            AppLanguage.SINDHI to "اڄ غير حاضر"
        ),
        "late_today" to mapOf(
            AppLanguage.ENGLISH to "Late Today",
            AppLanguage.URDU to "آج لیٹ",
            AppLanguage.SINDHI to "اڄ دير سان"
        ),
        "fee_collection" to mapOf(
            AppLanguage.ENGLISH to "Fee Collection",
            AppLanguage.URDU to "فیس وصولی",
            AppLanguage.SINDHI to "في وصولي"
        ),
        "pending_fees" to mapOf(
            AppLanguage.ENGLISH to "Pending Fees",
            AppLanguage.URDU to "بقایا فیس",
            AppLanguage.SINDHI to "بقايا فيون"
        ),
        "total_expenses" to mapOf(
            AppLanguage.ENGLISH to "Total Expenses",
            AppLanguage.URDU to "کل اخراجات",
            AppLanguage.SINDHI to "سمورا خرچ"
        ),
        "net_balance" to mapOf(
            AppLanguage.ENGLISH to "Net Financial Balance",
            AppLanguage.URDU to "خالص بچت / بیلنس",
            AppLanguage.SINDHI to "صاف بچت / بيلنس"
        ),
        "qr_scan" to mapOf(
            AppLanguage.ENGLISH to "QR Attendance Scan",
            AppLanguage.URDU to "کیو آر کوڈ سے حاضری",
            AppLanguage.SINDHI to "ڪيو آر ڪوڊ سان حاضري"
        ),
        "face_scan" to mapOf(
            AppLanguage.ENGLISH to "Face Attendance Scan",
            AppLanguage.URDU to "چہرہ شناسی سے حاضری",
            AppLanguage.SINDHI to "چهري جي سڃاڻپ سان حاضري"
        ),
        "collect_fee" to mapOf(
            AppLanguage.ENGLISH to "Collect Fee",
            AppLanguage.URDU to "فیس جمع کریں",
            AppLanguage.SINDHI to "في جمع ڪريو"
        ),
        "fee_receipt" to mapOf(
            AppLanguage.ENGLISH to "Fee Receipt",
            AppLanguage.URDU to "فیس رسید",
            AppLanguage.SINDHI to "في رسيد"
        ),
        "print_receipt" to mapOf(
            AppLanguage.ENGLISH to "Print / Thermal Receipt",
            AppLanguage.URDU to "رسید پرنٹ کریں",
            AppLanguage.SINDHI to "رسيد پرنٽ ڪريو"
        ),
        "id_card" to mapOf(
            AppLanguage.ENGLISH to "Student ID Card",
            AppLanguage.URDU to "اسٹوڈنٹ شناختی کارڈ",
            AppLanguage.SINDHI to "شاگرد سڃاڻپ ڪارڊ"
        ),
        "teacher_id_card" to mapOf(
            AppLanguage.ENGLISH to "Teacher ID Card",
            AppLanguage.URDU to "ٹیچر شناختی کارڈ",
            AppLanguage.SINDHI to "استاد سڃاڻپ ڪارڊ"
        ),
        "result_card" to mapOf(
            AppLanguage.ENGLISH to "Result Card / Marksheet",
            AppLanguage.URDU to "رزلٹ کارڈ / مارک شیٹ",
            AppLanguage.SINDHI to "نتيجي ڪارڊ / مارڪ شيٽ"
        ),
        "bonafide_certificate" to mapOf(
            AppLanguage.ENGLISH to "Bonafide Certificate",
            AppLanguage.URDU to "بونافائیڈ سرٹیفکیٹ",
            AppLanguage.SINDHI to "بونافائيڊ سرٽيفڪيٽ"
        ),
        "leaving_certificate" to mapOf(
            AppLanguage.ENGLISH to "School Leaving Certificate",
            AppLanguage.URDU to "اسکول چھوڑنے کا سرٹیفکیٹ",
            AppLanguage.SINDHI to "اسڪول ڇڏڻ جو سرٽيفڪيٽ"
        ),
        "salary_slip" to mapOf(
            AppLanguage.ENGLISH to "Salary Slip",
            AppLanguage.URDU to "تنخواہ کی سلپ",
            AppLanguage.SINDHI to "پگهار جي سلپ"
        ),
        "whatsapp_alerts" to mapOf(
            AppLanguage.ENGLISH to "WhatsApp & SMS Alerts",
            AppLanguage.URDU to "والدین کو واٹس ایپ الرٹس",
            AppLanguage.SINDHI to "والدين کي واٽس ايپ الرٽس"
        ),
        "dost_ai_assistant" to mapOf(
            AppLanguage.ENGLISH to "Dost School AI Assistant",
            AppLanguage.URDU to "دوست اسکول اے آئی معاون",
            AppLanguage.SINDHI to "دوست اسڪول اي آءِ مددگار"
        ),
        "ai_paper_generator" to mapOf(
            AppLanguage.ENGLISH to "AI Paper Generator",
            AppLanguage.URDU to "اے آئی امتحانی پرچہ جنریٹر",
            AppLanguage.SINDHI to "اي آءِ امتحاني پيپر جنريٽر"
        ),
        "ai_test_generator" to mapOf(
            AppLanguage.ENGLISH to "AI Class Test Generator",
            AppLanguage.URDU to "اے آئی کلاس ٹیسٹ جنریٹر",
            AppLanguage.SINDHI to "اي آءِ ڪلاس ٽيسٽ جنريٽر"
        ),
        "ai_syllabus_generator" to mapOf(
            AppLanguage.ENGLISH to "AI Syllabus Generator",
            AppLanguage.URDU to "اے آئی نصاب جنریٹر",
            AppLanguage.SINDHI to "اي آءِ نصاب جنريٽر"
        ),
        "ai_lesson_plan" to mapOf(
            AppLanguage.ENGLISH to "AI Lesson Planner",
            AppLanguage.URDU to "اے آئی سبق کا منصوبہ ساز",
            AppLanguage.SINDHI to "اي آءِ سبق جو منصوبو"
        ),
        "ai_worksheet" to mapOf(
            AppLanguage.ENGLISH to "AI Worksheet Generator",
            AppLanguage.URDU to "اے آئی ورک شیٹ جنریٹر",
            AppLanguage.SINDHI to "اي آءِ ورڪ شيٽ جنريٽر"
        ),
        "ocr_data_entry" to mapOf(
            AppLanguage.ENGLISH to "OCR / PDF Auto Data Entry",
            AppLanguage.URDU to "دستاویز اسکین اور خودکار اندراج",
            AppLanguage.SINDHI to "دستاويز اسڪين ۽ خودڪار داخلا"
        ),
        "search_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Search by Name, ID, Father, Roll #...",
            AppLanguage.URDU to "نام، آئی ڈی، والد کا نام یا رول نمبر سے تلاش کریں...",
            AppLanguage.SINDHI to "نالو، آءِ ڊي، والد جو نالو يا رول نمبر سان ڳوليو..."
        ),
        "add_student" to mapOf(
            AppLanguage.ENGLISH to "Add New Student",
            AppLanguage.URDU to "نیا طالب علم شامل کریں",
            AppLanguage.SINDHI to "نئون شاگرد شامل ڪريو"
        ),
        "add_teacher" to mapOf(
            AppLanguage.ENGLISH to "Add New Teacher",
            AppLanguage.URDU to "نیا استاد شامل کریں",
            AppLanguage.SINDHI to "نئون استاد شامل ڪريو"
        ),
        "backup_restore" to mapOf(
            AppLanguage.ENGLISH to "Database Backup & Export",
            AppLanguage.URDU to "ڈیٹا بیک اپ اور ایکسپورٹ",
            AppLanguage.SINDHI to "ڊيٽا بيڪ اپ ۽ ايڪسپورٽ"
        ),
        "audit_logs" to mapOf(
            AppLanguage.ENGLISH to "System Audit Logs",
            AppLanguage.URDU to "آڈٹ لاگز",
            AppLanguage.SINDHI to "آڊٽ لاگز"
        ),
        "quick_actions" to mapOf(
            AppLanguage.ENGLISH to "Quick Actions",
            AppLanguage.URDU to "فوری کارروائیاں",
            AppLanguage.SINDHI to "فوري ڪارروايون"
        ),
        "send_alert" to mapOf(
            AppLanguage.ENGLISH to "Send WhatsApp Alert",
            AppLanguage.URDU to "واٹس ایپ پیغام بھیجیں",
            AppLanguage.SINDHI to "واٽس ايپ نياپو موڪليو"
        ),
        "close" to mapOf(
            AppLanguage.ENGLISH to "Close",
            AppLanguage.URDU to "بند کریں",
            AppLanguage.SINDHI to "بند ڪريو"
        ),
        "save" to mapOf(
            AppLanguage.ENGLISH to "Save",
            AppLanguage.URDU to "محفوظ کریں",
            AppLanguage.SINDHI to "محفوظ ڪريو"
        ),
        "cancel" to mapOf(
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.URDU to "منسوخ کریں",
            AppLanguage.SINDHI to "رد ڪريو"
        ),
        "print" to mapOf(
            AppLanguage.ENGLISH to "Print",
            AppLanguage.URDU to "پرنٹ کریں",
            AppLanguage.SINDHI to "پرنٽ ڪريو"
        ),
        "preview_approve" to mapOf(
            AppLanguage.ENGLISH to "Preview & Approve",
            AppLanguage.URDU to "پیش منظر اور منظوری",
            AppLanguage.SINDHI to "ڏسو ۽ منظور ڪريو"
        )
    )

    fun get(key: String, language: AppLanguage): String {
        return translations[key]?.get(language) ?: translations[key]?.get(AppLanguage.ENGLISH) ?: key
    }

    // Helper for generating localized WhatsApp / SMS alerts
    fun generateAttendanceAlert(
        studentName: String,
        status: String,
        time: String,
        schoolName: String,
        language: AppLanguage
    ): String {
        return when (language) {
            AppLanguage.ENGLISH ->
                "Respected Parent, your child $studentName was marked $status at $time at $schoolName. Best regards."
            AppLanguage.URDU ->
                "محترم والدین، آپ کے بچے $studentName کی حاضری آج $time پر بطور $status ریکارڈ کی گئی ہے۔ $schoolName"
            AppLanguage.SINDHI ->
                "محترم والدين، اوهان جي ٻار $studentName جي حاضري اڄ $time وڳي $status طور درج ڪئي وئي آهي. $schoolName"
        }
    }

    fun generateFeeDueAlert(
        studentName: String,
        amount: Double,
        month: String,
        schoolName: String,
        language: AppLanguage
    ): String {
        return when (language) {
            AppLanguage.ENGLISH ->
                "Dear Parent, monthly fee of Rs. ${amount.toInt()} for $studentName for $month is due. Please clear before the due date. $schoolName"
            AppLanguage.URDU ->
                "محترم والدین، $studentName کی ماہانہ فیس برائے $month بمبلغ ${amount.toInt()} روپے واجب الادا ہے۔ برائے مہربانی وقت پر ادا فرمائیں۔ $schoolName"
            AppLanguage.SINDHI ->
                "محترم والدين، $studentName جي مهيني $month جي في ${amount.toInt()} رپيا واجب الادا آهي. مهرباني ڪري بروقت جمع ڪرايو. $schoolName"
        }
    }

    fun generateFeePaidAlert(
        studentName: String,
        amount: Double,
        receiptNo: String,
        schoolName: String,
        language: AppLanguage
    ): String {
        return when (language) {
            AppLanguage.ENGLISH ->
                "Dear Parent, fee payment of Rs. ${amount.toInt()} for $studentName has been received successfully. Receipt No: $receiptNo. $schoolName"
            AppLanguage.URDU ->
                "محترم والدین، $studentName کی فیس بمبلغ ${amount.toInt()} روپے وصول کر لی گئی ہے۔ رسید نمبر: $receiptNo۔ شکریہ، $schoolName"
            AppLanguage.SINDHI ->
                "محترم والدين، $studentName جي في ${amount.toInt()} رپيا وصول ٿي وئي آهي. رسيد نمبر: $receiptNo. مهرباني، $schoolName"
        }
    }

    fun generateExamAlert(
        studentName: String,
        examTitle: String,
        startDate: String,
        schoolName: String,
        language: AppLanguage
    ): String {
        return when (language) {
            AppLanguage.ENGLISH ->
                "Dear Parent, $examTitle for $studentName is scheduled to begin on $startDate. Kindly ensure regular revision. $schoolName"
            AppLanguage.URDU ->
                "محترم والدین، $studentName کے امتحانات ($examTitle) مورخہ $startDate سے شروع ہو رہے ہیں۔ تیاری پر توجہ دیں۔ $schoolName"
            AppLanguage.SINDHI ->
                "محترم والدين، $studentName جا امتحان ($examTitle) تاريخ $startDate کان شروع ٿي رهيا آهن. تياري تي ڌيان ڏيو. $schoolName"
        }
    }
}
