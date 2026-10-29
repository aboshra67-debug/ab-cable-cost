package com.ab.cablecost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cable FAMILIES (names only, no specifications) that a manufacturer lists on its own
 * official product / catalogue page. Generated from pages read on 2026-10-29 by
 * tools/gen_cable_families.py; names are exactly as printed there. A family entry says
 * "this company lists this type of cable", never what its specifications are.
 * Companies not in this table simply have not been collected yet.
 * Pure Java (no Android dependency); checked by tools/VerifyLibraryData.java.
 */
public final class CableFamilies {
    public static final class Family {
        public final String companyId, name, url;
        Family(String companyId,String name,String url){this.companyId=companyId;this.name=name;this.url=url;}
    }
    /** status "read": families collected; "unreadable": the official page could not be read from here. */
    public static final class Source {
        public final String companyId, companyName, status, pageUrl, hosts, note, retrieved;
        Source(String companyId,String companyName,String status,String pageUrl,String hosts,String note,String retrieved){
            this.companyId=companyId;this.companyName=companyName;this.status=status;this.pageUrl=pageUrl;
            this.hosts=hosts;this.note=note;this.retrieved=retrieved;
        }
        public boolean read(){return "read".equals(status);}
    }
    private static final Map<String,Source> SOURCES=new LinkedHashMap<>();
    private static final List<Family> FAMILIES=new ArrayList<>();
    private static void src(String id,String name,String status,String page,String hosts,String note,String retrieved){
        SOURCES.put(id,new Source(id,name,status,page,hosts,note,retrieved));
    }
    private static void fam(String id,String name,String url){FAMILIES.add(new Family(id,name,url));}
    static {

        src("GLM-012","Belden","read","https://catalog.belden.com/","belden.com","","2026-10-29");
        fam("GLM-012","Electronic Wire & Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172590");
        fam("GLM-012","Networking & Data Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172593");
        fam("GLM-012","Audio Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172595");
        fam("GLM-012","Video Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172596");
        fam("GLM-012","Coax & Triax Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172597");
        fam("GLM-012","Multi-Media Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172598");
        fam("GLM-012","Fiber Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_160497");
        fam("GLM-012","Flexible Automation Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172600");
        fam("GLM-012","Tray Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172602");
        fam("GLM-012","VFD Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_172603");
        fam("GLM-012","Digital Electricity","https://catalog.belden.com/index.cfm?event=browse&c=category_201131");
        fam("GLM-012","Hybrid Fiber-Copper Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_341771");
        fam("GLM-012","Rolling Stock Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_757528");
        fam("GLM-012","Safety, Sound, & Security Cable","https://catalog.belden.com/index.cfm?event=browse&c=category_182866");
        src("GLM-031","Prysmian","read","https://ar.prysmian.com/en/toolsdownloads","prysmian.com","Argentina site (Spanish datasheet list); names as printed","2026-10-29");
        fam("GLM-031","BAJA TENSIÓN - PRYSUN™ - CABLE FOTOVOLTAICO","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRYAR_Prysun_v05.pdf");
        fam("GLM-031","BAJA TENSIÓN - INSTRUM@X® - CABLES PARA INSTRUMENTACIÓN ELECTRÓNICA","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2020_Instrumax_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES AFUMEX 1000 +®","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Afumex%201000%2B_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES AFUMEX® Green 750","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Afumex%20Green_V2final.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES RETENAX ENLACE","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Retenax-enlace_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES RETENAX PREENSAMBLADO","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Retenax%20preensamblado_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES RETENAX VALIO®","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Retenax%20Valio_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES SINTENAX VALIO®","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Sintenax%20Valio_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES TPR® ECOPLUS","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_TPR%20Ecoplus._v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES SUPERASTIC JET®","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Superastic%20Jet_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLES PRYSAL AISLADOS","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2020_Prysal%20Aislado_v2.pdf");
        fam("GLM-031","BAJA TENSIÓN - CABLE SOLDADURA - PORTALECTRODOS","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2020_Cable%20Soldadura_v2.pdf");
        fam("GLM-031","MEDIA TENSIÓN - CABLES ECO-AR","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2022_EcoAr_v2.pdf");
        fam("GLM-031","MEDIA TENSIÓN - CABLES RETENAX","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2021_Retenax%20MT_v2.pdf");
        fam("GLM-031","MEDIA TENSIÓN - CABLES PROTEGIDOS ECO COMPACT DUO","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2021_V02_EcoCompactDuo_v2.pdf");
        fam("GLM-031","ALTA TENSIÓN - RETENAX® AT","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2020_Retenax_AT_v2.pdf");
        fam("GLM-031","LÍNEAS AÉREAS - CABLES RETENAX ANTIHURTO","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_Retenax-antihurto_v2.pdf");
        fam("GLM-031","LÍNEAS AÉREAS - CONDUCTORES DESNUDOS - (PRYSAL / PRYSALAC)","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2021_CondDesnudosPrysal_Prysalac_v2.pdf");
        fam("GLM-031","LÍNEAS AÉREAS - CUERDAS DESNUDAS PRYS-CU","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2020_PrysCu_v2.pdf");
        fam("GLM-031","INSTALACIONES MÓVILES - PVN ECOPLUS","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_PVN%20Ecoplus_v2.pdf");
        fam("GLM-031","INSTALACIONES MÓVILES - VN 202","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/PRY2026_VN202_v2.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Cubierta Sencilla Aéreo Auto Soportado en Vanos de hasta 200m","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_006_Prysmian_A%C3%A9reo_AS%20KP_es%20.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Doble Cubierta Aéreo Auto Soportado en Vanos de hasta 200m","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_008_Prysmian_A%C3%A9reo_AR%20PKP_es.pdf");
        fam("GLM-031","Cable Óptico Aéreo tipo Figura 8","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_007_Prysmian_Areo_AS%20F8_es.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Tubo Único Aéreo Auto Soportado en Vanos de hasta 120m","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_009_Prysmian_A%C3%A9reo_ASU_es_rev.1.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Doble Cubierta Aéreo Auto Soportado en Vanos de hasta 1.000m","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_010_Prysmian_A%C3%A9reo%20Vanos%20Largos_AS%20PKP_es.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico para Ductos o Aéreo Devanado","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_001_Prysmian_Subterr%C3%A1neo_Ducto_0A1J_es.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Protección contra Roedores para Ductos o Aéreo Devanado","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_002_Prysmian_Subterr%C3%A1neo_Ducto_Anti-Roedores_IF2J_es.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Directamente Enterrado","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_003_Prysmian_Subterr%C3%A1neo_Enterrado_1N2J_es.pdf");
        fam("GLM-031","Cable Óptico Dieléctrico Protección contra Roedores Directamente Enterrado","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_004_Prysmian_Subterr%C3%A1neo_Enterrado_Anti-Roedores_NF2J_es.pdf");
        fam("GLM-031","Cable Óptico Armado Cubierta Sencilla para Ductos, Directamente enterrado o Aéreo Devanado","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_005_Prysmian_Subterr%C3%A1neo_Armado_1A1J_es.pdf");
        fam("GLM-031","DROP Óptico tipo Plano (Flat)","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_014_Prysmian_DROP_FD_es.pdf");
        fam("GLM-031","DROP Óptico Redondo","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_013_Prysmian_DROP_RD_es_rev.1.pdf");
        fam("GLM-031","DROP Óptico tipo Figura 8 con Mensajero Metálico","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_015_Prysmian_DROP_F8M%20.pdf");
        fam("GLM-031","DROP Óptico tipo Figura 8 con Mensajero Dieléctrico","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_016_Prysmian_DROP_F8D.pdf");
        fam("GLM-031","Cable Óptico Interior","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_011_Prysmian_Interior_LT_es.pdf");
        fam("GLM-031","Cable Óptico Interior Vertical","https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/TDS_SO_012_Prysmian_Interior%20Vertical_FT_es.pdf");
        src("GLM-011","Southwire","read","https://www.southwire.com/manuals-and-catalogs","southwire.com","Catalog names; all link to the official catalogs page","2026-10-29");
        fam("GLM-011","Low Voltage Catalog","https://www.southwire.com/manuals-and-catalogs");
        fam("GLM-011","BX Flex, and MC Catalog","https://www.southwire.com/manuals-and-catalogs");
        fam("GLM-011","High Voltage Underground Transmission Catalog","https://www.southwire.com/manuals-and-catalogs");
        src("GLM-041","Polycab","read","https://polycab.com/product-brochures/cable-brochures","polycab.com","Brochure names as printed","2026-10-29");
        fam("GLM-041","Uninyvin cable","https://cms.polycab.com/media/3dsjre3i/uninyvin-cable-compressed.pdf");
        fam("GLM-041","LT Cable","https://cms.polycab.com/media/kb1l12kq/lt-catalogue-ord_17720_v2.pdf");
        fam("GLM-041","HT Cable","https://cms.polycab.com/media/2bgh00kf/ht-cable-catalogue.pdf");
        fam("GLM-041","Fire Survival Cable Brochure","https://cms.polycab.com/media/k5lfvqld/fire-survival-cable-brochure-artwork_2.pdf");
        fam("GLM-041","EHV","https://cms.polycab.com/media/tdidggtl/polycab-ehv-brochure.pdf");
        fam("GLM-041","Rubber Cable Catalogue","https://cms.polycab.com/media/44sptk1w/final_rubber-cable_catalogue.pdf");
        fam("GLM-041","Instrumentation Cable Catalogue","https://cms.polycab.com/media/v0hf0z4m/instrumentation-cable_catalogue_innerpage_v9_ord-10763-final-c2c.pdf");
        fam("GLM-041","Housewire Catalogue","https://cms.polycab.com/media/rpzdby5w/housewire-catalogue.pdf");
        fam("GLM-041","Communication & Data Cable","https://cms.polycab.com/media/1nvdf0tf/approved_communicaton-cable-catlogue.pdf");
        fam("GLM-041","Polycab MVCC Condactor Catalogue","https://cms.polycab.com/media/bu3ixzub/polycab-mvcc-condactor-catalogue-pdf.pdf");
        fam("GLM-041","B2B All Products Catalogue","https://cms.polycab.com/media/2hlozcdj/b2b-all-products-catalogue.pdf");
        src("GLM-098","Riyadh Cables","read","https://riyadh-cables.com/products/","riyadh-cables.com","","2026-10-29");
        fam("GLM-098","Wires","https://riyadh-cables.com/product-category/wires/");
        fam("GLM-098","Low Voltage Cables (LV)","https://riyadh-cables.com/product-category/low-voltage-cables-lv/");
        fam("GLM-098","Medium Voltage Cables (MV)","https://riyadh-cables.com/product-category/medium-voltage-cables-mv/");
        fam("GLM-098","High & Extra High Voltage Cables (HV/EHV)","https://riyadh-cables.com/product-category/high-extra-high-voltage-cables-hv-ehv/");
        fam("GLM-098","Overhead Lines Conductors (OHL)","https://riyadh-cables.com/product-category/overhead-lines-conductors-ohl/");
        fam("GLM-098","Aerial Bundle Cables (ABC)","https://riyadh-cables.com/product-category/overhead-lines-conductors-ohl/aerial-bundle-cables-abc/");
        fam("GLM-098","High Temperature Low Sag Conductors (HTLS)","https://riyadh-cables.com/product-category/overhead-lines-conductors-ohl/high-temperature-low-sag-conductors-htls/");
        fam("GLM-098","Instrumentation & Control Cables","https://riyadh-cables.com/product-category/instrumentation-control-cables/");
        fam("GLM-098","Renewable Energy Cables","https://riyadh-cables.com/product-category/renewable-energy-cables/");
        fam("GLM-098","Fiber Optic & Telephone Cables","https://riyadh-cables.com/product-category/fiber-optic-telephone-cables/");
        src("GLM-095","Elsewedy Electric","read","https://www.elsewedyelectric.com/","elsewedyelectric.com","Cable-related families only (steel, polymers, insulators, metals and accessories omitted)","2026-10-29");
        fam("GLM-095","Energy Cables","https://www.elsewedyelectric.com/product/energy-cables-4");
        fam("GLM-095","Special Cables","https://www.elsewedyelectric.com/product/special-cables-5");
        fam("GLM-095","Marine Cables","https://www.elsewedyelectric.com/product/marine-cables-44");
        fam("GLM-095","Telecom Cabling Solutions","https://www.elsewedyelectric.com/product/telecom-cabling-solutions-34");
        fam("GLM-095","Automotive Wires & Cables","https://www.elsewedyelectric.com/product/automotive-wires-cables-33");
        fam("GLM-095","OHTL & OPGW","https://www.elsewedyelectric.com/product/ohtl-opgw-6");
        fam("GLM-095","Winding Wires","https://www.elsewedyelectric.com/product/winding-wires-8");
        src("GLM-021","LAPP","read","https://products.lappgroup.com/online-catalogue/","lappgroup.com","Cable families only (connectors, glands, conduit and marking omitted)","2026-10-29");
        fam("GLM-021","ÖLFLEX® (Power and control cables)","https://products.lappgroup.com/online-catalogue/power-and-control-cables.html");
        fam("GLM-021","UNITRONIC® (Data communication systems)","https://products.lappgroup.com/online-catalogue/data-communication-systems.html");
        fam("GLM-021","ETHERLINE® (Data communication systems for ETHERNET technology)","https://products.lappgroup.com/online-catalogue/data-communication-systems-for-ethernet-technology.html");
        fam("GLM-021","HITRONIC® (Optical transmission systems)","https://products.lappgroup.com/online-catalogue/optical-transmission-systems.html");
        src("GLM-022","HELUKABEL","read","https://www.helu.com/vn-en/products-solutions/","helu.com","Vietnam regional site of HELUKABEL; cable categories only","2026-10-29");
        fam("GLM-022","Control & Connection Cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/control-connection-cables/");
        fam("GLM-022","Data cables and computers","https://www.helu.com/vn-en/products-solutions/electrical-cables/data-cables-and-computers/");
        fam("GLM-022","Motor, Servo, Feedback Cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/motor-servo-feedback-cables/");
        fam("GLM-022","Industrial Ethernet cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/industrial-ethernet-cables/");
        fam("GLM-022","DC cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/dc-cables/");
        fam("GLM-022","HELUCOM® fiber optic cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/helucom-fiber-optic-cables/");
        fam("GLM-022","Single-core cable","https://www.helu.com/vn-en/products-solutions/electrical-cables/single-core-cable/");
        fam("GLM-022","Fire-resistant cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/fire-resistant-cables/");
        fam("GLM-022","Flame-retardant cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/flame-retardant-cables/");
        fam("GLM-022","Anti-interference cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/anti-interference-cables/");
        fam("GLM-022","Temperature-resistant cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/temperature-resistant-cables/");
        fam("GLM-022","Rubber cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/rubber-cables/");
        fam("GLM-022","AWG cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/awg-cables/");
        fam("GLM-022","RS485 cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/rs485-cable/");
        fam("GLM-022","CVV cables","https://www.helu.com/vn-en/products-solutions/electrical-cables/cvv-cables/");
        fam("GLM-022","Bus cables","https://www.helu.com/vn-en/products-solutions/cables-for-infrastructures-and-buildings/bus-cables/");
        fam("GLM-022","Cat5, Cat6, Cat7 LAN cables","https://www.helu.com/vn-en/products-solutions/cables-for-infrastructures-and-buildings/cat5-cat6-cat7-lan-cables/");
        fam("GLM-022","Installation, telephone, fire alarm cables","https://www.helu.com/vn-en/products-solutions/cables-for-infrastructures-and-buildings/installation-telephone-fire-alarm-cables/");
        fam("GLM-022","Energy and Power Cables","https://www.helu.com/vn-en/products-solutions/cables-for-infrastructures-and-buildings/energy-and-power-cables/");
        fam("GLM-022","Low-voltage power cable","https://www.helu.com/vn-en/products-solutions/cables-for-infrastructures-and-buildings/low-voltage-power-cable/");
        fam("GLM-022","Spiral cables","https://www.helu.com/vn-en/products-solutions/cables-for-infrastructures-and-buildings/spiral-cables/");
        fam("GLM-022","Reeling cables","https://www.helu.com/vn-en/products-solutions/cables-for-heavy-duty-industries/reeling-cables/");
        fam("GLM-022","Flat cables","https://www.helu.com/vn-en/products-solutions/cables-for-heavy-duty-industries/flat-cables/");
        fam("GLM-022","Audio cables","https://www.helu.com/vn-en/products-solutions/cables-for-event-and-media-technology/audio-cables/");
        fam("GLM-022","HDMI cables","https://www.helu.com/vn-en/products-solutions/cables-for-event-and-media-technology/hdmi-cables/");
        fam("GLM-022","Harness cable","https://www.helu.com/vn-en/products-solutions/harness-cable/");
        fam("GLM-022","Custom Cables","https://www.helu.com/vn-en/products-solutions/custom-cables/");
        fam("GLM-022","Drag Chain Compatible Cables","https://www.helu.com/vn-en/products-solutions/drag-chains/drag-chain-compatible-cables/");
        src("GLM-065","Nexans","unreadable","https://www.nexans.com/electrification-solutions/product-catalog/","nexans.com","The page lists market segments only, not cable families","2026-10-29");
        src("GLM-103","Ducab","unreadable","https://ducab.com/download-center/","ducab.com","The official page could not be fetched","2026-10-29");
    }
    public static List<Family> forCompany(String companyId){
        List<Family> out=new ArrayList<>();
        for(Family f:FAMILIES)if(f.companyId.equals(companyId))out.add(f);
        return Collections.unmodifiableList(out);
    }
    public static Source sourceFor(String companyId){return SOURCES.get(companyId);}
    public static Map<String,Source> sources(){return Collections.unmodifiableMap(SOURCES);}
    public static List<Family> all(){return Collections.unmodifiableList(FAMILIES);}
    private CableFamilies(){}
}
