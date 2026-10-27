package com.ab.cablecost;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Space;

/** Premium RTL landing page. The original working calculator is preserved in CalculatorActivity. */
public class MainActivity extends Activity {
    static final int NAVY=0xff0e2037, GOLD=0xffd6a847, BG=0xfff5f7fa, INK=0xff192b43;
    static final int SOFT=0xffeaf0f6, BORDER=0xffdee5ec, MUTED=0xff66778d;
    private LinearLayout root, main;
    int px(float dp){return (int)(dp*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable shape(int color,int radius,int stroke){
        GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(px(radius));
        if(stroke!=0)g.setStroke(px(1),stroke);return g;
    }
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);return l;}
    TextView text(String value,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);
        t.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }
    void space(LinearLayout l,int h){l.addView(new View(this),new LinearLayout.LayoutParams(1,px(h)));}
    LinearLayout.LayoutParams margin(int w,int h,int bottom){
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(w<0?w:px(w),h<0?h:px(h));
        p.bottomMargin=px(bottom);return p;
    }
    void route(String family) {
        Intent i=new Intent(this,CatalogActivity.class);
        i.putExtra("family",family);
        startActivity(i);
    }
    void routeMode(String mode){
        Intent i=new Intent(this,CatalogActivity.class);
        i.putExtra("screen",mode);startActivity(i);
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        getWindow().getDecorView().setSystemUiVisibility(0);
        root=column();root.setBackgroundColor(BG);setContentView(root);
        if(Build.VERSION.SDK_INT>=21){
            root.setOnApplyWindowInsetsListener((v,i)->{
                int top,bottom;
                if(Build.VERSION.SDK_INT>=30){
                    top=i.getInsets(WindowInsets.Type.statusBars()).top;
                    bottom=i.getInsets(WindowInsets.Type.navigationBars()).bottom;
                }else{top=i.getSystemWindowInsetTop();bottom=i.getSystemWindowInsetBottom();}
                root.setPadding(0,top,0,bottom);return i;
            });
            root.requestApplyInsets();
        }
        makeHeader();
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        main=column();main.setPadding(px(16),px(17),px(16),px(25));scroll.addView(main);
        makeHero();
        section("أنواع الكابلات","اختار النوع والمقاس، وبعدها راجع وصفة التصنيع");
        addCategoryRow(
            CableCatalog.THERMO,"مقاسات 2×0.75 لحد 5×6","المقاسات والموصلات والعزل",1,
            CableCatalog.CAMERA,"RG59 وكابلات CCTV","سنتر وشيلد وغلاف",2);
        addCategoryRow(
            CableCatalog.FIRE,"إنذار الحريق والكابلات المقاومة","عزل وشيلد وDrain",3,
            CableCatalog.SHIELD,"Shielded Cables","فويل / تجديل / Drain",4);
        addCategoryRow(
            "كابلات مسلحة","مدرّع ومحمي بطبقات","تسليح وأسلاك صلب",5,
            "كابلات كنترول","كابلات التحكم متعددة الأطراف","مقاسات قابلة للتعديل",4);
        addCategoryRow(
            "أسلاك معزولة","أسلاك مفردة ومرنة","نحاس أو ألومنيوم",1,
            "كابلات مخصوصة","تكوين خاص حسب المصنع","وصفة قابلة للتخصيص",2);
        section("أدوات التصنيع","كل أدواتك الفنية في مكان واحد");
        action("حاسبة الأوزان التفصيلية","قطر الشعرة • عدد الشعرات • العزل",false,
            ()->startActivity(new Intent(this,CalculatorActivity.class)));
        action("دليل خامات المصنع والأسعار","نحاس • PVC • شيلد • فويل • إضافات",true,
            ()->routeMode("prices"));
        main.addView(text("AB Software  •  مرجع فني لحساب تكلفة تصنيع الكابلات",12,MUTED,false),
            margin(-1,-2,10));
        makeBottomBar();
    }
    private ImageView logo(int width,int height){
        ImageView image=new ImageView(this);
        image.setImageResource(R.drawable.ab_brand_logo);
        image.setContentDescription("شعار AB Cable Cost الأصلي");
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setPadding(px(3),px(3),px(3),px(3));
        image.setLayoutParams(new LinearLayout.LayoutParams(px(width),px(height)));
        return image;
    }
    private void makeHeader(){
        LinearLayout header=new LinearLayout(this);header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        header.setGravity(Gravity.CENTER_VERTICAL);header.setPadding(px(13),px(9),px(13),px(11));
        header.setBackgroundColor(NAVY);
        root.addView(header,new LinearLayout.LayoutParams(-1,px(90)));
        LinearLayout labels=column();
        header.addView(labels,new LinearLayout.LayoutParams(0,-2,1));
        labels.addView(text("AB Cable Cost",23,Color.WHITE,true));
        labels.addView(text("حاسبة تكلفة تصنيع الكابلات",12,GOLD,false));
        header.addView(logo(100,68));
    }
    private void makeHero(){
        FrameLayout frame=new FrameLayout(this);
        GradientDrawable gradient=new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,new int[]{0xff143452,0xff0b1c33});
        gradient.setCornerRadius(px(19));frame.setBackground(gradient);
        LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,px(174));fp.bottomMargin=px(21);
        main.addView(frame,fp);
        CableVisual visual=new CableVisual(this,1);
        FrameLayout.LayoutParams ivp=new FrameLayout.LayoutParams(px(141),px(125),Gravity.LEFT|Gravity.CENTER_VERTICAL);
        ivp.leftMargin=px(2);frame.addView(visual,ivp);
        LinearLayout textBlock=column();textBlock.setPadding(px(12),px(16),px(12),px(12));
        FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(
            px(216),-1,Gravity.RIGHT|Gravity.TOP);
        frame.addView(textBlock,tp);
        textBlock.addView(text("من المقاس للتكلفة",23,GOLD,true));
        space(textBlock,6);
        textBlock.addView(text("اختار الكابل، شوف مكوناته، واحسب تكلفة التصنيع.",13,Color.WHITE,false));
        space(textBlock,13);
        Button b=new Button(this);b.setAllCaps(false);b.setText("ابدأ حساب الكابل");
        b.setTextSize(12);b.setTextColor(NAVY);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setBackgroundTintList(ColorStateList.valueOf(GOLD));
        textBlock.addView(b,new LinearLayout.LayoutParams(-1,px(43)));
        b.setOnClickListener(v->route(CableCatalog.THERMO));
    }
    private void section(String heading,String note){
        TextView h=text(heading,21,INK,true);main.addView(h,margin(-1,-2,3));
        TextView n=text(note,12,MUTED,false);main.addView(n,margin(-1,-2,12));
    }
    private void addCategoryRow(String a,String suba,String notea,int kindA,
                                String b,String subb,String noteb,int kindB){
        LinearLayout row=new LinearLayout(this);row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.bottomMargin=px(12);
        main.addView(row,rp);
        LinearLayout.LayoutParams first=new LinearLayout.LayoutParams(0,px(177),1);
        first.leftMargin=px(6);
        row.addView(category(a,suba,notea,kindA),first);
        LinearLayout.LayoutParams second=new LinearLayout.LayoutParams(0,px(177),1);
        second.rightMargin=px(6);
        row.addView(category(b,subb,noteb,kindB),second);
    }
    private View category(String title,String subtitle,String description,int kind){
        LinearLayout item=column();item.setPadding(px(10),px(8),px(10),px(10));
        item.setGravity(Gravity.CENTER_HORIZONTAL);
        item.setBackground(shape(Color.WHITE,16,BORDER));item.setElevation(px(2));
        CableVisual visual=new CableVisual(this,kind);
        item.addView(visual,new LinearLayout.LayoutParams(-1,px(79)));
        item.addView(text(title,15,INK,true),margin(-1,-2,2));
        TextView t=text(subtitle,11,MUTED,false);
        t.setMaxLines(2);t.setEllipsize(android.text.TextUtils.TruncateAt.END);
        item.addView(t,margin(-1,-2,5));
        item.setClickable(true);item.setFocusable(true);
        item.setOnClickListener(v->route(title));
        item.setContentDescription(title+" — افتح المقاسات والوصفة");
        return item;
    }
    private void action(String heading,String note,boolean gold,Runnable callback){
        LinearLayout row=new LinearLayout(this);row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(px(13),px(12),px(13),px(12));
        row.setBackground(shape(gold?0xfff9f0d9:Color.WHITE,14,gold?GOLD:BORDER));
        LinearLayout.LayoutParams rp=margin(-1,px(72),11);main.addView(row,rp);
        TextView ic=text(gold?"◉":"◈",25,gold?NAVY:GOLD,true);
        ic.setGravity(Gravity.CENTER);row.addView(ic,new LinearLayout.LayoutParams(px(36),-1));
        LinearLayout left=column();
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.rightMargin=px(10);
        row.addView(left,lp);left.addView(text(heading,16,INK,true));
        left.addView(text(note,11,MUTED,false));
        row.setOnClickListener(v->callback.run());
        row.setContentDescription(heading);
    }
    private void makeBottomBar(){
        LinearLayout bar=new LinearLayout(this);
        bar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        bar.setBackgroundColor(Color.WHITE);
        bar.setPadding(px(7),px(6),px(7),px(6));
        root.addView(bar,new LinearLayout.LayoutParams(-1,px(65)));
        addTab(bar,"الرئيسية","⌂",true,()->{});
        addTab(bar,"المقاسات","≡",false,()->route(CableCatalog.THERMO));
        addTab(bar,"الخامات","◉",false,()->routeMode("prices"));
        addTab(bar,"الإعدادات","⚙",false,()->routeMode("settings"));
    }
    private void addTab(LinearLayout bar,String label,String symbol,boolean selected,Runnable action){
        LinearLayout tab=column();tab.setGravity(Gravity.CENTER);
        bar.addView(tab,new LinearLayout.LayoutParams(0,-1,1));
        TextView ico=text(symbol,24,selected?GOLD:0xff556882,true);ico.setGravity(Gravity.CENTER);
        tab.addView(ico,new LinearLayout.LayoutParams(-1,px(30)));
        TextView name=text(label,11,selected?GOLD:MUTED,selected);
        name.setGravity(Gravity.CENTER);tab.addView(name);
        tab.setOnClickListener(v->action.run());
    }
}
