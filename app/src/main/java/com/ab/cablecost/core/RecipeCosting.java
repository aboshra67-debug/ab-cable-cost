package com.ab.cablecost.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory recipe costing, moved verbatim from CatalogActivity.calculate() (V0.5.5).
 * Raw field text is parsed here, in the same order as before, so the first error
 * the user sees is unchanged.
 */
public final class RecipeCosting {
    public static final class Row {
        public final String material, unit, amountText, wasteText;
        public final double price;
        public final int wasteUnitIndex;
        public Row(String material, String unit, String amountText, double price,
                   String wasteText, int wasteUnitIndex) {
            this.material = material; this.unit = unit; this.amountText = amountText;
            this.price = price; this.wasteText = wasteText; this.wasteUnitIndex = wasteUnitIndex;
        }
    }

    public static final class Line {
        public final String material, unit;
        public final double quantity, cost;
        Line(String material, String unit, double quantity, double cost) {
            this.material = material; this.unit = unit; this.quantity = quantity; this.cost = cost;
        }
    }

    public static final class Result {
        public final double total, operational, length;
        public final boolean missing;
        public final List<String> missingNames;
        public final List<Line> lines;
        Result(double total, double operational, double length, boolean missing,
               List<String> missingNames, List<Line> lines) {
            this.total = total; this.operational = operational; this.length = length;
            this.missing = missing; this.missingNames = missingNames; this.lines = lines;
        }
    }

    /**
     * @param operationUnitIndex 0 = per metre, otherwise per roll of {@code rollText} metres
     */
    public static Result calculate(String lengthText, List<Row> rows, String operationText,
                                   int operationUnitIndex, String rollText) {
        double l = NumberParser.nonNegative(lengthText, "الطول", false);
        if (l <= 0) throw new IllegalArgumentException("الطول لازم يكون أكبر من صفر.");
        double total = 0;
        boolean missing = false;
        List<String> missingNames = new ArrayList<>();
        List<Line> lines = new ArrayList<>();
        for (Row r : rows) {
            double usage = NumberParser.nonNegative(r.amountText, r.material, true);
            double price = r.price;
            double w = NumberParser.nonNegative(r.wasteText, r.material + " هالك", true);
            if (Double.isNaN(w)) w = 0;
            if (Double.isNaN(usage)) {
                missing = true;
                missingNames.add(r.material + " (الاستهلاك)");
                continue;
            }
            if (usage > 0 && price <= 0) {
                missing = true;
                missingNames.add(r.material + " (السعر)");
                continue;
            }
            double required = r.unit.equals("متر") ? usage * l : usage * l / 1000.0;
            double extra;
            if (r.wasteUnitIndex == 0) extra = required * w / 100;
            else if (r.unit.equals("متر"))
                throw new IllegalArgumentException("الهالك بالكيلو مش مناسب للخامة بالمتر.");
            else extra = w; // kg of wastage is kg regardless of purchase price unit
            double bought = required + extra;
            double cost = r.unit.equals("جرام") ? bought * 1000 * price : bought * price;
            total += cost;
            lines.add(new Line(r.material, r.unit,
                    r.unit.equals("جرام") ? bought * 1000 : bought, cost));
        }
        double op = NumberParser.nonNegative(operationText, "تكلفة التشغيل", true);
        if (Double.isNaN(op)) op = 0;
        double operational;
        if (operationUnitIndex == 0) operational = op * l;
        else {
            double roll = NumberParser.nonNegative(rollText, "طول اللفة", false);
            if (roll <= 0) throw new IllegalArgumentException("طول اللفة لازم أكبر من صفر.");
            operational = op * l / roll;
        }
        total += operational;
        return new Result(total, operational, l, missing, missingNames, lines);
    }

    private RecipeCosting() {}
}
