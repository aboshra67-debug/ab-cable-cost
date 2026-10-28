package com.ab.cablecost;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Explicit links between the two cable data sets, so the same real cable is never
 * described twice with different numbers:
 *
 *   CableCatalog.Spec      : factory-costing recipes (published refs + editable templates)
 *   VerifiedCableData.Cable: datasheet cards shown in the Global Library
 *
 * Nothing is merged or moved. tools/VerifyLibraryData.java fails the build when a
 * linked pair disagrees on diameter or source, or when a link points nowhere.
 * Pure Java (no Android dependency).
 */
public final class CableLinks {
    private static final Map<String,String> SPEC_TO_VERIFIED;
    static {
        Map<String,String> m = new HashMap<>();
        m.put("BE_8723", "BELDEN_8723");
        m.put("BE_5120", "BELDEN_5120FN");
        m.put("SW_RG59_F", "ELSEWEDY_RG59_F");
        SPEC_TO_VERIFIED = Collections.unmodifiableMap(m);
    }
    /** Library record id for a costing-catalog spec id, or null when there is none. */
    public static String verifiedIdFor(String specId) { return SPEC_TO_VERIFIED.get(specId); }
    public static Map<String,String> all() { return SPEC_TO_VERIFIED; }
    private CableLinks() {}
}
