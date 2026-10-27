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

        add("libraryTitle","المكتبة العالمية للكابلات","Global Cable Library","Bibliothèque mondiale des câbles","Küresel Kablo Kütüphanesi");
        add("libraryHeadline","مرجع الكابلات من مصادر الشركات","Manufacturer-sourced cable reference","Références de câbles des fabricants","Üretici kaynaklı kablo bilgileri");
        add("searchLibrary","ابحث بالشركة أو الموديل أو المقاس...","Search by maker, model or size...","Rechercher fabricant, modèle ou section...","Üretici, model veya kesit ara...");
        add("manufacturer","الشركة","Manufacturer","Fabricant","Üretici");
        add("country","الدولة","Country","Pays","Ülke");
        add("cableType","نوع الكابل","Cable type","Type de câble","Kablo tipi");
        add("voltage","الجهد","Voltage","Tension","Gerilim");
        add("section","المقطع","Section","Section","Kesit");
        add("everyCable","جميع الأنواع","All cables","Tous les câbles","Tüm kablolar");
        add("powerCables","قوى","Power","Puissance","Enerji");
        add("controlCables","كنترول","Control","Contrôle","Kontrol");
        add("fireCables","إنذار حريق","Fire alarm","Alarme incendie","Yangın alarmı");
        add("dataCables","اتصالات","Data & Telecom","Données et télécom","Veri ve Telekom");
        add("coaxCables","كاميرات / محوري","Coax / CCTV","Coaxial / CCTV","Koaksiyel / CCTV");
        add("searchResults","نتائج البحث","Search results","Résultats de recherche","Arama sonuçları");
        add("relevance","الأكثر صلة","Relevance","Pertinence","İlgililik");
        add("approvedSource","مصدر رسمي","Official source","Source officielle","Resmî kaynak");
        add("viewDetails","عرض التفاصيل","View details","Voir détails","Detayları gör");
        add("sourcePdf","الداتا شيت الأصلية","Original data sheet","Fiche technique d’origine","Orijinal veri sayfası");
        add("noMatching","مفيش كابلات موثقة مطابقة للبحث.","No verified matching cables.","Aucun câble vérifié correspondant.","Eşleşen doğrulanmış kablo yok.");
        add("verifiedOnly","نعرض بيانات موثقة من الشركات فقط. القوالب غير المعتمدة مش موجودة في المكتبة.","Manufacturer-sourced entries only. Generic templates are excluded.","Données officielles uniquement. Modèles génériques exclus.","Yalnızca üretici verileri. Genel şablonlar hariç.");
        add("back","رجوع","Back","Retour","Geri");
        add("favourite","المفضلة","Favorites","Favoris","Favoriler");
        add("history","السجل","History","Historique","Geçmiş");
        add("list","القائمة","List","Liste","Liste");
        add("diagram","مقطع هندسي","Cross-section","Coupe technique","Kesit çizimi");
        add("threeD","عرض 3D","3D view","Vue 3D","3D görünüm");
        add("technical","المواصفات","Specifications","Caractéristiques","Teknik özellikler");
        add("applications","الاستخدامات","Applications","Applications","Kullanım alanları");
        add("standards","المعايير","Standards","Normes","Standartlar");
        add("conductor","الموصل","Conductor","Conducteur","İletken");
        add("insulation","العزل","Insulation","Isolant","Yalıtım");
        add("shield","الشيلد","Shield","Blindage","Ekran");
        add("jacket","الغلاف","Outer jacket","Gaine","Dış kılıf");
        add("nominalDiameter","القطر الخارجي","Overall diameter","Diamètre extérieur","Dış çap");
        add("ampacity","تحمل التيار","Max. current","Courant maximal","Azami akım");
        add("resistanceValue","المقاومة","Resistance","Résistance","Direnç");
        add("temperature","درجة الحرارة","Temperature","Température","Sıcaklık");
        add("notPublished","غير منشور في المصدر","Not published in source","Non publié par la source","Kaynakta yayınlanmamış");
        add("technicalDiagramNote","رسم توضيحي للطبقات، مش نموذج أبعاد معتمد للتصنيع.","Schematic layers; not manufacturing-accurate geometry.","Coupe illustrative, non destinée à la fabrication.","Katman şeması; üretim ölçüsü değildir.");
        add("swipe3D","اسحب على الرسم لتدوير التمثيل التوضيحي.","Swipe to rotate the illustrative model.","Glisser pour faire pivoter le schéma.","Görünümü döndürmek için kaydır.");
        add("openOriginalPdf","فتح PDF الأصلي","Open original PDF","Ouvrir le PDF original","Orijinal PDF'yi aç");
        add("exportPdf","تصدير تقرير PDF","Export PDF report","Exporter rapport PDF","PDF raporu dışa aktar");
        add("sourceDetails","المصدر وتاريخ المراجعة","Source and revision","Source et révision","Kaynak ve sürüm");
        add("dataDisclaimer","القيم الكهربائية المنشورة مش بديل لتصميم الحمل والحماية حسب ظروف التركيب.","Published ratings do not replace site-specific electrical design.","Les valeurs publiées ne remplacent pas les calculs de projet.","Yayımlanmış değerler sahaya özel tasarımın yerine geçmez.");
        add("pdfSaved","تم حفظ التقرير الفني PDF.","Technical PDF report saved.","Rapport PDF enregistré.","Teknik PDF kaydedildi.");
        add("pdfFailed","تعذر إنشاء ملف PDF.","Could not create PDF.","Échec de la création du PDF.","PDF oluşturulamadı.");
        add("openSource","افتح موقع الشركة","Open manufacturer source","Ouvrir le fabricant","Üretici kaynağını aç");
        add("indoor","حسب النشرة الفنية","Per official datasheet","Selon fiche technique","Teknik veri sayfasına göre");
        add("savedFavorites","اتحفظ في المفضلة","Saved to favorites","Ajouté aux favoris","Favorilere eklendi");
        add("sourceMissing","رابط المصدر مش متاح.","Source link unavailable.","Lien source indisponible.","Kaynak bağlantısı yok.");
        add("verifiedCount","موديلات موثقة حاليًا","Verified models so far","Modèles vérifiés","Şimdiye kadar doğrulanan modeller");

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
