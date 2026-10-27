# AB Cable Cost — 150-Company Import Audit (2026-10-27)

## Provenance

User-uploaded source: `AB_Cable_Cost_150_Manufacturers_IMPORT_PACK.zip`.

- Original ZIP SHA-256: `e7f2cde33bc452df18b150fed91f02cbb93366718281308bf18fc034421de3ae`
- Original manufacturer JSON SHA-256: `176d8e95ff060574f98fd7b52e7c36496c2e5a212c97effa8e6b547a6ad69e1a`
- Compact embedded rows SHA-256: `d26d73a0cf62d880c83ecefd89322e2ed52e2634094da43d0081bdda7e10f12d`
- Same 150 IDs and row order in CSV and JSON (checked)
- Workbook contains `150_Manufacturers` A1:K151, `Research_Summary`, `Brands_Not_Factories`, `Research_Sources` and `Official_Verified_Sample` (research only).

## Import statistics

| Property | Count |
|---|---:|
| Manufacturer candidates | 150 |
| Countries covered | 33 |
| Unique GLM IDs | 150 |
| Unique names, case-folded | 150 |
| Manufacturer website candidate present | 87 |
| Website candidate missing | 63 |
| Catalog/index page candidate | 47 |
| Product or overview page candidate only | 22 |
| Catalog unverified or not present | 81 |
| Catalog or product URL candidate present | 69 |
| Third-party research reference URL present | 148 |
| Source reference missing | 2 |
| Separate brand/entity records (NOT added to 150) | 9 |

**CRITICAL:** Every imported manufacturer is tagged `manufacturer_candidate`, `pending_review`, and `research_date=2026-10-27`. No row is certified or promoted to the technical library. The 47 catalog-link rows are merely sites **spotted during research**. Their URL alone does not substantiate a manufacturer, product, datasheet, or technical performance. Generic manufacturer list URLs and distributor catalogs are not factory evidence.

## Review findings

1. **150 candidates ≠ 150 independently verified manufacturers.** Legal manufacturer status, official ownership, product range and local country location should be confirmed individually before approval.
2. **81 missing catalog verification, 63 missing official website candidates**. Do not show invented product technical specifications for these entries.
3. Country is a research affiliation provided in Arabic, not verified headquarters/country of actual production.
4. `reference_url` is a *research lead*: many records share industry directory URLs. It must not be styled as an official product source.
5. Some brands, mergers and subsidiaries overlap with major groups; avoid double-counting the same corporate manufacturer.
6. Separate brands table includes Premium-Line (distinct brand, no verified factory), Draka / General Cable (Prysmian group), La Triveneta Cavi / Olex (Nexans group as research hypothesis), and distributor or unrelated-company exclusions. Those statements also require routine ownership validation before commercial certification.
7. An official Nexans company catalog page was checked publicly: https://www.nexans.com/electrification-solutions/product-catalog/ . Premium-Line download center https://premiumline-cabling.com/service_category/download/ was located, but **a catalog does not prove manufacturing origin**.
8. A sample alfanar manufacturer cables PDF is accessible at https://www.alfanar.com/catalogs/building_cables_wires.pdf . No detailed cable amperage, voltage or resistance is automatically imported from this corporate directory.
9. The app's **existing verified cable records** remain in `VerifiedCableData.java`, unchanged. **Candidate company listing is separated** to avoid unsafe professional engineering claims.

## Implementation and data maintenance

The app bundles candidates offline as `app/src/main/assets/global_150_candidates_zlib_base64.txt`: 150 compact JSON rows compressed with zlib and Base64 for reliable UTF-8 GitHub transfer, verified with SHA-256 and row counts at runtime. The 9 brands are preserved separately in `global_150_separate_brands.csv`.

The companion `tools/verify_import_data.py` can rebuild this compressed asset deterministically from the ORIGINAL ZIP, preserving ID, name, country, candidate site URL, candidate catalog/product URL, status, research reference, and notes. Three invariant columns (record type, review status, research date) are enforced as constants by the loader; the Arabic status label is generated from the status code.

**No mutation to prior app data or manufacturing calculator**. No online scraping or mass PDF distribution. External company URLs are only opened after explicit taps and remain labelled unchecked.

## Further work

- Verify each manufacturer, jurisdiction, factory versus brand entity, product catalogue, and current official links individually.
- Approve only verified manufacturer directory entries under an explicit human review workflow, later connect each approved company to **source-backed individual product datasheets**.
- Archive official evidence dates/revisions, licensing, and update cadence for a commercial global release.
