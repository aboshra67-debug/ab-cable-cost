#!/usr/bin/env python3
"""End-to-end smoke test of approved home, original AB logo and cable catalogue.

Run inside Android 15 emulator after installing the APK.
Never export a private Android Activity to make tests pass.
"""
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

Path("screenshots").mkdir(exist_ok=True)

def adb(*args):
    return subprocess.check_output(["adb", *args], text=True, stderr=subprocess.STDOUT)

def dump(name):
    adb("shell","uiautomator","dump","/sdcard/window.xml")
    subprocess.check_call(["adb","pull","/sdcard/window.xml",str(name)])
    return ET.parse(name).getroot()

def find_node(root, token, field="text"):
    for node in root.iter("node"):
        if token in node.attrib.get(field,""):
            return node
    return None

def screenshot(name):
    adb("shell","screencap","-p","/sdcard/Download/ab_image.png")
    subprocess.check_call(["adb","pull","/sdcard/Download/ab_image.png","screenshots/"+name])

def tap(node):
    bounds=node.attrib.get("bounds","")
    m=re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]",bounds)
    if not m:
        raise RuntimeError("Invalid control bounds: "+bounds)
    x1,y1,x2,y2=map(int,m.groups())
    adb("shell","input","tap",str((x1+x2)//2),str((y1+y2)//2))

home=None
for _ in range(10):
    home=dump("screenshots/home.xml")
    if find_node(home,"ابدأ حساب الكابل"):
        break
    time.sleep(2)
screenshot("home.png")
print("HOME NODES:",[(n.attrib.get("class",""),n.attrib.get("text",""),
                      n.attrib.get("content-desc","")) for n in home.iter("node")][:80])
assert find_node(home,"AB Cable Cost"),"Brand title missing from home"
assert find_node(home,"شعار AB Cable Cost الأصلي","content-desc"),"Approved AB logo missing"
open_button=find_node(home,"ابدأ حساب الكابل")
assert open_button is not None,"Main hero catalogue action missing"
tap(open_button)
time.sleep(3)
assert adb("shell","pidof","com.ab.cablecost").strip(),"App process exited"
catalog=dump("screenshots/catalog.xml")
assert find_node(catalog,"اختار الكابل والمقاس"),"Catalogue selection card missing"
assert find_node(catalog,"أسعار الخامات المركزية"),"Central prices shortcut missing"
assert find_node(catalog,"شعار AB Cable Cost الأصلي","content-desc"),"Catalogue logo missing"
screenshot("catalog.png")
print("PASS: Approved AB logo visible, home action works, catalogue/price controls open on Android 15")
