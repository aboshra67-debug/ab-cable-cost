# AB Cable Cost — V0.3.0 (Catalog & Recipe Safe Patch)

**Android, offline-first.** Approved AB Cable Cost logo is embedded as a verified binary PNG, with cable category pictures cropped from the approved visual reference. Version 0.3.0 opens on a dedicated home dashboard with cable families, a size-based catalogue and one-tap shortcuts for materials and factory settings. The original V0.1.2 standalone copper / insulation calculator remains preserved under CalculatorActivity. Existing V0.1.2 conductor and insulation calculator is preserved. New `دليل الكابلات والمقاسات وتكلفة التصنيع` opens an independent catalogue screen.

## Actual scope
- Cable families: ترمو، كاميرات، إنذار حريق، شيلد.
- Manufacturer references are identified by product code, specification and original source URL.
- Basic reference dataset: Elsewedy Cu/XLPE/PVC four-core sizes, Elsewedy fire alarm screened models FA002001-FA002006, Elsewedy RG59 foam/solid PE, Belden 8723 and 5120FN.
- Unverified common thermo sizes and combined camera cables are clearly marked **قالب قابل للتعديل**; these are not certified manufacturer data.
- Reference overall cable weight kg/km (if supplied) is **not** an ingredient weight and is **never** used in cost equations.
- Manufacturer / cable / nominal size selection; composition layers shown by type.
- User-editable BOM: actual input grams of raw material per meter of cable, or input consumption meters raw per meter cable, with separate waste in % or kg (weight materials only).
- Main conductor line is populated from theoretical nominal area and the established copper density of 8.96 g/cm³; 5% estimated allowance is used for tinned copper conductor only. All other material consumption must be provided by manufacturer; blank items are marked as missing.
- Central editable raw materials price book with kilo/gram/meter price units and ability to add new materials.
- Price currency selection is a **single currency per factory**, with no current automatic FX. Mixed currency purchasing / rate feeds are not implemented yet.
- Editable and locally stored factory recipe, without modifying manufacturer records.
- Operation cost per meter or per roll. No profit/sales/accounting.
- Android local SharedPreferences, no login or internet required for calculations.

## Primary references

1. Elsewedy Electric Power Cables Catalogue, Cu/XLPE/PVC 0.6/1 kV multicore 4C:
   https://elsewedyelectric.com/pdf/Catalouges/02424280/elsewedy-cables-power-cables-catalogue.pdf
2. Elsewedy Special Cables Catalogue, RG59 Foamed PE p.59, RG59 Solid PE p.58, screened Fire Alarm 500V p.83:
   https://elsewedyelectric.com/pdf/Catalouges/02113240/special-cables-catalogue.pdf
3. Belden 8723 official product technical specifications:
   https://www.belden.com/products/cable/electronic-wire-cable/multi-pair-cable/8723
4. Belden 5120FN official product technical specifications:
   https://www.belden.com/products/cable/safety-sound-security/fire-alarm-cable/5120fn

**Intellectual property:** This repository only contains a small curated set of public **facts** with citations. It does not bundle manufacturer PDFs, imagery or trademarks for resale. Verify specifications/permission before commercial distribution.

## Weight / costing limitations

- For strand-specific conductor: g/m = pi/4 * (strand-diameter-mm)^2 * strand-count * cores * density. Density copper = 8.96 g/cm³, and the **5% uplift on tinned copper is the user's estimation rule, not a material constant**.
- For reference nominal cross section: g/m = nominal-mm² * cores * density; this is **theoretical nominal metal** (not proof of actual copper draw weight).
- Outer diameter and manufacturer published total kg/km describe the **finished cable**, not per-material consumption.
- Coaxial foil and braiding are not inferred from optical shielding coverage alone; manufacturing weight needs actual tape width/overlap/metal weight or a measured quantity.
- Missing material weights or prices mean **incomplete cost**. Avoid displaying a falsely complete estimate.
- Saved local recipe fields are not a validated machine recipe. Factory should confirm tolerances, packaging, process scrap and measured weights.

## Building

GitHub Actions builds Android debug APK, verifies its signature, installs and launches the main Activity and the Catalogue Activity on an Android 15 emulator, and uploads APK, source ZIP and screenshots separately. Debug signing key can change across runner executions; don't treat it as a stable update key.

### Pending for a commercial release
- Stable private production signing keystore and secure release signing.
- Per-factory currency exchange-rate conversion, other languages, factory logo upload and branded report templates.
- Validation against manufacturer production trials, full compound and shielding calculations, editable measured/theoretical dual-weight history, database migration/sync and comprehensive unit tests.
- Independent licensing and rights checks for catalog usage.
