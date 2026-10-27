package com.ab.cablecost;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Offline manufacturer directory. Research candidates are intentionally distinct
 * from manufacturer-verified cable models. No inferred engineering ratings.
 */
public final class ManufacturersActivity extends Activity {
    private final int NAVY=LibraryUi.NAVY, GOLD=LibraryUi.GOLD;
    private LinearLayout root, list, counter;
    private TextView countryFilter;
    private EditText input;
    private String query="", selectedCountry="*";
    private int visibleLimit=30;
    private List<GlobalManufacturersData.Manufacturer> companies;
    private boolean ar;

    private int d(int n){return LibraryUi.dp(this,n);}
    private TextView text(String s,int size,int color,boolean bold){
        return LibraryUi.text(this,s,size,color,bold);
    }
    private String tr(String arText,String enText){return ar?arText:enText;}
    private LinearLayout col(){return LibraryUi.col(this);}
    private LinearLayout row(){return LibraryUi.row(this);}

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        ar=LibraryUi.rtl(this);
        companies=GlobalManufacturersData.all(this);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        draw();
    }
    private void draw() {
        root=col();root.setBackgroundColor(NAVY);setContentView(root);
        if(Build.VERSION.SDK_INT>=21) {
            root.setOnApplyWindowInsetsListener((v,i)->{
                int top,bottom;
                if(Build.VERSION.SDK_INT>=30) {
                    top=i.getInsets(WindowInsets.Type.statusBars()).top;
                    bottom=i.getInsets(WindowInsets.Type.navigationBars()).bottom;
                }else{
                    top=i.getSystemWindowInsetTop();
                    bottom=i.getSystemWindowInsetBottom();
                }
                root.setPadding(0,top,0,bottom);return i;
            });
            root.requestApplyInsets();
        }
        LinearLayout head=row();head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(d(12),d(8),d(12),d(8));
        root.addView(head,LibraryUi.lp(this,-1,60));
        TextView back=LibraryUi.button(this,tr("رجوع","Back"),false,this::finish);
        head.addView(back,LibraryUi.lp(this,72,44));
        TextView title=text(tr("دليل الشركات العالمية","Global Manufacturers"),18,GOLD,true);
        title.setGravity(Gravity.CENTER);
        head.addView(title,new LinearLayout.LayoutParams(0,-1,1));

        ScrollView sc=new ScrollView(this);
        sc.setFillViewport(false);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout body=col();body.setPadding(d(12),d(6),d(12),d(16));sc.addView(body);

        TextView summary=text(companies.size()+" "+tr("شركة مرشحة · 33 دولة","research candidates · 33 countries"),14,GOLD,true);
        body.addView(summary,LibraryUi.lp(this,-1,36));

        TextView caveat=text(tr("قائمة بحثية: روابط المواقع والكتالوجات قيد التحقق. ليست 150 مصنعًا معتمدًا، ومواصفات الكابلات لا تُستنتج من أسماء الشركات.",
            "Research directory only: links await review. These are not 150 approved manufacturers. No cable specs are inferred."),12,LibraryUi.MUTED,false);
        caveat.setPadding(d(10),d(8),d(10),d(8));
        caveat.setBackground(LibraryUi.bg(this,LibraryUi.CARD,9,LibraryUi.BORDER));
        body.addView(caveat,LibraryUi.lp(this,-1,-2));
        LibraryUi.gap(this,body,10);

        input=new EditText(this);
        input.setSingleLine(true);input.setTextSize(15);input.setTextColor(LibraryUi.INK);
        input.setHintTextColor(0xff6d7b8a);input.setHint(tr("ابحث باسم الشركة أو الدولة...","Search company or country..."));
        input.setPadding(d(12),0,d(12),0);
        input.setBackground(LibraryUi.bg(this,Color.WHITE,10,0));
        body.addView(input,LibraryUi.lp(this,-1,52));
        input.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int a,int c,int f){}
            public void onTextChanged(CharSequence s,int a,int before,int count){
                query=s.toString().trim();visibleLimit=30;refresh();
            }
            public void afterTextChanged(Editable e){}
        });

        countryFilter=LibraryUi.button(this,tr("كل الدول  ▾","All countries  ▾"),false,this::selectCountry);
        LinearLayout.LayoutParams fp=LibraryUi.lp(this,-1,45);fp.topMargin=d(8);body.addView(countryFilter,fp);

        counter=col();body.addView(counter,LibraryUi.lp(this,-1,-2));
        list=col();body.addView(list,LibraryUi.lp(this,-1,-2));
        refresh();
    }
    private void selectCountry() {
        HashSet<String> countries=new HashSet<>();
        for(GlobalManufacturersData.Manufacturer m:companies)countries.add(m.country);
        ArrayList<String> names=new ArrayList<>(countries);Collections.sort(names);
        names.add(0,tr("كل الدول","All countries"));
        new AlertDialog.Builder(this).setTitle(tr("اختر الدولة","Choose country"))
            .setItems(names.toArray(new String[0]),(dia,index)->{
                selectedCountry=index==0?"*":names.get(index);
                countryFilter.setText(index==0?tr("كل الدول  ▾","All countries  ▾"):names.get(index)+"  ▾");
                visibleLimit=30;refresh();
            }).setNegativeButton(tr("إلغاء","Cancel"),null).show();
    }
    private void refresh() {
        if(list==null||counter==null)return;
        list.removeAllViews();counter.removeAllViews();
        List<GlobalManufacturersData.Manufacturer> found=new ArrayList<>();
        for(GlobalManufacturersData.Manufacturer m:companies){
            if(m.matches(query,selectedCountry))found.add(m);
        }
        TextView count=text(found.size()+" / "+companies.size()+"  •  "+tr("شركات بحثية","Research companies"),
            13,GOLD,true);
        LinearLayout.LayoutParams cp=LibraryUi.lp(this,-1,35);cp.topMargin=d(8);
        counter.addView(count,cp);
        if(found.isEmpty()){
            TextView no=text(tr("لا توجد نتائج","No matches"),14,LibraryUi.MUTED,false);
            no.setGravity(Gravity.CENTER);list.addView(no,LibraryUi.lp(this,-1,70));return;
        }
        for(int k=0;k<Math.min(visibleLimit,found.size());k++){
            card(found.get(k));
        }
        if(visibleLimit<found.size()){
            TextView more=LibraryUi.button(this,tr("عرض 30 شركة أخرى","Show 30 more"),true,
                ()->{visibleLimit+=30;refresh();});
            LinearLayout.LayoutParams mp=LibraryUi.lp(this,-1,49);mp.topMargin=d(8);
            list.addView(more,mp);
        }
    }
    private void card(GlobalManufacturersData.Manufacturer m) {
        LinearLayout holder=col();
        holder.setPadding(d(12),d(9),d(12),d(9));
        holder.setBackground(LibraryUi.bg(this,LibraryUi.CARD,12,LibraryUi.BORDER));
        LinearLayout.LayoutParams p=LibraryUi.lp(this,-1,-2);p.bottomMargin=d(8);
        list.addView(holder,p);
        holder.addView(text(m.name,16,LibraryUi.WHITE,true),LibraryUi.lp(this,-1,-2));
        holder.addView(text(m.id+"  ·  "+m.country,12,GOLD,false),LibraryUi.lp(this,-1,25));
        holder.addView(text(m.displayStatus(ar),12,LibraryUi.MUTED,false),LibraryUi.lp(this,-1,-2));
        TextView view=LibraryUi.button(this,tr("بيانات ومصادر الشركة","Company details and sources"),
            false,()->details(m));
        LinearLayout.LayoutParams vp=LibraryUi.lp(this,-1,42);vp.topMargin=d(8);
        holder.addView(view,vp);
    }
    private void details(GlobalManufacturersData.Manufacturer m) {
        StringBuilder b=new StringBuilder();
        b.append(m.id).append("  ·  ").append(m.country).append("\n\n");
        b.append(tr("حالة التحقق: ","Verification: ")).append(m.displayStatus(ar)).append("\n\n");
        b.append(tr("موقع رسمي مرشح: ","Website candidate: "))
            .append(m.website.isEmpty()?tr("غير متوفر","Not available"):m.website).append("\n\n");
        b.append(tr("رابط كتالوج/منتجات مرشح: ","Catalog/product candidate: "))
            .append(m.catalog.isEmpty()?tr("غير متوفر","Not available"):m.catalog).append("\n\n");
        b.append(tr("رابط مرجع الإدراج: ","Listing reference: "))
            .append(m.source.isEmpty()?tr("غير متوفر","Not available"):m.source);
        AlertDialog.Builder dialog=new AlertDialog.Builder(this)
            .setTitle(m.name).setMessage(b.toString())
            .setNegativeButton(tr("إغلاق","Close"),null);
        if(isHttps(m.website))dialog.setNeutralButton(tr("الموقع الرسمي","Website"),
            (di,w)->open(m.website));
        if(isHttps(m.catalog))dialog.setPositiveButton(tr("الكتالوج / المنتجات","Catalog / Products"),
            (di,w)->open(m.catalog));
        dialog.show();
    }
    private boolean isHttps(String url){return url!=null&&url.startsWith("https://");}
    private void open(String url) {
        if(!isHttps(url))return;
        try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
        catch(Exception e){
            new AlertDialog.Builder(this).setMessage(tr("تعذر فتح الرابط","Could not open URL"))
                .setPositiveButton("OK",null).show();
        }
    }
}
