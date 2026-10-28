package com.ab.cablecost.core;

/**
 * Bare-conductor weight, insulation weight and conductor cost.
 * The formulas are moved verbatim from CalculatorActivity (V0.5.5); they must
 * not change. tools/VerifyCoreParity.java compares them with the original code.
 */
public final class ConductorMath {
    /** Metal list index: 0 = copper, 1 = aluminium, 2 = CCA (tinned/clad, x1.05). */
    public static double densityGramsPerCm3(int metalIndex) {
        return (metalIndex == 1 || metalIndex == 2) ? 2.70 : 8.96;
    }

    public static double metalFactor(int metalIndex) {
        return (metalIndex >= 2) ? 1.05 : 1.0;
    }

    /** Weight in grams of {@code strands} wires of {@code diameterMm} over {@code lengthM}. */
    public static double conductorGrams(double diameterMm, double strands, double lengthM, int metalIndex) {
        if (diameterMm <= 0 || strands <= 0 || lengthM <= 0 || strands != Math.floor(strands))
            throw new IllegalArgumentException(
                    "المقاسات لازم تكون أكبر من صفر، وعدد الشعرات عدد صحيح.");
        double density = densityGramsPerCm3(metalIndex);
        double factor = metalFactor(metalIndex);
        return Math.PI / 4 * diameterMm * diameterMm * strands * lengthM * density * factor;
    }

    /** Metal cross-section in mm². */
    public static double crossSectionMm2(double diameterMm, double strands) {
        return Math.PI / 4 * Math.pow(diameterMm, 2) * strands;
    }

    /** Insulation weight from the difference between the insulated sample and the bare conductor. */
    public static double insulationFromWeight(double insulatedGrams, double bareGrams) {
        double plastic = insulatedGrams - bareGrams;
        return requireNotNegative(plastic);
    }

    /** Insulation weight from the geometry (outer diameters, density, length). */
    public static double insulationFromGeometry(double bareOdMm, double finishedOdMm,
                                                double density, double lengthM) {
        if (bareOdMm <= 0 || finishedOdMm <= bareOdMm || density <= 0)
            throw new IllegalArgumentException("راجع الأقطار والكثافة: لازم تكون موجبة وقطر العزل أكبر.");
        double plastic = Math.PI / 4 * (finishedOdMm * finishedOdMm - bareOdMm * bareOdMm)
                * lengthM * density;
        return requireNotNegative(plastic);
    }

    private static double requireNotNegative(double plastic) {
        if (plastic < 0)
            throw new IllegalArgumentException("وزن العزل طلع بالسالب. راجع وزن العينة.");
        return plastic;
    }

    public static void requireNonNegativePrice(double pricePerKg) {
        if (pricePerKg < 0) throw new IllegalArgumentException("السعر لازم يكون صفر أو أكبر.");
    }

    /** Result of the conductor-only cost screen. */
    public static final class Cost {
        public final double total, perMeter, kg;
        Cost(double total, double perMeter, double kg) {
            this.total = total; this.perMeter = perMeter; this.kg = kg;
        }
    }

    public static Cost conductorCost(double conductorGrams, double pricePerKg, double lengthM) {
        double metalKg = conductorGrams / 1000.0;
        double total = metalKg * pricePerKg;
        return new Cost(total, total / lengthM, metalKg);
    }

    private ConductorMath() {}
}
