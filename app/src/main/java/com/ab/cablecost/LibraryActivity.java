package com.ab.cablecost;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * User-approved Global Cable Library search page.
 * Searches ONLY VerifiedCableData; generic manufacturing templates are excluded.
 * Each displayed record has a traceable official manufacturer URL.
 */
public class LibraryActivity extends Activity {
    private LinearLayout root, content, results, filters, types;
    private EditText search;
    private SharedPreferences pref;
    private String brand="*", country="*", category="*", voltage="*", size="*";
    private boolean favoritesOnly=false;
    private int displayedLanguage=-1;
    private int currentCount=0;
    private String pendingQuery="";
    private final String[] CAT_KEYS={"*","Power","Control","Fire Alarm","Coax","Data"};
    private final String[] CAT_TEXT={"everyCable","powerCables","controlCables","fireCables","coaxCables","dataCables"};
    private static final int NAVY=LibraryUi.NAVY, GOLD=LibraryUi.GOLD;
    private int d(int n){return LibraryUi.dp(this,n);}
    private String t(String key){return LibraryUi.tr(this,key);}
    private TextView label(String s,int size,int color,boolean bold){
        return LibraryUi.text(this,s,size,color,bold);
    }
    private LinearLayout col(){return LibraryUi.col(this);}
    private LinearLayout row(){return LibraryUi.row(this);}
    private boolean isAr(){return LibraryUi.rtl(this);}

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        pref=getSharedPreferences("ab_global_cable_library",MODE_PRIVATE);
        pendingQuery=getIntent().getStringExtra("query");
        if(pendingQuery==null)pendingQuery="";
        String preset=getIntent().getStringExtra("category");
        if(preset!=null)category=preset;
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        renderPage();
    }
    @Override protected void onResume(){
        super.onResume();
        if(displayedLanguage!=I18n.index(this))renderPage();
        else if(results!=null)updateResults();
    }
    private void renderPage(){
        displayedLanguage=I18n.index(this);
        root=col();root.setBackgroundColor(NAVY);
        setContentView(root);
        if(Build.VERSION.SDK_INT>=21) {
            root.setOnApplyWindowInsetsListener((v,i)->{
                int top,bottom;
                if(Build.VERSION.SDK_INT>=30) {
                    top=i.getInsets(WindowInsets.Type.statusBars()).top;
                    bottom=i.getInsets(WindowInsets.Type.navigationBars()).bottom;
                }else {
                    top=i.getSystemWindowInsetTop();bottom=i.getSystemWindowInsetBottom();
                }
                root.setPadding(0,top,0,bottom);return i;
            });
            root.requestApplyInsets();
        }
        header();
        ScrollView scroll=new ScrollView(this);
        scroll.setBackgroundColor(NAVY);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        content=col();
        content.setPadding(d(11),d(7),d(11),d(14));
        scroll.addView(content);
        TextView intro=label(t("libraryHeadline"),13,GOLD,true);
        intro.setGravity(Gravity.CENTER);
        content.addView(intro,LibraryUi.lp(this,-1,30));
        searchBox();
        filterChips();
        categoryCards();
        LinearLayout heading=row();
        LinearLayout.LayoutParams hp=LibraryUi.lp(this,-1,44);hp.topMargin=d(10);
        content.addView(heading,hp);
        TextView resultTitle=label(t("searchResults"),17,GOLD,true);
        heading.addView(resultTitle,new LinearLayout.LayoutParams(0,-1,1));
        TextView sort=LibraryUi.chip(this,t("relevance")+" ▾",false,
            ()->new AlertDialog.Builder(this).setItems(new String[]{
                t("relevance"),"A → Z","Z → A"},(dlg,which)->{
                    sorting=which;updateResults();
                }).show());
        heading.addView(sort,LibraryUi.lp(this,105,36));
        results=col();
        content.addView(results,LibraryUi.lp(this,-1,-2));
        TextView disclaimer=label(t("verifiedOnly"),11,LibraryUi.MUTED,false);
        disclaimer.setPadding(d(8),d(8),d(8),d(12));
        content.addView(disclaimer);
        bottomNav();
        search.setText(pendingQuery);
        search.setSelection(search.length());
        updateResults();
    }
    private int sorting=0;
    private void header(){
        LinearLayout head=row();head.setGravity(Gravity.CENTER_VERTICAL);
        head.setPadding(d(7),d(7),d(7),d(4));
        root.addView(head,LibraryUi.lp(this,-1,75));
        ImageView logo=new ImageView(this);
        logo.setImageResource(R.drawable.ab_brand_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("Original AB logo");
        head.addView(logo,LibraryUi.lp(this,61,61));
        LinearLayout names=col();
        LinearLayout.LayoutParams nm=new LinearLayout.LayoutParams(0,-2,1);
        nm.leftMargin=d(5);nm.rightMargin=d(5);
        head.addView(names,nm);
        TextView brandName=label("AB Cable Engineering",18,GOLD,true);
        names.addView(brandName);
        TextView subtitle=label("Global Cable Library",12,LibraryUi.WHITE,false);
        names.addView(subtitle);
        TextView lang=LibraryUi.button(this,"◎ "+I18n.language(this).toUpperCase(Locale.ROOT)+"⌄",
            false,this::languageDialog);
        head.addView(lang,LibraryUi.lp(this,68,40));
        TextView back=LibraryUi.centered(this,isAr()?"›":"‹",29,LibraryUi.WHITE,true);
        head.addView(back,LibraryUi.lp(this,30,48));
        LibraryUi.tappable(back,t("back"),this::finish);
    }
    private void searchBox(){
        LinearLayout wrap=row();wrap.setGravity(Gravity.CENTER_VERTICAL);
        wrap.setBackground(LibraryUi.bg(this,Color.WHITE,13,0));
        wrap.setPadding(d(9),d(3),d(9),d(3));
        LinearLayout.LayoutParams wp=LibraryUi.lp(this,-1,55);
        wp.bottomMargin=d(6);content.addView(wrap,wp);
        TextView icon=LibraryUi.centered(this,"⌕",28,LibraryUi.INK,true);
        wrap.addView(icon,LibraryUi.lp(this,37,44));
        search=new EditText(this);
        search.setSingleLine(true);search.setTextSize(14);
        search.setTextColor(LibraryUi.INK);search.setHintTextColor(0xff64778d);
        search.setBackgroundColor(Color.TRANSPARENT);
        search.setHint(t("searchLibrary"));
        search.setGravity((isAr()?Gravity.RIGHT:Gravity.LEFT)|Gravity.CENTER_VERTICAL);
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        wrap.addView(search,new LinearLayout.LayoutParams(0,-1,1));
        search.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){
                pendingQuery=s.toString();updateResults();
            }
            public void afterTextChanged(Editable s){}
        });
        TextView barcode=LibraryUi.centered(this,"▥",22,LibraryUi.INK,true);
        wrap.addView(barcode,LibraryUi.lp(this,34,44));
        LibraryUi.tappable(barcode,t("searchLibrary"),()->search.requestFocus());
    }
    private void filterChips(){
        HorizontalScrollView sc=new HorizontalScrollView(this);
        sc.setHorizontalScrollBarEnabled(false);sc.setFillViewport(false);
        content.addView(sc,LibraryUi.lp(this,-1,49));
        filters=row();filters.setPadding(d(3),d(3),d(3),d(3));
        sc.addView(filters);
        addFilter(t("manufacturer"),brand,()->filterDialog("brand"));
        addFilter(t("country"),country,()->filterDialog("country"));
        addFilter(t("cableType"),category,()->filterDialog("category"));
        addFilter(t("voltage"),voltage,()->filterDialog("voltage"));
        addFilter(t("section"),size,()->filterDialog("size"));
    }
    private void addFilter(String title,String selected,Runnable handler){
        String display=selected.equals("*")?title:title+" · "+selected;
        TextView item=LibraryUi.chip(this,display+" ⌄",!selected.equals("*"),handler);
        LinearLayout.LayoutParams p=LibraryUi.lp(this,-2,36);p.rightMargin=d(7);
        filters.addView(item,p);
    }
    private String[] uniqueOptions(String field){
        Set<String> options=new HashSet<>();
        for(VerifiedCableData.Cable c:VerifiedCableData.all()){
            if(field.equals("brand"))options.add(c.brand);
            else if(field.equals("country"))options.add(c.country(isAr()));
            else if(field.equals("category"))options.add(c.category(isAr()));
            else if(field.equals("voltage")&&c.voltage!=null)options.add(c.voltage);
            else if(field.equals("size"))options.add(c.size);
        }
        List<String> sorted=new ArrayList<>(options);Collections.sort(sorted);
        sorted.add(0,t("all"));return sorted.toArray(new String[0]);
    }
    private void filterDialog(String field){
        String[] opts=uniqueOptions(field);
        new AlertDialog.Builder(this).setTitle(
            field.equals("brand")?t("manufacturer"):field.equals("country")?t("country"):
            field.equals("category")?t("cableType"):field.equals("voltage")?t("voltage"):t("section"))
            .setItems(opts,(dialog,which)->{
                String value=which==0?"*":opts[which];
                if(field.equals("brand"))brand=value;
                else if(field.equals("country"))country=value;
                else if(field.equals("category")){
                    category="*";
                    if(which>0)for(VerifiedCableData.Cable c:VerifiedCableData.all()){
                        if(c.category(isAr()).equals(value)){category=c.category;break;}
                    }
                }
                else if(field.equals("voltage"))voltage=value;
                else size=value;
                refreshDynamicSections();
            }).setNegativeButton(t("cancel"),null).show();
    }
    private void refreshDynamicSections(){
        if(filters!=null){filters.removeAllViews();
            addFilter(t("manufacturer"),brand,()->filterDialog("brand"));
            addFilter(t("country"),country,()->filterDialog("country"));
            addFilter(t("cableType"),category,()->filterDialog("category"));
            addFilter(t("voltage"),voltage,()->filterDialog("voltage"));
            addFilter(t("section"),size,()->filterDialog("size"));
        }
        if(types!=null)populateCategories();
        updateResults();
    }
    private void categoryCards(){
        HorizontalScrollView sc=new HorizontalScrollView(this);
        sc.setHorizontalScrollBarEnabled(false);
        LinearLayout.LayoutParams sp=LibraryUi.lp(this,-1,111);sp.topMargin=d(5);
        content.addView(sc,sp);
        types=row();
        sc.addView(types);
        populateCategories();
    }
    private void populateCategories(){
        types.removeAllViews();
        String[] icons={"◈","⚡","≋","♨","◉","⌁"};
        for(int i=0;i<CAT_KEYS.length;i++){
            final String key=CAT_KEYS[i];
            LinearLayout tile=col();tile.setGravity(Gravity.CENTER);
            tile.setBackground(LibraryUi.bg(this,key.equals(category)?0xff273d4c:LibraryUi.CARD,12,
                key.equals(category)?GOLD:LibraryUi.BORDER));
            LinearLayout.LayoutParams pp=LibraryUi.lp(this,91,100);pp.rightMargin=d(6);
            types.addView(tile,pp);
            TextView pict=LibraryUi.centered(this,icons[i],27,key.equals(category)?GOLD:0xffd9edf8,true);
            tile.addView(pict,LibraryUi.lp(this,-1,45));
            TextView name=LibraryUi.centered(this,t(CAT_TEXT[i]),12,LibraryUi.WHITE,true);
            name.setMaxLines(2);tile.addView(name,LibraryUi.lp(this,-1,43));
            LibraryUi.tappable(tile,t(CAT_TEXT[i]),()->{category=key;refreshDynamicSections();});
        }
    }
    private boolean matches(VerifiedCableData.Cable c){
        if(favoritesOnly&&!pref.getBoolean("fav_"+c.id,false))return false;
        String q=pendingQuery.trim().toLowerCase(Locale.ROOT);
        if(!q.isEmpty()&&!c.searchIndex().contains(q))return false;
        if(!brand.equals("*")&&!brand.equals(c.brand))return false;
        if(!country.equals("*")&&!country.equals(c.country(isAr())))return false;
        if(!category.equals("*")&&!category.equals(c.category))return false;
        if(!voltage.equals("*")&&!voltage.equals(c.voltage))return false;
        if(!size.equals("*")&&!size.equals(c.size))return false;
        return true;
    }
    private void updateResults(){
        if(results==null)return;
        results.removeAllViews();
        List<VerifiedCableData.Cable> list=new ArrayList<>();
        for(VerifiedCableData.Cable c:VerifiedCableData.all()){
            if(matches(c))list.add(c);
        }
        if(sorting==1)Collections.sort(list,(a,b)->a.model.compareToIgnoreCase(b.model));
        if(sorting==2)Collections.sort(list,(a,b)->b.model.compareToIgnoreCase(a.model));
        currentCount=list.size();
        TextView counter=label(list.size()+" / "+VerifiedCableData.officialCount()+
            "  •  "+t("verifiedCount"),11,GOLD,false);
        LinearLayout.LayoutParams cp=LibraryUi.lp(this,-1,30);
        results.addView(counter,cp);
        if(list.isEmpty()){
            TextView empty=label(t("noMatching"),14,LibraryUi.MUTED,false);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(d(16),d(30),d(16),d(30));
            empty.setBackground(LibraryUi.bg(this,LibraryUi.CARD,12,LibraryUi.BORDER));
            results.addView(empty,LibraryUi.lp(this,-1,-2));return;
        }
        for(VerifiedCableData.Cable c:list)resultCard(c);
    }
    private void resultCard(VerifiedCableData.Cable c){
        LinearLayout box=col();
        box.setPadding(d(8),d(8),d(8),d(9));
        box.setBackground(LibraryUi.bg(this,LibraryUi.CARD,14,LibraryUi.BORDER));
        LinearLayout.LayoutParams outer=LibraryUi.lp(this,-1,-2);outer.bottomMargin=d(10);
        results.addView(box,outer);
        LinearLayout top=row();
        box.addView(top,LibraryUi.lp(this,-1,-2));
        CableArtView illustration=new CableArtView(this,c,false);
        illustration.setBackground(LibraryUi.bg(this,LibraryUi.LIGHT,10,0));
        top.addView(illustration,LibraryUi.lp(this,117,116));
        LinearLayout info=col();
        LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(0,-2,1);
        ip.leftMargin=d(8);ip.rightMargin=d(8);
        top.addView(info,ip);
        info.addView(label(c.brand+"  ·  "+c.model,16,LibraryUi.WHITE,true));
        info.addView(label(c.size,13,LibraryUi.MUTED,false));
        info.addView(label(c.title(isAr()),12,LibraryUi.WHITE,false));
        TextView badge=label("✓ "+t("approvedSource")+"  •  "+c.country(isAr()),11,
            LibraryUi.GREEN,true);
        info.addView(badge);
        TextView tags=label(c.category(isAr())+"   |   "+
            (c.voltage==null?t("notPublished"):c.voltage),11,GOLD,false);
        info.addView(tags);
        LinearLayout actions=row();
        LinearLayout.LayoutParams ap=LibraryUi.lp(this,-1,43);ap.topMargin=d(7);
        box.addView(actions,ap);
        TextView details=LibraryUi.button(this,t("viewDetails"),true,()->openDetail(c));
        actions.addView(details,new LinearLayout.LayoutParams(0,-1,1));
        TextView pdf=LibraryUi.button(this,"PDF ↗",false,()->openSource(c));
        LinearLayout.LayoutParams pdfp=LibraryUi.lp(this,87,-1);
        pdfp.leftMargin=d(7);pdfp.rightMargin=d(7);
        actions.addView(pdf,pdfp);
        boolean saved=pref.getBoolean("fav_"+c.id,false);
        TextView fav=LibraryUi.centered(this,saved?"★":"☆",27,GOLD,true);
        actions.addView(fav,LibraryUi.lp(this,36,-1));
        LibraryUi.tappable(fav,t("favourite"),()->{
            pref.edit().putBoolean("fav_"+c.id,!saved).apply();updateResults();
        });
        LibraryUi.tappable(illustration,t("viewDetails"),()->openDetail(c));
    }
    private void openDetail(VerifiedCableData.Cable c){
        pref.edit().putString("lastViewed",c.id).apply();
        Intent i=new Intent(this,CableDetailActivity.class);
        i.putExtra("id",c.id);startActivity(i);
    }
    private void openSource(VerifiedCableData.Cable c){
        if(c.pdfUrl==null||!c.pdfUrl.startsWith("https://")){
            new AlertDialog.Builder(this).setMessage(t("sourceMissing"))
                .setPositiveButton("OK",null).show();return;
        }
        try{
            startActivity(new Intent(Intent.ACTION_VIEW,android.net.Uri.parse(c.pdfUrl)));
        }catch(Exception e){
            new AlertDialog.Builder(this).setMessage(t("sourceMissing"))
                .setPositiveButton("OK",null).show();
        }
    }
    private void bottomNav(){
        LinearLayout bar=row();
        bar.setBackgroundColor(LibraryUi.MID);
        bar.setPadding(d(4),d(3),d(4),d(3));
        root.addView(bar,LibraryUi.lp(this,-1,62));
        tab(bar,"⌂",t("home"),false,this::finish);
        tab(bar,"▣",t("catalog"),true,()->{favoritesOnly=false;refreshDynamicSections();});
        tab(bar,"☆",t("favourite"),favoritesOnly,()->{
            favoritesOnly=!favoritesOnly;refreshDynamicSections();
        });
        tab(bar,"◷",t("history"),false,()->{
            String last=pref.getString("lastViewed",null);
            VerifiedCableData.Cable c=VerifiedCableData.byId(last);
            if(c==null)new AlertDialog.Builder(this).setMessage(t("noMatching"))
                .setPositiveButton("OK",null).show();
            else openDetail(c);
        });
        tab(bar,"≡",t("list"),false,()->{
            brand="*";country="*";category="*";voltage="*";size="*";favoritesOnly=false;
            pendingQuery="";search.setText("");refreshDynamicSections();
        });
    }
    private void tab(LinearLayout bar,String glyph,String title,boolean selected,Runnable run){
        LinearLayout item=col();item.setGravity(Gravity.CENTER);
        bar.addView(item,new LinearLayout.LayoutParams(0,-1,1));
        item.addView(LibraryUi.centered(this,glyph,23,selected?GOLD:LibraryUi.MUTED,true));
        item.addView(LibraryUi.centered(this,title,10,selected?GOLD:LibraryUi.WHITE,selected));
        LibraryUi.tappable(item,title,run);
    }
    private void languageDialog(){
        String[] values={"العربية","English","Français","Türkçe"};
        new AlertDialog.Builder(this).setTitle(t("langTitle"))
            .setSingleChoiceItems(values,I18n.index(this),(dlg,index)->{
                I18n.set(this,I18n.CODES[index]);dlg.dismiss();renderPage();
            }).setNegativeButton(t("cancel"),null).show();
    }
}
