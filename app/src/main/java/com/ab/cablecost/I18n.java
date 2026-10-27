package com.ab.cablecost;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

/** Persistent UI language selection, starting with the new engineering home.
 * Keys are semantic; modules use the same translator as each one is localized.
 */
public final class I18n {
    private static final String PREFERENCES="ab_cable_cost_ui";
    private static final String LANGUAGE="language";
    public static final String[] CODES={"ar","en","fr","tr"};
    private static final Map<String,String[]> TEXT=new HashMap<>();
    static {
        add("tagline","المرجع الهندسي للكابلات والكهرباء","Cable & Electrical Engineering Reference","Référence technique des câbles et de l’électricité","Kablo ve Elektrik Mühendisliği Rehberi");
        add("search","ابحث عن كابل أو مواصفة ...","Search for a cable or specification ...","Rechercher un câble ou une norme ...","Kablo veya teknik özellik ara ...");
        add("lib","مكتبة الكابلات","Cable Library","Bibliothèque des câbles","Kablo Kütüphanesi");
        add("libDesc","مواصفات وأنواع الكابلات من المراجع المتاحة","Cable types and available reference specifications","Types de câbles et fiches techniques disponibles","Kablo türleri ve mevcut teknik veriler");
        add("sel","اختيار الكابل","Cable Selection","Sélection de câble","Kablo Seçimi");
        add("selDesc","اختار النوع والمقاس","Choose type and size","Choisir le type et la section","Tür ve kesit seçimi");
        add("calc","الحسابات الكهربائية","Electrical Calculations","Calculs électriques","Elektrik Hesapları");
        add("calcDesc","الأمبير والفولت والقدرة","Current, voltage and power","Courant, tension et puissance","Akım, gerilim ve güç");
        add("res","مقاومة الكابلات والنحاس","Cable Resistance","Résistance des câbles","Kablo Direnci");
        add("resDesc","المقاومة والطول والحرارة","Resistance, length and temperature","Résistance, longueur et température","Direnç, uzunluk ve sıcaklık");
        add("protect","الحماية والتحمل","Cable Protection","Protection des câbles","Kablo Koruma");
        add("protectDesc","حماية الكابلات وقدرة التحمل","Protection and current-carrying limits","Protection et intensité admissible","Koruma ve akım taşıma sınırları");
        add("manufacture","تكلفة التصنيع","Manufacturing Cost","Coût de fabrication","Üretim Maliyeti");
        add("manufactureDesc","المواد والأوزان والتكلفة","Materials, weights and costs","Matières, poids et coûts","Malzeme, ağırlık ve maliyet");
        add("install","التركيب والاختبارات","Installation & Testing","Installation et essais","Kurulum ve Testler");
        add("installDesc","التركيب والفحص الفني","Installation and inspection","Installation et contrôle","Kurulum ve kontrol");
        add("home","الرئيسية","Home","Accueil","Ana Sayfa");
        add("catalog","الكتالوج","Catalogue","Catalogue","Katalog");
        add("tools","الأدوات","Tools","Outils","Araçlar");
        add("settings","الإعدادات","Settings","Paramètres","Ayarlar");
        add("language","اللغة","Language","Langue","Dil");
        add("langTitle","اختار لغة البرنامج","Choose app language","Choisissez la langue","Uygulama dilini seçin");
        add("cancel","إلغاء","Cancel","Annuler","İptal");
        add("soon","القسم ده هنبنيه في المرحلة الجاية، زرار زرار. مفيش حسابات فنية غير مُختبرة.",
            "This module will be developed and tested next, one feature at a time.",
            "Ce module sera développé et testé ensuite, étape par étape.",
            "Bu modül sonraki aşamada tek tek geliştirilecek ve test edilecek.");
        add("modules","الأقسام الهندسية","Engineering modules","Modules techniques","Mühendislik modülleri");
        add("engineering","الأدوات والحسابات الكهربائية","Electrical engineering tools","Outils d’ingénierie électrique","Elektrik mühendisliği araçları");
        add("catalogHint","مكتبة بمصادر معلنة؛ القوالب غير المعتمدة مميزة بوضوح",
            "Source-linked catalogue; unverified templates are marked clearly",
            "Catalogue sourcé ; modèles non vérifiés signalés",
            "Kaynaklı katalog; doğrulanmamış şablonlar işaretlidir");
        add("toolsHint","الحاسبة القديمة محفوظة بكل معادلاتها","Existing weight calculator remains available",
            "L’ancien calculateur de poids reste disponible","Eski ağırlık hesaplayıcı kullanılabilir");
        add("weights","حاسبة الأوزان","Weight Calculator","Calculateur de poids","Ağırlık Hesaplayıcı");
        add("prices","الأسعار المركزية","Material Prices","Prix des matières","Malzeme Fiyatları");
        add("factory","إعدادات المصنع","Factory Settings","Paramètres de l’usine","Fabrika Ayarları");
        add("ready","متاح","Available","Disponible","Kullanılabilir");
        add("pending","قيد التطوير","Coming next","À venir","Yakında");
        add("results","نتائج البحث","Search results","Résultats de recherche","Arama Sonuçları");
        add("noResults","مفيش مقاسات مطابقة. جرّب كلمة أو مقاس تاني.","No matching cable sizes.","Aucun câble correspondant.","Eşleşen kablo bulunamadı.");
        add("searchTip","البحث في المقاسات المتاحة داخل الكتالوج","Searches available catalog specifications",
            "Recherche dans le catalogue disponible","Mevcut katalogda arama yapar");
        add("choose","اختار","Select","Choisir","Seç");
        add("all","الكل","All","Tout","Tümü");
    }
    private static void add(String k,String ar,String en,String fr,String tr){
        TEXT.put(k,new String[]{ar,en,fr,tr});
    }
    public static String language(Context c){
        return c.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE).getString(LANGUAGE,"ar");
    }
    public static int index(Context c){
        String current=language(c);
        for(int i=0;i<CODES.length;i++)if(CODES[i].equals(current))return i;
        return 0;
    }
    public static String t(Context c,String key){
        String[] vals=TEXT.get(key);
        return vals==null?key:vals[index(c)];
    }
    public static void set(Context c,String language){
        for(String supported:CODES)if(supported.equals(language)){
            c.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE)
                .edit().putString(LANGUAGE,language).apply();
            return;
        }
    }
    private I18n(){}
}
