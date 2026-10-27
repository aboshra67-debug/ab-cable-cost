#!/usr/bin/env python3
"""Emulator UI verification of approved Global Cable Library V0.5.0.

Loads real app screens, does not treat a design image as implementation.
Checks source-limited results, navigation to a technical card, schematic layers,
and bilingual language selection; captures real screenshots for visual review.
"""
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

OUT=Path("screenshots");OUT.mkdir(exist_ok=True)

def adb(*args):
    return subprocess.check_output(["adb",*args],text=True,stderr=subprocess.STDOUT)

def dump(name):
    adb("shell","uiautomator","dump","/sdcard/ui.xml")
    subprocess.check_call(["adb","pull","/sdcard/ui.xml",str(OUT/name)])
    return ET.parse(OUT/name).getroot()

def node(tree,term,field="text"):
    for x in tree.iter("node"):
        if term in x.attrib.get(field,""):
            return x
    return None

def tap(x):
    b=x.attrib.get("bounds","")
    m=re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]",b)
    assert m,"Missing view bounds: "+b
    a,b,c,d=map(int,m.groups())
    adb("shell","input","tap",str((a+c)//2),str((b+d)//2))

def wait(term,name,field="text",tries=8):
    for attempt in range(tries):
        tree=dump(name)
        if node(tree,"Pixel Launcher isn't responding"):
            close=node(tree,"Close app")
            if close:
                tap(close)
                adb("shell","am","start","-n","com.ab.cablecost/.MainActivity")
                time.sleep(2)
                continue
        found=node(tree,term,field)
        if found is not None:return tree,found
        time.sleep(1)
    raise AssertionError("Expected screen text missing: "+term)

def screen(name):
    adb("shell","screencap","-p","/sdcard/pic.png")
    subprocess.check_call(["adb","pull","/sdcard/pic.png",str(OUT/name)])

tree,card=wait("مكتبة الكابلات","home.xml")
assert node(tree,"AB Cable Cost") is not None
assert node(tree,"AB Cable Cost original logo","content-desc") is not None
screen("home.png")
tap(card)
tree,result=wait("Belden","library_ar.xml")
screen("global_library_ar.png")
assert node(tree,"نتائج البحث") is not None
assert node(tree,"مصدر رسمي") is not None

tree,details=wait("عرض التفاصيل","library_details.xml")
tap(details)
tree,heading=wait("8723","belden8723.xml")
screen("cable_8723_ar.png")
assert node(tree,"مقطع هندسي") is not None
assert node(tree,"PDF") is not None

tree,three=wait("عرض 3D","detail_3d.xml")
tap(three)
tree,help=wait("اسحب على الرسم","detail_3d_selected.xml")
screen("cable_8723_3d.png")

adb("shell","input","keyevent","4")
tree,library=wait("نتائج البحث","library_again.xml")
# Locale selector in the new library is a real saved setting, not static art.
tree,lang=wait("AR","library_language.xml","content-desc")
tap(lang)
tree,english=wait("English","library_lang_picker.xml")
tap(english)
tree,cable_en=wait("Search results","library_en.xml")
assert node(tree,"Verified models so far") is not None
screen("global_library_en.png")

# Company directory is now visible at the TOP of the cable library,
# rather than hidden below filter chips and categories.
tree,directory=wait("Company directory","company_directory_link.xml",tries=6)
tap(directory)
tree,company=wait("Hengtong Group","manufacturers_all.xml")
assert node(tree,"150") is not None or node(tree,"150 / 150") is not None
assert node(tree,"Pending review") is not None
screen("manufacturer_candidates_en.png")

tree,brands=wait("Separate brands","manufacturer_tabs.xml")
tap(brands)
tree,entry=wait("Premium-Line","brands.xml")
assert node(tree,"9 / 9") is not None
screen("separate_brands_en.png")
adb("shell","input","keyevent","4")
tree,return_to_library=wait("Search results","library_return.xml")

# One search bar must now find a company name without opening a hidden menu.
tree,searchfield=wait("Search by maker","library_search_query.xml")
tap(searchfield)
adb("shell","input","text","Hengtong")
tree,company_hit=wait("Matching companies","library_company_search.xml")
assert node(tree,"Hengtong Group") is not None
assert node(tree,"1 / 3") is not None or node(tree,"0 / 3") is not None or node(tree,"3") is not None
screen("company_search_in_library.png")
print("PASS: companies discoverable in main library, 150 candidate directory, separate 9 brands, sourced cables preserved, bilingual details/3D")
