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
