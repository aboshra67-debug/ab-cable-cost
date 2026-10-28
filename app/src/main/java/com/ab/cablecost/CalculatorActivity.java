package com.ab.cablecost;

import com.ab.cablecost.core.ConductorMath;
import com.ab.cablecost.core.NumberParser;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import java.util.Locale;

/**
 * AB Cable Cost - offline calculator. Layout-only refactor from the working 0.1.1
 * baseline. Conductor densities and 5% estimation factors remain unchanged.
 */
public class CalculatorActivity extends Activity {
    private static final int NAVY = Color.rgb(14, 29, 51);
    private static final int GOLD = Color.rgb(206, 164, 82);
    private static final int PAGE = Color.rgb(246, 248, 251);
    private static final int INK = Color.rgb(25, 41, 62);
    private static final int MUTED = Color.rgb(107, 119, 136);
    private static final int BORDER = Color.rgb(223, 230, 237);
    private static final int ERROR = Color.rgb(169, 48, 54);
    private static final int GOOD_BG = Color.rgb(249, 245, 233);

    private LinearLayout root, content, weighFields, geometryFields;
    private EditText diameter, strands, length, insulated, conductorOuter, outer, plasticDensity, price;
    private Spinner metal, category, mode;
    private LinearLayout conductorResult, insulationResult, costResult;
    private TextView conductorText, insulationText, costText;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable background(int color, int radius, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (strokeColor != Color.TRANSPARENT) d.setStroke(dp(1), strokeColor);
        return d;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        return layout;
    }

    private TextView label(String text, int size, int color, boolean strong) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        t.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        if (strong) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setIncludeFontPadding(true);
        return t;
    }

    private void gap(LinearLayout parent, int pixels) {
        View v = new View(this);
        parent.addView(v, new LinearLayout.LayoutParams(1, dp(pixels)));
    }

    private LinearLayout makeCard(String index, String heading, String description) {
        LinearLayout card = column();
        card.setPadding(dp(15), dp(15), dp(15), dp(17));
        card.setBackground(background(Color.WHITE, 17, BORDER));
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cp.bottomMargin = dp(14);
        content.addView(card, cp);

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView number = label(index, 13, NAVY, true);
        number.setGravity(Gravity.CENTER);
        number.setBackground(background(GOOD_BG, 9, Color.TRANSPARENT));
        titleRow.addView(number, new LinearLayout.LayoutParams(dp(32), dp(32)));

        TextView headingView = label(heading, 19, INK, true);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        hp.rightMargin = dp(10);
        titleRow.addView(headingView, hp);
        card.addView(titleRow);
        gap(card, 5);
        TextView info = label(description, 12, MUTED, false);
        card.addView(info);
        gap(card, 14);
        return card;
    }

    private void sectionLabel(LinearLayout parent, String title) {
        TextView t = label(title, 14, INK, true);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = dp(7);
        parent.addView(t, p);
    }

    private EditText input(LinearLayout parent, String title, String value, String hint) {
        sectionLabel(parent, title);
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setTextSize(17);
        e.setTextColor(INK);
        e.setHintTextColor(MUTED);
        e.setHint(hint);
        e.setText(value);
        e.setSelectAllOnFocus(true);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        e.setPadding(dp(13), 0, dp(13), 0);
        e.setBackground(background(PAGE, 11, BORDER));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(49));
        parent.addView(e, lp);
        return e;
    }

    private Spinner choose(LinearLayout parent, String title, String... options) {
        sectionLabel(parent, title);
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup rowParent) {
                return spinnerItem(getItem(position), false);
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup rowParent) {
                return spinnerItem(getItem(position), true);
            }
        };
        spinner.setAdapter(adapter);
        spinner.setBackground(background(PAGE, 11, BORDER));
        spinner.setPadding(dp(12), 0, dp(12), 0);
        spinner.setDropDownVerticalOffset(dp(5));
        parent.addView(spinner, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(51)));
        return spinner;
    }

    private TextView spinnerItem(String text, boolean inMenu) {
        TextView item = label(text == null ? "" : text, 15, INK, false);
        item.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        item.setPadding(dp(14), dp(8), dp(14), dp(8));
        item.setMinHeight(dp(inMenu ? 48 : 45));
        return item;
    }

    private void action(LinearLayout parent, String text, View.OnClickListener onClick) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(ColorStateList.valueOf(NAVY));
        button.setMinHeight(dp(50));
        button.setElevation(dp(1));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(52));
        p.topMargin = dp(13);
        parent.addView(button, p);
        button.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
            button.clearFocus();
            onClick.onClick(v);
        });
    }

    private LinearLayout addResult(LinearLayout parent) {
        LinearLayout result = column();
        result.setBackground(background(GOOD_BG, 12, GOLD));
        result.setPadding(dp(13), dp(12), dp(13), dp(13));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(12);
        parent.addView(result, lp);
        result.setVisibility(View.GONE);
        return result;
    }

    private TextView resultText(LinearLayout container) {
        TextView t = label("", 17, NAVY, true);
        t.setLineSpacing(dp(4), 1f);
        container.addView(t);
        return t;
    }

    private void showResult(LinearLayout box, TextView text, String message, boolean failure) {
        box.setVisibility(View.VISIBLE);
        box.setBackground(background(failure ? Color.rgb(255, 244, 244) : GOOD_BG,
                12, failure ? ERROR : GOLD));
        text.setTextColor(failure ? ERROR : NAVY);
        text.setText(message);
    }

    private void setMode(int selected) {
        weighFields.setVisibility(selected == 0 ? View.VISIBLE : View.GONE);
        geometryFields.setVisibility(selected == 1 ? View.VISIBLE : View.GONE);
        if (insulationResult != null) insulationResult.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        getWindow().getDecorView().setSystemUiVisibility(0);

        root = column();
        root.setBackgroundColor(NAVY);
        setContentView(root);

        // Android 15+ enforces edge-to-edge: make space for system bars so
        // the header never overlaps the clock, signal or navigation indicators.
        if (Build.VERSION.SDK_INT >= 21) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                int top, bottom;
                if (Build.VERSION.SDK_INT >= 30) {
                    top = insets.getInsets(WindowInsets.Type.statusBars()).top;
                    bottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                } else {
                    top = insets.getSystemWindowInsetTop();
                    bottom = insets.getSystemWindowInsetBottom();
                }
                if (root.getPaddingTop() != top || root.getPaddingBottom() != bottom)
                    root.setPadding(0, top, 0, bottom);
                return insets;
            });
            root.requestApplyInsets();
        }

        LinearLayout header = new LinearLayout(this);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(13), dp(18), dp(14));
        header.setBackgroundColor(NAVY);
        root.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        android.widget.ImageView logo = new android.widget.ImageView(this);
        logo.setImageResource(R.drawable.ab_brand_logo);
        logo.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("شعار AB Cable Cost الأصلي");
        header.addView(logo, new LinearLayout.LayoutParams(dp(85), dp(61)));

        LinearLayout titles = column();
        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        titleParams.rightMargin = dp(12);
        header.addView(titles, titleParams);
        titles.addView(label("AB Cable Cost", 22, Color.WHITE, true));
        titles.addView(label("حاسبة أوزان الكابلات", 12, GOLD, false));

        Button openCatalogue = new Button(this);
        openCatalogue.setText("دليل الكابلات والمقاسات وتكلفة التصنيع");
        openCatalogue.setAllCaps(false);
        openCatalogue.setTextColor(NAVY);
        openCatalogue.setTextSize(15);
        openCatalogue.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        openCatalogue.setBackgroundTintList(ColorStateList.valueOf(GOLD));
        LinearLayout.LayoutParams catalogueButtonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(52));
        root.addView(openCatalogue, catalogueButtonParams);
        openCatalogue.setOnClickListener(v ->
                startActivity(new android.content.Intent(CalculatorActivity.this, CatalogActivity.class)));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(PAGE);
        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        content = column();
        content.setPadding(dp(15), dp(17), dp(15), dp(24));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout intro = column();
        intro.setPadding(dp(3), 0, dp(3), dp(12));
        intro.addView(label("احسب وزن السلك بدقة", 23, NAVY, true));
        gap(intro, 4);
        intro.addView(label("اختار النوع، دخل المقاسات، وشوف النتيجة فورًا.", 13, MUTED, false));
        content.addView(intro);

        LinearLayout choice = makeCard("١", "بيانات الكابل", "اختار نوع الكابل والخامة اللي هتحسبها");
        category = choose(choice, "نوع الكابل",
                "أسلاك معزولة", "كابلات ترمو", "كابلات مسلحة",
                "كابلات كنترول", "كابلات مخصوصة");
        gap(choice, 12);
        metal = choose(choice, "خامة الموصل",
                "نحاس أحمر", "ألومنيوم أبيض",
                "ألومنيوم منحّس CCA (+5%)", "نحاس مقصدر (+5%)");

        LinearLayout conductor = makeCard("٢", "وزن الموصل", "دخل قطر الشعرة وعدد الشعرات والطول");
        LinearLayout dims = new LinearLayout(this);
        dims.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout first = column();
        LinearLayout second = column();
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        p1.leftMargin = dp(7);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        p2.rightMargin = dp(7);
        dims.addView(first, p1);
        dims.addView(second, p2);
        conductor.addView(dims);
        diameter = input(first, "قطر الشعرة (مم)", "0.16", "مثال: 0.16");
        strands = input(second, "عدد الشعرات", "48", "مثال: 48");
        gap(conductor, 13);
        length = input(conductor, "الطول (متر)", "100", "مثال: 100");
        action(conductor, "احسب وزن الموصل", v -> calc());
        conductorResult = addResult(conductor);
        conductorText = resultText(conductorResult);
        gap(conductor, 8);
        conductor.addView(label("الـ 5% للـ CCA والمقصدّر تقدير قابل للمراجعة، مش كثافة قياسية.",
                12, MUTED, false));

        LinearLayout insulation = makeCard("٣", "وزن العزل", "اختار طريقة الحساب المناسبة عندك");
        mode = choose(insulation, "طريقة حساب العزل", "بالميزان", "بالأقطار والكثافة");
        gap(insulation, 12);
        weighFields = column();
        insulated = input(weighFields, "وزن السلك المعزول كاملًا (جرام)",
                "", "اكتب وزن نفس الطول");
        insulation.addView(weighFields);
        geometryFields = column();
        conductorOuter = input(geometryFields, "القطر الخارجي الفعلي للموصل (مم)",
                "", "قيس قطر الموصل بدون عزل");
        gap(geometryFields, 12);
        outer = input(geometryFields, "القطر الخارجي للسلك بعد العزل (مم)",
                "", "قيس القطر بالعزل");
        gap(geometryFields, 12);
        plasticDensity = input(geometryFields, "كثافة مادة العزل (جم/سم³)",
                "", "من الداتا شيت");
        insulation.addView(geometryFields);
        setMode(0);
        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {
                setMode(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        action(insulation, "احسب وزن العزل", v -> calcInsulation());
        insulationResult = addResult(insulation);
        insulationText = resultText(insulationResult);

        LinearLayout costs = makeCard("٤", "تكلفة الموصل", "اختياري — لو عندك سعر الكيلو");
        price = input(costs, "سعر كيلو الموصل", "", "اكتب السعر");
        action(costs, "احسب التكلفة", v -> cost());
        costResult = addResult(costs);
        costText = resultText(costResult);

        TextView footer = label(
                "AB Software  •  الحسابات تقديرية وبتعتمد على صحة المقاسات المدخلة.",
                12, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        content.addView(footer);
    }

    private double number(EditText edit, String title) {
        return NumberParser.finite(edit.getText().toString(), title);
    }

    private double conductor() {
        double d = number(diameter, "قطر الشعرة");
        double n = number(strands, "عدد الشعرات");
        double l = number(length, "الطول");
        return ConductorMath.conductorGrams(d, n, l, metal.getSelectedItemPosition());
    }

    private void calc() {
        try {
            double grams = conductor();
            double area = ConductorMath.crossSectionMm2(
                    number(diameter, "قطر الشعرة"), number(strands, "عدد الشعرات"));
            showResult(conductorResult, conductorText,
                    String.format(Locale.US,
                            "وزن الموصل: %,.2f جرام\n= %,.4f كجم\nمساحة مقطع المعدن: %.4f مم²",
                            grams, grams / 1000.0, area), false);
        } catch (Exception e) {
            showResult(conductorResult, conductorText, e.getMessage(), true);
        }
    }

    private void calcInsulation() {
        try {
            double bare = conductor();
            double plastic;
            if (mode.getSelectedItemPosition() == 0) {
                plastic = ConductorMath.insulationFromWeight(
                        number(insulated, "وزن السلك المعزول"), bare);
            } else {
                double bareOD = number(conductorOuter, "قطر الموصل");
                double finishedOD = number(outer, "قطر السلك المعزول");
                double density = number(plasticDensity, "كثافة العزل");
                plastic = ConductorMath.insulationFromGeometry(bareOD, finishedOD, density,
                        number(length, "الطول"));
            }
            showResult(insulationResult, insulationText,
                    String.format(Locale.US,
                            "وزن العزل: %,.2f جرام\n= %,.4f كجم\n%s",
                            plastic, plastic / 1000,
                            mode.getSelectedItemPosition() == 0 ?
                                    "النتيجة من فرق وزن السلك المعزول والموصل." :
                                    "النتيجة هندسية تقديرية حسب القطر والكثافة."), false);
        } catch (Exception e) {
            showResult(insulationResult, insulationText, e.getMessage(), true);
        }
    }

    private void cost() {
        try {
            double p = number(price, "سعر الكيلو");
            ConductorMath.requireNonNegativePrice(p);
            double grams = conductor();
            ConductorMath.Cost c = ConductorMath.conductorCost(grams, p, number(length, "الطول"));
            showResult(costResult, costText,
                    String.format(Locale.US,
                            "تكلفة الموصل: %,.2f\nتكلفة المتر: %,.4f\nالوزن: %,.4f كجم\nمش شامل العزل أو التصنيع.",
                            c.total, c.perMeter, c.kg), false);
        } catch (Exception e) {
            showResult(costResult, costText, e.getMessage(), true);
        }
    }
}
