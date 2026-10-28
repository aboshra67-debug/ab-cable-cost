package com.ab.cablecost;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Country-name translations for the 33 countries of the imported company
 * directory (stored in Arabic). Pure Java, no Android dependency, so it can be
 * unit-checked on any JDK (tools/VerifyLibraryData.java).
 *
 * Index order matches I18n.CODES: 0 = ar, 1 = en, 2 = fr, 3 = tr.
 * Unknown names are returned unchanged, never guessed.
 */
public final class CountryNames {
    private static final Map<String,String[]> BY_ARABIC=new HashMap<>();
    private static final Map<String,String> ENGLISH_TO_ARABIC=new HashMap<>();
    private static void add(String ar,String en,String fr,String tr){
        BY_ARABIC.put(ar,new String[]{ar,en,fr,tr});
        ENGLISH_TO_ARABIC.put(en.toLowerCase(Locale.ROOT),ar);
    }
    static {
        add("الصين","China","Chine","Çin");
        add("الولايات المتحدة","United States","États-Unis","Amerika Birleşik Devletleri");
        add("ألمانيا","Germany","Allemagne","Almanya");
        add("إيطاليا","Italy","Italie","İtalya");
        add("الهند","India","Inde","Hindistan");
        add("اليابان","Japan","Japon","Japonya");
        add("كوريا الجنوبية","South Korea","Corée du Sud","Güney Kore");
        add("فرنسا","France","France","Fransa");
        add("تركيا","Turkey","Turquie","Türkiye");
        add("المملكة المتحدة","United Kingdom","Royaume-Uni","Birleşik Krallık");
        add("إسبانيا","Spain","Espagne","İspanya");
        add("السعودية","Saudi Arabia","Arabie saoudite","Suudi Arabistan");
        add("بولندا","Poland","Pologne","Polonya");
        add("تايوان","Taiwan","Taïwan","Tayvan");
        add("مصر","Egypt","Égypte","Mısır");
        add("الإمارات","United Arab Emirates","Émirats arabes unis","Birleşik Arap Emirlikleri");
        add("جنوب أفريقيا","South Africa","Afrique du Sud","Güney Afrika");
        add("البرازيل","Brazil","Brésil","Brezilya");
        add("المكسيك","Mexico","Mexique","Meksika");
        add("أستراليا","Australia","Australie","Avustralya");
        add("كندا","Canada","Canada","Kanada");
        add("السويد","Sweden","Suède","İsveç");
        add("سويسرا","Switzerland","Suisse","İsviçre");
        add("البرتغال","Portugal","Portugal","Portekiz");
        add("ماليزيا","Malaysia","Malaisie","Malezya");
        add("إندونيسيا","Indonesia","Indonésie","Endonezya");
        add("فيتنام","Vietnam","Viêt Nam","Vietnam");
        add("فنلندا","Finland","Finlande","Finlandiya");
        add("الدنمارك","Denmark","Danemark","Danimarka");
        add("النمسا","Austria","Autriche","Avusturya");
        add("هولندا","Netherlands","Pays-Bas","Hollanda");
        add("بلجيكا","Belgium","Belgique","Belçika");
        add("تايلاند","Thailand","Thaïlande","Tayland");
    }

    /** Name in the requested UI language (0 ar, 1 en, 2 fr, 3 tr); unchanged if unknown. */
    public static String name(String arabic,int language){
        String[] v=BY_ARABIC.get(arabic);
        if(v==null||language<0||language>=v.length)return arabic;
        return v[language];
    }
    public static String english(String arabic){return name(arabic,1);}
    public static boolean known(String arabic){return BY_ARABIC.containsKey(arabic);}
    /** Arabic key for an English display name, or null when it is not one of the known countries. */
    public static String arabicFromEnglish(String english){
        if(english==null)return null;
        return ENGLISH_TO_ARABIC.get(english.toLowerCase(Locale.ROOT));
    }
    /** Lower-case text containing the country in all four languages, for search indexes. */
    public static String searchText(String arabic){
        String[] v=BY_ARABIC.get(arabic);
        if(v==null)return arabic.toLowerCase(Locale.ROOT);
        return (v[0]+" "+v[1]+" "+v[2]+" "+v[3]).toLowerCase(Locale.ROOT);
    }
    public static int count(){return BY_ARABIC.size();}
    private CountryNames(){}
}
