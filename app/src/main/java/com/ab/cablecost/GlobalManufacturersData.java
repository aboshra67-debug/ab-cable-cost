package com.ab.cablecost;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 150 manufacturer CANDIDATES, separate from verified product specs.
 * Source: curated research snapshot 2026-10-27. No model-level ratings/figures implied.
 * The bundled CSV contains no quoted commas; reject any unexpected schema.
 */
final class GlobalManufacturersData {
    static final class Manufacturer {
        final String id, name, country, website, catalog, status, source;
        Manufacturer(String[] c) {
            id=c[0];name=c[1];country=c[2];website=c[4];catalog=c[5];
            status=c[6];source=c[9];
        }
        boolean hasCatalogPage() { return status.equals("catalog_index_found_review_needed"); }
        boolean hasProductPage() { return status.equals("products_page_only"); }
        String displayStatus(boolean ar) {
            if(hasCatalogPage()) return ar?"رابط كتالوج مرصود — تحت المراجعة":"Catalog link found — pending review";
            if(hasProductPage()) return ar?"صفحة منتجات فقط — تحت المراجعة":"Product page only — pending review";
            return ar?"الكتالوج غير متحقق":"Catalog not verified";
        }
        boolean matches(String q, String countryFilter) {
            if(!countryFilter.equals("*")&&!country.equals(countryFilter))return false;
            if(q.length()==0)return true;
            String k=q.toLowerCase(Locale.ROOT);
            return name.toLowerCase(Locale.ROOT).contains(k)
                || country.toLowerCase(Locale.ROOT).contains(k)
                || id.toLowerCase(Locale.ROOT).contains(k);
        }
    }

    private static List<Manufacturer> cache;
    static synchronized List<Manufacturer> all(Context context) {
        if(cache!=null)return cache;
        List<Manufacturer> loaded=new ArrayList<>();
        try(BufferedReader in=new BufferedReader(new InputStreamReader(
                context.getAssets().open("global_manufacturers_150.csv"),StandardCharsets.UTF_8))) {
            String header=in.readLine();
            if(header==null||!header.replace("\uFEFF","").startsWith("id,name,country_ar,")) {
                return Collections.emptyList();
            }
            String line;
            while((line=in.readLine())!=null) {
                if(line.trim().isEmpty())continue;
                String[] parts=line.split(",",-1);
                if(parts.length!=12||!parts[0].startsWith("GLM-"))continue;
                loaded.add(new Manufacturer(parts));
            }
        }catch(Exception e) {
            return Collections.emptyList();
        }
        cache=Collections.unmodifiableList(loaded);
        return cache;
    }

    private GlobalManufacturersData(){}
}
