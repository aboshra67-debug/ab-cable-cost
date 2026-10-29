package com.ab.cablecost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Source-linked, manufacturer-published cable records for the technical LIBRARY.
 *
 * IMPORTANT: This is intentionally distinct from CableCatalog, which contains
 * manufacturing templates that must NEVER appear as certified product entries.
 * Unknown electrical properties stay null; no numerical value is inferred
 * merely from a nominal conductor size or a marketing illustration.
 */
public final class VerifiedCableData {
    public static final class Layer {
        public final String ar, en, descriptionAr, descriptionEn;
        Layer(String ar,String en,String descriptionAr,String descriptionEn) {
            this.ar=ar;this.en=en;this.descriptionAr=descriptionAr;this.descriptionEn=descriptionEn;
        }
        public String name(boolean arabic){return arabic?ar:en;}
        public String description(boolean arabic){return arabic?descriptionAr:descriptionEn;}
    }
    public static final class Cable {
        public final String id, model, size, brand, countryAr, countryEn;
        public final String category, categoryAr, categoryEn, construction;
        public final String voltage, current, conductorResistance, diameter;
        public final String temperature, insulation, shield, standards;
        public final String usesAr, usesEn, titleAr, titleEn, notesAr, notesEn;
        public final String sourceUrl, pdfUrl, revision, sourceName;
        public final List<Layer> layers;

        Cable(String id, String model,String size,String brand,String countryAr,String countryEn,
              String category,String categoryAr,String categoryEn,
              String construction,String voltage,String current,String conductorResistance,
              String diameter,String temperature,String insulation,String shield,
              String standards,String usesAr,String usesEn,String titleAr,String titleEn,
              String notesAr,String notesEn,String sourceUrl,String pdfUrl,
              String revision,String sourceName,Layer...layers) {
            this.id=id;this.model=model;this.size=size;this.brand=brand;
            this.countryAr=countryAr;this.countryEn=countryEn;this.category=category;
            this.categoryAr=categoryAr;this.categoryEn=categoryEn;this.construction=construction;
            this.voltage=voltage;this.current=current;
            this.conductorResistance=conductorResistance;this.diameter=diameter;
            this.temperature=temperature;this.insulation=insulation;this.shield=shield;
            this.standards=standards;this.usesAr=usesAr;this.usesEn=usesEn;
            this.titleAr=titleAr;this.titleEn=titleEn;this.notesAr=notesAr;this.notesEn=notesEn;
            this.sourceUrl=sourceUrl;this.pdfUrl=pdfUrl;
            this.revision=revision;this.sourceName=sourceName;
            this.layers=Collections.unmodifiableList(Arrays.asList(layers));
        }
        public String country(boolean ar){return ar?countryAr:countryEn;}
        public String category(boolean ar){return ar?categoryAr:categoryEn;}
        public String title(boolean ar){return ar?titleAr:titleEn;}
        public String uses(boolean ar){return ar?usesAr:usesEn;}
        public String note(boolean ar){return ar?notesAr:notesEn;}
        public String searchIndex(){
            StringBuilder s=new StringBuilder();
            s.append(model).append(' ').append(size).append(' ').append(brand).append(' ')
             .append(countryAr).append(' ').append(countryEn).append(' ')
             .append(categoryAr).append(' ').append(categoryEn).append(' ')
             .append(construction).append(' ').append(titleAr).append(' ')
             .append(titleEn).append(' ').append(usesAr).append(' ')
             .append(usesEn).append(' ').append(voltage);
            for(Layer l:layers)s.append(' ').append(l.ar).append(' ').append(l.en);
            return s.toString().toLowerCase(Locale.ROOT);
        }
    }

    private static Layer layer(String ar,String en,String descAr,String descEn){
        return new Layer(ar,en,descAr,descEn);
    }
    private static final List<Cable> CABLES;
    static {
        List<Cable> records=new ArrayList<>();
        records.add(new Cable(
            "BELDEN_8723","8723","2 Pair × 22 AWG","Belden","الولايات المتحدة","United States",
            "Control","كنترول وشيلد","Control & Shielded",
            "4 × 22 AWG (7×30), TC / PP / 2 individual Beldfoil® / PVC",
            "300 V (UL CM)","2.8 A / conductor @ 25 °C","18.2 Ω / 1000 ft (nominal)",
            "4.06 mm (nominal)","-20 to +75 °C","PP 0.23 mm / conductor",
            "Individually foil shielded pairs, 100%; 24 AWG (7×32) TC drain",
            "CM (UL) · IEC 60332-1-2 (reaction-to-fire as stated by manufacturer)",
            "إشارات التحكم منخفضة الجهد والقياس، الاتصالات بين الأجهزة والصوت منخفض المستوى.",
            "Low-voltage analogue/digital control, instrumentation, panel wiring and line-level audio.",
            "كابل تحكم وإشارات — زوجان مجدولان مع شيلد مستقل",
            "Audio, control and instrument cable — two individually foiled twisted pairs",
            "التيار 2.8 أمبير لكل موصل عند 25°C حسب داتا شيت Belden، وليس قيمة عامة لكل التركيبات. الرسم توضيحي.",
            "2.8 A per conductor at 25 °C is Belden's stated maximum, not a universal installed-cable rating. Diagram is schematic.",
            "https://www.belden.com/products/cable/electronic-wire-cable/multi-pair-cable/8723",
            "https://catalog.belden.com/techdata/EN/8723_techdata.pdf",
            "20 Feb 2026 · rev 0.576","Belden 8723 Technical Data Sheet",
            layer("الموصل","Conductor","4 موصلات نحاس مقصدر، كل موصل 22 AWG بتكوين 7×30.","4 × 22 AWG tinned-copper conductors, 7×30 stranding."),
            layer("العزل","Insulation","بولي بروبيلين PP، سُمك اسمي 0.23 مم؛ الألوان أحمر وأسود وأخضر وأبيض.","PP nominal thickness 0.23 mm; red, black, green and white."),
            layer("الشيلد","Shield","فويل ألومنيوم/بوليستر مستقل حول كل زوج، تغطية 100%.","Individual Al/polyester foil shield on each pair, 100% coverage."),
            layer("سلك التصريف","Drain wire","سلك نحاس مقصدر 24 AWG بتركيب 7×32.","24 AWG (7×32) tinned copper drain wire."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 0.51 مم، قطر نهائي اسمي 4.06 مم.","PVC jacket, nominal thickness 0.51 mm; OD 4.06 mm.")
        ));
        records.add(new Cable(
            "BELDEN_5120FN","5120FN","2C × 14 AWG","Belden","الولايات المتحدة","United States",
            "Fire Alarm","إنذار حريق","Fire Alarm",
            "2 × 14 AWG solid bare Cu / PVC+Nylon / overall Beldfoil® / PVC",
            "150 V (UL)","8 A / conductor @ 25 °C","2.67 Ω / 1000 ft (nominal)",
            "0.303 in ≈ 7.70 mm","-20 to +75 °C","PVC/Nylon 0.53 mm nominal",
            "Overall aluminium/polyester foil, 100%; 20 AWG (7×28) TC drain",
            "NPLF · NEC Article 760 · UL 1685 UL Loading",
            "دوائر إنذار الحريق غير المحدودة القدرة: إشارات وبدء تشغيل وتحكم وتنبيه في البيئات المعرضة للتشويش.",
            "Non-power-limited fire alarm circuits: initiating, control, notification and audio in electrically noisy spaces.",
            "كابل إنذار حريق بشيلد شامل — موصلان نحاس صلب",
            "Shielded fire alarm cable — two solid copper conductors",
            "تصنيف NPLF ومقاومة انتشار اللهب لا يعنيان استمرار التشغيل أثناء الحريق. التيار المذكور مشروط بـ25°C.",
            "NPLF / flame test rating is not circuit-integrity fire survival. Current is specified at 25 °C.",
            "https://www.belden.com/products/cable/safety-sound-security/fire-alarm-cable/5120fn",
            "https://catalog.belden.com/techdata/EN/5120FN_techdata.pdf",
            "07 Sep 2025 · rev 0.318","Belden 5120FN Technical Data Sheet",
            layer("الموصل","Conductor","موصلان نحاس أحمر صلب بمقاس 14 AWG.","2 solid bare-copper 14 AWG conductors."),
            layer("العزل","Insulation","عزل PVC/Nylon بسمك اسمي 0.53 مم، أحمر وأسود.","PVC/Nylon insulation, nominal 0.53 mm, red and black."),
            layer("الشيلد","Shield","فويل ألومنيوم/بوليستر عام بتغطية 100%.","Overall Al/polyester foil, 100% coverage."),
            layer("سلك التصريف","Drain wire","نحاس مقصدر 20 AWG بتكوين 7×28.","Tinned copper 20 AWG, 7×28 drain wire."),
            layer("الغلاف الخارجي","Outer jacket","غلاف PVC بسمك اسمي 1.1 مم مع خيط فتح.","PVC jacket, nominal 1.1 mm thick, with ripcord.")
        ));
        records.add(new Cable(
            "ELSEWEDY_RG59_F","RG59 Foamed PE","75 Ω · Ø0.81 mm centre","Elsewedy Electric",
            "مصر","Egypt","Coax","كابلات كاميرات ومحوري","Coax / CCTV",
            "Solid bare Cu Ø0.81 / Foamed PE / Al-PET foil / tinned Cu braid / FR-PVC",
            null,null,"Inner conductor ≤37 Ω/km (20 °C)",
            "6.20 mm (approx.)","up to +75 °C","Foamed PE Ø3.70 mm",
            "Al/PET foil 100% optical coverage + tinned copper braid 61%",
            "Based on MIL-C-17/29C (catalogue reference)",
            "إشارات الفيديو منخفضة القدرة والتوصيلات ذات الترددات اللاسلكية.",
            "Low-power video signalling and RF connections.",
            "كابل RG59 محوري — عزل فوم وشيلد مزدوج",
            "75 Ω RG59 coaxial cable — foamed PE and foil/braid shield",
            "75 أوم دي الممانعة المميزة للترددات وليست المقاومة المستمرة. لم يُذكر تحمل أمبير أو جهد مقنن في جدول هذا الموديل؛ مش هنعرض أرقام تقديرية.",
            "75 Ω is characteristic impedance, not DC resistance. No published ampacity or rated voltage in this model's cited table; none is invented.",
            "https://elsewedyelectric.com/pdf/Catalouges/02113240/special-cables-catalogue.pdf",
            "https://elsewedyelectric.com/pdf/Catalouges/02113240/special-cables-catalogue.pdf",
            "Special Cables catalogue · p. 59","Elsewedy Special Cables, p. 59",
            layer("الموصل المركزي","Centre conductor","سلك نحاس أحمر مصمت بقطر اسمي 0.81 مم.","Solid bare copper, nominal diameter 0.81 mm."),
            layer("العازل","Dielectric","بولي إيثيلين فوم بقطر اسمي 3.70 مم.","Foamed PE, nominal diameter 3.70 mm."),
            layer("الشيلد الأول","Foil shield","فويل ألومنيوم/بوليستر، تغطية بصرية 100%.","Aluminium/polyester foil, 100% optical coverage."),
            layer("التجديل","Braid shield","تجديل من النحاس المقصدر، تغطية بصرية 61%.","Tinned-copper braid, optical coverage 61%."),
            layer("الغلاف الخارجي","Outer sheath","PVC مثبط للهب، قطر الكابل التقريبي 6.20 مم.","Flame-retardant PVC, approximate OD 6.20 mm.")
        ));
        // ---- V0.5.5 additions -------------------------------------------------
        // Every value below is copied from the manufacturer's own datasheet named
        // in sourceName/pdfUrl (verbatim quotes are archived in
        // tools/source_evidence/ and checked by tools/VerifyLibraryData.java).
        // Anything the datasheet does not state is null or says so explicitly.
        records.add(new Cable(
            "BELDEN_9841","9841","1 Pair × 24 AWG (7×32)","Belden","الولايات المتحدة","United States",
            "Data","اتصالات وبيانات","Data & Telecom",
            "2 × 24 AWG (7×32) TC (1 pair) / PE / overall Beldfoil® + TC braid / PVC",
            "300 V (CM) · 30 V (UL AWM 2919)","2.1 A / conductor @ 25 °C (10 °C temperature rise)",
            "24 Ω / 1000 ft (79 Ω/km) (nominal)",
            "5.89 mm (0.232 in) (nominal)","-30 to +80 °C (UL temperature operating)",
            "PE 0.58 mm nominal (0.023 in)",
            "Al/polyester tape 100% with 24 AWG (7×32) TC drain wire + tinned-copper braid 90%",
            "UL 1685 UL Loading · IEC 60332-1-2 · NEC Art. 800 CM · AWM 2919 · DMX-512 · CE / UKCA / RoHS as listed",
            "RS-485 ونقاط البيع (POS) واتصالات الكمبيوتر والإشارات التناظرية منخفضة الجهد (4–20 مللي أمبير، 0–10 فولت) حسب نشرة الشركة.",
            "RS-485, POS, computer communications and low-voltage analogue signals (4–20 mA, 0–10 V …) as listed by the manufacturer.",
            "كابل RS-485 — زوج مجدول 24 AWG بشيلد فويل + جديلة",
            "RS-485 cable — 1 pair 24 AWG, overall foil + braid shield",
            "التيار 2.1 أمبير لكل موصل عند 25°C (ارتفاع حرارة 10°C) حسب Belden وليس تصنيفًا عامًا للتركيب. الممانعة المميزة الاسمية 120 أوم، والسعة بين الموصلين 12.8 pF/ft (42.0 pF/m). الرسم توضيحي.",
            "2.1 A per conductor at 25 °C (10 °C temperature rise) is Belden's stated value, not a universal installed rating. Nominal characteristic impedance 120 Ω; conductor-to-conductor capacitance 12.8 pF/ft (42.0 pF/m). Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/9841_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/9841_techdata.pdf",
            "25 Feb 2026 · rev 0.725","Belden 9841 Technical Data Sheet",
            layer("الموصل","Conductor","زوج واحد: موصلان نحاس مقصدر 24 AWG بتكوين 7×32.","1 pair: 2 × 24 AWG tinned-copper conductors, 7×32 stranding."),
            layer("العزل","Insulation","بولي إيثيلين PE بسمك اسمي 0.58 مم؛ ألوان الزوج White/Blue Stripe وBlue/White Stripe.","PE, nominal thickness 0.58 mm; pair colours White/Blue Stripe & Blue/White Stripe."),
            layer("الشيلد","Shield","شريط فويل ألومنيوم/بوليستر بتغطية 100% مع سلك تصريف 24 AWG (7×32) نحاس مقصدر، ثم جديلة نحاس مقصدر بتغطية 90%.","Al/polyester tape, 100% coverage, with 24 AWG (7×32) TC drain wire; then tinned-copper braid, 90% coverage."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 0.89 مم، القطر النهائي الاسمي 5.89 مم.","PVC jacket, nominal thickness 0.89 mm; OD 5.89 mm.")
        ));
        records.add(new Cable(
            "BELDEN_8760","8760","1 Pair × 18 AWG (16×30)","Belden","الولايات المتحدة","United States",
            "Control","كنترول وشيلد","Control & Shielded",
            "2 × 18 AWG (16×30) TC (1 pair) / PE / overall Beldfoil® / PVC",
            "300 V (CM) · 300 V (UL AWM 2092)","14 A / conductor @ 30 °C",
            "7.06 Ω / 1000 ft (nominal)",
            "5.64 mm (0.222 in) (nominal)","-20 to +60 °C (UL temperature operating)",
            "PE 0.46 mm nominal (0.018 in); black and clear",
            "Al/polyester tape 100% with 20 AWG (7×28) TC drain wire",
            "UL 1685 UL Loading · IEC 60332-1-2 · CPR Euroclass Eca · NEC Art. 800 CM · CEC/C(UL) CM · AWM 2092 · CE / UKCA as listed",
            "إشارات تناظرية منخفضة الجهد (4–20 مللي أمبير، 0–10 فولت)، تحكم رقمي منخفض الجهد (24 فولت)، صوت بمستوى الخط، اتصالات الكمبيوتر وتوصيلات اللوحات حسب نشرة الشركة.",
            "Low-voltage analogue signals (4–20 mA, 0–10 V …), low-voltage digital control (24 V …), line-level audio, computer communication and panel wiring as listed by the manufacturer.",
            "كابل إلكترونيات/تحكم — زوج 18 AWG بشيلد فويل شامل",
            "Electronic / control cable — 1 pair 18 AWG, overall foil shield",
            "التيار 14 أمبير لكل موصل عند 30°C حسب Belden وليس تصنيفًا عامًا لكل التركيبات. نسخ Plenum المذكورة: 88760 أو 87760 أو 82760. الرسم توضيحي.",
            "14 A per conductor at 30 °C is Belden's stated value, not a universal installed rating. Plenum numbers listed: 88760, 87760 or 82760. Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/8760_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/8760_techdata.pdf",
            "20 Feb 2026 · rev 0.667","Belden 8760 Technical Data Sheet",
            layer("الموصل","Conductor","موصلان نحاس مقصدر 18 AWG بتكوين 16×30 (زوج واحد).","2 × 18 AWG tinned-copper conductors, 16×30 stranding (1 pair)."),
            layer("العزل","Insulation","بولي إيثيلين PE بسمك اسمي 0.46 مم؛ الألوان Black وClear.","PE, nominal thickness 0.46 mm; colours Black and Clear."),
            layer("الشيلد","Shield","شريط فويل ألومنيوم/بوليستر بتغطية 100% مع سلك تصريف 20 AWG (7×28) نحاس مقصدر.","Al/polyester tape, 100% coverage, with 20 AWG (7×28) TC drain wire."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 0.71 مم، القطر النهائي الاسمي 5.64 مم.","PVC jacket, nominal thickness 0.71 mm; OD 5.64 mm.")
        ));
        records.add(new Cable(
            "BELDEN_1694A","1694A","75 Ω · RG-6 · 18 AWG solid BC","Belden","الولايات المتحدة","United States",
            "Coax","كابلات كاميرات ومحوري","Coax / CCTV",
            "Solid bare Cu 18 AWG / foam PE / Duofoil® (Al+Poly+Al) + TC braid 95% / PVC",
            "300 V (CMR, CMG)",null,
            "Centre conductor 6.4 Ω / 1000 ft (21 Ω/km); outer shield 3.8 Ω / 1000 ft (12 Ω/km) (nominal)",
            "6.96 mm (0.274 in) (nominal)","-30 to +75 °C (UL temperature operating)",
            "Foam PE Ø4.57 mm (0.180 in) nominal, white",
            "Duofoil® tri-laminate (Alum+Poly+Alum) 100% + tinned-copper braid 95%",
            "NEC CMR · CEC CMG · UL 1666 Vertical Shaft · CPR Euroclass Eca · CE / UKCA / RoHS as listed",
            "فيديو رقمي، UHDTV بسرعة 6 Gb/s، وHD-SDI 1080p حسب نشرة الشركة.",
            "Digital video, 6 Gb/s UHDTV and HD-SDI 1080p as listed by the manufacturer.",
            "كابل محوري SDI / RG-6 — 75 أوم، موصل نحاس مصمت 18 AWG",
            "75 Ω SDI coax, RG-6 — 18 AWG solid bare copper centre conductor",
            "75 أوم دي الممانعة المميزة وليست المقاومة المستمرة. لم تنشر النشرة تحمل تيار فلا نعرض رقمًا تقديريًا. سرعة الانتشار الاسمية 82%، والتوهين الاسمي: 1.95 dB/100ft عند 100 MHz و6.3 عند 1000 MHz و11.5 عند 3000 MHz.",
            "75 Ω is characteristic impedance, not DC resistance. The datasheet publishes no ampacity, so none is invented. Nominal velocity of propagation 82%; nominal attenuation 1.95 dB/100 ft @ 100 MHz, 6.3 @ 1000 MHz, 11.5 @ 3000 MHz.",
            "https://catalog.belden.com/techdata/EN/1694A_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/1694A_techdata.pdf",
            "27 May 2026 · rev 0.716","Belden 1694A Technical Data Sheet",
            layer("الموصل المركزي","Centre conductor","نحاس أحمر مصمت 18 AWG (0.040 in).","Solid bare copper, 18 AWG (0.040 in)."),
            layer("العازل","Dielectric","بولي إيثيلين فوم بقطر اسمي 4.57 مم، أبيض.","Foam PE, nominal diameter 4.57 mm, white."),
            layer("الشيلد الأول","Foil shield","Duofoil® ثلاثي الطبقات (ألومنيوم/بوليستر/ألومنيوم) بتغطية 100%.","Duofoil® tri-laminate (Alum+Poly+Alum), 100% coverage."),
            layer("التجديل","Braid shield","جديلة نحاس مقصدر بتغطية 95%.","Tinned-copper braid, 95% coverage."),
            layer("الغلاف الخارجي","Outer jacket","PVC؛ القطر النهائي الاسمي 6.96 مم. سمك الغلاف غير منشور في النشرة.","PVC jacket; OD 6.96 mm. Jacket thickness is not published in the datasheet.")
        ));
        records.add(new Cable(
            "BELDEN_1583A","1583A","4 Pair × 24 AWG solid BC","Belden","الولايات المتحدة","United States",
            "Data","اتصالات وبيانات","Data & Telecom",
            "4 pairs × 24 AWG solid bare Cu / PO / U/UTP / PVC",
            "300 V (CMR)",null,
            "Max. conductor DCR 28.6 Ω / 1000 ft (93.8 Ω/km)",
            "4.83 mm (0.190 in) (nominal)","-20 to +75 °C",
            "Polyolefin (PO); pair colours white/blue stripe & blue, white/orange stripe & orange, white/green stripe & green, white/brown stripe & brown",
            "U/UTP (unshielded, as designated in the datasheet)",
            "Category 5e (200 MHz) · ANSI/TIA-568.2-D · ISO/IEC 11801-1 · IEC 61156-5 · NEC Art. 800 CMR",
            "كابل أفقي للمباني (Premise Horizontal): Ethernet 1000BASE-T و100BASE-TX و10BASE-T وPoE وPoE+ وPoE++ حسب نشرة الشركة.",
            "Premise horizontal cable: Ethernet 1000BASE-T, 100BASE-TX, 10BASE-T, PoE, PoE+ and PoE++ as listed by the manufacturer.",
            "كابل شبكات Category 5e — 4 أزواج U/UTP",
            "Category 5e cable — 4 pair U/UTP, CMR",
            "القيمة المنشورة للمقاومة هي حد أقصى (Max) وليست قيمة اسمية. الممانعة 100 ± 15 أوم. لم تنشر النشرة تحمل تيار فلا نعرض رقمًا تقديريًا. الرسم توضيحي.",
            "The published conductor DCR is a maximum, not a nominal value. Impedance 100 ± 15 Ω. No ampacity is published, so none is invented. Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/1583A_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/1583A_techdata.pdf",
            "06 Jul 2026 · rev 0.796","Belden 1583A Technical Data Sheet",
            layer("الموصل","Conductor","4 أزواج؛ موصلات نحاس أحمر مصمت 24 AWG.","4 pairs of 24 AWG solid bare-copper conductors."),
            layer("العزل","Insulation","بولي أوليفين PO بألوان الأزواج: أبيض/أزرق وأزرق، أبيض/برتقالي وبرتقالي، أبيض/أخضر وأخضر، أبيض/بني وبني. سمك العزل غير منشور.","Polyolefin (PO); pair colours white/blue stripe & blue, white/orange stripe & orange, white/green stripe & green, white/brown stripe & brown. Insulation thickness is not published."),
            layer("الغلاف الخارجي","Outer jacket","PVC؛ القطر النهائي الاسمي 4.83 مم. سمك الغلاف غير منشور. التصنيف U/UTP أي بدون شيلد.","PVC jacket; OD 4.83 mm. Jacket thickness is not published. Designated U/UTP (no shield).")
        ));
        records.add(new Cable(
            "PRYSMIAN_NYY_4X16RM","NYY 0,6/1KV CPR E","4 × 16 mm² RM","Prysmian","إيطاليا","Italy",
            "Power","قوى","Power",
            "4 × 16 mm² Cu, stranded (RM) / PVC core insulation / black PVC outer sheath",
            "0.6/1 (1.2) kV (U0/U (Um))",
            "16 mm² multi-core: 102 A in ground · 79 A in air (ground 20 °C, 0.7 m, soil 1 K·m/W; air 30 °C)",
            "1.15 Ω/km @ 20 °C",
            "22.2 mm (nominal)",
            "Conductor max. +70 °C · short-circuit +160 °C · laying −5 to +50 °C",
            "PVC (core insulation)",
            null,
            "EN 60228 · HD 603 (0.6/1 kV distribution cables) · EN 13501-6 · CPR E (as in the product designation) · core identification per HD 308 S2",
            "لم تذكر النشرة استخدامات صريحة؛ النوع موصوف كابل توزيع 0.6/1 kV حسب HD 603.",
            "The datasheet lists no explicit applications; it is described as a 0.6/1 kV distribution cable per HD 603.",
            "كابل قوى NYY — 4 × 16 مم² نحاس، عزل وغلاف PVC",
            "NYY power cable — 4 × 16 mm² copper, PVC insulated and sheathed",
            "قيم التيار هي قيم جدول الشركة للكابلات متعددة القلوب بمقطع 16 مم² وبشروط الدفن/الهواء المذكورة، وليست تيار تصميم لتركيبك (لازم معاملات التجميع والحرارة). الوزن 1,084 كجم/كم، وأقل نصف قطر انحناء أثناء المد 267 مم، وتيار القصر (ثانية واحدة) 1.9 كيلو أمبير. كود -J ونوع Extruded مطبوعان في جدول النشرة كما هما. النشرة ديناميكية مولّدة بتاريخ 28/10/2026 ولا يوجد رقم مراجعة رسمي.",
            "Current values are the datasheet's multi-core table values for 16 mm² under the stated ground/air conditions, not a design current for your installation (grouping and temperature corrections apply). Weight 1,084 kg/km; minimum bending radius during laying 267 mm; short-circuit current (1 s) 1.9 kA. Type code '-J' and variant 'Extruded' are printed in the table as shown. Dynamically generated datasheet dated 28/10/2026; no formal revision number.",
            "https://datasheet.prysmiangroup.com/pdf/datasheet/en/332526",
            "https://datasheet.prysmiangroup.com/pdf/datasheet/en/332526",
            "RO00_NYY1KVCPRE · generated 28 Oct 2026","Prysmian NYY 0,6/1KV CPR E datasheet",
            layer("الموصل","Conductor","نحاس مجدول بمقطع 16 مم² (4 قلوب)، مقاومة 1.15 أوم/كم عند 20°C.","Stranded copper, 16 mm² (4 cores); resistance 1.15 Ω/km at 20 °C."),
            layer("العزل","Insulation","عزل القلوب PVC.","PVC core insulation."),
            layer("الغلاف الخارجي","Outer sheath","غلاف خارجي PVC أسود، القطر النهائي الاسمي 22.2 مم.","Black PVC outer sheath; nominal OD 22.2 mm.")
        ));
        records.add(new Cable(
            "BELDEN_9842","9842","2 Pair × 24 AWG (7×32)","Belden","الولايات المتحدة","United States",
            "Data","اتصالات وبيانات","Data & Telecom",
            "2 pairs × 24 AWG (7×32) TC / PE / overall Beldfoil® + TC braid / PVC",
            "300 V (CM) · 30 V (UL AWM 2919)","2.1 A / conductor @ 25 °C",
            "24 Ω / 1000 ft (79 Ω/km) (nominal)",
            "8.64 mm (0.340 in) (nominal)","-30 to +80 °C (UL temperature operating)",
            "PE 0.56 mm nominal (0.022 in)",
            "Al/polyester tape 100% with 24 AWG (7×32) TC drain wire + tinned-copper braid 90%",
            "UL 1685 UL Loading · IEC 60332-1-2 · NEC Art. 800 CM · AWM 2919 · DMX-512",
            "كابل RS-485 بزوجين مجدولين وشيلد فويل + جديلة، حسب وصف الداتا شيت. لم تُذكر استخدامات أخرى.",
            "RS-485 cable with two pairs and an overall foil + braid shield, as described in the datasheet title. No other applications are claimed.",
            "كابل RS-485 — زوجان مجدولان 24 AWG بشيلد فويل + جديلة",
            "RS-485 cable — 2 pairs 24 AWG, overall foil + braid shield",
            "التيار 2.1 أمبير لكل موصل عند 25°C حسب Belden وليس تصنيفًا عامًا للتركيب. الممانعة المميزة الاسمية 120 أوم، والسعة بين الموصلين 12.8 pF/ft. الرسم توضيحي.",
            "2.1 A per conductor at 25 °C is Belden's stated value, not a universal installed rating. Nominal characteristic impedance 120 Ω; conductor-to-conductor capacitance 12.8 pF/ft (42.0 pF/m). Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/9842_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/9842_techdata.pdf",
            "25 Feb 2026 · rev 0.736","Belden 9842 Technical Data Sheet",
            layer("الموصل","Conductor","زوجان: موصلات نحاس مقصدر 24 AWG بتكوين 7×32.","2 pairs of 24 AWG tinned-copper conductors, 7×32 stranding."),
            layer("العزل","Insulation","بولي إيثيلين PE بسمك اسمي 0.56 مم.","PE, nominal thickness 0.56 mm."),
            layer("الشيلد","Shield","شريط فويل ألومنيوم/بوليستر بتغطية 100% مع سلك تصريف 24 AWG (7×32) نحاس مقصدر، ثم جديلة نحاس مقصدر بتغطية 90%.","Al/polyester tape, 100% coverage, with 24 AWG (7×32) tinned-copper drain wire, then tinned-copper braid, 90% coverage."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 0.89 مم، القطر النهائي الاسمي 8.64 مم.","PVC jacket, nominal thickness 0.89 mm; OD 8.64 mm.")
        ));
        records.add(new Cable(
            "BELDEN_8761","8761","1 Pair × 22 AWG (7×30)","Belden","الولايات المتحدة","United States",
            "Control","كنترول وشيلد","Control & Shielded",
            "2 × 22 AWG (7×30) TC / PE / overall Beldfoil® / PVC",
            "300 V (CM) · 300 V (UL AWM 2092)","6 A / conductor @ 30 °C",
            "18.2 Ω / 1000 ft (nominal)",
            "4.45 mm (0.175 in) (nominal)","-20 to +60 °C",
            "PE 0.43 mm nominal (0.017 in)",
            "Al/polyester tape 100% with 22 AWG (7×30) TC drain wire",
            "UL 1685 UL Loading · IEC 60332-1-2 · NEC Art. 800 CM · AWM 2092",
            "كابل إلكتروني بموصلين مع شيلد فويل شامل، حسب وصف الداتا شيت. لم تُذكر استخدامات أخرى.",
            "Electronic cable with two conductors and an overall foil shield, as described in the datasheet title. No other applications are claimed.",
            "كابل إلكتروني — موصلان 22 AWG بشيلد فويل",
            "Electronic cable — 2 conductors 22 AWG, overall foil shield",
            "التيار 6 أمبير لكل موصل عند 30°C حسب Belden وليس تصنيفًا عامًا للتركيب. الممانعة المميزة 64 أوم، والسعة بين الموصلين 24 pF/ft. الرسم توضيحي.",
            "6 A per conductor at 30 °C is Belden's stated value, not a universal installed rating. Characteristic impedance 64 Ω; conductor-to-conductor capacitance 24 pF/ft (79 pF/m). Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/8761_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/8761_techdata.pdf",
            "20 Feb 2026 · rev 0.537","Belden 8761 Technical Data Sheet",
            layer("الموصل","Conductor","موصلان نحاس مقصدر 22 AWG بتكوين 7×30.","2 tinned-copper conductors, 22 AWG, 7×30 stranding."),
            layer("العزل","Insulation","بولي إيثيلين PE بسمك اسمي 0.43 مم؛ الألوان أسود وشفاف.","PE, nominal thickness 0.43 mm; colours black and clear."),
            layer("الشيلد","Shield","شريط فويل ألومنيوم/بوليستر بتغطية 100% مع سلك تصريف 22 AWG (7×30) نحاس مقصدر.","Al/polyester tape, 100% coverage, with 22 AWG (7×30) tinned-copper drain wire."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 0.64 مم، القطر النهائي الاسمي 4.45 مم.","PVC jacket, nominal thickness 0.64 mm; OD 4.45 mm.")
        ));
        records.add(new Cable(
            "BELDEN_3105A","3105A","1 Pair × 22 AWG (7×30)","Belden","الولايات المتحدة","United States",
            "Data","اتصالات وبيانات","Data & Telecom",
            "1 pair × 22 AWG (7×30) TC / foam PE / overall foil + TC braid / PVC",
            "300 V (CM, PLTC)","6 A / conductor @ 30 °C",
            "18.2 Ω / 1000 ft (nominal)",
            "7.47 mm (0.294 in) (nominal)","-20 to +60 °C",
            "PE (foam) 0.74 mm nominal (0.029 in)",
            "Al/polyester tape 100% with 22 AWG (7×30) TC drain wire + tinned-copper braid 65%",
            "UL 1685 UL Loading · IEC 60332-1-2 · NEC Art. 725 / 800 CM, PLTC · indoor, outdoor, sunlight and oil resistant",
            "كابل RS-485 بممانعة 120 أوم، مناسب للداخل والخارج ومقاوم لأشعة الشمس والزيت حسب الداتا شيت.",
            "RS-485 cable, 120 Ω; indoor/outdoor, sunlight- and oil-resistant per the datasheet.",
            "كابل RS-485 — زوج 22 AWG بشيلد فويل + جديلة، مقاوم للشمس والزيت",
            "RS-485 cable — 1 pair 22 AWG, foil + braid shield, sun and oil resistant",
            "التيار 6 أمبير لكل موصل عند 30°C حسب Belden وليس تصنيفًا عامًا للتركيب. الممانعة المميزة 120 أوم، وسرعة الانتشار 78%. الرسم توضيحي.",
            "6 A per conductor at 30 °C is Belden's stated value, not a universal installed rating. Characteristic impedance 120 Ω; velocity of propagation 78%; conductor-to-conductor capacitance 11 pF/ft (36 pF/m). Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/3105A_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/3105A_techdata.pdf",
            "20 Feb 2026 · rev 0.598","Belden 3105A Technical Data Sheet",
            layer("الموصل","Conductor","زوج واحد: موصلان نحاس مقصدر 22 AWG بتكوين 7×30.","1 pair: 2 tinned-copper conductors, 22 AWG, 7×30 stranding."),
            layer("العزل","Insulation","بولي إيثيلين فوم بسمك اسمي 0.74 مم.","Foam polyethylene, nominal thickness 0.74 mm."),
            layer("الشيلد","Shield","شريط فويل ألومنيوم/بوليستر بتغطية 100% مع سلك تصريف 22 AWG (7×30) نحاس مقصدر، ثم جديلة نحاس مقصدر بتغطية 65%.","Al/polyester tape, 100% coverage, with 22 AWG (7×30) tinned-copper drain wire, then tinned-copper braid, 65% coverage."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 1.2 مم، القطر النهائي الاسمي 7.47 مم.","PVC jacket, nominal thickness 1.2 mm; OD 7.47 mm.")
        ));
        records.add(new Cable(
            "BELDEN_8451","8451","1 Pair × 22 AWG (7×30)","Belden","الولايات المتحدة","United States",
            "Control","كنترول وشيلد","Control & Shielded",
            "2 × 22 AWG (7×30) TC (1 twisted pair, paper wrap) / PP / overall Beldfoil® / PVC",
            "300 V (UL)","2.9 A / conductor @ 25 °C",
            "17.8 Ω / 1000 ft (58.4 Ω/km) (nominal)",
            "3.51 mm (0.138 in) (nominal)","-20 to +75 °C",
            "PP 0.23 mm nominal (0.009 in)",
            "Al/polyester tape 100% with 22 AWG (7×30) TC drain wire",
            "UL 1666 Riser, FT4 · IEC 60332-1-2 · NEC Art. 800 CMR · CEC/C(UL) CMG",
            "كابل صوت وأجهزة (Audio & Instrumentation) بزوج مجدول وشيلد فويل شامل، حسب عنوان الداتا شيت.",
            "Audio & instrumentation cable with one twisted pair and an overall foil shield, per the datasheet title.",
            "كابل صوت وأجهزة — زوج 22 AWG بشيلد فويل",
            "Audio & instrumentation cable — 1 pair 22 AWG, overall foil shield",
            "التيار 2.9 أمبير لكل موصل عند 25°C حسب Belden وليس تصنيفًا عامًا للتركيب. الممانعة المميزة 45 أوم، والسعة بين الموصلين 34 pF/ft. الرسم توضيحي.",
            "2.9 A per conductor at 25 °C is Belden's stated value, not a universal installed rating. Characteristic impedance 45 Ω; conductor-to-conductor capacitance 34 pF/ft (110 pF/m). Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/8451_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/8451_techdata.pdf",
            "20 Feb 2026 · rev 0.667","Belden 8451 Technical Data Sheet",
            layer("الموصل","Conductor","زوج واحد: موصلان نحاس مقصدر 22 AWG بتكوين 7×30.","1 pair: 2 tinned-copper conductors, 22 AWG, 7×30 stranding."),
            layer("العزل","Insulation","بولي بروبيلين PP بسمك اسمي 0.23 مم؛ الألوان أسود وأحمر.","PP, nominal thickness 0.23 mm; colours black and red."),
            layer("الشيلد","Shield","شريط فويل ألومنيوم/بوليستر بتغطية 100% مع سلك تصريف 22 AWG (7×30) نحاس مقصدر.","Al/polyester tape, 100% coverage, with 22 AWG (7×30) tinned-copper drain wire."),
            layer("الغلاف الخارجي","Outer jacket","PVC بسمك اسمي 0.51 مم، القطر النهائي الاسمي 3.51 مم.","PVC jacket, nominal thickness 0.51 mm; OD 3.51 mm.")
        ));
        records.add(new Cable(
            "BELDEN_1700A","1700A","4 Pair × 24 AWG solid BC","Belden","الولايات المتحدة","United States",
            "Data","اتصالات وبيانات","Data & Telecom",
            "4 pairs × 24 AWG solid bare Cu / PO / U/UTP / PVC",
            "300 V (CMR)",null,
            "Max. conductor DCR 27 Ω / 1000 ft (90 Ω/km)",
            "5.13 mm (0.202 in) (nominal)","-20 to +75 °C (installation 0 to +50 °C)",
            "Polyolefin (PO); thickness not stated; pair colours white/blue stripe & blue, white/orange stripe & orange, white/green stripe & green, white/brown stripe & brown",
            "U/UTP (unshielded, as designated in the datasheet)",
            "Category 5e (350 MHz) · ANSI/TIA-568.2-D · UL 1666 Riser, FT4 · IEC 60332-1-2 · NEC Art. 800 CMR · CEC/C(UL) CMR",
            "كابل أفقي للمباني (Premise Horizontal) من فئة Category 5e للشبكات حسب نشرة الشركة.",
            "Category 5e premise horizontal cable for network cabling as described by the manufacturer.",
            "كابل شبكات Category 5e — 4 أزواج U/UTP، Riser (CMR)",
            "Category 5e cable — 4 pair U/UTP, Riser (CMR)",
            "القيمة المنشورة للمقاومة حد أقصى (Max) وليست اسمية. الممانعة 100 ± 15 أوم في نطاق 25 إلى 100 ميجاهرتز، وسرعة الانتشار 70%، وأقصى فقد إدخال 21.0 ديسيبل لكل 100 متر عند 100 ميجاهرتز. لم تنشر النشرة تحمل تيار ولا سمك العزل، فلا نعرض رقمًا تقديريًا. الرسم توضيحي.",
            "The published conductor DCR is a maximum, not a nominal value. Impedance 100 ± 15 Ω (25 to 100 MHz); nominal velocity of propagation 70%; maximum insertion loss 21.0 dB/100 m at 100 MHz. No ampacity or insulation thickness is published, so none is invented. Diagram is schematic.",
            "https://catalog.belden.com/techdata/EN/1700A_techdata.pdf",
            "https://catalog.belden.com/techdata/EN/1700A_techdata.pdf",
            "20 Feb 2026 · rev 0.529","Belden 1700A Technical Data Sheet",
            layer("الموصل","Conductor","4 أزواج؛ موصلات نحاس أحمر مصمت 24 AWG.","4 pairs of 24 AWG solid bare-copper conductors."),
            layer("العزل","Insulation","بولي أوليفين PO بألوان الأزواج: أبيض/أزرق وأزرق، أبيض/برتقالي وبرتقالي، أبيض/أخضر وأخضر، أبيض/بني وبني. سمك العزل غير منشور.","Polyolefin (PO); pair colours white/blue stripe & blue, white/orange stripe & orange, white/green stripe & green, white/brown stripe & brown. Insulation thickness is not published."),
            layer("الغلاف الخارجي","Outer jacket","PVC مع خيط فتح (ripcord)؛ القطر النهائي الاسمي 5.13 مم. سمك الغلاف غير منشور. التصنيف U/UTP أي بدون شيلد.","PVC jacket with ripcord; OD 5.13 mm. Jacket thickness is not published. U/UTP means unshielded.")
        ));
        CABLES=Collections.unmodifiableList(records);
    }

    public static List<Cable> all(){return CABLES;}
    public static Cable byId(String id){
        if(id==null)return null;
        for(Cable c:CABLES)if(c.id.equals(id))return c;
        return null;
    }
    public static String officialCount(){return String.valueOf(CABLES.size());}
    private VerifiedCableData(){}
}
