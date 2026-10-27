# AB Cable Cost V0.4.0 — Engineering Home UI (safe iteration)

## Implemented in this phase
- New home entrypoint, following the user-approved engineering dashboard.
- Original AB gold/navy logo retained.
- Cropped photography from the **user-approved home design** shipped as compressed, offline local artwork (seven tiles), not linked to any manufacturer technical standard.
- Large Cable Library tile, six independent engineering module cards, header search, bottom navigation, settings and language selector.
- Language selector persists the choice: Arabic (AR), English (EN), French (FR), Turkish (TR). **The new home and its menus** switch immediately (with appropriate RTL/LTR direction).
- Real local catalogue search matches the existing technical specification records; tapping a result opens the exact cable model.
- Existing original weight calculator and manufacturer BOM / prices / catalog activities have been preserved and remain accessible.
- Technical modules not built yet are explicitly labeled as in development, without fabricated engineering results.

## Deferred by agreement
- Each engineering module's actual calculators (resistance, loads, current, voltage, cable protection, install and testing) will be specified and implemented separately, one button at a time.
- Deeper technical catalogue/manufacturing screens are inherited Arabic-only in this iteration. They need localization in each upcoming module; this is *not* claimed as complete end-to-end multilingual support yet.
- Broken external manufacturer datasheet URLs must be verified or replaced by reliable current pages during the Catalogue module iteration. External links are not guaranteed offline.
- Exact technical values need validated original manufacturer specifications; generic templates stay visibly non-certified.
- Private, stable production signing is not set up yet. This file is a **debug-signed Android APK**, which may not upgrade prior debug builds with different certificate.

## Safety/verification
- No prior production calculations or stored data schemas were removed.
- No mass edits to the existing calculating engines.
- GitHub Actions builds the APK and Safe Source ZIP and verifies debug signature, then a separate Android 15 emulator smoke test checks the home, logo, AR↔EN switching and catalogue navigation.
