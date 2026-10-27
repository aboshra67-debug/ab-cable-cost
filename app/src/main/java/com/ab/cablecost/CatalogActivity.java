package com.ab.cablecost;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Offline catalogue, editable BOM and centralized material prices.
 * The published finished cable weight never becomes a constituent mass estimate.
 */
public class CatalogActivity extends Activity {
    static final int NAVY=Color.rgb(14,29,51), GOLD=Color.rgb(206,164,82);
    static final int PAGE=Color.rgb(246,248,251), INK=Color.rgb(25,41,62);
    static final int MUTED=Color.rgb(105,119,135), BORDER=Color.rgb(222,228,235);
    static final int SOFT=Color.rgb(249,245,232), ERROR=Color.rgb(162,43,45);
    SharedPreferences pref;
    LinearLayout root, page, body;
    Spinner family, maker, model;
    CableCatalog.Spec selected;
    final List<CableCatalog.Spec> matches=new ArrayList<>();
    final List<Row> rows=new ArrayList<>();
    EditText length, operation, operatingLength;
    Spinner operationUnit;
    TextView calculation;

    static class Row {
        String material, unit;
        EditText amount, waste;
        Spinner wasteUnit;
        double theoretical;
        Row(String material, String unit, EditText amount, EditText waste,
            Spinner wasteUnit, double theoretical) {
            this.material=material; this.unit=unit; this.amount=amount;
            this.waste=waste; this.wasteUnit=wasteUnit; this.theoretical=theoretical;
        }
    }
    int dp(float px) { return (int)(px*getResources().getDisplayMetrics().density+.5f); }
    GradientDrawable bg(int color,int radius,int stroke) {
        GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));
        if(stroke!=Color.TRANSPARENT)d.setStroke(dp(1),stroke);return d;
    }
    LinearLayout vertical(){
        LinearLayout v=new LinearLayout(this);v.setOrientation(1);
        v.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);return v;
    }
    TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);
        t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        t.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
    }
    void gap(LinearLayout where,int amount) {
        where.addView(new View(this),new LinearLayout.LayoutParams(dp(1),dp(amount)));
    }
    void label(LinearLayout to,String title){
        TextView t=text(title,14,INK,true);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(6);
        to.addView(t,lp);
    }
    EditText field(LinearLayout to,String hint,String value) {
        EditText e=new EditText(this);
        e.setSingleLine(true);e.setTextSize(16);e.setHint(hint);e.setText(value);
        e.setTextColor(INK);e.setHintTextColor(MUTED);
        e.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        e.setPadding(dp(10),0,dp(10),0);
        e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setBackground(bg(PAGE,10,BORDER));
        to.addView(e,new LinearLayout.LayoutParams(-1,dp(47)));
        return e;
    }
    Spinner spinner(LinearLayout to,String... values){
        Spinner s=new Spinner(this);
        ArrayAdapter<String> a=new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,values){
            @Override public View getView(int index,View old,ViewGroup parent){
                return choice(getItem(index));
            }
            @Override public View getDropDownView(int index,View old,ViewGroup parent){
                return choice(getItem(index));
            }
        };
        s.setAdapter(a);s.setPadding(dp(9),0,dp(9),0);
        s.setBackground(bg(PAGE,10,BORDER));
        to.addView(s,new LinearLayout.LayoutParams(-1,dp(48)));return s;
    }
    TextView choice(String value){
        TextView t=text(value==null?"":value,14,INK,false);
        t.setPadding(dp(10),dp(8),dp(10),dp(8));t.setMinHeight(dp(46));return t;
    }
    void button(LinearLayout p,String title,Runnable handler,boolean gold){
        Button b=new Button(this);b.setAllCaps(false);b.setText(title);b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setTextColor(gold?NAVY:Color.WHITE);
        b.setBackgroundTintList(ColorStateList.valueOf(gold?GOLD:NAVY));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(49));
        lp.topMargin=dp(9);p.addView(b,lp);b.setOnClickListener(v->handler.run());
    }
    LinearLayout card(String heading){
        LinearLayout c=vertical();c.setPadding(dp(14),dp(14),dp(14),dp(14));
        c.setBackground(bg(Color.WHITE,15,BORDER));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(13);
        body.addView(c,lp);
        c.addView(text(heading,19,INK,true));
        gap(c,12);return c;
    }
    void message(String info){
        new AlertDialog.Builder(this).setTitle("AB Cable Cost").setMessage(info)
            .setPositiveButton("تمام",null).show();
    }
    @Override public void onCreate(Bundle state){
        super.onCreate(state);pref=getSharedPreferences("ab_cable_cost_catalog_v1",MODE_PRIVATE);
        getWindow().setStatusBarColor(NAVY);getWindow().setNavigationBarColor(NAVY);
        root=vertical();root.setBackgroundColor(PAGE);setContentView(root);
        LinearLayout top=vertical();top.setPadding(dp(16),dp(18),dp(16),dp(16));
        top.setBackgroundColor(NAVY);root.addView(top);
        top.addView(text("AB Cable Cost  •  دليل الكابلات",21,GOLD,true));
        gap(top,4);
        top.addView(text("مرجع فني + وصفة تصنيع قابلة للتعديل",13,Color.WHITE,false));
        ScrollView scroll=new ScrollView(this);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        body=vertical();body.setPadding(dp(13),dp(16),dp(13),dp(24));scroll.addView(body);
        showCatalog();
    }
    void showCatalog(){
        body.removeAllViews();rows.clear();
        LinearLayout links=card("دليل الكابلات والخامات");
        button(links,"أسعار الخامات المركزية",()->showPrices(),false);
        button(links,"إعدادات المصنع والعملة",()->showSettings(),true);

        LinearLayout filters=card("اختار الكابل والمقاس");
        label(filters,"نوع الكابل");
        family=spinner(filters,CableCatalog.THERMO,CableCatalog.CAMERA,
            CableCatalog.FIRE,CableCatalog.SHIELD);
        gap(filters,10);label(filters,"الشركة المرجعية");
        maker=spinner(filters,"كل الشركات","السويدي","Belden","قالب قابل للتعديل");
        gap(filters,10);label(filters,"المقاس / الموديل");
        model=spinner(filters,"—");
        family.setOnItemSelectedListener(onSelect(()->refreshModels()));
        maker.setOnItemSelectedListener(onSelect(()->refreshModels()));
        model.setOnItemSelectedListener(onSelect(()->renderSpec()));
        page=vertical();body.addView(page);
        refreshModels();
    }
    AdapterView.OnItemSelectedListener onSelect(Runnable call) {
        return new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p,View v,int i,long id){call.run();}
            public void onNothingSelected(AdapterView<?> p){}
        };
    }
    void refreshModels(){
        if(family==null||maker==null||model==null)return;
        String familyName=String.valueOf(family.getSelectedItem());
        String brand=String.valueOf(maker.getSelectedItem());
        matches.clear();matches.addAll(CableCatalog.match(familyName,brand));
        List<String> names=new ArrayList<>();
        for(CableCatalog.Spec s:matches)names.add(s.display());
        if(names.isEmpty())names.add("مفيش مقاسات متاحة للشركة دي");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,names){
            @Override public View getView(int i,View v,ViewGroup p){return choice(getItem(i));}
            @Override public View getDropDownView(int i,View v,ViewGroup p){return choice(getItem(i));}
        };
        model.setAdapter(adapter);
        renderSpec();
    }
    void line(LinearLayout box,String title,String value){
        TextView t=text(title+"  "+value,14,INK,false);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(5);
        box.addView(t,lp);
    }
    String key(String suffix){return "recipe_"+selected.id+"_"+suffix;}
    String saved(String suffix,String fallback){return pref.getString(key(suffix),fallback);}
    void renderSpec(){
        if(page==null)return;
        page.removeAllViews();rows.clear();selected=null;
        int pos=model.getSelectedItemPosition();
        if(pos<0||pos>=matches.size())return;
        selected=matches.get(pos);
        LinearLayout source=vertical();
        source.setPadding(dp(14),dp(14),dp(14),dp(14));
        source.setBackground(bg(Color.WHITE,15,BORDER));
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.bottomMargin=dp(13);
        page.addView(source,sp);
        source.addView(text("بيانات المواصفة",19,INK,true));
        gap(source,9);
        line(source,"الشركة:",selected.manufacturer);
        line(source,"النوع والمقاس:",selected.model);
        line(source,"التركيب:",selected.standard);
        source.addView(text(selected.note,14,MUTED,false));
        gap(source,9);
        String status=selected.published ?
            "● مصدر منشور من الشركة — القيم تقريبية حسب الداتا شيت" :
            "● قالب غير معتمد — راجع المواصفات قبل التصنيع";
        TextView flag=text(status,13,selected.published?Color.rgb(41,110,79):ERROR,true);
        source.addView(flag);
        if(selected.finishedDiameterMm>0)line(source,"القطر النهائي المنشور:",
            String.format(Locale.US,"%.3f مم",selected.finishedDiameterMm));
        if(selected.finishedKgKm>0)line(source,"وزن الكابل النهائي المنشور:",
            String.format(Locale.US,"%.2f كجم/كم (كابل كامل، مش نحاس)",selected.finishedKgKm));
        if(!selected.sourceUrl.isEmpty()){
            button(source,"فتح مرجع الشركة",()->{
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(selected.sourceUrl));
                try{startActivity(intent);}catch(Exception e){message("مافيش تطبيق يفتح الرابط.");}
            },true);
        }
        // The blank quantities of other layers deliberately prevent fake totals.
        LinearLayout bom=vertical();
        bom.setPadding(dp(14),dp(14),dp(14),dp(14));
        bom.setBackground(bg(Color.WHITE,15,BORDER));
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,-2);bp.bottomMargin=dp(13);
        page.addView(bom,bp);
        bom.addView(text("مكونات ووصفة التصنيع",19,INK,true));
        gap(bom,5);
        bom.addView(text("الوزن النظري للنحاس محسوب. باقي الطبقات أدخل استهلاكها الفعلي من المصنع؛ مش هنخترع أوزان.",13,MUTED,false));
        gap(bom,13);
        List<String> recipeMaterials=new ArrayList<>(Arrays.asList(selected.materials));
        String additional=pref.getString(key("extraMaterials"),"");
        for(String x:additional.split("\\n")){
            if(!x.trim().isEmpty()&&!recipeMaterials.contains(x.trim()))recipeMaterials.add(x.trim());
        }
        for(String mat:recipeMaterials){
            LinearLayout section=vertical();
            LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(-1,-2);
            qp.bottomMargin=dp(14);bom.addView(section,qp);
            section.addView(text(mat,16,NAVY,true));
            String unit=getUnit(mat);
            double theory=mat.equals(selected.conductorMaterial()) ?
                selected.conductorGramsPerMeter():0;
            if(theory>0) {
                section.addView(text(String.format(Locale.US,
                    "الوزن النظري للموصل: %.3f جم/م — قابل للاستبدال بوزن فعلي",
                    theory),12,MUTED,false));
            }
            gap(section,6);
            String unitHint=unit.equals("متر")?"متر خام / متر كابل":
                "جرام خام / متر كابل";
            label(section,"استهلاك "+unitHint);
            EditText consumption=field(section,unitHint,
                saved(mat+"_amount",theory>0?String.format(Locale.US,"%.4f",theory):""));
            gap(section,6);
            String priceText=String.format(Locale.US,"سعر الوحدة المركزي: %.2f / %s",
                getPrice(mat),unit);
            section.addView(text(priceText,12,MUTED,false));
            gap(section,6);
            label(section,"الهالك (نسبة مئوية أو كيلو)");
            LinearLayout wasteRow=new LinearLayout(this);
            wasteRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            section.addView(wasteRow);
            LinearLayout wasteFirst=vertical(),wasteSecond=vertical();
            LinearLayout.LayoutParams a=new LinearLayout.LayoutParams(0,-2,1);
            a.leftMargin=dp(6);
            wasteRow.addView(wasteFirst,a);
            wasteRow.addView(wasteSecond,new LinearLayout.LayoutParams(0,-2,1));
            Spinner wasteUnit=spinner(wasteFirst,"%","كجم");
            int chosen=(int)pref.getLong(key(mat+"_wasteUnit"),0);
            wasteUnit.setSelection(chosen);
            EditText waste=field(wasteSecond,"0",saved(mat+"_waste","0"));
            if(unit.equals("متر")){
                wasteUnit.setSelection(0);
                wasteUnit.setEnabled(false);
                section.addView(text("بالنسبة للخامات المباعة بالمتر، الهالك هنا بالنسبة فقط.",12,MUTED,false));
            }
            section.addView(new View(this),new LinearLayout.LayoutParams(-1,dp(1)));
            rows.add(new Row(mat,unit,consumption,waste,wasteUnit,theory));
        }
        button(bom,"أضف خامة لوصفة الكابل",()->{
            List<String> names=materialNames();
            String[] items=names.toArray(new String[0]);
            new AlertDialog.Builder(this)
                .setTitle("اختار خامة تضيفها للوصفة")
                .setItems(items,(dialog,which)->{
                    String name=items[which];
                    String original=pref.getString(key("extraMaterials"),"");
                    if(original.contains(name+"\\n")||
                        Arrays.asList(selected.materials).contains(name)){
                        message("الخامة دي موجودة في الوصفة بالفعل.");
                        return;
                    }
                    pref.edit().putString(key("extraMaterials"),original+name+"\n").apply();
                    renderSpec();
                })
                .setNegativeButton("إلغاء",null).show();
        },false);
        button(bom,"احفظ وصفة المصنع",()->saveRecipe(),true);

        LinearLayout costing=vertical();costing.setPadding(dp(14),dp(14),dp(14),dp(14));
        costing.setBackground(bg(Color.WHITE,15,BORDER));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(13);
        page.addView(costing,cp);
        costing.addView(text("حساب تكلفة التصنيع",19,INK,true));
        gap(costing,10);
        label(costing,"طول الكابل المطلوب (متر)");
        length=field(costing,"مثال: 100",saved("length","100"));
        gap(costing,10);
        label(costing,"حساب التشغيل");
        operationUnit=spinner(costing,"تكلفة لكل متر","تكلفة لكل لفة");
        operationUnit.setSelection((int)pref.getLong(key("opUnit"),0));
        gap(costing,7);
        label(costing,"تكلفة التشغيل بعملة المصنع");
        operation=field(costing,"0",saved("operation","0"));
        gap(costing,7);
        label(costing,"طول اللفة (متر) لو التشغيل باللفة");
        operatingLength=field(costing,"100",saved("operatingLength","100"));
        gap(costing,10);
        calculation=text("أدخل أسعار الخامات وكمياتها، وبعدين احسب.",14,MUTED,false);
        button(costing,"احسب تكلفة التصنيع",()->calculate(),false);
        gap(costing,12);
        calculation.setPadding(dp(12),dp(12),dp(12),dp(12));
        calculation.setBackground(bg(SOFT,12,GOLD));
        costing.addView(calculation);
        calculate();
    }
    double parse(String s,String field,boolean optional) {
        s=s.trim().replace('٫','.').replace('٬','.').replace(',','.');
        StringBuilder b=new StringBuilder();
        for(int i=0;i<s.length();i++){
            char c=s.charAt(i);int v=Character.digit(c,10);
            b.append(v<0?c:(char)('0'+v));
        }
        if(optional&&b.length()==0)return Double.NaN;
        try{
            double n=Double.parseDouble(b.toString());
            if(Double.isInfinite(n)||Double.isNaN(n)||n<0)throw new NumberFormatException();
            return n;
        }catch(Exception e){throw new IllegalArgumentException("راجع "+field+"؛ محتاج رقم صحيح غير سالب.");}
    }
    double value(EditText e,String title,boolean optional){
        return parse(e.getText().toString(),title,optional);
    }
    String savedCurrency(){return pref.getString("currency","EGP");}
    void saveRecipe(){
        if(selected==null)return;
        SharedPreferences.Editor e=pref.edit();
        for(Row r:rows){
            e.putString(key(r.material+"_amount"),r.amount.getText().toString().trim());
            e.putString(key(r.material+"_waste"),r.waste.getText().toString().trim());
            e.putLong(key(r.material+"_wasteUnit"),r.wasteUnit.getSelectedItemPosition());
        }
        e.putString(key("length"),length.getText().toString());
        e.putString(key("operation"),operation.getText().toString());
        e.putString(key("operatingLength"),operatingLength.getText().toString());
        e.putLong(key("opUnit"),operationUnit.getSelectedItemPosition());
        e.apply();message("تم حفظ وصفة المصنع محليًا. بيانات المرجع الأصلي ما اتغيرتش.");
    }
    void calculate(){
        try{
            double l=value(length,"الطول",false);
            if(l<=0)throw new IllegalArgumentException("الطول لازم يكون أكبر من صفر.");
            double total=0;boolean missing=false;
            List<String> missingNames=new ArrayList<>();
            StringBuilder detail=new StringBuilder();
            for(Row r:rows){
                double usage=value(r.amount,r.material,true);
                double price=getPrice(r.material);
                double w=value(r.waste,r.material+" هالك",true);
                if(Double.isNaN(w))w=0;
                if(Double.isNaN(usage)){
                    missing=true;missingNames.add(r.material+" (الاستهلاك)");continue;
                }
                if(usage>0&&price<=0){
                    missing=true;missingNames.add(r.material+" (السعر)");continue;
                }
                double required=r.unit.equals("متر")?usage*l:usage*l/1000.0;
                double extra;
                if(r.wasteUnit.getSelectedItemPosition()==0)extra=required*w/100;
                else if(r.unit.equals("متر"))
                    throw new IllegalArgumentException("الهالك بالكيلو مش مناسب للخامة بالمتر.");
                else extra=r.unit.equals("جرام")?w*1000:w;
                double bought=required+extra;
                double cost=r.unit.equals("جرام")?bought*1000*price:bought*price;
                // For g/m input and gram purchase pricing, calculation needs grams:
                // required is kg (usage*g/m*m/1000). Convert kg -> grams.
                total+=cost;
                detail.append(String.format(Locale.US,
                    "%s: %.2f %s | تكلفة %.2f\n",r.material,
                    r.unit.equals("جرام")?bought*1000:bought,r.unit,cost));
            }
            double op=value(operation,"تكلفة التشغيل",true);
            if(Double.isNaN(op))op=0;
            double operational;
            if(operationUnit.getSelectedItemPosition()==0)operational=op*l;
            else{
                double roll=value(operatingLength,"طول اللفة",false);
                if(roll<=0)throw new IllegalArgumentException("طول اللفة لازم أكبر من صفر.");
                operational=op*l/roll;
            }
            total+=operational;
            String status=missing?"⚠ حساب غير مكتمل — لازم تدخل بيانات:\n"+
                join(missingNames)+"\n\n":"";
            calculation.setText(String.format(Locale.US,
                "%sتكلفة المواد المدخلة + التشغيل: %,.2f %s\nللمتر: %,.4f %s\nتشغيل: %,.2f\n\n%s\n%s",
                status,total,savedCurrency(),total/l,savedCurrency(),operational,
                detail.toString(),"أوزان الكابل المنشورة من المصنع للمرجعية فقط، مش جزء من حساب التكلفة."));
        }catch(Exception e){calculation.setTextColor(ERROR);calculation.setText(e.getMessage());}
    }
    String join(List<String> values){
        StringBuilder b=new StringBuilder();
        for(String s:values)b.append("• ").append(s).append("\n");
        return b.toString();
    }
    List<String> materialNames(){
        ArrayList<String> names=new ArrayList<>(CableCatalog.ALL_MATERIALS);
        String custom=pref.getString("customMaterials","");
        for(String s:custom.split("\n"))if(!s.trim().isEmpty()&&!names.contains(s.trim()))names.add(s.trim());
        return names;
    }
    double getPrice(String name){
        return Double.longBitsToDouble(pref.getLong("price:"+name,
            Double.doubleToLongBits(0)));
    }
    String getUnit(String name){return pref.getString("unit:"+name,"كيلو");}
    void showPrices(){
        body.removeAllViews();rows.clear();page=null;
        LinearLayout c=card("الأسعار المركزية للخامات");
        c.addView(text("السعر ده لكل وحدة شراء، وبيستخدم في الحسابات الجديدة. لو ما دخلتش سعر، الحساب هيفضل غير مكتمل.",13,MUTED,false));
        button(c,"رجوع للكتالوج",()->showCatalog(),true);
        ArrayList<String> items=new ArrayList<>(materialNames());
        List<EditText> priceFields=new ArrayList<>();
        List<Spinner> unitFields=new ArrayList<>();
        for(String mat:items){
            LinearLayout block=card(mat);
            label(block,"وحدة الشراء");
            Spinner s=spinner(block,"كيلو","جرام","متر");
            int idx=Arrays.asList("كيلو","جرام","متر").indexOf(getUnit(mat));
            s.setSelection(Math.max(0,idx));
            gap(block,8);
            label(block,"السعر لكل وحدة ("+savedCurrency()+")");
            EditText p=field(block,"0",getPrice(mat)>0?
                String.format(Locale.US,"%.4f",getPrice(mat)):"");
            priceFields.add(p);unitFields.add(s);
        }
        LinearLayout actions=card("إدارة الأسعار");
        button(actions,"حفظ قائمة الأسعار",()->{
            SharedPreferences.Editor ed=pref.edit();
            try{
                for(int i=0;i<items.size();i++){
                    double price=value(priceFields.get(i),"سعر "+items.get(i),true);
                    if(Double.isNaN(price))price=0;
                    ed.putLong("price:"+items.get(i),Double.doubleToLongBits(price));
                    ed.putString("unit:"+items.get(i),String.valueOf(unitFields.get(i).getSelectedItem()));
                }
                ed.apply();message("تم حفظ أسعار الخامات ووحداتها.");
            }catch(Exception e){message(e.getMessage());}
        },false);
        button(actions,"إضافة خامة جديدة",()->{
            EditText name=new EditText(this);name.setSingleLine();
            name.setHint("اسم الخامة");
            new AlertDialog.Builder(this).setTitle("إضافة خامة")
                .setView(name).setPositiveButton("إضافة",(d,w)->{
                    String value=name.getText().toString().trim().replace("\n"," ");
                    if(value.isEmpty()||materialNames().contains(value)){
                        message("الاسم فارغ أو الخامة موجودة بالفعل.");return;
                    }
                    String cur=pref.getString("customMaterials","");
                    pref.edit().putString("customMaterials",cur+value+"\n").apply();
                    showPrices();
                }).setNegativeButton("إلغاء",null).show();
        },true);
    }
    void showSettings(){
        body.removeAllViews();rows.clear();page=null;
        LinearLayout card=card("إعدادات المصنع");
        label(card,"اسم المصنع");
        EditText company=new EditText(this);
        company.setSingleLine();company.setText(pref.getString("factoryName",""));
        company.setHint("اسم المصنع");company.setPadding(dp(10),0,dp(10),0);
        company.setBackground(bg(PAGE,10,BORDER));
        card.addView(company,new LinearLayout.LayoutParams(-1,dp(48)));
        gap(card,12);label(card,"عملة المصنع");
        String[] currencies={"EGP","USD","EUR","SAR","AED","GBP","KWD","QAR","OMR",
            "BHD","JOD","MAD","DZD","TND","NGN","ZAR","INR","CNY","JPY","CAD",
            "AUD","TRY","CHF","BRL","MXN"};
        Spinner currenciesSpinner=spinner(card,currencies);
        int index=Arrays.asList(currencies).indexOf(savedCurrency());
        if(index>=0)currenciesSpinner.setSelection(index);
        gap(card,8);
        card.addView(text("العملات الأخرى وأسعار الصرف المحوّلة تلقائيًا هتضاف لاحقًا؛ الأسعار هنا بعملة المصنع فقط.",12,MUTED,false));
        button(card,"حفظ الإعدادات",()->{
            pref.edit().putString("factoryName",company.getText().toString().trim())
                .putString("currency",String.valueOf(currenciesSpinner.getSelectedItem()))
                .apply();
            message("تم حفظ اسم المصنع والعملة محليًا.");
        },false);
        button(card,"رجوع للكتالوج",()->showCatalog(),true);
    }
}
