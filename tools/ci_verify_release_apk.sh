#!/usr/bin/env bash
# Checks a stably signed release APK before it is published.
# usage: tools/ci_verify_release_apk.sh path/to/app-release.apk
set -euo pipefail
APK="${1:?usage: ci_verify_release_apk.sh <apk>}"
BT="${ANDROID_HOME:?ANDROID_HOME is not set}/build-tools/35.0.0"
EXPECTED="$(tr -d '[:space:]' < signing/EXPECTED_CERT_SHA256.txt | tr 'A-F' 'a-f')"

SIG="$("$BT/apksigner" verify --verbose --print-certs "$APK")"
echo "$SIG"
for scheme in "v1 scheme (JAR signing)" "v2 scheme (APK Signature Scheme v2)" "v3 scheme (APK Signature Scheme v3)"; do
  grep -Fq "Verified using ${scheme}: true" <<<"$SIG" || { echo "FAIL: not verified using ${scheme}"; exit 1; }
done
ACTUAL="$(sed -n 's/^Signer #1 certificate SHA-256 digest: //p' <<<"$SIG" | tr -d '[:space:]' | tr 'A-F' 'a-f')"
if [ "$ACTUAL" != "$EXPECTED" ]; then
  echo "FAIL: certificate SHA-256 $ACTUAL does not match signing/EXPECTED_CERT_SHA256.txt ($EXPECTED)"
  exit 1
fi
echo "OK: signed with the permanent key (SHA-256 $ACTUAL)"

BADGING="$("$BT/aapt" dump badging "$APK")"
grep -Fq "package: name='com.ab.cablecost.app'" <<<"$BADGING" || { echo "FAIL: unexpected package name"; exit 1; }
grep -Fq "versionName='0.6.2-families'" <<<"$BADGING" || { echo "FAIL: unexpected versionName"; exit 1; }
grep -Fq "targetSdkVersion:'35'" <<<"$BADGING" || { echo "FAIL: unexpected targetSdkVersion"; exit 1; }
if grep -Fq "application-debuggable" <<<"$BADGING"; then echo "FAIL: release APK is debuggable"; exit 1; fi
grep -E "^package:|^sdkVersion|^targetSdkVersion|^application-label:" <<<"$BADGING"

"$BT/zipalign" -c 4 "$APK" && echo "OK: zipalign"
sha256sum "$APK"
ls -l "$APK"
