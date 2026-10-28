#!/usr/bin/env bash
# Runs inside an emulator: fresh install of the stable APK, launch, then an in-place
# update with a higher versionCode signed by the same key. firstInstallTime must be
# unchanged, which proves the update kept the installed app (and therefore its data).
# usage: tools/ci_release_install_test.sh <stable.apk> <upgrade-test.apk>
set -euo pipefail
FIRST_APK="${1:?stable apk}"; UPGRADE_APK="${2:?upgrade test apk}"
PKG=com.ab.cablecost.app
ACT="$PKG/com.ab.cablecost.MainActivity"

field() {   # first dumpsys line matching $1 (no early-closing pipes, so no SIGPIPE under pipefail)
  local out; out="$(adb shell dumpsys package "$PKG" | tr -d '\r' | grep -E "$1" || true)"
  printf '%s\n' "${out%%$'\n'*}" | sed -E 's/^[[:space:]]+//'
}

echo "== fresh install of the stable APK"
adb install "$FIRST_APK"
adb shell pm path "$PKG"
V1="$(field 'versionCode=')"; T1="$(field 'firstInstallTime=')"
adb logcat -c
adb shell am start -W -n "$ACT"
sleep 4
adb shell pidof "$PKG"

echo "== in-place update, same key, no uninstall"
adb install -r "$UPGRADE_APK"
V2="$(field 'versionCode=')"; T2="$(field 'firstInstallTime=')"
echo "before: $V1 | $T1"
echo "after:  $V2 | $T2"
[ -n "$T1" ] || { echo "FAIL: could not read firstInstallTime"; exit 1; }
[ "$T1" = "$T2" ] || { echo "FAIL: firstInstallTime changed, the app was reinstalled"; exit 1; }
[ "$V1" != "$V2" ] || { echo "FAIL: versionCode did not change"; exit 1; }
adb shell am start -W -n "$ACT"
sleep 4
adb shell pidof "$PKG"
if adb logcat -d -b crash | grep -q "$PKG"; then
  echo "FAIL: crash log mentions $PKG"; adb logcat -d -b crash | tail -40; exit 1
fi
echo "PASS: install + launch + in-place update on API $(adb shell getprop ro.build.version.sdk | tr -d '\r')"
