# Generates CableFamilies.java from the lists read on 2026-10-29. Run with python3 -I.
import sys
OUT=sys.argv[1]
RET="2026-10-29"
PB="https://ar.prysmian.com/sites/ar.prysmian.com/files/media/documents/"
prys=[
("BAJA TENSIÓN - PRYSUN™ - CABLE FOTOVOLTAICO","PRYAR_Prysun_v05.pdf"),
("BAJA TENSIÓN - INSTRUM@X® - CABLES PARA INSTRUMENTACIÓN ELECTRÓNICA","PRY2020_Instrumax_v2.pdf"),
("BAJA TENSIÓN - CABLES AFUMEX 1000 +®","PRY2026_Afumex%201000%2B_v2.pdf"),
("BAJA TENSIÓN - CABLES AFUMEX® Green 750","PRY2026_Afumex%20Green_V2final.pdf"),
("BAJA TENSIÓN - CABLES RETENAX ENLACE","PRY2026_Retenax-enlace_v2.pdf"),
("BAJA TENSIÓN - CABLES RETENAX PREENSAMBLADO","PRY2026_Retenax%20preensamblado_v2.pdf"),
("BAJA TENSIÓN - CABLES RETENAX VALIO®","PRY2026_Retenax%20Valio_v2.pdf"),
("BAJA TENSIÓN - CABLES SINTENAX VALIO®","PRY2026_Sintenax%20Valio_v2.pdf"),
("BAJA TENSIÓN - CABLES TPR® ECOPLUS","PRY2026_TPR%20Ecoplus._v2.pdf"),
("BAJA TENSIÓN - CABLES SUPERASTIC JET®","PRY2026_Superastic%20Jet_v2.pdf"),
("BAJA TENSIÓN - CABLES PRYSAL AISLADOS","PRY2020_Prysal%20Aislado_v2.pdf"),
("BAJA TENSIÓN - CABLE SOLDADURA - PORTALECTRODOS","PRY2020_Cable%20Soldadura_v2.pdf"),
("MEDIA TENSIÓN - CABLES ECO-AR","PRY2022_EcoAr_v2.pdf"),
("MEDIA TENSIÓN - CABLES RETENAX","PRY2021_Retenax%20MT_v2.pdf"),
("MEDIA TENSIÓN - CABLES PROTEGIDOS ECO COMPACT DUO","PRY2021_V02_EcoCompactDuo_v2.pdf"),
("ALTA TENSIÓN - RETENAX® AT","PRY2020_Retenax_AT_v2.pdf"),
("LÍNEAS AÉREAS - CABLES RETENAX ANTIHURTO","PRY2026_Retenax-antihurto_v2.pdf"),
("LÍNEAS AÉREAS - CONDUCTORES DESNUDOS - (PRYSAL / PRYSALAC)","PRY2021_CondDesnudosPrysal_Prysalac_v2.pdf"),
("LÍNEAS AÉREAS - CUERDAS DESNUDAS PRYS-CU","PRY2020_PrysCu_v2.pdf"),
("INSTALACIONES MÓVILES - PVN ECOPLUS","PRY2026_PVN%20Ecoplus_v2.pdf"),
("INSTALACIONES MÓVILES - VN 202","PRY2026_VN202_v2.pdf"),
("Cable Óptico Dieléctrico Cubierta Sencilla Aéreo Auto Soportado en Vanos de hasta 200m","TDS_SO_006_Prysmian_A%C3%A9reo_AS%20KP_es%20.pdf"),
("Cable Óptico Dieléctrico Doble Cubierta Aéreo Auto Soportado en Vanos de hasta 200m","TDS_SO_008_Prysmian_A%C3%A9reo_AR%20PKP_es.pdf"),
("Cable Óptico Aéreo tipo Figura 8","TDS_SO_007_Prysmian_Areo_AS%20F8_es.pdf"),
("Cable Óptico Dieléctrico Tubo Único Aéreo Auto Soportado en Vanos de hasta 120m","TDS_SO_009_Prysmian_A%C3%A9reo_ASU_es_rev.1.pdf"),
("Cable Óptico Dieléctrico Doble Cubierta Aéreo Auto Soportado en Vanos de hasta 1.000m","TDS_SO_010_Prysmian_A%C3%A9reo%20Vanos%20Largos_AS%20PKP_es.pdf"),
("Cable Óptico Dieléctrico para Ductos o Aéreo Devanado","TDS_SO_001_Prysmian_Subterr%C3%A1neo_Ducto_0A1J_es.pdf"),
("Cable Óptico Dieléctrico Protección contra Roedores para Ductos o Aéreo Devanado","TDS_SO_002_Prysmian_Subterr%C3%A1neo_Ducto_Anti-Roedores_IF2J_es.pdf"),
("Cable Óptico Dieléctrico Directamente Enterrado","TDS_SO_003_Prysmian_Subterr%C3%A1neo_Enterrado_1N2J_es.pdf"),
("Cable Óptico Dieléctrico Protección contra Roedores Directamente Enterrado","TDS_SO_004_Prysmian_Subterr%C3%A1neo_Enterrado_Anti-Roedores_NF2J_es.pdf"),
("Cable Óptico Armado Cubierta Sencilla para Ductos, Directamente enterrado o Aéreo Devanado","TDS_SO_005_Prysmian_Subterr%C3%A1neo_Armado_1A1J_es.pdf"),
("DROP Óptico tipo Plano (Flat)","TDS_SO_014_Prysmian_DROP_FD_es.pdf"),
("DROP Óptico Redondo","TDS_SO_013_Prysmian_DROP_RD_es_rev.1.pdf"),
("DROP Óptico tipo Figura 8 con Mensajero Metálico","TDS_SO_015_Prysmian_DROP_F8M%20.pdf"),
("DROP Óptico tipo Figura 8 con Mensajero Dieléctrico","TDS_SO_016_Prysmian_DROP_F8D.pdf"),
("Cable Óptico Interior","TDS_SO_011_Prysmian_Interior_LT_es.pdf"),
("Cable Óptico Interior Vertical","TDS_SO_012_Prysmian_Interior%20Vertical_FT_es.pdf"),
]
BEL="https://catalog.belden.com/index.cfm?event=browse&c="
belden=[("Electronic Wire & Cable","category_172590"),("Networking & Data Cable","category_172593"),("Audio Cable","category_172595"),("Video Cable","category_172596"),("Coax & Triax Cable","category_172597"),("Multi-Media Cable","category_172598"),("Fiber Cable","category_160497"),("Flexible Automation Cable","category_172600"),("Tray Cable","category_172602"),("VFD Cable","category_172603"),("Digital Electricity","category_201131"),("Hybrid Fiber-Copper Cable","category_341771"),("Rolling Stock Cable","category_757528"),("Safety, Sound, & Security Cable","category_182866")]
SW="https://www.southwire.com/manuals-and-catalogs"
southwire=[("Low Voltage Catalog",SW),("BX Flex, and MC Catalog",SW),("High Voltage Underground Transmission Catalog",SW)]
PC="https://cms.polycab.com/media/"
polycab=[("Uninyvin cable","3dsjre3i/uninyvin-cable-compressed.pdf"),("LT Cable","kb1l12kq/lt-catalogue-ord_17720_v2.pdf"),("HT Cable","2bgh00kf/ht-cable-catalogue.pdf"),("Fire Survival Cable Brochure","k5lfvqld/fire-survival-cable-brochure-artwork_2.pdf"),("EHV","tdidggtl/polycab-ehv-brochure.pdf"),("Rubber Cable Catalogue","44sptk1w/final_rubber-cable_catalogue.pdf"),("Instrumentation Cable Catalogue","v0hf0z4m/instrumentation-cable_catalogue_innerpage_v9_ord-10763-final-c2c.pdf"),("Housewire Catalogue","rpzdby5w/housewire-catalogue.pdf"),("Communication & Data Cable","1nvdf0tf/approved_communicaton-cable-catlogue.pdf"),("Polycab MVCC Condactor Catalogue","bu3ixzub/polycab-mvcc-condactor-catalogue-pdf.pdf"),("B2B All Products Catalogue","2hlozcdj/b2b-all-products-catalogue.pdf")]
RC="https://riyadh-cables.com/product-category/"
riyadh=[("Wires","wires/"),("Low Voltage Cables (LV)","low-voltage-cables-lv/"),("Medium Voltage Cables (MV)","medium-voltage-cables-mv/"),("High & Extra High Voltage Cables (HV/EHV)","high-extra-high-voltage-cables-hv-ehv/"),("Overhead Lines Conductors (OHL)","overhead-lines-conductors-ohl/"),("Aerial Bundle Cables (ABC)","overhead-lines-conductors-ohl/aerial-bundle-cables-abc/"),("High Temperature Low Sag Conductors (HTLS)","overhead-lines-conductors-ohl/high-temperature-low-sag-conductors-htls/"),("Instrumentation & Control Cables","instrumentation-control-cables/"),("Renewable Energy Cables","renewable-energy-cables/"),("Fiber Optic & Telephone Cables","fiber-optic-telephone-cables/")]
EW="https://www.elsewedyelectric.com/product/"
elsewedy=[("Energy Cables","energy-cables-4"),("Special Cables","special-cables-5"),("Marine Cables","marine-cables-44"),("Telecom Cabling Solutions","telecom-cabling-solutions-34"),("Automotive Wires & Cables","automotive-wires-cables-33"),("OHTL & OPGW","ohtl-opgw-6"),("Winding Wires","winding-wires-8")]
LP="https://products.lappgroup.com/online-catalogue/"
lapp=[("ÖLFLEX® (Power and control cables)","power-and-control-cables.html"),("UNITRONIC® (Data communication systems)","data-communication-systems.html"),("ETHERLINE® (Data communication systems for ETHERNET technology)","data-communication-systems-for-ethernet-technology.html"),("HITRONIC® (Optical transmission systems)","optical-transmission-systems.html")]
HB="https://www.helu.com/vn-en/products-solutions/"
helu=[(n,p) for n,p in [
("Control & Connection Cables","electrical-cables/control-connection-cables/"),("Data cables and computers","electrical-cables/data-cables-and-computers/"),("Motor, Servo, Feedback Cables","electrical-cables/motor-servo-feedback-cables/"),("Industrial Ethernet cables","electrical-cables/industrial-ethernet-cables/"),("DC cables","electrical-cables/dc-cables/"),("HELUCOM® fiber optic cables","electrical-cables/helucom-fiber-optic-cables/"),("Single-core cable","electrical-cables/single-core-cable/"),("Fire-resistant cables","electrical-cables/fire-resistant-cables/"),("Flame-retardant cables","electrical-cables/flame-retardant-cables/"),("Anti-interference cables","electrical-cables/anti-interference-cables/"),("Temperature-resistant cables","electrical-cables/temperature-resistant-cables/"),("Rubber cables","electrical-cables/rubber-cables/"),("AWG cables","electrical-cables/awg-cables/"),("RS485 cables","electrical-cables/rs485-cable/"),("CVV cables","electrical-cables/cvv-cables/"),
("Bus cables","cables-for-infrastructures-and-buildings/bus-cables/"),("Cat5, Cat6, Cat7 LAN cables","cables-for-infrastructures-and-buildings/cat5-cat6-cat7-lan-cables/"),("Installation, telephone, fire alarm cables","cables-for-infrastructures-and-buildings/installation-telephone-fire-alarm-cables/"),("Energy and Power Cables","cables-for-infrastructures-and-buildings/energy-and-power-cables/"),("Low-voltage power cable","cables-for-infrastructures-and-buildings/low-voltage-power-cable/"),("Spiral cables","cables-for-infrastructures-and-buildings/spiral-cables/"),
("Reeling cables","cables-for-heavy-duty-industries/reeling-cables/"),("Flat cables","cables-for-heavy-duty-industries/flat-cables/"),
("Audio cables","cables-for-event-and-media-technology/audio-cables/"),("HDMI cables","cables-for-event-and-media-technology/hdmi-cables/"),
("Harness cable","harness-cable/"),("Custom Cables","custom-cables/"),("Drag Chain Compatible Cables","drag-chains/drag-chain-compatible-cables/")]]
# (companyId, companyName as in directory, status, pageUrl, hostsCsv, note, families)
S=[
("GLM-012","Belden","read","https://catalog.belden.com/","belden.com","",[(n,BEL+c) for n,c in belden]),
("GLM-031","Prysmian","read","https://ar.prysmian.com/en/toolsdownloads","prysmian.com","Argentina site (Spanish datasheet list); names as printed",[(n,PB+f) for n,f in prys]),
("GLM-011","Southwire","read","https://www.southwire.com/manuals-and-catalogs","southwire.com","Catalog names; all link to the official catalogs page",southwire),
("GLM-041","Polycab","read","https://polycab.com/product-brochures/cable-brochures","polycab.com","Brochure names as printed",[(n,PC+f) for n,f in polycab]),
("GLM-098","Riyadh Cables","read","https://riyadh-cables.com/products/","riyadh-cables.com","",[(n,RC+f) for n,f in riyadh]),
("GLM-095","Elsewedy Electric","read","https://www.elsewedyelectric.com/","elsewedyelectric.com","Cable-related families only (steel, polymers, insulators, metals and accessories omitted)",[(n,EW+f) for n,f in elsewedy]),
("GLM-021","LAPP","read","https://products.lappgroup.com/online-catalogue/","lappgroup.com","Cable families only (connectors, glands, conduit and marking omitted)",[(n,LP+f) for n,f in lapp]),
("GLM-022","HELUKABEL","read","https://www.helu.com/vn-en/products-solutions/","helu.com","Vietnam regional site of HELUKABEL; cable categories only",[(n,HB+p) for n,p in helu]),
("GLM-065","Nexans","unreadable","https://www.nexans.com/electrification-solutions/product-catalog/","nexans.com","The page lists market segments only, not cable families",[]),
("GLM-103","Ducab","unreadable","https://ducab.com/download-center/","ducab.com","The official page could not be fetched",[]),
]
import glob,json,os
_here=os.path.dirname(os.path.abspath(__file__))
for _f in sorted(glob.glob(os.path.join(_here,'families_data','batch*.json'))):
    for e in json.load(open(_f,encoding='utf-8')):
        S.append((e['id'],e['name'],e['status'],e['page'],e['hosts'],e['note'],[tuple(x) for x in e['fams']]))
_ids=[x[0] for x in S]
assert len(_ids)==len(set(_ids)),'duplicate company in data'
def j(s): return '"'+s.replace('\\','\\\\').replace('"','\\"')+'"'
lines=[]
lines.append('package com.ab.cablecost;\n\nimport java.util.ArrayList;\nimport java.util.Collections;\nimport java.util.LinkedHashMap;\nimport java.util.List;\nimport java.util.Map;\n')
lines.append('''/**
 * Cable FAMILIES (names only, no specifications) that a manufacturer lists on its own
 * official product / catalogue page. Generated from pages read on %s by
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
    /** status "read": families collected; "unreadable": the official page could not be read from here;
     *  "none_listed": the page was read but names no cable families. */
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
''' % RET)
for cid,cname,status,page,hosts,note,fams in S:
    lines.append('        src(%s,%s,%s,%s,%s,%s,%s);'%(j(cid),j(cname),j(status),j(page),j(hosts),j(note),j(RET)))
    for n,u in fams:
        lines.append('        fam(%s,%s,%s);'%(j(cid),j(n),j(u)))
lines.append('''    }
    public static List<Family> forCompany(String companyId){
        List<Family> out=new ArrayList<>();
        for(Family f:FAMILIES)if(f.companyId.equals(companyId))out.add(f);
        return Collections.unmodifiableList(out);
    }
    public static Source sourceFor(String companyId){return SOURCES.get(companyId);}
    public static Map<String,Source> sources(){return Collections.unmodifiableMap(SOURCES);}
    public static List<Family> all(){return Collections.unmodifiableList(FAMILIES);}
    /** Number of companies whose family list was collected (status "read"). */
    public static int readCount(){
        int n=0;
        for(Source s:SOURCES.values())if(s.read())n++;
        return n;
    }
    private CableFamilies(){}
}
''')
open(OUT,'w',encoding='utf-8').write('\n'.join(lines))
print(sum(len(s[6]) for s in S),'families,',len(S),'companies')
