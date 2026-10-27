package com.ab.cablecost;

import android.content.Context;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.InflaterInputStream;

/**
 * Offline import of the user's 150-company RESEARCH directory (2026-10-27).
 *
 * Safety boundary: EVERY record is a pending manufacturer CANDIDATE.
 * Source URL candidates are never upgraded to "verified" merely by importing.
 * No record in this directory creates a VerifiedCableData product.
 *
 * Data asset is a compact, zlib+base64 representation of the JSON/CSV pack
 * (id, name, country, candidate website/catalog, review status, research source,
 * notes). Constant fields from the original upload: entity_type =
 * manufacturer_candidate, listing_status = pending_review, research_date =
 * 2026-10-27. Source pack SHA-256 recorded in IMPORT_AUDIT.md.
 */
public final class ManufacturerCandidates {
    private static final String ASSET="global_150_candidates_zlib_base64.txt";
    private static final String EXPECTED_SHA256=
        "d26d73a0cf62d880c83ecefd89322e2ed52e2634094da43d0081bdda7e10f12d";
    private static volatile List<Record> cache;

    public static final class Record {
        public final String id,name,countryAr,websiteCandidate,catalogCandidate;
        public final String verificationStatus,referenceUrl,notes;
        public static final String LISTING_STATUS="pending_review";
        public static final String RESEARCH_DATE="2026-10-27";
        Record(JSONArray row) throws JSONException {
            id=row.getString(0);
            name=row.getString(1);
            countryAr=row.getString(2);
            websiteCandidate=row.getString(3);
            catalogCandidate=row.getString(4);
            verificationStatus=row.getString(5);
            referenceUrl=row.getString(6);
            notes=row.getString(7);
            if(!id.matches("GLM-[0-9]{3}")||name.trim().isEmpty()||countryAr.trim().isEmpty())
                throw new JSONException("Invalid candidate identity");
            if(!verificationStatus.equals("catalog_unverified") &&
               !verificationStatus.equals("products_page_only") &&
               !verificationStatus.equals("catalog_index_found_review_needed"))
                throw new JSONException("Invalid review status: "+id);
        }
        public String searchKey(){
            return (id+" "+name+" "+countryAr+" "+notes).toLowerCase(Locale.ROOT);
        }
        public boolean hasCatalogCandidate(){return isHttps(catalogCandidate);}
        public boolean hasWebsiteCandidate(){return isHttps(websiteCandidate);}
    }

    public static List<Record> all(Context context){
        List<Record> existing=cache;
        if(existing!=null)return existing;
        synchronized(ManufacturerCandidates.class) {
            if(cache==null)cache=Collections.unmodifiableList(load(context.getApplicationContext()));
            return cache;
        }
    }
    private static List<Record> load(Context context) {
        try {
            ByteArrayOutputStream encoded=new ByteArrayOutputStream();
            try(InputStream in=context.getAssets().open(ASSET)) {
                byte[] block=new byte[4096];int count;
                while((count=in.read(block))!=-1) {
                    if(encoded.size()+count>20000)throw new IllegalStateException("Oversized import asset");
                    encoded.write(block,0,count);
                }
            }
            byte[] zipped=Base64.decode(encoded.toByteArray(),Base64.DEFAULT);
            ByteArrayOutputStream uncompressed=new ByteArrayOutputStream();
            try(InputStream in=new InflaterInputStream(new java.io.ByteArrayInputStream(zipped))) {
                byte[] chunk=new byte[4096];int n;
                while((n=in.read(chunk))!=-1) {
                    if(uncompressed.size()+n>150000)throw new IllegalStateException("Oversized manufacturer directory");
                    uncompressed.write(chunk,0,n);
                }
            }
            byte[] json=uncompressed.toByteArray();
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            StringBuilder sha=new StringBuilder();
            for(byte b:md.digest(json))sha.append(String.format(Locale.ROOT,"%02x",b&255));
            if(!EXPECTED_SHA256.equals(sha.toString()))
                throw new IllegalStateException("Manufacturer import integrity check failed");
            JSONArray array=new JSONArray(new String(json,StandardCharsets.UTF_8));
            if(array.length()!=150)throw new IllegalStateException("Expected exactly 150 candidates");
            Set<String> ids=new HashSet<>();
            List<Record> list=new ArrayList<>(array.length());
            for(int i=0;i<array.length();i++) {
                Record r=new Record(array.getJSONArray(i));
                if(!ids.add(r.id))throw new IllegalStateException("Duplicate candidate ID: "+r.id);
                list.add(r);
            }
            return list;
        }catch(Exception e){
            throw new IllegalStateException("Cannot load candidate directory: "+e.getMessage(),e);
        }
    }
    public static boolean isHttps(String url){
        if(url==null||!url.startsWith("https://"))return false;
        try{
            java.net.URI uri=new java.net.URI(url);
            return "https".equals(uri.getScheme())&&
                uri.getHost()!=null&&!uri.getHost().trim().isEmpty()&&
                uri.getRawUserInfo()==null;
        }catch(Exception e){return false;}
    }
    public static int catalogIndexes(List<Record> rows){
        int n=0;for(Record r:rows)
            if(r.verificationStatus.equals("catalog_index_found_review_needed"))n++;
        return n;
    }
    public static int productsOnly(List<Record> rows){
        int n=0;for(Record r:rows)
            if(r.verificationStatus.equals("products_page_only"))n++;
        return n;
    }
    public static int unverified(List<Record> rows){
        int n=0;for(Record r:rows)
            if(r.verificationStatus.equals("catalog_unverified"))n++;
        return n;
    }
    public static int countries(List<Record> rows){
        Set<String> c=new HashSet<>();for(Record r:rows)c.add(r.countryAr);return c.size();
    }
    private ManufacturerCandidates(){}
}
