#!/usr/bin/env python3
"""Real Android 15 UI smoke test for the approved engineering home.

Only assert implemented capabilities. Test switches AR -> EN -> AR and opens
the existing library from the functional home tile. Screenshots are taken
from the emulator, not mockups.
"""
import re
import subprocess
import time
from pathlib import Path
from xml.etree import ElementTree as ET

OUT = Path("screenshots")
OUT.mkdir(exist_ok=True)

def adb(*parts):
    return subprocess.check_output(["adb", *parts], text=True, stderr=subprocess.STDOUT)

def snapshot(name):
    adb("shell","screencap","-p","/sdcard/Download/ab_home_test.png")
    subprocess.check_call(["adb","pull","/sdcard/Download/ab_home_test.png",str(OUT/name)])

def dump(filename):
    adb("shell","uiautomator","dump","/sdcard/ab_view.xml")
    subprocess.check_call(["adb","pull","/sdcard/ab_view.xml",str(OUT/filename)])
    return ET.parse(OUT/filename).getroot()

def find(root, term, attr="text"):
    for node in root.iter("node"):
        if term in node.attrib.get(attr,""):
            return node
    return None

def wait_for(term, filename, attr="text", tries=8):
    for _ in range(tries):
        tree=dump(filename)
        # Fresh Google API emulator may show a Pixel Launcher ANR dialog even
        # when our app's Activity has started and its process is healthy.
        if find(tree,"Pixel Launcher isn't responding") is not None:
            close=find(tree,"Close app")
            if close is not None:
                tap(close)
                time.sleep(2)
                adb("shell","am","start","-n","com.ab.cablecost/.MainActivity")
                time.sleep(2)
                continue
        node=find(tree,term,attr)
        if node is not None:
            return tree,node
        time.sleep(1.2)
    raise AssertionError("Missing UI element: "+term)

def tap(node):
    bounds=node.attrib.get("bounds","")
    match=re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]",bounds)
    assert match, "No clickable bounds: "+bounds
    a,b,c,d=map(int,match.groups())
    adb("shell","input","tap",str((a+c)//2),str((b+d)//2))

tree,library=wait_for("مكتبة الكابلات","home_ar.xml")
assert find(tree,"AB Cable Cost") is not None, "Brand text missing"
assert find(tree,"AB Cable Cost original logo","content-desc") is not None,"Logo image missing"
assert find(tree,"ابحث عن كابل") is not None,"Search control missing"
assert find(tree,"اختيار الكابل") is not None,"Selection card missing"
assert find(tree,"الحسابات الكهربائية") is not None,"Electrical calculation card missing"
assert find(tree,"الرئيسية") is not None,"Home tab missing"
snapshot("engineering_home_ar.png")

tree,lang_button=wait_for("اللغة","language_launch.xml","content-desc")
tap(lang_button)
tree,english=wait_for("English","language_picker.xml")
tap(english)
tree,lib_en=wait_for("Cable Library","home_en.xml")
assert find(tree,"Electrical Calculations") is not None, "EN translations not applied"
snapshot("engineering_home_en.png")

tree,lang_button=wait_for("Language","language_reopen.xml","content-desc")
tap(lang_button)
tree,arabic=wait_for("العربية — Arabic","language_ar.xml")
tap(arabic)
tree,library=wait_for("مكتبة الكابلات","home_ar_restored.xml")
tap(library)
tree,catalog=wait_for("اختار الكابل والمقاس","catalog.xml")
assert find(tree,"أسعار الخامات المركزية") is not None,"Existing price shortcut missing"
snapshot("catalog.png")
print("PASS: Android installed/launched, original logo present, AR+EN UI switch, "
      "functional library navigation, prior catalogue retained")
