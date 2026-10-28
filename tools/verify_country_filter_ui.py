#!/usr/bin/env python3
"""Advisory emulator test (V0.5.5): the Country filter lists the 33 directory countries.

Runs after tools/verify_library_ui.py (app language is already English). It opens the
Global Cable Library, picks Australia and checks that its 3 pending companies appear
while no verified cable is invented for that country, then picks Austria (2 companies).
Package is passed as argv[1] (default com.ab.cablecost).
"""
from pathlib import Path
import re
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

PKG = sys.argv[1] if len(sys.argv) > 1 else "com.ab.cablecost"
OUT = Path("screenshots"); OUT.mkdir(exist_ok=True)

def adb(*args):
    return subprocess.check_output(["adb", *args], text=True, stderr=subprocess.STDOUT)

def dump(name):
    adb("shell", "uiautomator", "dump", "/sdcard/ui_country.xml")
    subprocess.check_call(["adb", "pull", "/sdcard/ui_country.xml", str(OUT / name)])
    return ET.parse(OUT / name).getroot()

def find(tree, term, field="text", exact=False):
    for x in tree.iter("node"):
        value = x.attrib.get(field, "")
        if (value == term) if exact else (term in value):
            return x
    return None

def tap(node):
    m = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", node.attrib.get("bounds", ""))
    assert m, "missing bounds"
    a, b, c, d = map(int, m.groups())
    adb("shell", "input", "tap", str((a + c) // 2), str((b + d) // 2))

def wait(term, name, field="text", exact=False, tries=8):
    for _ in range(tries):
        tree = dump(name)
        node = find(tree, term, field, exact)
        if node is not None:
            return tree, node
        time.sleep(1)
    raise AssertionError("Expected screen text missing: " + term)

def screen(name):
    adb("shell", "screencap", "-p", "/sdcard/pic_country.png")
    subprocess.check_call(["adb", "pull", "/sdcard/pic_country.png", str(OUT / name)])

adb("shell", "am", "force-stop", PKG)
adb("shell", "am", "start", "-W", "-n", PKG + "/com.ab.cablecost.MainActivity")
time.sleep(2)
tree, library = wait("Cable Library", "country_home.xml", exact=True)
tap(library)
tree, chip = wait("Country ⌄", "country_library.xml", field="content-desc")
tap(chip)
tree, australia = wait("Australia", "country_picker.xml", exact=True)
screen("country_picker_en.png")
tap(australia)
tree, _ = wait("Matching companies (3)", "country_australia.xml")
assert find(tree, "No verified matching cables") is not None, "no verified cable may be shown for Australia"
screen("country_australia_en.png")
tree, chip = wait("Country · Australia ⌄", "country_selected.xml", field="content-desc")
tap(chip)
tree, austria = wait("Austria", "country_picker_again.xml", exact=True)
tap(austria)
wait("Matching companies (2)", "country_austria.xml")
print("PASS: Country filter lists directory countries and shows their pending companies without inventing cable records")
