#!/usr/bin/env python3
"""Navigate to the catalogue through the user-visible home button.
Does not export the private CatalogActivity to other applications.
"""
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

Path("screenshots").mkdir(exist_ok=True)

def adb(*args):
    return subprocess.check_output(["adb", *args], text=True, stderr=subprocess.STDOUT)

def dump(path):
    adb("shell", "uiautomator", "dump", "/sdcard/window.xml")
    subprocess.check_call(["adb", "pull", "/sdcard/window.xml", str(path)])
    return ET.parse(path).getroot()

home = dump("screenshots/home.xml")
button = None
for node in home.iter("node"):
    txt = node.attrib.get("text", "")
    if "دليل الكابلات والمقاسات وتكلفة التصنيع" in txt:
        button = node
        break
if button is None:
    raise RuntimeError("Home screen catalogue button not found")
bounds = button.attrib.get("bounds", "")
m = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", bounds)
if not m:
    raise RuntimeError("Invalid home button bounds: " + bounds)
x1,y1,x2,y2 = map(int,m.groups())
adb("shell","input","tap",str((x1+x2)//2),str((y1+y2)//2))
time.sleep(3)
catalog = dump("screenshots/catalog.xml")
texts = " ".join(node.attrib.get("text", "") for node in catalog.iter("node"))
if "اختار الكابل" not in texts and "اختار الكابل والمقاس" not in texts:
    raise RuntimeError("Catalogue did not become visible after opening")
if "أسعار الخامات المركزية" not in texts:
    raise RuntimeError("Material pricing shortcut missing")
adb("shell", "screencap", "-p", "/sdcard/Download/ab_catalog_ui.png")
subprocess.check_call(["adb","pull","/sdcard/Download/ab_catalog_ui.png","screenshots/catalog.png"])
print("PASS: MainActivity to CatalogActivity via UI, catalogue controls visible")
