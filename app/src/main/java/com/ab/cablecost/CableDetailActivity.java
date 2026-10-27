package com.ab.cablecost;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.IOException;
import java.util.Locale;

/**
 * Structured manufacturer-sourced technical card; vector cutaway, swipeable
 * illustrative 3D, specifications, uses, standards, original PDF, export report.
 */
public class CableDetailActivity extends Activity {
    private static final int SAVE_PDF=4210;
    private final int GOLD=LibraryUi.GOLD, NAVY=LibraryUi.NAVY;
    private LinearLayout root, body, tabRow, tabPane;
    private VerifiedCableData.Cable cable;
    private SharedPreferences prefs;
    private int activeTab=0,language=-1;
    private boolean ar(){return LibraryUi.rtl(this);}
    private int d(int n){return LibraryUi.dp(this,n);}
    private String tr(String s){return LibraryUi.tr(this,s);}
    private TextView txt(String s,int size,int color,boolean bold){
        return LibraryUi.text(this,s,size,color,bold);
    }
    private LinearLayout col(){return LibraryUi.col(this);}
    private LinearLayout row(){return LibraryUi.row(this);}
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        prefs=getSharedPreferences("ab_global_cable_library",MODE_PRIVATE);
        cable=VerifiedCableData.byId(getIntent().getStringExtra("id"));
        if(cable==null){finish();return;}
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        render();
    }
    @Override protected void onResume(){
        super.onResume();
        if(cable!=null&&language!=I18n.index(this))render();
    }
    private void render(){
        language=I18n.index(this);
        root=col();
        root.setBackgroundColor(NAVY);
        setContentView(root);
        if(Build.VERSION.SDK_INT>=21){
            root.setOnApplyWindowInsetsListener((v,i)->{
                int top,bottom;
                if(Build.VERSION.SDK_INT>=30) {
                    top=i.getInsets(WindowInsets.Type.statusBars()).top;
                    bottom=i.getInsets(WindowInsets.Type.navigationBars()).bottom;
                }else{
                    top=i.getSystemWindowInsetTop();bottom=i.getSystemWindowInsetBottom();
                }
                root.setPadding(0,top,0,bottom);return i;
            });
            root.requestApplyInsets();
        }
        header();
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setBackgroundColor(NAVY);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        body=col();
        body.setPadding(d(11),d(7),d(11),d(14));
        scroll.addView(body);
        productIdentity();
        tabs();
        tabPane=col();
        body.addView(tabPane,LibraryUi.lp(this,-1,-2));
        renderTab();
        conditions();
        sourceFooter();
    }
    private void header(){
        LinearLayout h=row();h.setPadding(d(8),d(4),d(8),d(4));
        h.setGravity(Gravity.CENTER_VERTICAL);
        h.setBackgroundColor(0xff091a2d);
        root.addView(h,LibraryUi.lp(this,-1,61));
        TextView back=LibraryUi.centered(this,ar()?"›":"‹",29,LibraryUi.WHITE,true);
        h.addView(back,LibraryUi.lp(this,36,-1));
        LibraryUi.tappable(back,tr("back"),this::finish);
        ImageView logo=new ImageView(this);
        logo.setImageResource(R.drawable.ab_brand_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("Original AB logo");
        h.addView(logo,LibraryUi.lp(this,44,45));
        LinearLayout titles=col();
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);
        tp.leftMargin=d(6);tp.rightMargin=d(6);
        h.addView(titles,tp);
        TextView app=txt("AB Cable Engineering",15,GOLD,true);
        titles.addView(app);
        titles.addView(txt("Global Cable Library",10,LibraryUi.MUTED,false));
        TextView locale=LibraryUi.chip(this,I18n.language(this).toUpperCase(Locale.ROOT)+" ⌄",
            false,this::languageDialog);
        h.addView(locale,LibraryUi.lp(this,52,40));
        TextView star=LibraryUi.centered(this,
            prefs.getBoolean("fav_"+cable.id,false)?"★":"☆",27,GOLD,true);
        h.addView(star,LibraryUi.lp(this,39,43));
        LibraryUi.tappable(star,tr("favourite"),()->{
            boolean newValue=!prefs.getBoolean("fav_"+cable.id,false);
            prefs.edit().putBoolean("fav_"+cable.id,newValue).apply();
            star.setText(newValue?"★":"☆");
        });
    }
    private void productIdentity(){
        LinearLayout row=row();
        LinearLayout.LayoutParams pp=LibraryUi.lp(this,-1,-2);pp.bottomMargin=d(9);
        body.addView(row,pp);
        LinearLayout info=col();
        row.addView(info,new LinearLayout.LayoutParams(0,-2,1));
        info.addView(txt(cable.brand+" "+cable.model,22,LibraryUi.WHITE,true));
        info.addView(txt(cable.size,15,LibraryUi.WHITE,false));
        info.addView(txt(cable.title(ar()),12,LibraryUi.MUTED,false));
        info.addView(txt("✓ "+tr("approvedSource")+"  ·  "+cable.country(ar()),
            12,LibraryUi.GREEN,true));
    }
    private void tabs(){
        HorizontalScrollView sc=new HorizontalScrollView(this);
        sc.setHorizontalScrollBarEnabled(false);
        LinearLayout.LayoutParams sp=LibraryUi.lp(this,-1,51);sp.bottomMargin=d(5);
        body.addView(sc,sp);
        tabRow=row();sc.addView(tabRow);
        String[] keys={"diagram","threeD","technical","applications","standards"};
        String[] symbols={"◉","⬡","▤","⚙","♧"};
        for(int i=0;i<keys.length;i++){
            final int index=i;
            TextView tab=LibraryUi.chip(this,symbols[i]+" "+tr(keys[i]),i==activeTab,
                ()->{activeTab=index;refreshTabs();renderTab();});
            LinearLayout.LayoutParams tp=LibraryUi.lp(this,-2,45);
            tp.rightMargin=d(5);tabRow.addView(tab,tp);
        }
    }
    private void refreshTabs(){
        tabRow.removeAllViews();
        String[] keys={"diagram","threeD","technical","applications","standards"};
        String[] symbols={"◉","⬡","▤","⚙","♧"};
        for(int i=0;i<keys.length;i++){
            final int index=i;
            TextView tab=LibraryUi.chip(this,symbols[i]+" "+tr(keys[i]),i==activeTab,
                ()->{activeTab=index;refreshTabs();renderTab();});
            LinearLayout.LayoutParams tp=LibraryUi.lp(this,-2,45);
            tp.rightMargin=d(5);tabRow.addView(tab,tp);
        }
    }
    private void renderTab(){
        if(tabPane==null)return;
        tabPane.removeAllViews();
        if(activeTab==0||activeTab==1)illustrationTab(activeTab==1);
        else if(activeTab==2)specificationTab();
        else if(activeTab==3)applicationTab();
        else standardTab();
    }
    private void illustrationTab(boolean threeD){
        LinearLayout white=col();
        white.setPadding(d(7),d(9),d(7),d(8));
        white.setBackground(LibraryUi.bg(this,Color.WHITE,12,0));
        tabPane.addView(white,LibraryUi.lp(this,-1,-2));
        CableArtView picture=new CableArtView(this,cable,threeD);
        white.addView(picture,LibraryUi.lp(this,-1,196));
        TextView hint=txt(threeD?tr("swipe3D"):tr("technicalDiagramNote"),12,
            LibraryUi.INK,false);
        hint.setPadding(d(8),d(7),d(8),d(7));
        white.addView(hint);
        LibraryUi.gap(this,tabPane,10);
        if(!threeD)renderLayerList();
        else{
            TextView schematic=txt(tr("technicalDiagramNote"),12,LibraryUi.MUTED,false);
            schematic.setPadding(d(8),d(7),d(8),d(8));
            tabPane.addView(schematic);
            renderLayerList();
        }
    }
    private void renderLayerList(){
        int[] colors={0xffdb6a34,0xff2d7ac0,0xff2a896a,0xffda9a27,0xff667383,
                      0xff9875a9,0xffad567b};
        for(int i=0;i<cable.layers.size();i++){
            final VerifiedCableData.Layer layer=cable.layers.get(i);
            LinearLayout line=row();
            line.setPadding(d(8),d(7),d(8),d(7));
            line.setGravity(Gravity.CENTER_VERTICAL);
            line.setBackground(LibraryUi.bg(this,0xfff9fbfe,8,0xffdde4eb));
            LinearLayout.LayoutParams lp=LibraryUi.lp(this,-1,-2);
            lp.bottomMargin=d(6);
            tabPane.addView(line,lp);
            TextView number=LibraryUi.centered(this,String.valueOf(i+1),14,Color.WHITE,true);
            number.setBackground(LibraryUi.bg(this,colors[i%colors.length],32,0));
            line.addView(number,LibraryUi.lp(this,31,31));
            LinearLayout item=col();
            LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(0,-2,1);
            ip.leftMargin=d(8);ip.rightMargin=d(8);
            line.addView(item,ip);
            item.addView(txt(layer.name(ar()),13,LibraryUi.INK,true));
            TextView description=txt(layer.description(ar()),11,0xff4c6175,false);
            item.addView(description);
            LibraryUi.tappable(line,layer.name(ar()),()->new AlertDialog.Builder(this)
                .setTitle(layer.name(ar())).setMessage(layer.description(ar()))
                .setPositiveButton("OK",null).show());
        }
    }
    private void specificationTab(){
        metric(tr("voltage"),cable.voltage);
        metric(tr("ampacity"),cable.current);
        metric(tr("resistanceValue"),cable.conductorResistance);
        metric(tr("nominalDiameter"),cable.diameter);
        metric(tr("temperature"),cable.temperature);
        metric(tr("insulation"),cable.insulation);
        metric(tr("shield"),cable.shield);
        metric(tr("conductor"),cable.construction);
    }
    private void metric(String title,String value){
        LinearLayout card=row();card.setPadding(d(12),d(12),d(12),d(12));
        card.setBackground(LibraryUi.bg(this,Color.WHITE,10,0));
        LinearLayout.LayoutParams cp=LibraryUi.lp(this,-1,-2);cp.bottomMargin=d(7);
        tabPane.addView(card,cp);
        LinearLayout names=col();card.addView(names,new LinearLayout.LayoutParams(0,-2,1));
        names.addView(txt(title,12,0xff576b81,false));
        names.addView(txt(value==null?tr("notPublished"):value,14,LibraryUi.INK,true));
    }
    private void applicationTab(){
        plainSection(tr("applications"),cable.uses(ar()),false);
        plainSection(tr("technical"),cable.title(ar()),false);
        plainSection(tr("dataDisclaimer"),cable.note(ar()),true);
    }
    private void standardTab(){
        plainSection(tr("standards"),cable.standards,false);
        plainSection(tr("sourceDetails"),cable.sourceName+"\n"+cable.revision,false);
        plainSection(tr("openSource"),cable.sourceUrl,false);
        TextView open=LibraryUi.button(this,tr("openSource"),true,this::openOfficialPage);
        tabPane.addView(open,LibraryUi.lp(this,-1,47));
    }
    private void plainSection(String title,String value,boolean warn){
        LinearLayout c=col();
        c.setPadding(d(12),d(12),d(12),d(12));
        c.setBackground(LibraryUi.bg(this,warn?0xfffff0e8:Color.WHITE,11,0));
        LinearLayout.LayoutParams lp=LibraryUi.lp(this,-1,-2);
        lp.bottomMargin=d(8);
        tabPane.addView(c,lp);
        c.addView(txt(title,14,warn?0xffac5a25:LibraryUi.INK,true));
        TextView description=txt(value,12,0xff415973,false);
        description.setPadding(0,d(6),0,0);
        c.addView(description);
    }
    private void conditions(){
        LinearLayout info=col();
        info.setPadding(d(11),d(12),d(11),d(11));
        info.setBackground(LibraryUi.bg(this,0xffe9f1f8,10,0));
        LinearLayout.LayoutParams bp=LibraryUi.lp(this,-1,-2);bp.topMargin=d(11);
        body.addView(info,bp);
        info.addView(txt("⚡ "+tr("voltage")+" : "+
            (cable.voltage==null?tr("notPublished"):cable.voltage),13,LibraryUi.INK,true));
        info.addView(txt("◉ "+tr("nominalDiameter")+" : "+
            (cable.diameter==null?tr("notPublished"):cable.diameter),12,LibraryUi.INK,false));
        info.addView(txt("◷ "+tr("ampacity")+" : "+
            (cable.current==null?tr("notPublished"):cable.current),12,LibraryUi.INK,false));
        info.addView(txt("🌡 "+tr("temperature")+" : "+cable.temperature,
            12,LibraryUi.INK,false));
        TextView notice=txt(cable.note(ar()),11,0xff8a4c20,false);
        LinearLayout.LayoutParams lp=LibraryUi.lp(this,-1,-2);lp.topMargin=d(5);
        info.addView(notice,lp);
    }
    private void sourceFooter(){
        LinearLayout foot=col();
        foot.setPadding(d(10),d(9),d(10),d(10));
        foot.setBackgroundColor(0xff061629);
        root.addView(foot,LibraryUi.lp(this,-1,-2));
        TextView url=txt(tr("sourceDetails")+"  ·  "+cable.sourceName,11,GOLD,true);
        foot.addView(url);
        TextView original=txt(cable.sourceUrl,10,0xff7fc0f2,false);
        original.setMaxLines(2);
        foot.addView(original);
        LibraryUi.tappable(original,cable.sourceName,this::openOfficialPage);
        LinearLayout buttons=row();
        LinearLayout.LayoutParams bp=LibraryUi.lp(this,-1,47);bp.topMargin=d(7);
        foot.addView(buttons,bp);
        TextView source=LibraryUi.button(this,"PDF ↗  "+tr("openOriginalPdf"),true,this::openManufacturerPdf);
        buttons.addView(source,new LinearLayout.LayoutParams(0,-1,1));
        TextView report=LibraryUi.button(this,"▤  "+tr("exportPdf"),false,this::saveReportPicker);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(0,-1,1);
        rp.leftMargin=d(6);rp.rightMargin=d(6);
        buttons.addView(report,rp);
    }
    private void openOfficialPage(){openUrl(cable.sourceUrl);}
    private void openManufacturerPdf(){openUrl(cable.pdfUrl);}
    private void openUrl(String url){
        if(url==null||!url.startsWith("https://")){
            new AlertDialog.Builder(this).setMessage(tr("sourceMissing"))
                .setPositiveButton("OK",null).show();return;
        }
        try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
        catch(Exception e){
            new AlertDialog.Builder(this).setMessage(tr("sourceMissing"))
                .setPositiveButton("OK",null).show();
        }
    }
    private void saveReportPicker(){
        Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE,"AB_Cable_"+cable.model.replaceAll("[^A-Za-z0-9_-]","_")+
            "_Technical_Report.pdf");
        try{startActivityForResult(intent,SAVE_PDF);}
        catch(Exception e){error(tr("pdfFailed"));}
    }
    @Override protected void onActivityResult(int code,int result,Intent data){
        super.onActivityResult(code,result,data);
        if(code!=SAVE_PDF||result!=RESULT_OK||data==null||data.getData()==null)return;
        try{
            CableReportPdf.export(this,cable,data.getData());
            new AlertDialog.Builder(this).setMessage(tr("pdfSaved"))
                .setPositiveButton("OK",null).show();
        }catch(IOException|RuntimeException e){
            error(tr("pdfFailed"));
        }
    }
    private void error(String error){
        new AlertDialog.Builder(this).setMessage(error).setPositiveButton("OK",null).show();
    }
    private void languageDialog(){
        String[] languages={"العربية","English","Français","Türkçe"};
        new AlertDialog.Builder(this).setTitle(tr("langTitle"))
            .setSingleChoiceItems(languages,I18n.index(this),(dlg,n)->{
                I18n.set(this,I18n.CODES[n]);dlg.dismiss();render();
            }).setNegativeButton(tr("cancel"),null).show();
    }
}
