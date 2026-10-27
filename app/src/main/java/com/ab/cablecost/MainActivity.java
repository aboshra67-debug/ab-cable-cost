package com.ab.cablecost;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The agreed engineering-home composition. This does not replace any working
 * manufacturing / cable reference / conductor calculator implementation.
 * Inactive engineering modules disclose their status rather than return
 * fictitious electrical calculations.
 */
public class MainActivity extends Activity {
    private static final int NAVY=0xff0d2039;
    private static final int NAVY2=0xff122f50;
    private static final int GOLD=0xffe1b34f;
    private static final int SURFACE=0xfff5f7fb;
    private static final int INK=0xff112a45;
    private static final int SUB=0xff586d84;
    private static final int LINE=0xffdee5ee;
    private static final String[] IDS={"sel","calc","res","protect","manufacture","install"};
    private static final String[] EN={"Cable Selection","Electrical Calculations","Cable Resistance",
            "Cable Protection","Manufacturing Cost","Installation & Testing"};
    private static final int[] ART={11,12,13,14,15,16};
    private static final String[] SYMBOL={"⌖","▦","Ω","♢","▥","⚒"};
    private static final int[] TINT={0xfffff6e6,0xffecf4fe,0xfff7f3ea,0xffffeeec,0xfffff5df,0xffeaf3fd};
    private LinearLayout root, page;
    private EditText queryInput;
    private int mode;
    private int d(float v){return (int)(getResources().getDisplayMetrics().density*v+.5f);}
    private String tr(String key){return I18n.t(this,key);}
    private boolean rtl(){return I18n.index(this)==0;}
    private String lang(){return I18n.language(this);}

    private GradientDrawable rounded(int background,int radius,int stroke){
        GradientDrawable g=new GradientDrawable();
        g.setColor(background);g.setCornerRadius(d(radius));
        if(stroke!=0)g.setStroke(d(1),stroke);
        return g;
    }
    private LinearLayout column(){
        LinearLayout v=new LinearLayout(this);v.setOrientation(LinearLayout.VERTICAL);
        v.setLayoutDirection(rtl()?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        return v;
    }
    private TextView txt(String value,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(value);t.setTextColor(color);t.setTextSize(size);
        t.setTextDirection(rtl()?View.TEXT_DIRECTION_RTL:View.TEXT_DIRECTION_LTR);
        t.setGravity((rtl()?Gravity.RIGHT:Gravity.LEFT)|Gravity.CENTER_VERTICAL);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }
    private LinearLayout.LayoutParams lp(int w,int h){
        return new LinearLayout.LayoutParams(w<0?w:d(w),h<0?h:d(h));
    }
    private void padSpace(LinearLayout v,int px){v.addView(new View(this),lp(1,px));}
    private void setClick(View view,CharSequence description,Runnable cb){
        view.setClickable(true);view.setFocusable(true);view.setContentDescription(description);
        view.setOnClickListener(v->cb.run());
    }

    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        forceLightIcons();
        build();
    }
    private void forceLightIcons(){
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        if(Build.VERSION.SDK_INT>=30 && getWindow().getInsetsController()!=null){
            getWindow().getInsetsController().setSystemBarsAppearance(0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS|
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
        }else{
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }
    @Override public void onWindowFocusChanged(boolean focused){
        super.onWindowFocusChanged(focused);
        if(focused)forceLightIcons();
    }
    @Override protected void onResume(){
        super.onResume();
        if(mode!=I18n.index(this))build();
    }

    private void build(){
        mode=I18n.index(this);
        root=column();root.setBackgroundColor(NAVY);
        setContentView(root);
        // Target SDK 35 edge-to-edge: reserve real system bar insets, not guessed pixel offsets.
        root.setOnApplyWindowInsetsListener((v,insets)->{
            int top,bottom;
            if(Build.VERSION.SDK_INT>=30){
                top=insets.getInsets(WindowInsets.Type.statusBars()).top;
                bottom=insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            }else{
                top=insets.getSystemWindowInsetTop();bottom=insets.getSystemWindowInsetBottom();
            }
            root.setPadding(0,top,0,bottom);
            return insets;
        });
        root.requestApplyInsets();
        header();
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setBackgroundColor(SURFACE);
        scroll.setClipToPadding(false);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        page=column();
        page.setPadding(d(14),d(16),d(14),d(18));
        scroll.addView(page);
        search();
        largeLibraryCard();
        LinearLayout.LayoutParams intro=lp(-1,-2);intro.topMargin=d(18);intro.bottomMargin=d(9);
        TextView sec=txt(tr("modules"),19,INK,true);
        page.addView(sec,intro);
        cardRow(0,1);
        cardRow(2,3);
        cardRow(4,5);
        TextView safe=txt(tr("catalogHint"),11,SUB,false);
        safe.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sl=lp(-1,-2);sl.topMargin=d(8);
        page.addView(safe,sl);
        bottomNav();
    }

    private void header(){
        LinearLayout row=new LinearLayout(this);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(d(10),d(8),d(10),d(8));
        row.setBackgroundColor(NAVY);
        root.addView(row,lp(-1,94));
        ImageView logo=new ImageView(this);
        logo.setImageResource(R.drawable.ab_brand_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("AB Cable Cost original logo");
        row.addView(logo,lp(86,72));
        LinearLayout titles=column();
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);
        tp.leftMargin=d(4);
        row.addView(titles,tp);
        TextView name=txt("AB Cable Cost",20,Color.WHITE,true);
        name.setTextDirection(View.TEXT_DIRECTION_LTR);
        name.setGravity(Gravity.CENTER);
        titles.addView(name);
        TextView sub=txt(tr("tagline"),10,GOLD,false);sub.setGravity(Gravity.CENTER);
        sub.setMaxLines(2);sub.setEllipsize(TextUtils.TruncateAt.END);titles.addView(sub);
        LinearLayout right=column();right.setGravity(Gravity.CENTER);
        row.addView(right,lp(68,-1));
        TextView langButton=txt("🌐 "+lang().toUpperCase(Locale.ROOT)+" ▾",12,GOLD,true);
        langButton.setTextDirection(View.TEXT_DIRECTION_LTR);
        langButton.setGravity(Gravity.CENTER);
        langButton.setBackground(rounded(0xff193753,13,0xff9baec1));
        right.addView(langButton,lp(-1,40));
        setClick(langButton,tr("language"),this::showLanguages);
        TextView gear=txt("⚙",21,Color.WHITE,true);
        gear.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams gp=lp(-1,33);gp.topMargin=d(4);
        right.addView(gear,gp);
        setClick(gear,tr("settings"),this::showSettingsMenu);
    }

    private void search(){
        LinearLayout wrap=new LinearLayout(this);
        wrap.setLayoutDirection(rtl()?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        wrap.setGravity(Gravity.CENTER_VERTICAL);
        wrap.setPadding(d(12),d(4),d(12),d(4));
        wrap.setBackground(rounded(Color.WHITE,24,LINE));
        wrap.setElevation(d(2));
        LinearLayout.LayoutParams wp=lp(-1,54);wp.bottomMargin=d(13);
        page.addView(wrap,wp);
        TextView searchIcon=txt("⌕",29,NAVY2,true);
        searchIcon.setGravity(Gravity.CENTER);
        wrap.addView(searchIcon,lp(42,-1));
        queryInput=new EditText(this);
        queryInput.setSingleLine(true);
        queryInput.setTextSize(15);
        queryInput.setHint(tr("search"));
        queryInput.setHintTextColor(0xff8798aa);
        queryInput.setTextColor(INK);
        queryInput.setBackgroundColor(Color.TRANSPARENT);
        queryInput.setInputType(InputType.TYPE_CLASS_TEXT);
        queryInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        queryInput.setGravity((rtl()?Gravity.RIGHT:Gravity.LEFT)|Gravity.CENTER_VERTICAL);
        wrap.addView(queryInput,new LinearLayout.LayoutParams(0,-1,1));
        queryInput.setOnEditorActionListener((v,action,event)->{
            if(action==EditorInfo.IME_ACTION_SEARCH){searchCatalog();return true;}
            return false;
        });
        setClick(searchIcon,tr("searchTip"),this::searchCatalog);
    }

    private void largeLibraryCard(){
        FrameLayout feature=new FrameLayout(this);
        feature.setBackground(rounded(NAVY,17,0));
        feature.setClipToOutline(true);
        feature.setElevation(d(3));
        LinearLayout.LayoutParams fp=lp(-1,140);fp.bottomMargin=d(12);
        page.addView(feature,fp);
        CableVisual art=new CableVisual(this,10);
        FrameLayout.LayoutParams imageLp=new FrameLayout.LayoutParams(d(143),d(132),
                rtl()?Gravity.LEFT|Gravity.CENTER_VERTICAL:Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        feature.addView(art,imageLp);
        LinearLayout copy=column();
        copy.setGravity(Gravity.CENTER_VERTICAL);
        copy.setPadding(d(12),d(12),d(12),d(12));
        FrameLayout.LayoutParams txtLp=new FrameLayout.LayoutParams(-1,-1);
        if(rtl()){txtLp.leftMargin=d(132);}else{txtLp.rightMargin=d(132);}
        feature.addView(copy,txtLp);
        TextView mark=txt("▣",29,GOLD,true);copy.addView(mark);
        TextView h=txt(tr("lib"),19,Color.WHITE,true);copy.addView(h);
        TextView en=txt("Cable Library",12,0xffccd6e5,false);if(mode!=1)copy.addView(en);
        padSpace(copy,5);
        TextView blurb=txt(tr("libDesc"),11,Color.WHITE,false);
        blurb.setMaxLines(3);
        copy.addView(blurb);
        setClick(feature,tr("lib"),()->openCatalog(null,null));
    }

    private void cardRow(int a,int b){
        LinearLayout line=new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setLayoutDirection(rtl()?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        LinearLayout.LayoutParams rp=lp(-1,-2);rp.bottomMargin=d(11);
        page.addView(line,rp);
        LinearLayout.LayoutParams first=new LinearLayout.LayoutParams(0,d(173),1);
        first.rightMargin=d(5);first.leftMargin=d(3);
        line.addView(card(a),first);
        LinearLayout.LayoutParams second=new LinearLayout.LayoutParams(0,d(173),1);
        second.leftMargin=d(3);second.rightMargin=d(5);
        line.addView(card(b),second);
    }

    private FrameLayout card(int id){
        FrameLayout f=new FrameLayout(this);
        f.setBackground(rounded(TINT[id],15,LINE));f.setClipToOutline(true);
        f.setElevation(d(2));
        CableVisual photo=new CableVisual(this,ART[id]);
        FrameLayout.LayoutParams image=new FrameLayout.LayoutParams(d(100),d(91),
                rtl()?Gravity.LEFT|Gravity.TOP:Gravity.RIGHT|Gravity.TOP);
        image.topMargin=d(3);
        f.addView(photo,image);
        TextView emblem=txt(SYMBOL[id],22,GOLD,true);
        emblem.setGravity(Gravity.CENTER);
        emblem.setBackground(rounded(NAVY,11,GOLD));
        FrameLayout.LayoutParams el=new FrameLayout.LayoutParams(d(41),d(43),
                rtl()?Gravity.RIGHT|Gravity.TOP:Gravity.LEFT|Gravity.TOP);
        el.topMargin=d(10);el.leftMargin=d(9);el.rightMargin=d(9);
        f.addView(emblem,el);
        LinearLayout texts=column();
        texts.setPadding(d(10),d(3),d(10),d(4));
        FrameLayout.LayoutParams tx=new FrameLayout.LayoutParams(-1,d(75),Gravity.BOTTOM);
        f.addView(texts,tx);
        TextView h=txt(tr(IDS[id]),14,INK,true);
        h.setMaxLines(2);h.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(h);
        TextView sub=txt(EN[id],10,0xff425b77,false);
        sub.setTextDirection(View.TEXT_DIRECTION_LTR);
        sub.setMaxLines(1);sub.setEllipsize(TextUtils.TruncateAt.END);
        if(mode!=1)texts.addView(sub);
        TextView desc=txt(tr(IDS[id]+"Desc"),10,SUB,false);
        desc.setMaxLines(2);desc.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(desc);
        setClick(f,tr(IDS[id]),()->handleCard(id));
        return f;
    }

    private void handleCard(int id){
        switch(id){
            case 0: openCatalog(null,null);break; // existing technical catalogue
            case 4: openCatalog(CableCatalog.THERMO,null);break; // existing manufacturing BOM
            default: showNotReady(IDS[id]);break; // future calculators are NOT invented
        }
    }
    private void openCatalog(String family,String spec){
        Intent i=new Intent(this,CatalogActivity.class);
        if(family!=null)i.putExtra("family",family);
        if(spec!=null)i.putExtra("specId",spec);
        startActivity(i);
    }
    private void openOldCalculator(){
        startActivity(new Intent(this,CalculatorActivity.class));
    }
    private void showNotReady(String key){
        new AlertDialog.Builder(this)
            .setTitle(tr(key))
            .setMessage(tr("soon"))
            .setPositiveButton("OK",null).show();
    }

    private void bottomNav(){
        LinearLayout bar=new LinearLayout(this);
        bar.setBackgroundColor(NAVY);
        bar.setLayoutDirection(rtl()?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(d(7),d(5),d(7),d(3));
        root.addView(bar,lp(-1,67));
        addTab(bar,"⌂","home",true,()->{});
        addTab(bar,"▣","catalog",false,()->openCatalog(null,null));
        addTab(bar,"▦","tools",false,this::showTools);
        addTab(bar,"⚙","settings",false,this::showSettingsMenu);
    }
    private void addTab(LinearLayout bar,String glyph,String id,boolean active,Runnable action){
        LinearLayout tab=column();tab.setGravity(Gravity.CENTER);
        bar.addView(tab,new LinearLayout.LayoutParams(0,-1,1));
        TextView icon=txt(glyph,27,active?GOLD:0xffc8d7e8,true);
        icon.setGravity(Gravity.CENTER);tab.addView(icon,lp(-1,32));
        TextView label=txt(tr(id),11,active?GOLD:0xffdce6f4,active);
        label.setGravity(Gravity.CENTER);tab.addView(label);
        setClick(tab,tr(id),action);
    }
    private void showTools(){
        String[] choices={tr("weights"),tr("prices"),tr("engineering")};
        new AlertDialog.Builder(this).setTitle(tr("tools"))
            .setItems(choices,(dialog,which)->{
                if(which==0)openOldCalculator();
                else if(which==1)openCatalogSettings("prices");
                else showNotReady("calc");
            }).setNegativeButton(tr("cancel"),null).show();
    }
    private void showSettingsMenu(){
        String[] choices={tr("language"),tr("factory"),tr("prices")};
        new AlertDialog.Builder(this).setTitle(tr("settings"))
            .setItems(choices,(dialog,which)->{
                if(which==0)showLanguages();
                else openCatalogSettings(which==1?"settings":"prices");
            }).setNegativeButton(tr("cancel"),null).show();
    }
    private void openCatalogSettings(String screen){
        Intent i=new Intent(this,CatalogActivity.class);
        i.putExtra("screen",screen);
        startActivity(i);
    }
    private void showLanguages(){
        String[] values={"العربية — Arabic","English","Français","Türkçe"};
        new AlertDialog.Builder(this).setTitle(tr("langTitle"))
            .setSingleChoiceItems(values,I18n.index(this),(dlg,which)->{
                I18n.set(this,I18n.CODES[which]);
                dlg.dismiss();
                build();
            }).setNegativeButton(tr("cancel"),null).show();
    }
    private void searchCatalog(){
        String q=queryInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        if(q.isEmpty()){
            openCatalog(null,null);return;
        }
        List<CableCatalog.Spec> found=new ArrayList<>();
        for(CableCatalog.Spec s:CableCatalog.all()){
            String content=(s.model+" "+s.family+" "+s.manufacturer+" "+s.standard+
                " "+s.note).toLowerCase(Locale.ROOT);
            if(content.contains(q))found.add(s);
        }
        if(found.isEmpty()){
            new AlertDialog.Builder(this).setTitle(tr("results"))
                .setMessage(tr("noResults")).setPositiveButton("OK",null).show();
            return;
        }
        int limit=Math.min(25,found.size());
        String[] items=new String[limit];
        for(int x=0;x<limit;x++){
            CableCatalog.Spec s=found.get(x);
            items[x]=s.model+" • "+s.manufacturer+" • "+s.family;
        }
        new AlertDialog.Builder(this).setTitle(tr("results")+" ("+found.size()+")")
            .setItems(items,(dlg,idx)->{
                CableCatalog.Spec s=found.get(idx);
                openCatalog(s.family,s.id);
            }).setNegativeButton(tr("cancel"),null).show();
    }
}
