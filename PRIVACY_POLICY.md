# Privacy Policy — Cumulari

_Last updated: 2026-07-28_

Cumulari ("the app") is a savings and inflation simulator. This policy explains what data the app does and does not handle.

## Data collection

Cumulari does **not** collect, store, or transmit any personal data. Specifically, the app:

- Does not require an account, login, or any form of registration.
- Does not collect names, email addresses, location, device identifiers, or any other personally identifiable information.
- Does not use analytics, crash reporting, or advertising SDKs.
- Does not use cookies or any local tracking mechanism.

All values you enter (initial capital, monthly contribution, return rate, inflation rate, duration) stay on your device, in memory, for the duration of your session. Nothing is saved to disk or sent anywhere.

## Network access

The app makes outbound HTTPS requests to the **World Bank Open Data API** (`api.worldbank.org`) to fetch publicly available, non-personal inflation-rate statistics for the country you select. This request contains only the selected country code — no data about you or your device is included, and no response is stored beyond the current session.

The World Bank's own data usage terms apply to this public API and are outside Cumulari's control.

## Permissions

The app requests a single permission, `INTERNET`, solely to perform the network request described above. No other device permission is requested.

## Children's privacy

Cumulari does not knowingly collect data from anyone, including children, because it does not collect data from any user.

## Changes to this policy

If this policy changes (for example, if a future version adds a new feature that involves data handling), this document will be updated and the "Last updated" date above will reflect the change.

## Contact

For any question about this policy, open an issue on the project's repository: <https://github.com/Mandallaz/Cumulari>.
