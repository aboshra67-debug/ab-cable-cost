import com.ab.cablecost.core.ConductorMath;
import com.ab.cablecost.core.NumberParser;
import com.ab.cablecost.core.RecipeCosting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Proves the extracted core (com.ab.cablecost.core) gives exactly the same numbers
 * and the same error messages as the ORIGINAL V0.5.5 screen code.
 *
 * The "legacy" methods below are verbatim copies of CalculatorActivity and
 * CatalogActivity from commit 6be23ac, with only the Android widgets replaced by
 * plain strings. Run on any JDK (no Android SDK):
 *
 *   javac -encoding UTF-8 -d build/parity app/src/main/java/com/ab/cablecost/core/*.java tools/VerifyCoreParity.java
 *   java -cp build/parity VerifyCoreParity
 */
public final class VerifyCoreParity {
    // ------------------------------------------------------------------ legacy (verbatim)
    static double legacyNumber(String text, String title) {
        String s = text.trim()
                .replace('٫', '.').replace('٬', '.').replace(',', '.');
        StringBuilder converted = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int digit = Character.digit(c, 10);
            converted.append(digit < 0 ? c : (char) ('0' + digit));
        }
        try {
            double value = Double.parseDouble(converted.toString());
            if (Double.isNaN(value) || Double.isInfinite(value))
                throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("راجع " + title + " واكتب رقم صحيح.");
        }
    }
    static double legacyConductor(String diameter, String strands, String length, int m) {
        double d = legacyNumber(diameter, "قطر الشعرة");
        double n = legacyNumber(strands, "عدد الشعرات");
        double l = legacyNumber(length, "الطول");
        if (d <= 0 || n <= 0 || l <= 0 || n != Math.floor(n))
            throw new IllegalArgumentException(
                    "المقاسات لازم تكون أكبر من صفر، وعدد الشعرات عدد صحيح.");
        double density = (m == 1 || m == 2) ? 2.70 : 8.96;
        double factor = (m >= 2) ? 1.05 : 1.0;
        return Math.PI / 4 * d * d * n * l * density * factor;
    }
    static double legacyArea(String diameter, String strands) {
        return Math.PI / 4 * Math.pow(legacyNumber(diameter, "قطر الشعرة"), 2)
                * legacyNumber(strands, "عدد الشعرات");
    }
    static double legacyInsulation(int mode, String d, String n, String l, int m, String insulated,
                                   String conductorOuter, String outer, String plasticDensity) {
        double bare = legacyConductor(d, n, l, m);
        double plastic;
        if (mode == 0) {
            plastic = legacyNumber(insulated, "وزن السلك المعزول") - bare;
        } else {
            double bareOD = legacyNumber(conductorOuter, "قطر الموصل");
            double finishedOD = legacyNumber(outer, "قطر السلك المعزول");
            double density = legacyNumber(plasticDensity, "كثافة العزل");
            if (bareOD <= 0 || finishedOD <= bareOD || density <= 0)
                throw new IllegalArgumentException("راجع الأقطار والكثافة: لازم تكون موجبة وقطر العزل أكبر.");
            plastic = Math.PI / 4 * (finishedOD * finishedOD - bareOD * bareOD)
                    * legacyNumber(l, "الطول") * density;
        }
        if (plastic < 0)
            throw new IllegalArgumentException("وزن العزل طلع بالسالب. راجع وزن العينة.");
        return plastic;
    }
    static double[] legacyCost(String price, String d, String n, String l, int m) {
        double p = legacyNumber(price, "سعر الكيلو");
        if (p < 0) throw new IllegalArgumentException("السعر لازم يكون صفر أو أكبر.");
        double metalKg = legacyConductor(d, n, l, m) / 1000.0;
        double total = metalKg * p;
        return new double[] {total, total / legacyNumber(l, "الطول"), metalKg};
    }
    static double legacyParse(String s, String field, boolean optional) {
        s = s.trim().replace('٫', '.').replace('٬', '.').replace(',', '.');
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i); int v = Character.digit(c, 10);
            b.append(v < 0 ? c : (char) ('0' + v));
        }
        if (optional && b.length() == 0) return Double.NaN;
        try {
            double n = Double.parseDouble(b.toString());
            if (Double.isInfinite(n) || Double.isNaN(n) || n < 0) throw new NumberFormatException();
            return n;
        } catch (Exception e) { throw new IllegalArgumentException("راجع " + field + "؛ محتاج رقم صحيح غير سالب."); }
    }
    static final class LRow {
        String material, unit, amount, waste; double price; int wasteUnit;
        LRow(String material, String unit, String amount, double price, String waste, int wasteUnit) {
            this.material = material; this.unit = unit; this.amount = amount;
            this.price = price; this.waste = waste; this.wasteUnit = wasteUnit;
        }
    }
    /** Returns {total, operational, missingFlag} + detail text + missing names. */
    static String legacyRecipe(String length, List<LRow> rows, String operation, int operationUnit, String operatingLength) {
        double l = legacyParse(length, "الطول", false);
        if (l <= 0) throw new IllegalArgumentException("الطول لازم يكون أكبر من صفر.");
        double total = 0; boolean missing = false;
        List<String> missingNames = new ArrayList<>();
        StringBuilder detail = new StringBuilder();
        for (LRow r : rows) {
            double usage = legacyParse(r.amount, r.material, true);
            double price = r.price;
            double w = legacyParse(r.waste, r.material + " هالك", true);
            if (Double.isNaN(w)) w = 0;
            if (Double.isNaN(usage)) { missing = true; missingNames.add(r.material + " (الاستهلاك)"); continue; }
            if (usage > 0 && price <= 0) { missing = true; missingNames.add(r.material + " (السعر)"); continue; }
            double required = r.unit.equals("متر") ? usage * l : usage * l / 1000.0;
            double extra;
            if (r.wasteUnit == 0) extra = required * w / 100;
            else if (r.unit.equals("متر"))
                throw new IllegalArgumentException("الهالك بالكيلو مش مناسب للخامة بالمتر.");
            else extra = w;
            double bought = required + extra;
            double cost = r.unit.equals("جرام") ? bought * 1000 * price : bought * price;
            total += cost;
            detail.append(String.format(java.util.Locale.US, "%s: %.2f %s | تكلفة %.2f\n", r.material,
                    r.unit.equals("جرام") ? bought * 1000 : bought, r.unit, cost));
        }
        double op = legacyParse(operation, "تكلفة التشغيل", true);
        if (Double.isNaN(op)) op = 0;
        double operational;
        if (operationUnit == 0) operational = op * l;
        else {
            double roll = legacyParse(operatingLength, "طول اللفة", false);
            if (roll <= 0) throw new IllegalArgumentException("طول اللفة لازم أكبر من صفر.");
            operational = op * l / roll;
        }
        total += operational;
        return bits(total) + "|" + bits(operational) + "|" + bits(l) + "|" + missing + "|" + missingNames + "|" + detail;
    }

    // ------------------------------------------------------------------ core wrappers
    static double coreConductor(String d, String n, String l, int m) {
        double dd = NumberParser.finite(d, "قطر الشعرة");
        double nn = NumberParser.finite(n, "عدد الشعرات");
        double ll = NumberParser.finite(l, "الطول");
        return ConductorMath.conductorGrams(dd, nn, ll, m);
    }
    static double coreArea(String d, String n) {
        return ConductorMath.crossSectionMm2(NumberParser.finite(d, "قطر الشعرة"),
                NumberParser.finite(n, "عدد الشعرات"));
    }
    static double coreInsulation(int mode, String d, String n, String l, int m, String insulated,
                                 String conductorOuter, String outer, String plasticDensity) {
        double bare = coreConductor(d, n, l, m);
        if (mode == 0)
            return ConductorMath.insulationFromWeight(NumberParser.finite(insulated, "وزن السلك المعزول"), bare);
        double bareOD = NumberParser.finite(conductorOuter, "قطر الموصل");
        double finishedOD = NumberParser.finite(outer, "قطر السلك المعزول");
        double density = NumberParser.finite(plasticDensity, "كثافة العزل");
        return ConductorMath.insulationFromGeometry(bareOD, finishedOD, density, NumberParser.finite(l, "الطول"));
    }
    static double[] coreCost(String price, String d, String n, String l, int m) {
        double p = NumberParser.finite(price, "سعر الكيلو");
        ConductorMath.requireNonNegativePrice(p);
        double grams = coreConductor(d, n, l, m);
        ConductorMath.Cost c = ConductorMath.conductorCost(grams, p, NumberParser.finite(l, "الطول"));
        return new double[] {c.total, c.perMeter, c.kg};
    }
    static String coreRecipe(String length, List<LRow> rows, String operation, int operationUnit, String operatingLength) {
        List<RecipeCosting.Row> input = new ArrayList<>();
        for (LRow r : rows)
            input.add(new RecipeCosting.Row(r.material, r.unit, r.amount, r.price, r.waste, r.wasteUnit));
        RecipeCosting.Result res = RecipeCosting.calculate(length, input, operation, operationUnit, operatingLength);
        StringBuilder detail = new StringBuilder();
        for (RecipeCosting.Line line : res.lines)
            detail.append(String.format(java.util.Locale.US, "%s: %.2f %s | تكلفة %.2f\n",
                    line.material, line.quantity, line.unit, line.cost));
        return bits(res.total) + "|" + bits(res.operational) + "|" + bits(res.length) + "|" + res.missing
                + "|" + res.missingNames + "|" + detail;
    }

    // ------------------------------------------------------------------ harness
    static String bits(double v) { return Long.toHexString(Double.doubleToRawLongBits(v)); }
    static String bits(double[] v) { StringBuilder b = new StringBuilder(); for (double d : v) b.append(bits(d)).append(','); return b.toString(); }
    interface Call { Object run(); }
    static String outcome(Call c) {
        try {
            Object o = c.run();
            if (o instanceof Double) return "OK " + bits((Double) o);
            if (o instanceof double[]) return "OK " + bits((double[]) o);
            return "OK " + o;
        } catch (IllegalArgumentException e) { return "ERR " + e.getMessage(); }
        catch (RuntimeException e) { return "CRASH " + e; }
    }
    static int checks = 0, bad = 0;
    static void same(String what, String a, String b) {
        checks++;
        if (!a.equals(b)) { bad++; if (bad <= 15) System.out.println("MISMATCH " + what + "\n  legacy: " + a + "\n  core:   " + b); }
    }

    static final String[] POOL = {"", " ", "0", "1", "0.2", "0.25", "2.5", "10", "37", "100", "1000", "1,5", "٣٫٥",
        "٠٫٢٥", "١٢", "abc", "-1", "-0.5", "1e3", "NaN", "Infinity", "7.", ".5", "1 000", "٣٬٥", "0.0", "00012", "12abc"};
    static String pick(Random r) { return POOL[r.nextInt(POOL.length)]; }
    static String pickNum(Random r) {  // mostly valid numbers so the happy paths are exercised
        if (r.nextInt(10) == 0) return pick(r);
        double[] v = {0.1, 0.2, 0.25, 0.4, 0.5, 1, 1.5, 2, 2.5, 3, 7, 19, 37, 61, 100, 305, 1000};
        return String.valueOf(v[r.nextInt(v.length)]);
    }

    public static void main(String[] args) {
        Random r = new Random(20261028L);
        String[] units = {"متر", "جرام", "كجم"};
        for (int i = 0; i < 60000; i++) {
            String d = pickNum(r), n = pickNum(r), l = pickNum(r), p = pickNum(r), ins = pickNum(r);
            String co = pickNum(r), fo = pickNum(r), den = pickNum(r);
            int m = r.nextInt(3), mode = r.nextInt(2);
            same("conductor", outcome(() -> legacyConductor(d, n, l, m)), outcome(() -> coreConductor(d, n, l, m)));
            same("area", outcome(() -> legacyArea(d, n)), outcome(() -> coreArea(d, n)));
            same("insulation", outcome(() -> legacyInsulation(mode, d, n, l, m, ins, co, fo, den)),
                    outcome(() -> coreInsulation(mode, d, n, l, m, ins, co, fo, den)));
            same("cost", outcome(() -> legacyCost(p, d, n, l, m)), outcome(() -> coreCost(p, d, n, l, m)));
            // Use the same optional flag on both sides for parse().
            boolean opt = r.nextBoolean();
            same("parse-opt", outcome(() -> legacyParse(n, "حقل", opt)), outcome(() -> NumberParser.nonNegative(n, "حقل", opt)));
            same("finite", outcome(() -> legacyNumber(n, "حقل")), outcome(() -> NumberParser.finite(n, "حقل")));
            // factory recipe
            int count = 1 + r.nextInt(5);
            List<LRow> rows = new ArrayList<>();
            for (int k = 0; k < count; k++)
                rows.add(new LRow("خامة" + k, units[r.nextInt(3)], pickNum(r),
                        r.nextInt(6) == 0 ? 0 : 5 + r.nextInt(900) / 4.0, r.nextBoolean() ? pickNum(r) : "", r.nextInt(2)));
            String len = pickNum(r), op = pickNum(r), roll = pickNum(r);
            int opUnit = r.nextInt(2);
            same("recipe", outcome(() -> legacyRecipe(len, rows, op, opUnit, roll)),
                    outcome(() -> coreRecipe(len, rows, op, opUnit, roll)));
        }
        // Reference sanity values worked out by hand (copper 8.96 g/cm3):
        // 0.2 mm x 7 strands x 100 m -> pi/4*0.04*7*100*8.96
        double expected = Math.PI / 4 * 0.2 * 0.2 * 7 * 100 * 8.96;
        same("hand-copper", "OK " + bits(expected), outcome(() -> ConductorMath.conductorGrams(0.2, 7, 100, 0)));
        System.out.println("checks=" + checks + " mismatches=" + bad);
        if (bad != 0) { System.out.println("FAIL"); System.exit(1); }
        System.out.println("PASS: core gives identical numbers and error messages to the original screen code");
    }
    private VerifyCoreParity() {}
}
