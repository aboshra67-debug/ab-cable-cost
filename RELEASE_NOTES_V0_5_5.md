# AB Cable Cost — V0.5.5 (versionCode 12) — Safe Patch على V0.5.4

> الحالة: **السورس جاهز ومتراجع، لكن الـAPK لم يُبنَ بعد.** بيئة العمل اللي اشتغلت فيها ما كانش فيها
> Android SDK ولا Gradle dependencies (dl.google.com وmaven.google.com محجوبين)، فالبناء والتوقيع
> والاختبار على المحاكي هيتم على GitHub Actions بعد ما تضيف الـSecrets (خطوات في `signing/README.md`).

## 1) التوقيع الثابت (مشكلة التحديث بدون مسح البيانات)
* السبب الجذري المؤكد: كل Run على GitHub كان بيولّد مفتاح **debug عشوائي جديد**، فأي نسخة جديدة
  بتتعارض مع اللي قبلها (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`). المفاتيح القديمة ضاعت، فمفيش طريقة
  تحديث فوق النسخ القديمة بأي مفتاح جديد.
* الحل: نوع بناء جديد `release` بـpackage جديد **`com.ab.cablecost.app`** (ما اتثبّتش قبل كده)
  يتثبّت **جنب** النسخ القديمة ومن غير مسح أي حاجة، وموقّع بمفتاح دائم (RSA 4096، صلاحية حتى 2056،
  v1+v2+v3). بعد التثبيت الأول كل الإصدارات تتحدّث فوقه عادي.
* المفتاح **مش داخل السورس ولا الـZIP**: بيتقري من Secrets/متغيرات بيئة. بصمته العامة (SHA-256) محفوظة في
  `signing/EXPECTED_CERT_SHA256.txt`، والـworkflow بيرفض أي APK موقّع بغيرها.
* `debug` و`parallel` متسابوا كما هما (لا حذف).

## 2) مشكلة «فشل التحليل» على الموبايل — الحالة الصادقة
* الصورة المرفقة كانت لنسخة `company-selector-0.5.3`، مش 0.5.4.
* فحصت الـAPK المرفق (V0.5.4 parallel): فيه توقيع v2 سليم، الـmanifest صالح، `resources.arsc` غير مضغوط ومحاذى،
  وSHA-256 مطابق. يعني الملف نفسه مش مكسور.
* **السبب الدقيق على Realme/ColorOS غير مؤكد** (ما عنديش الجهاز ولا لوج adb). الاحتمالات: (أ) نسخة
  debuggable بترفضها الحماية، (ب) ملف نزل ناقص/اتغير من رابط المحادثة، (ج) تعارض توقيع بيظهر كخطأ تحليل.
  نسخة `release` غير debuggable وبحزمة جديدة بتستبعد (أ) و(ج). لو لسه فشل: `INSTALL_TROUBLESHOOTING_AR.md`.

## 3) الـ150 شركة في المكتبة والبحث والفلترة
* تأكدت إن الـ150 اسم بتظهر في اختيار «الشركة» بلا تكرار. Belden وElsewedy Electric وPrysmian موجودين
  أصلًا ضمن الـ150 فبيظهروا مرة واحدة بعلامة «لها كابلات موثقة». فحص آلي جديد بيضمن إن العدد يفضل 150 بالضبط
  (أي اسم علامة موثقة جديد لازم يطابق اسمه في الدليل حرفيًا).
* **كان ناقص:** (1) فلتر «الدولة» كان بيعرض 3 دول بس (دول الكابلات الموثقة). (2) البحث بالإنجليزي
  (`China`, `Egypt`) ما كانش بيلاقي الشركات لأن دولها محفوظة بالعربي فقط.
* **اتعدّل (إضافي فقط):** `CountryNames` (أسماء الـ33 دولة بـAR/EN/FR/TR)، فلتر الدولة بقى فيه الـ33 دولة،
  اختيار دولة بيعرض شركاتها المرشحة (مع «عرض الكل» لدليل الشركات مفلتر بنفس الدولة)، البحث في
  «الشركة» وفي شريط البحث بيفهم اسم الدولة بأي لغة، وصف كل شركة بيظهر معاه دولتها.
  حالة الشركات فضلت `pending_review` ومفيش أي مواصفة اتضافت لها.

## 4) المكتبة العالمية — 5 سجلات جديدة من داتا شيت الشركات نفسها (من 3 إلى 8)
| الموديل | الفئة | المصدر الأصلي | المراجعة |
|---|---|---|---|
| Belden 9841 (RS-485، زوج 24 AWG) | Data | catalog.belden.com/techdata/EN/9841_techdata.pdf | rev 0.725 · 25 Feb 2026 |
| Belden 8760 (زوج 18 AWG بشيلد) | Control | …/8760_techdata.pdf | rev 0.667 · 20 Feb 2026 |
| Belden 1694A (RG-6 SDI) | Coax | …/1694A_techdata.pdf | rev 0.716 · 27 May 2026 |
| Belden 1583A (Cat 5e U/UTP) | Data | …/1583A_techdata.pdf | rev 0.796 · 06 Jul 2026 |
| Prysmian NYY 0,6/1KV CPR E — 4×16 RM | Power | datasheet.prysmiangroup.com/pdf/datasheet/en/332526 | مولّد 28/10/2026 (لا رقم مراجعة) |

* كل رقم منقول حرفيًا من الداتا شيت. الاقتباسات محفوظة في `tools/source_evidence/` وفحص `VerifyLibraryData`
  بيفشل لو أي رقم في السجل مش موجود في الاقتباس (اختبرت ده بتغيير رقم متعمد فاتكشف).
* ما اتخمّنش أي حاجة: تحمل التيار لـ1694A و1583A ومقاومة/سماكات غير المنشورة = `null` أو «غير منشور»؛
  شيلد NYY = `null` لأن النشرة ما بتذكرهوش. معنى `-J` و`DC` و`trefoil` غير معرّف في نشرة Prysmian فاتنقل كما هو.
* **تنبيه صدق:** قراءة الـPDF تمت بأداة استخراج نص آلية (مرتين بصيغتين مختلفتين وطابقت)، مش بعين بشرية.
  افتح رابط PDF من الكارت وقارن قبل ما تعتمد أي سجل في البيع.
* نشرة Prysmian بتتولّد ساعة الطلب (التاريخ في تذييلها = تاريخ الطلب)، فالمراجعة المسجّلة هي تاريخ الجلب.
* السجلات الـ3 القديمة اتسابت كما هي بدون أي تعديل.

## 5) الملفات والدوال المعدّلة (لا حذف لأي كود شغال أو بيانات)
| الملف | التغيير |
|---|---|
| `app/build.gradle` | `signingConfigs.release` من متغيرات بيئة، نوع `release` (`.app`)، `abVersionCode`، versionCode 12 / `0.5.5-stable` |
| `VerifiedCableData.java` | +5 سجلات في آخر الـstatic block |
| `CountryNames.java` | **ملف جديد** (Java صرف) |
| `ManufacturerCandidates.java` | `Record.searchKey()` بيضيف أسماء الدولة بالأربع لغات |
| `LibraryActivity.java` | `countryLabel`, `countryKeyAr` (جديدتان)، `uniqueOptions("country")`، `ManufacturerChoice`، `manufacturerChooser` (بحث/عرض الدولة)، `updateCompanyMatches`، `openDirectory`، تصفير فلتر الدولة عند تغيير اللغة |
| `ManufacturerDirectoryActivity.java` | extra `country`، عرض أسماء الدول بلغة الواجهة، `pickCountry` |
| `CableArtView.java` | `isTwoConductor` (عدد الموصلات المرسومة فقط) |
| `.github/workflows/android-apk.yml` | job فحص البيانات، بناء/فحص/نشر الـrelease الموقّع، اختبار تثبيت وتحديث فعلي على API 35/36 |
| `tools/*` | `VerifyLibraryData.java`, `ci_verify_release_apk.sh`, `ci_release_install_test.sh`, `verify_country_filter_ui.py`, تعديل رقم 3→8 في `verify_library_ui.py`, `source_evidence/` |
| `signing/` | `README.md`, `EXPECTED_CERT_SHA256.txt` |

معادلات الحساب (`CalculatorActivity`, `CatalogActivity`, `CableCatalog`)، التصميم الكحلي/الذهبي، اللوجو، والبيانات
المخزنة **لم تُمَس**.

## 6) الاختبارات
**نجحت (في بيئتي):**
* `tools/verify_import_data.py`: 150 شركة / 33 دولة / 9 علامات، SHA-256 مطابق.
* `tools/VerifyLibraryData.java` (JDK 21): 8 سجلات مكتملة، كل رقم مطابق للاقتباس الأصلي، 150 شركة و33 دولة
  مترجمة، قائمة «الشركة» = 150 اسم بالضبط. + اختبار عكسي: رقم متغيّر عمدًا فشل كما هو متوقع.
* `javac` على كل ملفات السورس: لا أخطاء صياغة (أخطاء الحل الوحيدة = غياب Android SDK). الكلاسات الصرفة
  (`CountryNames`, `VerifiedCableData`) اتجمّعت وشغّلت فعليًا.
* `bash -n` للسكريبتات و`py_compile` للـPython، وتحليل YAML للـworkflow. وسكريبت فحص الـrelease اتجرّب على
  أدوات وهمية (mock) في حالات نجاح/فشل بصمة/فشل debuggable.
* فحص بنيوي للـAPK المرفق (v2، محاذاة، manifest، SHA-256).

**لم تُجرَ (وتحتاج GitHub Actions أو جهاز):**
* **تجميع Android الفعلي (Gradle/AGP) وبناء الـAPK** — مفيش SDK عندي.
* أي تثبيت/تشغيل على محاكي أو على موبايلك (Realme/Android 16). الـworkflow جاهز يجرّب API 35 و36.
* `apksigner verify` الحقيقي (السكريبت جاهز بس اتجرّب على mock).
* اختبار واجهة فلتر الدولة (`verify_country_filter_ui.py`): مكتوب وغير مجرّب، وفي الـworkflow **استشاري**.
* فتح كارت الكابلات الخمس الجديدة وتصدير PDF على جهاز (الكود مشترك مع السجلات القديمة وبيتعامل مع `null`).
* مطابقة بشرية للأرقام مع الـPDF الأصلي.

## 7) خطوات التشغيل
1. افرد الـZIP فوق مجلد الريبو (أو ارفع الملفات)، وضيف الـ4 Secrets من `signing/README.md`، واعمل push لـ`main`.
2. من Actions نزّل `AB-Cable-Cost-V0.5.5-Stable-Signed-APK` (الملف المخصص للموبايل).
   **متنزّلش** `CI-ONLY-Upgrade-Test`.
3. ثبّته على موبايلك (هيظهر جنب القديم باسم AB Cable Cost)، ولو فشل: `INSTALL_TROUBLESHOOTING_AR.md`.
