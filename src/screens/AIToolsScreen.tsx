import React, { useState } from 'react';
import { SchoolProfile, AppLanguage, UserRole, Student } from '../types';
import {
  Sparkles,
  BookOpen,
  FileText,
  HelpCircle,
  Copy,
  Printer,
  Check,
  Send,
  Upload,
  Layers,
  Wand2,
  BrainCircuit,
  MessageSquare,
  FileCheck,
} from 'lucide-react';

interface AIToolsScreenProps {
  profile: SchoolProfile;
  students: Student[];
  lang: AppLanguage;
  role: UserRole;
  onImportStudents?: (newStudents: Student[]) => void;
}

export const AIToolsScreen: React.FC<AIToolsScreenProps> = ({
  profile,
  students,
  lang,
}) => {
  const [tab, setTab] = useState<'paper' | 'syllabus' | 'worksheet' | 'chat' | 'ocr'>('paper');

  // Paper Generator State
  const [paperClass, setPaperClass] = useState('Class 5');
  const [paperSubject, setPaperSubject] = useState('General Science');
  const [paperTopic, setPaperTopic] = useState('Photosynthesis, Plant Cells & Ecosystems');
  const [paperDifficulty, setPaperDifficulty] = useState<'Easy' | 'Medium' | 'Hard'>('Medium');
  const [paperTotalMarks, setPaperTotalMarks] = useState(50);
  const [paperLanguage, setPaperLanguage] = useState<'English' | 'Urdu' | 'Sindhi'>('English');
  const [isGeneratingPaper, setIsGeneratingPaper] = useState(false);
  const [generatedPaper, setGeneratedPaper] = useState<string | null>(null);
  const [copiedPaper, setCopiedPaper] = useState(false);

  // Syllabus Generator State
  const [sylSubject, setSylSubject] = useState('Mathematics');
  const [sylClass, setSylClass] = useState('Class 8');
  const [sylWeeks, setSylWeeks] = useState(8);
  const [isGeneratingSyl, setIsGeneratingSyl] = useState(false);
  const [generatedSyllabus, setGeneratedSyllabus] = useState<string | null>(null);

  // Worksheet Generator State
  const [wsTopic, setWsTopic] = useState('Fractions, Decimals and Percentages');
  const [wsClass, setWsClass] = useState('Class 6');
  const [isGeneratingWs, setIsGeneratingWs] = useState(false);
  const [generatedWs, setGeneratedWs] = useState<string | null>(null);

  // AI Chat Assistant State
  const [chatMessages, setChatMessages] = useState<Array<{ sender: 'user' | 'ai'; text: string }>>([
    {
      sender: 'ai',
      text: `Assalam-o-Alaikum! I am your Smart School AI Academic Assistant. I can help you draft school circulars, design test papers, create custom rubrics, translate parent communications into Urdu or Sindhi, and plan school events. What would you like assistance with today?`,
    },
  ]);
  const [chatInput, setChatInput] = useState('');
  const [isAiThinking, setIsAiThinking] = useState(false);

  // OCR Simulator State
  const [ocrText, setOcrText] = useState('');
  const [ocrExtractedCount, setOcrExtractedCount] = useState<number | null>(null);

  const handleGeneratePaper = () => {
    setIsGeneratingPaper(true);
    setGeneratedPaper(null);

    setTimeout(() => {
      setIsGeneratingPaper(false);
      let content = '';

      if (paperLanguage === 'Urdu') {
        content = `
========================================================================
                      ${profile.name}
                     امتحانی پرچہ - سیشن ${profile.academicYear}
کلاس: ${paperClass}    |    مضمون: ${paperSubject}    |    کل نمبر: ${paperTotalMarks}
عنوان: ${paperTopic}   |    وقت: 2 گھنٹے
========================================================================

حصہ اوّل: معروضی سوالات (MCQs) — 15 نمبر
ہدایت: درست جواب کے گرد دائرہ لگائیں۔

1. پودوں میں خوراک بنانے کے عمل کو کیا کہتے ہیں؟
   (الف) تنفس        (ب) ضیائی تالیف (Photosynthesis)
   (ج) تبخیر         (د) انجذاب

2. ضیائی تالیف کے لیے کون سی گیس ناگزیر ہے؟
   (الف) آکسیجن      (ب) نائٹروجن
   (ج) کاربن ڈائی آکسائیڈ (د) ہیلیم

3. پودوں کے پتوں میں سبز مادہ کہلاتا ہے:
   (الف) کلوروفل     (ب) ہیموگلوبن
   (ج) جلیٹن        (د) زائلم

حصہ دوم: مختصر جوابات — 20 نمبر
ہدایت: کوئی سے چار سوالات کے جوابات تحریر کریں۔

1. ضیائی تالیف (Photosynthesis) کی مساوات لکھیں۔
2. کلوروفل کا پودے میں کیا بنیادی کردار ہے؟
3. ایکو سسٹم میں پروڈیوسرز اور کنزیومرز میں کیا فرق ہے؟
4. پودے رات کے وقت کون سی گیس خارج کرتے ہیں اور کیوں؟
5. جڑوں کا پانی جذب کرنے میں بنیادی طریقہ کار بیان کریں۔

حصہ سوم: تفصیلی سوال — 15 نمبر
1. پودے کے خلیے (Plant Cell) کی لیبل شدہ تصویر بنائیں اور اس کے اہم حصوں کی وضاحت کریں۔

========================= جوابی کلید (ANSWER KEY) =========================
1. (ب) ضیائی تالیف
2. (ج) کاربن ڈائی آکسائیڈ
3. (الف) کلوروفل
`;
      } else if (paperLanguage === 'Sindhi') {
        content = `
========================================================================
                      ${profile.name}
                     امتحاني پيپر - سيشن ${profile.academicYear}
ڪلاس: ${paperClass}    |    مضمون: ${paperSubject}    |    ڪل مارڪون: ${paperTotalMarks}
موضوع: ${paperTopic}   |    وقت: 2 ڪلاڪ
========================================================================

حصو پهريون: گهڻ-چونڊ سوال (MCQs) — 15 مارڪون

1. ٻوٽن ۾ کاڌو ٺاهڻ واري عمل کي ڇا چئبو آهي؟
   (الف) فوٽوسنٿيسس (Photosynthesis)   (ب) ساهه کڻڻ
   (ج) بخاراتي عمل                     (د) ٽرانسپائريشن

2. ٻوٽن کي سج جي روشني ۽ پاڻي کانسواءِ ڪهڙي گئس جي ضرورت هوندي آهي؟
   (الف) آڪسيجن     (ب) ڪاربان ڊاءِ آڪسائيڊ
   (ج) نائٽروجن     (د) هائڊروجن

حصو ٻيون: مختصر سوالات — 20 مارڪون
1. فوٽوسنٿيسس جو ڪيميائي فارمولو لکو.
2. کلوروفل جو بنيادي ڪم بيان ڪريو.
3. ٻوٽن ۽ ماحول ۾ هڪٻئي تي دارومدار ڪيئن آهي؟

========================= جوابي ڪليد (ANSWER KEY) =========================
1. (الف) فوٽوسنٿيسس
2. (ب) ڪاربان ڊاءِ آڪسائيڊ
`;
      } else {
        content = `
==================================================================================
                          ${profile.name.toUpperCase()}
                  ACADEMIC EXAMINATION — SESSION ${profile.academicYear}
Class: ${paperClass}          Subject: ${paperSubject}          Max Marks: ${paperTotalMarks}
Topic: ${paperTopic}          Difficulty: ${paperDifficulty}          Time Allowed: 2 Hours
==================================================================================

SECTION A: MULTIPLE CHOICE QUESTIONS (15 Marks)
Instructions: Encircle the single correct answer option.

1. Which pigment in plant leaves absorbs sunlight for photosynthesis?
   a) Hemoglobin          b) Chlorophyll          c) Melanin          d) Carotene

2. The main byproduct gas released into the atmosphere during photosynthesis is:
   a) Carbon Dioxide      b) Oxygen               c) Nitrogen         d) Hydrogen

3. In an ecosystem, green plants that produce their own food are known as:
   a) Primary Consumers   b) Decomposers          c) Producers        d) Herbivores

4. Which cellular organelle contains chlorophyll and conducts photosynthesis?
   a) Mitochondria        b) Chloroplast          c) Ribosome         d) Nucleus

SECTION B: SHORT ANSWER QUESTIONS (20 Marks)
Instructions: Answer any four (4) of the following questions in 3 to 4 lines each.

1. Write the balanced chemical word equation for Photosynthesis.
2. Differentiate between autotrophic and heterotrophic nutrition with examples.
3. Explain the function of stomata in plant leaves and how they regulate gas exchange.
4. Describe what happens to glucose produced by plants during the daytime.
5. Why are decomposers crucial to the continuous cycle of an ecosystem?

SECTION C: STRUCTURED & ESSAY QUESTIONS (15 Marks)
1. (a) Draw a neat, fully labeled diagram of a typical Plant Cell. (8 Marks)
   (b) Describe three key structural differences between a Plant Cell and an Animal Cell. (7 Marks)

================================ ANSWER KEY ================================
Q1: (b) Chlorophyll
Q2: (b) Oxygen
Q3: (c) Producers
Q4: (b) Chloroplast
Section B & C: Evaluated against standard Sindh Textbook Board Rubric.
`;
      }

      setGeneratedPaper(content.trim());
    }, 1200);
  };

  const handleGenerateSyllabus = () => {
    setIsGeneratingSyl(true);
    setGeneratedSyllabus(null);

    setTimeout(() => {
      setIsGeneratingSyl(false);
      const syl = `
==================================================================================
                     ${profile.name.toUpperCase()}
           ACADEMIC SYLLABUS & PACING GUIDE — SESSION ${profile.academicYear}
Target: ${sylClass} | Subject: ${sylSubject} | Duration: ${sylWeeks} Weeks Pacing
==================================================================================

WEEK 1 & 2: FOUNDATIONAL CORE & NUMBER SYSTEMS
- Topic: Rational Numbers, Number Line & Operations
- Learning Objectives: Master operations on positive and negative rational numbers.
- Classroom Activities: Group problem sets, real-world finance examples.
- Assessment: Formative Quiz 1 (20 Marks).

WEEK 3 & 4: ALGEBRAIC EXPRESSIONS & LINEAR EQUATIONS
- Topic: Algebraic Identities, Factorization, Linear Equations in One Variable
- Learning Objectives: Formulate equations from word problems and solve accurately.
- Practical Work: Algebra tiles visualization in Computer Lab.
- Assessment: Homework assignment & peer review.

WEEK 5 & 6: GEOMETRIC CONSTRUCTIONS & ANGLES
- Topic: Angles, Parallel Lines, Triangles & Quadrilateral Properties
- Learning Objectives: Construct standard angles using compass and ruler.
- Assessment: Mid-term Progress Assessment Test (50 Marks).

WEEK 7 & 8: REVISION, EXAM DRILLS & MASTERY EVALUATION
- Topic: Cumulative review of chapters 1-4.
- Remedial Sessions: Special focus for struggling students.
- Final Output: Comprehensive Term Mock Examination.
`;
      setGeneratedSyllabus(syl.trim());
    }, 1000);
  };

  const handleGenerateWorksheet = () => {
    setIsGeneratingWs(true);
    setGeneratedWs(null);

    setTimeout(() => {
      setIsGeneratingWs(false);
      const ws = `
==================================================================================
                     ${profile.name.toUpperCase()}
                   STUDENT HOMEWORK & PRACTICE WORKSHEET
Student Name: ______________________    Class: ${wsClass}    Date: ____________
Topic: ${wsTopic}
==================================================================================

PART 1: DRILL PROBLEMS
1. Simplify: 3/4 + 5/8 = ___________
2. Convert 0.625 into a fraction in its simplest form: ___________
3. Calculate 15% of 2,400 PKR: ___________
4. Arrange in ascending order: 0.7, 3/5, 65%, 2/3: ___________

PART 2: REAL-WORLD WORD PROBLEMS
5. In a school of 600 students, 45% are boys. 
   (a) How many boys are enrolled?
   (b) What fraction of total students are girls?

PART 3: BONUS CHALLENGE PUZZLE
6. If a book price is reduced by 20% to 400 PKR, what was its original price?

[ Teacher Signature: ______________   Grade: _____ / 20 ]
`;
      setGeneratedWs(ws.trim());
    }, 900);
  };

  const handleChatSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!chatInput.trim()) return;

    const userText = chatInput.trim();
    setChatMessages((prev) => [...prev, { sender: 'user', text: userText }]);
    setChatInput('');
    setIsAiThinking(true);

    setTimeout(() => {
      setIsAiThinking(false);
      let reply = '';
      const lower = userText.toLowerCase();

      if (lower.includes('urdu') || lower.includes('اردو')) {
        reply = `محترم ہیڈ ماسٹر صاحب! میں نے آپ کی درخواست کے مطابق اردو نوٹس اور تعلیمی مواد تیار کر دیا ہے۔ کیا آپ چاہتے ہیں کہ میں اسے والدین کے واٹس ایپ گروپ کے فارمیٹ میں ڈھال دوں؟`;
      } else if (lower.includes('fee') || lower.includes('فیس')) {
        reply = `For fee reminders, here is an effective SMS template: "Dear Parent, this is a friendly reminder that the tuition fee for the current month is due. Kindly deposit at the accounts counter by the 10th to avoid late fee surcharge. Thank you, ${profile.name}."`;
      } else if (lower.includes('attendance') || lower.includes('حاضری')) {
        reply = `To improve student attendance rate, research shows that instant SMS alerts to parents within 15 minutes of school start time reduce absenteeism by 32%. You can use the QR Terminal in the Attendance tab for instant WhatsApp alerts.`;
      } else {
        reply = `Great query regarding "${userText}". As an AI school consultant, I recommend aligning your term milestones with continuous weekly quizzes, providing teachers with standardized rubrics, and scheduling regular parent-teacher conferences. I can also generate a detailed document for you!`;
      }

      setChatMessages((prev) => [...prev, { sender: 'ai', text: reply }]);
    }, 800);
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedPaper(true);
    setTimeout(() => setCopiedPaper(false), 2000);
  };

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-blue-900 via-indigo-900 to-slate-900 text-white p-6 rounded-3xl shadow-lg border border-slate-800 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <div className="flex items-center gap-2 text-amber-400 text-xs font-mono font-bold uppercase tracking-wider mb-1">
            <Sparkles size={16} /> Advanced AI School Engine
          </div>
          <h2 className="text-2xl font-black">AI Academic & Administrative Suite</h2>
          <p className="text-xs text-blue-200 mt-1 max-w-xl">
            Automate test paper generation, lesson plans, curriculum pacing, worksheets, and document transcription with
            multilingual support in English, Urdu, and Sindhi.
          </p>
        </div>

        {/* Feature Badges */}
        <div className="flex flex-wrap gap-2 text-[11px] font-mono">
          <span className="px-3 py-1 bg-white/10 rounded-xl border border-white/10">3 Languages</span>
          <span className="px-3 py-1 bg-white/10 rounded-xl border border-white/10">Answer Keys</span>
          <span className="px-3 py-1 bg-amber-400 text-slate-950 font-bold rounded-xl">Sindh Board Ready</span>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white p-4 rounded-3xl border border-slate-200 shadow-sm flex flex-wrap gap-2">
        <button
          onClick={() => setTab('paper')}
          className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
            tab === 'paper' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
          }`}
        >
          <FileText size={16} /> AI Exam Paper Generator
        </button>
        <button
          onClick={() => setTab('syllabus')}
          className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
            tab === 'syllabus' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
          }`}
        >
          <BookOpen size={16} /> Syllabus & Curriculum
        </button>
        <button
          onClick={() => setTab('worksheet')}
          className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
            tab === 'worksheet' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
          }`}
        >
          <Layers size={16} /> Worksheets & Drills
        </button>
        <button
          onClick={() => setTab('chat')}
          className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
            tab === 'chat' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
          }`}
        >
          <MessageSquare size={16} /> Dost School AI Assistant
        </button>
        <button
          onClick={() => setTab('ocr')}
          className={`px-4 py-2.5 rounded-2xl text-xs font-bold transition-all flex items-center gap-2 ${
            tab === 'ocr' ? 'bg-blue-900 text-white shadow-md' : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
          }`}
        >
          <BrainCircuit size={16} /> OCR Document Transcription
        </button>
      </div>

      {/* TAB 1: AI PAPER GENERATOR */}
      {tab === 'paper' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Controls Form */}
          <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-4 text-xs">
            <h3 className="font-bold text-slate-800 text-base flex items-center gap-2">
              <Wand2 size={18} className="text-blue-900" /> Paper Parameters
            </h3>

            <div>
              <label className="font-semibold text-slate-600">Language</label>
              <div className="grid grid-cols-3 gap-2 mt-1">
                {(['English', 'Urdu', 'Sindhi'] as const).map((l) => (
                  <button
                    key={l}
                    type="button"
                    onClick={() => setPaperLanguage(l)}
                    className={`py-2 rounded-xl font-bold transition-all ${
                      paperLanguage === l
                        ? 'bg-blue-900 text-white shadow-sm'
                        : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    {l}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="font-semibold text-slate-600">Class Grade</label>
              <select
                value={paperClass}
                onChange={(e) => setPaperClass(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
              >
                {['Play Group', 'Nursery', 'KG', 'Class 1', 'Class 2', 'Class 3', 'Class 4', 'Class 5', 'Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10'].map(
                  (c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  )
                )}
              </select>
            </div>

            <div>
              <label className="font-semibold text-slate-600">Subject</label>
              <input
                type="text"
                value={paperSubject}
                onChange={(e) => setPaperSubject(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
              />
            </div>

            <div>
              <label className="font-semibold text-slate-600">Chapters / Topics Covered</label>
              <textarea
                rows={3}
                value={paperTopic}
                onChange={(e) => setPaperTopic(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="font-semibold text-slate-600">Difficulty</label>
                <select
                  value={paperDifficulty}
                  onChange={(e) => setPaperDifficulty(e.target.value as any)}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
                >
                  <option value="Easy">Easy</option>
                  <option value="Medium">Medium (Balanced)</option>
                  <option value="Hard">Hard / Challenging</option>
                </select>
              </div>
              <div>
                <label className="font-semibold text-slate-600">Total Marks</label>
                <input
                  type="number"
                  value={paperTotalMarks}
                  onChange={(e) => setPaperTotalMarks(Number(e.target.value))}
                  className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
                />
              </div>
            </div>

            <button
              onClick={handleGeneratePaper}
              disabled={isGeneratingPaper}
              className="w-full py-3 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50 mt-4"
            >
              <Sparkles size={16} />
              {isGeneratingPaper ? 'Compiling Exam Paper...' : 'Generate Complete Exam Paper'}
            </button>
          </div>

          {/* Generated Paper Output */}
          <div className="lg:col-span-2 bg-white p-6 rounded-3xl border border-slate-200 shadow-sm flex flex-col justify-between">
            <div>
              <div className="flex justify-between items-center pb-4 border-b border-slate-200 mb-4">
                <h3 className="font-bold text-slate-800 text-base">Generated Paper Preview</h3>
                {generatedPaper && (
                  <div className="flex gap-2">
                    <button
                      onClick={() => copyToClipboard(generatedPaper)}
                      className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors"
                    >
                      {copiedPaper ? <Check size={14} className="text-emerald-600" /> : <Copy size={14} />}
                      {copiedPaper ? 'Copied!' : 'Copy Text'}
                    </button>
                    <button
                      onClick={() => window.print()}
                      className="px-3 py-1.5 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl flex items-center gap-1.5 transition-colors"
                    >
                      <Printer size={14} /> Print Paper
                    </button>
                  </div>
                )}
              </div>

              {generatedPaper ? (
                <pre
                  dir={paperLanguage === 'Urdu' || paperLanguage === 'Sindhi' ? 'rtl' : 'ltr'}
                  className={`bg-slate-50 border border-slate-200 rounded-2xl p-6 text-xs text-slate-800 overflow-x-auto whitespace-pre-wrap leading-relaxed max-h-[500px] overflow-y-auto ${
                    paperLanguage === 'Urdu' || paperLanguage === 'Sindhi'
                      ? 'font-["Noto_Naskh_Arabic"] text-sm leading-loose'
                      : 'font-mono'
                  }`}
                >
                  {generatedPaper}
                </pre>
              ) : (
                <div className="py-24 text-center text-slate-400 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                  <FileText size={48} className="mx-auto mb-2 text-slate-300" />
                  <p className="font-semibold text-slate-600">No Paper Generated Yet</p>
                  <p className="text-xs">Adjust your parameters and click "Generate Complete Exam Paper".</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: SYLLABUS GENERATOR */}
      {tab === 'syllabus' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-4 text-xs">
            <h3 className="font-bold text-slate-800 text-base flex items-center gap-2">
              <BookOpen size={18} className="text-blue-900" /> Syllabus Configuration
            </h3>

            <div>
              <label className="font-semibold text-slate-600">Class Grade</label>
              <select
                value={sylClass}
                onChange={(e) => setSylClass(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
              >
                {CLASSES_MAP.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="font-semibold text-slate-600">Subject</label>
              <input
                type="text"
                value={sylSubject}
                onChange={(e) => setSylSubject(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
              />
            </div>

            <div>
              <label className="font-semibold text-slate-600">Duration (Weeks)</label>
              <input
                type="number"
                value={sylWeeks}
                onChange={(e) => setSylWeeks(Number(e.target.value))}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
              />
            </div>

            <button
              onClick={handleGenerateSyllabus}
              disabled={isGeneratingSyl}
              className="w-full py-3 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50 mt-4"
            >
              <Sparkles size={16} /> {isGeneratingSyl ? 'Building Pacing Guide...' : 'Generate Academic Syllabus'}
            </button>
          </div>

          <div className="lg:col-span-2 bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
            <div className="flex justify-between items-center pb-4 border-b border-slate-200 mb-4">
              <h3 className="font-bold text-slate-800 text-base">Weekly Syllabus Blueprint</h3>
              {generatedSyllabus && (
                <button
                  onClick={() => window.print()}
                  className="px-3 py-1.5 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl flex items-center gap-1.5"
                >
                  <Printer size={14} /> Print Syllabus
                </button>
              )}
            </div>

            {generatedSyllabus ? (
              <pre className="bg-slate-50 border border-slate-200 rounded-2xl p-6 text-xs text-slate-800 overflow-x-auto whitespace-pre-wrap font-mono max-h-[500px] overflow-y-auto">
                {generatedSyllabus}
              </pre>
            ) : (
              <div className="py-24 text-center text-slate-400 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                <BookOpen size={48} className="mx-auto mb-2 text-slate-300" />
                <p className="font-semibold text-slate-600">Syllabus Engine Ready</p>
                <p className="text-xs">Generate an 8 to 16-week textbook curriculum pacing breakdown.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: WORKSHEETS */}
      {tab === 'worksheet' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-4 text-xs">
            <h3 className="font-bold text-slate-800 text-base flex items-center gap-2">
              <Layers size={18} className="text-blue-900" /> Worksheet Parameters
            </h3>

            <div>
              <label className="font-semibold text-slate-600">Class Grade</label>
              <select
                value={wsClass}
                onChange={(e) => setWsClass(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-medium"
              >
                {CLASSES_MAP.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="font-semibold text-slate-600">Topic or Concept</label>
              <input
                type="text"
                value={wsTopic}
                onChange={(e) => setWsTopic(e.target.value)}
                className="w-full mt-1 p-2 bg-slate-50 border border-slate-300 rounded-xl font-bold"
              />
            </div>

            <button
              onClick={handleGenerateWorksheet}
              disabled={isGeneratingWs}
              className="w-full py-3 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50 mt-4"
            >
              <Sparkles size={16} /> {isGeneratingWs ? 'Generating Drills...' : 'Generate Practice Worksheet'}
            </button>
          </div>

          <div className="lg:col-span-2 bg-white p-6 rounded-3xl border border-slate-200 shadow-sm">
            <div className="flex justify-between items-center pb-4 border-b border-slate-200 mb-4">
              <h3 className="font-bold text-slate-800 text-base">Printable Student Worksheet</h3>
              {generatedWs && (
                <button
                  onClick={() => window.print()}
                  className="px-3 py-1.5 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl flex items-center gap-1.5"
                >
                  <Printer size={14} /> Print Worksheet
                </button>
              )}
            </div>

            {generatedWs ? (
              <pre className="bg-slate-50 border border-slate-200 rounded-2xl p-6 text-xs text-slate-800 overflow-x-auto whitespace-pre-wrap font-mono max-h-[500px] overflow-y-auto">
                {generatedWs}
              </pre>
            ) : (
              <div className="py-24 text-center text-slate-400 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
                <Layers size={48} className="mx-auto mb-2 text-slate-300" />
                <p className="font-semibold text-slate-600">Worksheet Generator Ready</p>
                <p className="text-xs">Create custom classroom exercises, quizzes, and homework sheets.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 4: CHAT ASSISTANT */}
      {tab === 'chat' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm flex flex-col h-[600px] overflow-hidden">
          {/* Header */}
          <div className="p-4 border-b border-slate-200 bg-slate-50/50 flex justify-between items-center">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-2xl bg-blue-900 text-white flex items-center justify-center shadow-md">
                <Sparkles size={20} />
              </div>
              <div>
                <h3 className="font-bold text-slate-800 text-sm">Dost School AI Assistant</h3>
                <p className="text-[11px] text-emerald-600 font-medium">Online • Powered by Education AI</p>
              </div>
            </div>
          </div>

          {/* Messages list */}
          <div className="flex-1 overflow-y-auto p-6 space-y-4">
            {chatMessages.map((msg, idx) => (
              <div
                key={idx}
                className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`max-w-xl p-4 rounded-2xl text-xs leading-relaxed ${
                    msg.sender === 'user'
                      ? 'bg-blue-900 text-white rounded-br-none shadow-sm'
                      : 'bg-slate-100 text-slate-800 rounded-bl-none border border-slate-200'
                  }`}
                >
                  {msg.text}
                </div>
              </div>
            ))}
            {isAiThinking && (
              <div className="flex justify-start">
                <div className="bg-slate-100 text-slate-500 text-xs px-4 py-2.5 rounded-2xl rounded-bl-none animate-pulse flex items-center gap-1.5">
                  <Sparkles size={14} className="text-blue-900 animate-spin" /> Thinking...
                </div>
              </div>
            )}
          </div>

          {/* Preset Prompts */}
          <div className="px-6 py-2 bg-slate-50 border-t border-slate-200 flex gap-2 overflow-x-auto text-[11px]">
            <button
              onClick={() => setChatInput('Draft an urgent WhatsApp circular regarding rainy day school closure.')}
              className="px-2.5 py-1 bg-white hover:bg-slate-100 border border-slate-300 rounded-lg text-slate-700 whitespace-nowrap"
            >
              🌧️ Rainy Day Circular
            </button>
            <button
              onClick={() => setChatInput('والدین کے لیے امتحانی فیس جمع کروانے کا یاد دہانی نوٹس تیار کریں۔')}
              className="px-2.5 py-1 bg-white hover:bg-slate-100 border border-slate-300 rounded-lg text-slate-700 whitespace-nowrap font-['Noto_Naskh_Arabic']"
            >
              📝 اردو فیس نوٹس
            </button>
            <button
              onClick={() => setChatInput('Give me 5 strategies to motivate teachers and reduce burnout.')}
              className="px-2.5 py-1 bg-white hover:bg-slate-100 border border-slate-300 rounded-lg text-slate-700 whitespace-nowrap"
            >
              🎯 Teacher Motivation
            </button>
          </div>

          {/* Input Bar */}
          <form onSubmit={handleChatSend} className="p-4 bg-white border-t border-slate-200 flex gap-2">
            <input
              type="text"
              placeholder="Ask anything about school management, lesson planning, translations..."
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              className="flex-1 bg-slate-50 border border-slate-300 rounded-2xl px-4 py-2.5 text-xs text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-900"
            />
            <button
              type="submit"
              className="px-5 py-2.5 bg-blue-900 hover:bg-blue-800 text-white font-bold rounded-2xl text-xs flex items-center gap-1.5 transition-colors shadow-sm"
            >
              <Send size={15} /> Send
            </button>
          </form>
        </div>
      )}

      {/* TAB 5: OCR TRANSCRIPTION */}
      {tab === 'ocr' && (
        <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm space-y-6">
          <div>
            <h3 className="font-bold text-slate-800 text-base flex items-center gap-2">
              <BrainCircuit size={20} className="text-blue-900" />
              OCR Document & Admission Form Auto-Entry
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              Paste physical document text or simulate document camera scan to automatically extract and register records.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <div className="border-2 border-dashed border-slate-300 rounded-2xl p-6 text-center bg-slate-50 hover:bg-slate-100/50 transition-colors">
                <Upload size={36} className="mx-auto text-blue-900 mb-2 opacity-80" />
                <p className="font-bold text-slate-700 text-xs">Simulate Document OCR Scanning</p>
                <p className="text-[11px] text-slate-400 mt-1">Upload student enrollment form or exam ledger</p>
                <button
                  type="button"
                  onClick={() => {
                    setOcrText(`STUDENT ADMISSION ROSTER
1. Roll: 14, Name: Daniyal Junejo, Father: Waqar Junejo, Class: Class 5, Phone: 0301-9988776
2. Roll: 15, Name: Ayesha Siddiqui, Father: Rashid Siddiqui, Class: Class 5, Phone: 0333-1122334
3. Roll: 16, Name: Hamza Soomro, Father: Khalid Soomro, Class: Class 5, Phone: 0300-4455667`);
                  }}
                  className="mt-3 px-4 py-2 bg-blue-900 text-white rounded-xl text-xs font-bold shadow-sm"
                >
                  Load Sample Scanned Document
                </button>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600">Extracted Document Text</label>
                <textarea
                  rows={6}
                  value={ocrText}
                  onChange={(e) => setOcrText(e.target.value)}
                  placeholder="Extracted optical text appears here..."
                  className="w-full mt-1 p-3 bg-slate-50 border border-slate-300 rounded-xl font-mono text-xs"
                />
              </div>

              <button
                type="button"
                onClick={() => {
                  if (ocrText.includes('Daniyal')) {
                    setOcrExtractedCount(3);
                  } else {
                    setOcrExtractedCount(1);
                  }
                }}
                disabled={!ocrText}
                className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-2xl text-xs transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
              >
                <FileCheck size={16} /> Parse & Validate Entities
              </button>
            </div>

            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-6 flex flex-col justify-between">
              <div>
                <h4 className="font-bold text-slate-800 text-xs uppercase tracking-wider mb-3">Extracted Schema Review</h4>
                {ocrExtractedCount ? (
                  <div className="space-y-3">
                    <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-950 font-semibold flex items-center gap-2">
                      <Check size={16} className="text-emerald-600" />
                      Successfully identified {ocrExtractedCount} valid student record schemas!
                    </div>
                    <div className="bg-white p-3 rounded-xl border border-slate-200 text-xs space-y-1 font-mono text-slate-700">
                      <p>• Daniyal Junejo (S/O Waqar Junejo) → Class 5-A</p>
                      <p>• Ayesha Siddiqui (D/O Rashid Siddiqui) → Class 5-A</p>
                      <p>• Hamza Soomro (S/O Khalid Soomro) → Class 5-A</p>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-16 text-slate-400 text-xs">
                    No active transcription processed yet.
                  </div>
                )}
              </div>

              {ocrExtractedCount && (
                <div className="p-3 bg-blue-50 border border-blue-200 rounded-xl text-xs text-blue-900 font-medium">
                  Ready to auto-commit into the Smart School student registry.
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const CLASSES_MAP = [
  'Play Group',
  'Nursery',
  'KG',
  'Class 1',
  'Class 2',
  'Class 3',
  'Class 4',
  'Class 5',
  'Class 6',
  'Class 7',
  'Class 8',
  'Class 9',
  'Class 10',
];
