# Google Play publication checklist

Everything below the line was done in code/repo. Everything above requires
manual action in the Play Console (no API access from here) or a decision
only you can make.

## Manual steps — Play Console

### Data safety form
Answers to declare, based on the current codebase:
- **Does your app collect or share any of the required user data types?** No.
- **Is all user data encrypted in transit?** N/A (no user data collected). The one network call — fetching inflation rates — uses HTTPS.
- **Do you provide a way for users to request data deletion?** N/A — nothing is collected or stored.
- **Data types collected**: none.

### Privacy policy
- Host `PRIVACY_POLICY.md` (already drafted in the repo) at a public URL and paste that URL into the Play Console's "Privacy policy" field.
- Simplest option: enable GitHub Pages for `Mandallaz/Cumulari` and point it at this file — ask me and I'll set it up, since publishing a public page needs your explicit go-ahead.

### Content rating questionnaire
- Category: likely **Finance** or **Tools**, no gambling/loans/violence content → should land in "Everyone" / "PEGI 3".
- Answer "No" to all sensitive-content questions (no violence, no user-generated content, no gambling, no in-app purchases).

### Store listing assets
- App icon: `store-assets/play-store-icon-512.png` (512×512, generated from the new in-app icon) — ready to upload.
- Still needed from you: a feature graphic (1024×500), at least 2 phone screenshots, short description (≤80 chars) and full description (≤4000 chars).

### Developer account
- One-time 25 USD Google Play Developer registration if not already done.

### Declarations
- "Financial Services" declaration: Cumulari is a simulator/calculator, not a lender or investment product — should not require the special financial-services declaration, but double-check the questionnaire wording when you get there since it can change.

---

## Done in code (this branch)

- [x] Custom app icon (adaptive icon: gradient blue background + white ascending bar-chart glyph, legacy raster mipmaps regenerated for API < 26)
- [x] Release signing config wired up (`app/build.gradle.kts` reads `keystore.properties`, which is gitignored)
- [x] Upload keystore generated at `keystore/cumulari-upload.jks` (gitignored — **back this up outside the repo, losing it before your first upload means starting over with a new app listing**)
- [x] R8 minification enabled for `release` builds (`optimization { enable = true }`)
- [x] Gson keep rule added for `InflationRecord` so R8 doesn't break JSON parsing
- [x] `PRIVACY_POLICY.md` drafted
- [x] Verified: `./gradlew assembleRelease` produces a signed, minified APK

## Not done — needs your input

- Store listing copy (descriptions, screenshots, feature graphic)
- Hosting/publishing the privacy policy at a public URL
- Play Console data safety form and content rating questionnaire (manual, in-console)
- Developer account registration
