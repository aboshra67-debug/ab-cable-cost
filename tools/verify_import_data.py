#!/usr/bin/env python3
"""Validate (or regenerate) the review-only AB Cable Cost 150 manufacturer import.

Source of truth: user-supplied AB_Cable_Cost_150_Manufacturers_IMPORT_PACK.zip,
2026-10-27. The embedded asset is a zlib-compressed compact JSON encoding
of the eight non-constant fields. Source pack is archived by the user, not
redistributed by app. Other fields are constants:
entity_type=manufacturer_candidate, listing_status=pending_review,
research_date=2026-10-27; verification_label_ar is derived from status.

Examples:
  python3 tools/verify_import_data.py
  python3 tools/verify_import_data.py --compare-zip /path/IMPORT_PACK.zip
  python3 tools/verify_import_data.py --build-from-zip /path/IMPORT_PACK.zip
"""
from __future__ import annotations

import argparse
import base64
import collections
import csv
import hashlib
import io
import json
from pathlib import Path
import re
import zlib
import zipfile

ROOT=Path(__file__).resolve().parents[1]
ASSET=ROOT/"app/src/main/assets/global_150_candidates_zlib_base64.txt"
BRANDS=ROOT/"app/src/main/assets/global_150_separate_brands.csv"
FIELDS=["id","name","country_ar","official_website_candidate",
        "official_catalog_or_product_url_candidate","verification_status",
        "reference_url","notes"]
SHA256="d26d73a0cf62d880c83ecefd89322e2ed52e2634094da43d0081bdda7e10f12d"
STATUS_COUNTS={
    "catalog_unverified":81,
    "catalog_index_found_review_needed":47,
    "products_page_only":22
}
def compact(records):
    return json.dumps([[r.get(k,"") for k in FIELDS] for r in records],
                      ensure_ascii=False,separators=(",",":")).encode("utf-8")
def validate_bytes(raw):
    digest=hashlib.sha256(raw).hexdigest()
    assert digest==SHA256, f"Data integrity mismatch: {digest}"
    rows=json.loads(raw.decode("utf-8"))
    assert len(rows)==150
    assert all(isinstance(row,list) and len(row)==8 for row in rows)
    ids=[r[0] for r in rows]
    names=[r[1] for r in rows]
    assert ids==[f"GLM-{i:03d}" for i in range(1,151)]
    assert len(set(ids))==150 and len(set(n.lower() for n in names))==150
    assert len({r[2] for r in rows})==33
    assert all(r[0] and r[1] and r[2] for r in rows)
    assert collections.Counter(r[5] for r in rows)==STATUS_COUNTS
    assert sum(bool(r[3]) for r in rows)==87
    assert sum(bool(r[4]) for r in rows)==69
    assert sum(bool(r[6]) for r in rows)==148
    for row in rows:
        for url in row[3],row[4],row[6]:
            if url:
                assert url.startswith("https://"),(row[0],url)
    return rows
def build_from_zip(path):
    with zipfile.ZipFile(path) as archive:
        records=json.loads(archive.read("AB_Cable_Cost_150_Manufacturers_IMPORT.json"))
        raw=compact(records)
        validate_bytes(raw)
        csv_raw=archive.read("AB_Cable_Cost_150_Manufacturers_IMPORT.csv").decode("utf-8-sig")
        csv_rows=list(csv.DictReader(io.StringIO(csv_raw)))
        assert len(csv_rows)==150
        assert [r["id"] for r in csv_rows]==[r["id"] for r in records]
        assert all(r["listing_status"]=="pending_review" for r in records)
        assert all(r["entity_type"]=="manufacturer_candidate" for r in records)
        assert all(r["research_date"]=="2026-10-27" for r in records)
        return raw
def main():
    parser=argparse.ArgumentParser()
    parser.add_argument("--build-from-zip",type=Path)
    parser.add_argument("--compare-zip",type=Path)
    args=parser.parse_args()
    if args.build_from_zip:
        raw=build_from_zip(args.build_from_zip)
        ASSET.parent.mkdir(parents=True,exist_ok=True)
        ASSET.write_text(base64.b64encode(zlib.compress(raw,9)).decode()+"\n",
                         encoding="ascii")
    data=base64.b64decode(ASSET.read_text(encoding="ascii").strip(),validate=True)
    assert len(data)<12000
    raw=zlib.decompress(data)
    assert len(raw)<150000
    rows=validate_bytes(raw)
    if args.compare_zip:
        assert raw==build_from_zip(args.compare_zip), "ZIP import differs from embedded dataset"
    with BRANDS.open(encoding="utf-8-sig",newline="") as fh:
        brands=list(csv.DictReader(fh))
    assert len(brands)==9
    assert len({r["العلامة/الكيان"] for r in brands})==9
    print("PASS: candidate factory directory 150 rows / 33 countries / "
          "47 catalog-index leads / 22 product pages / 81 catalogs unverified")
    print("PASS: 87 candidate websites / 69 candidate catalog-or-product URLs / "
          "148 research references; 9 brands separate; SHA-256 "+SHA256)

if __name__=="__main__":
    main()
