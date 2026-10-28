package com.ab.cablecost.core;

/**
 * Locale-tolerant number parsing shared by the calculators (Arabic-Indic digits,
 * Arabic decimal/thousands separators, comma decimals). Pure Java: no Android.
 * Error messages are the exact strings the screens showed before the refactor.
 */
public final class NumberParser {
    /** Trim, map Arabic separators to '.', and map any Unicode decimal digit to 0-9. */
    public static String normalize(String text) {
        String s = text.trim().replace('٫', '.').replace('٬', '.').replace(',', '.');
        StringBuilder converted = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int digit = Character.digit(c, 10);
            converted.append(digit < 0 ? c : (char) ('0' + digit));
        }
        return converted.toString();
    }

    /** Calculator semantics: any finite number (sign is validated by the caller). */
    public static double finite(String text, String title) {
        try {
            double value = Double.parseDouble(normalize(text));
            if (Double.isNaN(value) || Double.isInfinite(value))
                throw new NumberFormatException();
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("راجع " + title + " واكتب رقم صحيح.");
        }
    }

    /**
     * Costing semantics: non-negative finite number. An empty field returns NaN
     * when {@code optional}.
     */
    public static double nonNegative(String text, String field, boolean optional) {
        String s = normalize(text);
        if (optional && s.length() == 0) return Double.NaN;
        try {
            double n = Double.parseDouble(s);
            if (Double.isInfinite(n) || Double.isNaN(n) || n < 0) throw new NumberFormatException();
            return n;
        } catch (Exception e) {
            throw new IllegalArgumentException("راجع " + field + "؛ محتاج رقم صحيح غير سالب.");
        }
    }

    private NumberParser() {}
}
