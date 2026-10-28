# Stable release signing (V0.5.5+)

Android only lets an APK update an installed app when both are signed with the **same key**.
Before V0.5.5 every GitHub run created a new random *debug* key, so no build could update the
previous one. From V0.5.5 the **release** build is signed with one permanent key.

* Release app id: `com.ab.cablecost.app` (never used before, installs beside older copies).
* The key (`ab-cable-cost-release.jks`) is **not** in this repository and must never be committed.
* `EXPECTED_CERT_SHA256.txt` is the public SHA-256 fingerprint of that key's certificate. The
  workflow refuses to publish a release APK whose certificate does not match it.

## One-time GitHub setup

Repository -> Settings -> Secrets and variables -> Actions -> New repository secret:

| Secret name           | Value                                                    |
|-----------------------|----------------------------------------------------------|
| `AB_KEYSTORE_BASE64`  | contents of `ab-cable-cost-release.jks.b64` (one line)   |
| `AB_KEYSTORE_PASSWORD`| store password (same as key password)                    |
| `AB_KEY_ALIAS`        | `ab-cable-cost`                                          |
| `AB_KEY_PASSWORD`     | key password (same as store password)                    |

Without these secrets the workflow still builds and tests the debug APKs, skips the release
job and says so in the log.

## Local build (optional)

```bash
export AB_KEYSTORE_FILE=/safe/place/ab-cable-cost-release.jks
export AB_KEYSTORE_PASSWORD=...  AB_KEY_ALIAS=ab-cable-cost  AB_KEY_PASSWORD=...
gradle :app:assembleRelease
$ANDROID_HOME/build-tools/35.0.0/apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk
```

## Back the key up

Keep at least two copies (password manager + offline drive). **If the key is lost no future
build can update installed copies** and a new app id would be required again.
