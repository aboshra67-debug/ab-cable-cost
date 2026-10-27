#!/usr/bin/env python3
"""Static checks for AB Cable Cost research manufacturers import."""
import csv
from collections import Counter
from pathlib import Path

base=Path(__file__).resolve().parents[1]
p=base/"app/src/main/assets/global_manufacturers_150.csv"
with p.open(encoding="utf-8-sig", newline="") as handle:
    rows=list(csv.DictReader(handle))
assert len(rows)==150, f"Expected 150 rows, got {len(rows)}"
assert len({x["id"] for x in rows})==150, "Duplicate IDs"
assert len({x["name"] for x in rows})==150, "Duplicate names"
assert len({x["country_ar"] for x in rows})==33, "Expected 33 countries"
assert all(x["id"]==f"GLM-{i:03d}" for i,x in enumerate(rows,1)), "Non-stable import IDs"
assert all(x["listing_status"]=="pending_review" for x in rows), "Unreviewed data mislabeled as approved"
assert all(x["verification_status"] in {
    "catalog_unverified","catalog_index_found_review_needed","products_page_only"} for x in rows)
assert all(not x["official_website_candidate"] or x["official_website_candidate"].startswith("https://") for x in rows)
assert all(not x["official_catalog_or_product_url_candidate"] or x["official_catalog_or_product_url_candidate"].startswith("https://") for x in rows)
statuses=Counter(x["verification_status"] for x in rows)
assert statuses=={"catalog_unverified":81,"catalog_index_found_review_needed":47,"products_page_only":22},statuses
java=(base/"app/src/main/java/com/ab/cablecost/LibraryActivity.java").read_text()
assert "ManufacturersActivity.class" in java
assert "VerifiedCableData.all()" in java, "Original official product list must remain"
manifest=(base/"app/src/main/AndroidManifest.xml").read_text()
assert 'android:name=".ManufacturersActivity"' in manifest
print("150 unique candidates; 33 countries; statuses",dict(statuses))
print("PASS: directory is accessible without overwriting verified cable data")
