package com.ab.cablecost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Source-backed cable specifications. Published finished cable kg/km are never
 * treated as the weight of individual constituent materials.
 * Generic sizes are templates, not manufacturer certified datasheets.
 */
public final class CableCatalog {
    public static final String THERMO = "كابلات ترمو";
    public static final String CAMERA = "كابلات كاميرات";
    public static final String FIRE = "كابلات إنذار حريق";
    public static final String SHIELD = "كابلات شيلد";

    public static final String ELSEWEDY_POWER =
        "https://elsewedyelectric.com/pdf/Catalouges/02424280/elsewedy-cables-power-cables-catalogue.pdf";
    public static final String ELSEWEDY_SPECIAL =
        "https://elsewedyelectric.com/pdf/Catalouges/02113240/special-cables-catalogue.pdf";
    public static final String BELDEN_8723 =
        "https://www.belden.com/products/cable/electronic-wire-cable/multi-pair-cable/8723";
    public static final String BELDEN_5120 =
        "https://www.belden.com/products/cable/safety-sound-security/fire-alarm-cable/5120fn";

    public static final String CU = "نحاس أحمر";
    public static final String TCU = "نحاس مقصدر";
    public static final String PVC_I = "PVC عزل";
    public static final String XLPE = "XLPE عزل";
    public static final String PE_F = "PE فوم";
    public static final String PE_S = "PE صلب";
    public static final String PP = "PP عزل";
    public static final String PVC_J = "PVC غلاف";
    public static final String FOIL = "فويل ألومنيوم / بوليستر";
    public static final String BRAID = "شيلد تجديل نحاس مقصدر";
    public static final String DRAIN = "سلك Drain نحاس مقصدر";
    public static final String FILLER = "حشو وشريط لف";

    public static final List<String> ALL_MATERIALS = Collections.unmodifiableList(
        Arrays.asList(CU, TCU, "ألومنيوم أبيض", "ألمنيوم منحّس CCA",
            PVC_I, XLPE, PE_F, PE_S, PP, PVC_J, FOIL, BRAID, DRAIN,
            FILLER, "شريط ميكا", "أسلاك تسليح", "غلاف LSZH", "حبر طباعة"));

    public static class Spec {
        public final String id, family, manufacturer, model, standard, sourceUrl, note;
        public final String[] materials;
        public final int cores, strandsPerCore;
        public final double areaMm2, strandDiameterMm, finishedDiameterMm, finishedKgKm;
        public final boolean published;

        Spec(String id, String family, String manufacturer, String model,
             String standard, int cores, double areaMm2, int strandsPerCore,
             double strandDiameterMm, double finishedDiameterMm, double finishedKgKm,
             String note, String sourceUrl, boolean published, String... materials) {
            this.id = id; this.family = family; this.manufacturer = manufacturer;
            this.model = model; this.standard = standard; this.cores = cores;
            this.areaMm2 = areaMm2; this.strandsPerCore = strandsPerCore;
            this.strandDiameterMm = strandDiameterMm; this.finishedDiameterMm = finishedDiameterMm;
            this.finishedKgKm = finishedKgKm; this.note = note;
            this.sourceUrl = sourceUrl; this.published = published;
            this.materials = materials;
        }
        public String display() { return model + " • " + manufacturer; }
        public String conductorMaterial() {
            return (family.equals(SHIELD)) ? TCU : CU;
        }
        public double conductorGramsPerMeter() {
            double area = strandsPerCore > 0 && strandDiameterMm > 0 ?
                Math.PI / 4 * strandDiameterMm * strandDiameterMm * strandsPerCore
                : areaMm2;
            if (area <= 0 || cores <= 0) return 0;
            return area * cores * 8.96 * (conductorMaterial().equals(TCU) ? 1.05 : 1.0);
        }
    }

    private static final List<Spec> SPECS = new ArrayList<>();

    static {
        // Published Elsewedy Power Cables Catalogue: 4C Cu/XLPE/PVC 0.6/1kV.
        SPECS.add(new Spec("SW_T_4_1_5", THERMO, "السويدي", "4×1.5 مم²",
            "Cu/XLPE/PVC 0.6/1kV", 4, 1.5, 0, 0, 10.3, 155,
            "XLPE عزل + PVC غلاف. القطر والوزن الإجمالي/كم من الجدول، مش أوزان خامات منفصلة.",
            ELSEWEDY_POWER, true, CU, XLPE, FILLER, PVC_J));
        SPECS.add(new Spec("SW_T_4_2_5", THERMO, "السويدي", "4×2.5 مم²",
            "Cu/XLPE/PVC 0.6/1kV", 4, 2.5, 0, 0, 11.4, 210,
            "مرجع كابل أربعة قلوب 2.5 مم²؛ وزن الكابل المنشور لا يصلح كوزن النحاس.",
            ELSEWEDY_POWER, true, CU, XLPE, FILLER, PVC_J));

        // Clearly labelled user-editable templates rather than manufacturer claims.
        for (int cores : new int[] {2, 3, 4, 5}) {
            for (double size : new double[] {0.75, 1.0, 1.5, 2.5, 4.0, 6.0}) {
                String id = "TMP_T_" + cores + "_" + String.valueOf(size).replace('.', '_');
                SPECS.add(new Spec(id, THERMO, "قالب قابل للتعديل",
                    cores + "×" + size + " مم²", "قالب Cu/PVC/PVC — غير موثق لشركة",
                    cores, size, 0, 0, 0, 0,
                    "مقاس شائع كبداية للوصفة. الطبقات والمقادير تحتاج تأكيد من داتا شيت المصنع.",
                    "", false, CU, PVC_I, FILLER, PVC_J));
            }
        }

        // Elsewedy Special Cables, Fire Alarm Stranded, BS EN 50288-7, p.83.
        fire("FA002001", 2, 1.0, 6.75, 62.7);
        fire("FA002002", 3, 1.0, 7.15, 80.75);
        fire("FA002003", 4, 1.0, 7.75, 100.3);
        fire("FA002004", 2, 1.5, 7.30, 75.5);
        fire("FA002005", 3, 1.5, 7.71, 99.2);
        fire("FA002006", 4, 1.5, 8.60, 128.0);

        // Elsewedy Special Cables, RG59 pages 58–59. One central conductor;
        // braid/shield metal amounts must be entered separately in the BOM.
        SPECS.add(new Spec("SW_RG59_F", CAMERA, "السويدي", "RG59 Foamed PE",
            "75 Ω — RG59 (Foamed PE)", 1, 0, 1, 0.81, 6.2, 50,
            "سنتر نحاس صلب Ø0.81 مم، PE فوم Ø3.7، فويل Al/PET، تجديل نحاس مقصدر تغطية 61%، غلاف FR-PVC.",
            ELSEWEDY_SPECIAL, true, CU, PE_F, FOIL, BRAID, PVC_J));
        SPECS.add(new Spec("SW_RG59_S", CAMERA, "السويدي", "RG59 Solid PE",
            "75 Ω — RG59 (Solid PE)", 1, 0, 1, 0.59, 6.3, 65,
            "سنتر نحاس صلب Ø0.59 مم، PE صلب Ø3.7، فويل Al/PET، تجديل نحاس مقصدر تغطية 95%، غلاف FR-PVC.",
            ELSEWEDY_SPECIAL, true, CU, PE_S, FOIL, BRAID, PVC_J));

        // Belden official datasheet: two twisted pairs, 22AWG 7x30AWG.
        SPECS.add(new Spec("BE_8723", SHIELD, "Belden", "8723 • 2 Pair 22 AWG",
            "CM 300V — PP / Individual Foil / PVC", 4, 0, 7, 0.2546, 4.064, 0,
            "4 موصلات مقصدر 7×AWG30، عزل PP، فويل لكل زوج 100%، Drain مقصدر 24AWG، غلاف PVC. قطر 4.064 مم.",
            BELDEN_8723, true, TCU, PP, FOIL, DRAIN, PVC_J));

        SPECS.add(new Spec("BE_5120", FIRE, "Belden", "5120FN • 2C 14 AWG",
            "NPLF — PVC/Nylon / Beldfoil / PVC", 2, 0, 1, 1.628, 7.696, 0,
            "موصلين صلب نحاس 14 AWG؛ عزل PVC/Nylon، شيلد Al/PET 100%، Drain 20AWG مقصدر، غلاف PVC.",
            BELDEN_5120, true, CU, PVC_I, FOIL, DRAIN, PVC_J));

        SPECS.add(new Spec("TMP_CCTV_COMBO", CAMERA, "قالب قابل للتعديل",
            "كاميرات RG59 + 2×0.75 طاقة", "قالب تجميعي — غير موثق لشركة",
            0, 0, 0, 0, 0, 0,
            "تركيب مجمّع: RG59 + سلكي طاقة. لازم تدخل أوزان الموصلات الإضافية ومكونات المجموعة بنفسك.",
            "", false, CU, PE_F, FOIL, BRAID, PVC_I, FILLER, PVC_J));
        SPECS.add(new Spec("TMP_SH_2_1", SHIELD, "قالب قابل للتعديل",
            "2×1 مم² • Shielded", "قالب تحكم بشيلد — غير موثق لشركة",
            2, 1.0, 0, 0, 0, 0,
            "قالب للاستخدام بعد إدخال قطر الموصل الفعلي وأوزان الفويل والـDrain والغلاف.",
            "", false, CU, PVC_I, FOIL, DRAIN, PVC_J));
    }

    private static void fire(String id, int cores, double area, double diameter, double weight) {
        SPECS.add(new Spec("SW_" + id, FIRE, "السويدي",
            cores + "×" + area + " مم² • " + id,
            "500V Shielded — BS EN 50288-7", cores, area, 0, 0, diameter, weight,
            "عزل PVC 105°C بسمك 0.44 مم، فويل Al/PET، سلك Drain مقصدر، غلاف PVC.",
            ELSEWEDY_SPECIAL, true, CU, PVC_I, FOIL, DRAIN, PVC_J));
    }

    public static List<Spec> all() {
        return Collections.unmodifiableList(SPECS);
    }
    public static List<Spec> match(String family, String manufacturer) {
        List<Spec> result = new ArrayList<>();
        for (Spec s : SPECS) {
            if (s.family.equals(family) &&
                (manufacturer.equals("كل الشركات") || s.manufacturer.equals(manufacturer))) {
                result.add(s);
            }
        }
        return result;
    }
    private CableCatalog() {}
}
