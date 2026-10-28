import com.ab.cablecost.CountryNames;
import com.ab.cablecost.VerifiedCableData;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

/**
 * Plain-JDK integrity check for the verified cable library and the 150-company
 * directory. No Android SDK needed:
 *
 *   javac -encoding UTF-8 -d build/verify \
 *     app/src/main/java/com/ab/cablecost/CountryNames.java \
 *     app/src/main/java/com/ab/cablecost/VerifiedCableData.java tools/VerifyLibraryData.java
 *   java -cp build/verify VerifyLibraryData
 *
 * Fails (exit 1) when a record is incomplete, a number written into a record is
 * not present in its archived datasheet quotes, a company country has no
 * translation, or the manufacturer chooser would no longer list exactly 150 names.
 */
public final class VerifyLibraryData {
    /** Records published before V0.5.5; their evidence predates tools/source_evidence. */
    private static final Set<String> LEGACY=new HashSet<>(List.of(
        "BELDEN_8723","BELDEN_5120FN","ELSEWEDY_RG59_F"));
    private static final Pattern NUMBER=Pattern.compile("\\d+(?:[.,]\\d+)?");
    private static final List<String> failures=new ArrayList<>();

    private static void fail(String message){failures.add(message);System.out.println("FAIL: "+message);}
    private static void need(String id,String field,String value){
        if(value==null||value.trim().isEmpty())fail(id+": missing "+field);
    }
    private static Set<String> numbers(String text){
        Set<String> out=new HashSet<>();
        Matcher m=NUMBER.matcher(text);
        while(m.find()){
            String n=m.group().replace(',','.');
            out.add(n);
            // "1,084" style thousands separators and "0,6/1" decimals appear in datasheets
            out.add(m.group());
            if(n.indexOf('.')>0)for(String part:n.split("\\."))out.add(part);
        }
        return out;
    }
    private static String all(VerifiedCableData.Cable c){
        StringBuilder s=new StringBuilder();
        for(String v:new String[]{c.size,c.construction,c.voltage,c.current,c.conductorResistance,
            c.diameter,c.temperature,c.insulation,c.shield,c.standards,c.revision,c.notesEn}){
            if(v!=null)s.append(v).append('\n');
        }
        for(VerifiedCableData.Layer l:c.layers)s.append(l.description(false)).append('\n');
        return s.toString();
    }
    private static byte[] inflate(byte[] data) throws Exception {
        Inflater inflater=new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buffer=new byte[4096];
        while(!inflater.finished()){
            int n=inflater.inflate(buffer);
            if(n==0&&(inflater.needsInput()||inflater.needsDictionary()))break;
            out.write(buffer,0,n);
        }
        return out.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        Path root=Paths.get(args.length>0?args[0]:".");
        // ---- verified records ------------------------------------------------
        List<VerifiedCableData.Cable> cables=VerifiedCableData.all();
        Set<String> ids=new HashSet<>();
        Map<String,String> categoryLabels=new HashMap<>();
        for(VerifiedCableData.Cable c:cables){
            if(!ids.add(c.id))fail("duplicate id "+c.id);
            for(String[] f:new String[][]{{"model",c.model},{"size",c.size},{"brand",c.brand},
                {"countryAr",c.countryAr},{"countryEn",c.countryEn},{"category",c.category},
                {"categoryAr",c.categoryAr},{"categoryEn",c.categoryEn},{"construction",c.construction},
                {"temperature",c.temperature},{"insulation",c.insulation},{"standards",c.standards},
                {"usesAr",c.usesAr},{"usesEn",c.usesEn},{"titleAr",c.titleAr},{"titleEn",c.titleEn},
                {"notesAr",c.notesAr},{"notesEn",c.notesEn},{"sourceUrl",c.sourceUrl},{"pdfUrl",c.pdfUrl},
                {"revision",c.revision},{"sourceName",c.sourceName}})
                need(c.id,f[0],f[1]);
            if(c.layers.size()<3)fail(c.id+": fewer than 3 documented layers");
            for(VerifiedCableData.Layer l:c.layers){
                need(c.id,"layer name",l.ar);need(c.id,"layer name EN",l.en);
                need(c.id,"layer description",l.descriptionAr);need(c.id,"layer description EN",l.descriptionEn);
            }
            if(c.sourceUrl==null||!c.sourceUrl.startsWith("https://"))fail(c.id+": sourceUrl must be https");
            if(c.pdfUrl==null||!c.pdfUrl.startsWith("https://"))fail(c.id+": pdfUrl must be https");
            // One label pair per category key, otherwise the Cable type dialog lists it twice.
            String label=c.categoryAr+"|"+c.categoryEn;
            String previous=categoryLabels.put(c.category,label);
            if(previous!=null&&!previous.equals(label))fail(c.id+": category "+c.category+" has inconsistent labels");
            if(!c.countryAr.equals(CountryNames.arabicFromEnglish(c.countryEn)))
                fail(c.id+": country "+c.countryEn+" / "+c.countryAr+" is not in CountryNames");
            // Evidence: every number in the record must exist in the archived quotes.
            if(!LEGACY.contains(c.id)){
                Path file=root.resolve("tools/source_evidence/"+c.id+".txt");
                if(!Files.isRegularFile(file)){fail(c.id+": no evidence file "+file);continue;}
                String evidence=new String(Files.readAllBytes(file),StandardCharsets.UTF_8);
                if(!evidence.contains(c.pdfUrl))fail(c.id+": evidence file does not cite the pdfUrl");
                Set<String> known=numbers(evidence);
                Set<String> seen=new HashSet<>();
                Matcher m=NUMBER.matcher(all(c));
                while(m.find()){
                    String n=m.group();
                    if(known.contains(n)||known.contains(n.replace(',','.')))continue;
                    if(seen.add(n))fail(c.id+": number '"+n+"' is not present in tools/source_evidence/"+c.id+".txt");
                }
            }
        }
        // ---- 150 companies: countries translated, chooser lists exactly 150 ------
        String asset=new String(Files.readAllBytes(root.resolve(
            "app/src/main/assets/global_150_candidates_zlib_base64.txt")),StandardCharsets.UTF_8).trim();
        String json=new String(inflate(Base64.getDecoder().decode(asset)),StandardCharsets.UTF_8);
        Matcher m=Pattern.compile("\\[\"(GLM-\\d{3})\",\"([^\"]*)\",\"([^\"]*)\"").matcher(json);
        Set<String> names=new HashSet<>();
        Set<String> countries=new HashSet<>();
        int rows=0;
        while(m.find()){
            rows++;
            names.add(m.group(2).toLowerCase(Locale.ROOT));
            countries.add(m.group(3));
            if(!CountryNames.known(m.group(3)))fail(m.group(1)+": country '"+m.group(3)+"' has no translation");
        }
        if(rows!=150)fail("expected 150 candidate rows, found "+rows);
        if(countries.size()!=33)fail("expected 33 countries, found "+countries.size());
        if(CountryNames.count()!=33)fail("CountryNames holds "+CountryNames.count()+" countries, expected 33");
        Set<String> chooser=new HashSet<>(names);
        for(VerifiedCableData.Cable c:cables)chooser.add(c.brand.toLowerCase(Locale.ROOT));
        if(chooser.size()!=150)
            fail("manufacturer chooser would list "+chooser.size()+" names, expected 150 "
                +"(a verified brand name must match its directory name exactly)");
        // ---- result ------------------------------------------------------------
        System.out.println("records="+cables.size()+" companies="+rows+" countries="+countries.size()
            +" chooser="+chooser.size());
        if(!failures.isEmpty()){
            System.out.println(failures.size()+" check(s) failed");
            System.exit(1);
        }
        System.out.println("PASS: verified records complete, numbers match archived datasheet quotes, "
            +"150 companies / 33 countries translated, chooser lists exactly 150 names");
    }
    private VerifyLibraryData(){}
}
