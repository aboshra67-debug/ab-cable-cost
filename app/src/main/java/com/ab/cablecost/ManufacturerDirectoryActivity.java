package com.ab.cablecost;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Review-only research index; separate from manufacturer-certified cable models.
 * 150 pending company candidates are displayed with clear "not verified" status.
 * Nine brands/related companies have their OWN table and are NOT added to the 150.
 */
public final class ManufacturerDirectoryActivity extends Activity {
    private static final int NAVY=LibraryUi.NAVY, GOLD=LibraryUi.GOLD, TEXT=LibraryUi.WHITE;
    private final List<ManufacturerCandidates.Record> companies=new ArrayList<>();
    private final List<BrandEntry> brands=new ArrayList<>();
    private final List<Object> visible=new ArrayList<>();
    private LinearLayout root,categoryBar,filterBar;
    private EditText search;
    private ListView list;
    private TextView count;
    private DirectoryAdapter adapter;
    private String country="*",verification="*";
    private boolean showBrands=false;
    private int activeLanguage=-1;
    private String lastQuery="";

    private static final class BrandEntry {
        final String name,country,role,url,note;
        BrandEntry(String[] v){name=v[0];country=v[1];role=v[2];url=v[3];note=v[4];}
        String searchIndex(){return (name+" "+country+" "+role+" "+note).toLowerCase(Locale.ROOT);}
    }
    private int d(int dp){return LibraryUi.dp(this,dp);}
    private String tr(String key){return I18n.t(this,key);}
    private boolean rtl(){return I18n.index(this)==0;}
    private LinearLayout col(){return LibraryUi.col(this);}
    private LinearLayout row(){return LibraryUi.row(this);}
    private LinearLayout.LayoutParams lp(int w,int h){return LibraryUi.lp(this,w,h);}
    private TextView text(String value,int size,int color,boolean bold){
        return LibraryUi.text(this,value,size,color,bold);
    }
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        String initialQuery=getIntent().getStringExtra("query");
        if(initialQuery!=null)lastQuery=initialQuery.trim();
        String initialCountry=getIntent().getStringExtra("country");
        if(initialCountry!=null&&!initialCountry.trim().isEmpty())country=initialCountry.trim();
        try{
            companies.addAll(ManufacturerCandidates.all(this));
            readBrands();
        }catch(Exception e){
            new AlertDialog.Builder(this).setTitle("Import validation failed")
                .setMessage("The research directory could not pass its integrity check. Verified cable entries were not modified.\n"+
                    e.getMessage()).setPositiveButton("OK",(dlg,i)->finish()).show();
            return;
        }
        render();
        // A pending manufacturer selected in the global library's Company
        // filter should open its actual review card, not an empty cable list.
        String focusId=getIntent().getStringExtra("focus_id");
        if(focusId!=null){
            for(ManufacturerCandidates.Record entry:companies){
                if(entry.id.equals(focusId)){
                    final ManufacturerCandidates.Record match=entry;
                    root.post(()->openCompany(match));
                    break;
                }
            }
        }
    }
    @Override protected void onResume(){
        super.onResume();
        if(root!=null&&activeLanguage!=I18n.index(this))render();
    }
    private void readBrands() throws Exception {
        try(BufferedReader br=new BufferedReader(new InputStreamReader(
            getAssets().open("global_150_separate_brands.csv"),StandardCharsets.UTF_8))) {
            String line=br.readLine();int n=0;
            while((line=br.readLine())!=null) {
                if(line.trim().isEmpty())continue;
                String[] cells=line.split(",",5);
                if(cells.length!=5)throw new IllegalStateException("Broken brand row");
                brands.add(new BrandEntry(cells));n++;
            }
            if(n!=9)throw new IllegalStateException("Expected 9 separately classified brands");
        }
    }
    private void render(){
        activeLanguage=I18n.index(this);
        root=col();root.setBackgroundColor(NAVY);setContentView(root);
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
        TextView caution=text(tr("directoryWarning"),12,0xfff5dec0,false);
        caution.setPadding(d(15),d(7),d(15),d(7));
        caution.setBackgroundColor(0xff3d3323);
        root.addView(caution,lp(-1,-2));
        modeTabs();
        searchControls();
        filterControls();
        count=text("",12,LibraryUi.MUTED,false);
        count.setPadding(d(13),d(5),d(13),d(5));
        root.addView(count,lp(-1,-2));
        list=new ListView(this);
        list.setBackgroundColor(NAVY);
        list.setDividerHeight(d(1));
        list.setDivider(new android.graphics.drawable.ColorDrawable(LibraryUi.BORDER));
        list.setCacheColorHint(Color.TRANSPARENT);
        list.setVerticalScrollBarEnabled(true);
        adapter=new DirectoryAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent,view,position,id)->{
            Object entry=visible.get(position);
            if(entry instanceof ManufacturerCandidates.Record)openCompany((ManufacturerCandidates.Record)entry);
            else openBrand((BrandEntry)entry);
        });
        root.addView(list,new LinearLayout.LayoutParams(-1,0,1));
        search.setText(lastQuery);
        applyFilter();
    }
    private void header(){
        LinearLayout header=row();
        header.setPadding(d(12),d(9),d(11),d(9));
        root.addView(header,lp(-1,66));
        ImageView logo=new ImageView(this);
        logo.setImageResource(R.drawable.ab_brand_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        header.addView(logo,lp(57,52));
        LinearLayout title=col();
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);
        p.leftMargin=d(9);p.rightMargin=d(9);
        header.addView(title,p);
        title.addView(text(tr("directoryTitle"),18,GOLD,true));
        title.addView(text("150 / 33  ·  Review candidates",11,LibraryUi.MUTED,false));
        TextView back=LibraryUi.button(this,tr("back"),false,this::finish);
        header.addView(back,lp(70,44));
    }
    private void modeTabs(){
        categoryBar=row();
        categoryBar.setPadding(d(12),d(8),d(12),d(4));
        root.addView(categoryBar,lp(-1,57));
        redrawModeTabs();
    }
    private void redrawModeTabs(){
        categoryBar.removeAllViews();
        TextView candidates=LibraryUi.button(this,tr("factoryCandidates")+" ("+companies.size()+")",!showBrands,
            ()->{showBrands=false;country="*";verification="*";redrawModeTabs();redrawFilterControls();applyFilter();});
        categoryBar.addView(candidates,new LinearLayout.LayoutParams(0,-1,1));
        TextView brand=LibraryUi.button(this,tr("separateBrands")+" ("+brands.size()+")",showBrands,
            ()->{showBrands=true;country="*";verification="*";redrawModeTabs();redrawFilterControls();applyFilter();});
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);
        p.leftMargin=d(8);p.rightMargin=d(8);
        categoryBar.addView(brand,p);
    }
    private void searchControls(){
        LinearLayout wrap=row();
        wrap.setPadding(d(9),d(5),d(9),d(5));
        wrap.setBackground(LibraryUi.bg(this,Color.WHITE,12,0));
        LinearLayout.LayoutParams sp=lp(-1,54);
        sp.leftMargin=d(12);sp.rightMargin=d(12);sp.bottomMargin=d(6);
        root.addView(wrap,sp);
        TextView icon=LibraryUi.centered(this,"⌕",25,LibraryUi.INK,true);
        wrap.addView(icon,lp(36,43));
        search=new EditText(this);
        search.setSingleLine(true);
        search.setBackgroundColor(Color.TRANSPARENT);
        search.setHint(tr("directorySearch"));
        search.setHintTextColor(0xff7890a4);
        search.setTextColor(LibraryUi.INK);
        search.setTextSize(14);
        search.setGravity((rtl()?Gravity.RIGHT:Gravity.LEFT)|Gravity.CENTER_VERTICAL);
        wrap.addView(search,new LinearLayout.LayoutParams(0,-1,1));
        search.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int ct,int after){}
            public void onTextChanged(CharSequence s,int st,int before,int count){
                lastQuery=s.toString();applyFilter();
            }
            public void afterTextChanged(Editable text){}
        });
    }
    private void filterControls(){
        filterBar=row();
        filterBar.setPadding(d(12),d(2),d(12),d(3));
        root.addView(filterBar,lp(-1,51));
        redrawFilterControls();
    }
    private void redrawFilterControls(){
        filterBar.removeAllViews();
        TextView countries=LibraryUi.chip(this,
            tr("country")+" · "+(country.equals("*")?tr("all"):
                CountryNames.name(country,I18n.index(this)))+" ▾",
            !country.equals("*"),this::pickCountry);
        filterBar.addView(countries,new LinearLayout.LayoutParams(0,-1,1));
        if(!showBrands) {
            TextView status=LibraryUi.chip(this,
                tr("reviewFilter")+" ▾",!verification.equals("*"),this::pickStatus);
            LinearLayout.LayoutParams vp=new LinearLayout.LayoutParams(0,-1,1);
            vp.leftMargin=d(8);vp.rightMargin=d(8);
            filterBar.addView(status,vp);
        }
    }
    private void pickCountry(){
        Set<String> countries=new HashSet<>();
        if(showBrands){for(BrandEntry b:brands)countries.add(b.country);}
        else for(ManufacturerCandidates.Record c:companies)countries.add(c.countryAr);
        final int language=I18n.index(this);
        final List<String> keys=new ArrayList<>(countries);
        Collections.sort(keys,(x,y)->CountryNames.name(x,language).compareToIgnoreCase(CountryNames.name(y,language)));
        final List<String> labels=new ArrayList<>();
        for(String key:keys)labels.add(CountryNames.name(key,language));
        labels.add(0,tr("all"));
        new AlertDialog.Builder(this).setTitle(tr("country"))
            .setItems(labels.toArray(new String[0]),(d,i)->{
                country=i==0?"*":keys.get(i-1);
                redrawFilterControls();applyFilter();
            }).setNegativeButton(tr("cancel"),null).show();
    }
    private void pickStatus(){
        String[] keys={"*","catalog_index_found_review_needed","products_page_only","catalog_unverified"};
        String[] labels={tr("all"),tr("catalogIndexCandidate"),tr("productsOnlyCandidate"),
            tr("unverifiedCatalog")};
        new AlertDialog.Builder(this).setTitle(tr("reviewFilter"))
            .setItems(labels,(dialog,which)->{
                verification=keys[which];
                redrawFilterControls();applyFilter();
            }).setNegativeButton(tr("cancel"),null).show();
    }
    private void applyFilter(){
        if(adapter==null)return;
        visible.clear();
        String q=lastQuery.trim().toLowerCase(Locale.ROOT);
        if(showBrands) {
            for(BrandEntry b:brands)
                if((country.equals("*")||b.country.equals(country))&&
                   (q.isEmpty()||b.searchIndex().contains(q)))visible.add(b);
        }else{
            for(ManufacturerCandidates.Record m:companies)
                if((country.equals("*")||m.countryAr.equals(country))&&
                   (verification.equals("*")||m.verificationStatus.equals(verification))&&
                   (q.isEmpty()||m.searchKey().contains(q)))visible.add(m);
        }
        count.setText(visible.size()+" / "+(showBrands?brands.size():companies.size())+
            "  ·  "+(showBrands?tr("brandsOnly"):tr("pendingReviewOnly")));
        adapter.notifyDataSetChanged();
    }
    private String reviewLabel(ManufacturerCandidates.Record r) {
        if(r.verificationStatus.equals("catalog_index_found_review_needed"))
            return tr("catalogIndexCandidate");
        if(r.verificationStatus.equals("products_page_only"))
            return tr("productsOnlyCandidate");
        return tr("unverifiedCatalog");
    }
    private void openCompany(ManufacturerCandidates.Record r){
        String message=tr("pendingReviewOnly")+"\n\n"+r.id+" • "+CountryNames.name(r.countryAr,I18n.index(this))+
            "\n"+tr("reviewFilter")+": "+reviewLabel(r)+
            "\n"+tr("researchDate")+": "+ManufacturerCandidates.Record.RESEARCH_DATE+
            "\n"+tr("websiteCandidate")+": "+
            (r.hasWebsiteCandidate()?r.websiteCandidate:tr("notPublished"))+
            "\n"+tr("catalogCandidate")+": "+
            (r.hasCatalogCandidate()?r.catalogCandidate:tr("notPublished"))+
            "\n"+tr("researchReference")+": "+
            (ManufacturerCandidates.isHttps(r.referenceUrl)?r.referenceUrl:tr("notPublished"))+
            (r.notes.isEmpty()?"":"\n"+r.notes)+
            "\n\n"+tr("cableFamilies")+": "+familiesSummary(r.id);
        List<String> options=new ArrayList<>();
        List<String> urls=new ArrayList<>();
        if(r.hasWebsiteCandidate()) {
            options.add(tr("websiteCandidate"));urls.add(r.websiteCandidate);
        }
        if(r.hasCatalogCandidate()) {
            options.add(tr("catalogCandidate"));urls.add(r.catalogCandidate);
        }
        if(ManufacturerCandidates.isHttps(r.referenceUrl)) {
            options.add(tr("researchReference"));urls.add(r.referenceUrl);
        }
        new AlertDialog.Builder(this).setTitle(r.name)
            .setMessage(message)
            .setPositiveButton(tr("openLinks"),(dlg,which)->{
                if(urls.isEmpty())return;
                new AlertDialog.Builder(this).setTitle(tr("unverifiedLinkCaution"))
                    .setItems(options.toArray(new String[0]),(x,index)->openUrl(urls.get(index)))
                    .setNegativeButton(tr("cancel"),null).show();
            })
            .setNeutralButton(tr("cableFamilies"),(dlg,which)->openFamilies(r))
            .setNegativeButton(tr("back"),null).show();
    }
    private String familiesSummary(String companyId){
        CableFamilies.Source src=CableFamilies.sourceFor(companyId);
        if(src==null)return tr("familiesNotCollected");
        if(!src.read())return tr("familiesUnreadable");
        return String.valueOf(CableFamilies.forCompany(companyId).size());
    }
    /** Names of cable families the company lists on its own official page; no specifications. */
    private void openFamilies(ManufacturerCandidates.Record r){
        CableFamilies.Source src=CableFamilies.sourceFor(r.id);
        if(src==null||!src.read()){
            String why=src==null?tr("familiesNotCollected"):tr("familiesUnreadable");
            AlertDialog.Builder b=new AlertDialog.Builder(this).setTitle(r.name).setMessage(why)
                .setNegativeButton(tr("back"),null);
            if(src!=null&&ManufacturerCandidates.isHttps(src.pageUrl))
                b.setPositiveButton(tr("openLinks"),(dlg,which)->openUrl(src.pageUrl));
            b.show();
            return;
        }
        List<CableFamilies.Family> fams=CableFamilies.forCompany(r.id);
        String[] names=new String[fams.size()];
        for(int i=0;i<names.length;i++)names[i]=fams.get(i).name;
        new AlertDialog.Builder(this)
            .setTitle(r.name+" ("+names.length+") — "+tr("familiesTitle"))
            .setItems(names,(dlg,index)->openUrl(fams.get(index).url))
            .setPositiveButton(tr("familiesOfficialPage"),(dlg,which)->openUrl(src.pageUrl))
            .setNegativeButton(tr("back"),null).show();
    }
    private void openBrand(BrandEntry b){
        String message=b.country+"\n"+b.role+"\n"+b.note+"\n\n"+
            tr("brandsOnly")+"\n"+b.url;
        new AlertDialog.Builder(this).setTitle(b.name).setMessage(message)
            .setPositiveButton(tr("openLinks"),(dlg,which)->openUrl(b.url))
            .setNegativeButton(tr("back"),null).show();
    }
    private void openUrl(String url){
        if(!ManufacturerCandidates.isHttps(url))return;
        try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}
        catch(Exception e){
            new AlertDialog.Builder(this).setMessage(tr("sourceMissing"))
                .setPositiveButton("OK",null).show();
        }
    }
    private final class DirectoryAdapter extends BaseAdapter {
        public int getCount(){return visible.size();}
        public Object getItem(int p){return visible.get(p);}
        public long getItemId(int p){return p;}
        public View getView(int position,View reuse,android.view.ViewGroup parent){
            LinearLayout card;
            TextView main,sub,status;
            if(reuse==null){
                card=col();
                card.setPadding(d(15),d(9),d(12),d(9));
                card.setBackgroundColor(NAVY);
                main=text("",16,TEXT,true);
                main.setTag("candidate_name");
                sub=text("",12,LibraryUi.MUTED,false);
                status=text("",11,0xfff1be74,false);
                card.addView(main,lp(-1,-2));
                card.addView(sub,lp(-1,-2));
                card.addView(status,lp(-1,-2));
            }else{
                card=(LinearLayout)reuse;
                main=(TextView)card.getChildAt(0);
                sub=(TextView)card.getChildAt(1);
                status=(TextView)card.getChildAt(2);
            }
            Object obj=visible.get(position);
            if(obj instanceof ManufacturerCandidates.Record) {
                ManufacturerCandidates.Record r=(ManufacturerCandidates.Record)obj;
                main.setText(r.name);
                sub.setText(r.id+"  ·  "+CountryNames.name(r.countryAr,I18n.index(ManufacturerDirectoryActivity.this)));
                status.setText("◷ "+tr("pendingReviewOnly")+"  ·  "+reviewLabel(r));
            }else{
                BrandEntry b=(BrandEntry)obj;
                main.setText(b.name);
                sub.setText(b.country);
                status.setText(tr("brandsOnly")+" • "+b.role);
            }
            return card;
        }
    }
}
