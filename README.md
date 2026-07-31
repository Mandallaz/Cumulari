# Cumulari 📈

**Cumulari** is a native Android application designed to simulate long-term savings growth while accounting for the impact of inflation on purchasing power.

## ✨ Features

- **Compound Interest Calculation:** Simulates monthly compound interest based on initial capital and recurring deposits.
- **Inflation Adjustment:** Calculates the *real value* of future savings adjusted for estimated annual inflation.
- **Interactive Chart:** Custom visual chart built with **Canvas Compose** showing Nominal Value, Real Value, and Total Invested.
- **Localized:** Full support for English and French.

## 🌍 External APIs & Data Sources

This application integrates the **World Bank Open Data REST API** to automatically retrieve up-to-date inflation rates per country:

* **API Endpoint:** `http://api.worldbank.org/v2/country/{country_code}/indicator/FP.CPI.TOTL.ZG?format=json`
* **Features:**
   * Dynamic fetch of the most recent annual inflation rate based on selected country (defaults to Euro Area / `EMU`).
   * Automatic fallback values if offline or if data is unavailable.

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) with `StateFlow`
- **Graphics:** Custom rendering using `Canvas`

## 🚀 Getting Started

### Prerequisites
- Android Studio Jellyfish or newer
- JDK 17+
- Android SDK 24+ (Android 7.0)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Mandallaz/Cumulari.git
   cd Cumulari
   ```

## 📄 License

This project is licensed under the [GNU General Public License v3.0](LICENSE) — you're free to use, study, modify, and redistribute this code, provided that any derivative work is also released under GPLv3 and its source made available.⏎     
