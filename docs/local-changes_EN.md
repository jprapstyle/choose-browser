[한국어](local-changes_KR.md)

# Local improvements

## Baseline and scope

- Applied: 2026-10-29 (Korea Standard Time).
- Repository: https://github.com/jprapstyle/choose-browser
- Upstream: https://github.com/felixb/choose-browser
- Baseline: `fa01bd9e3e5dd254562eddaaa576868b3bd53fc0` (`prepare release 1.3`, 2022-02-04).
- At comparison time, both GitHub master branches and the original local HEAD matched. This was not a merge of newer upstream changes.
- Existing changes and added source, resources, tests and Gradle wrapper files were imported. Build outputs, `.gradle`, and temporary scripts, device backups and logs under `work` were excluded.
- `local.properties` was copied for local builds only and is not tracked by Git.

## Features and UI

- Added hidden-app management screens and SharedPreferences persistence.
- Excluded the chooser itself, disabled apps and activities unavailable for external launch. If all remaining apps are hidden, the existing fallback shows the available apps again.
- Added Korean settings and guidance, plus loading, empty-result and failure messages.
- Adjusted task and exit behavior so the chooser does not linger in Recents.
- Included duplicate Fragment prevention, system-inset handling and portrait behavior.
- Preserved remembered choices per domain and checked the destination URL's preferred app after redirect resolution.

## Performance and stability

- Separated local URL parsing from network resolution of shortened links. Network resolution is prohibited on the main thread.
- Added a three-second call timeout, cancellation, response closing, relative Location resolution and preservation of the input URL on failure. Network resolution follows one redirect step.
- Validated HTTP/HTTPS URLs and hosts, checked Google redirect host boundaries and handled null text with ClipData fallback.
- Moved app queries, labels and icons to workers and added a 48-entry metadata cache. Package installation, removal or changes invalidate the cache.
- Cancelled work when views are destroyed and used generation IDs to reject stale UI results.
- Prevented repeated launches, handled missing-app/security exceptions and recovered from failed remembered-app launches.
- Added `ChooserPerf` logs for list preparation time. Actual performance depends on the device and URL type.

## Build and tests

- compile/target SDK: 31 to 36; min SDK remains 23.
- Java 17 source compatibility, Android Gradle Plugin 9.3.2 and Gradle 9.5.0.
- Alpha applicationId: `de.ub0r.android.choosebrowser.alpha`.
- Alpha version: `1.3-enlion.1-alpha.1`; versionCode: 230013001.
- Release version: `1.3-enlion.1`; see [Versioning](versioning_EN.md). The initial imported build used `1.3-ALPHA-perf1` / 230013000.
- Debug/release applicationId remains `de.ub0r.android.choosebrowser`.
- Imported regression tests for parsing, redirects, cancellation and filtering, and adjustments to existing device tests.
- MissingTranslation Lint checks remain disabled as in the local source; this does not imply complete translation coverage.

## Changed files


- Modified: `app/build.gradle`
- Added: `app/src/androidTest/java/de/ub0r/android/choosebrowser/ChooserRegressionTest.java`
- Modified: `app/src/androidTest/java/de/ub0r/android/choosebrowser/IntentParserInstrumentedTest.java`
- Modified: `app/src/androidTest/java/de/ub0r/android/choosebrowser/PreferredAppsStoreInstrumentedTest.java`
- Modified: `app/src/main/AndroidManifest.xml`
- Added: `app/src/main/java/de/ub0r/android/choosebrowser/BrowserCatalog.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserActivity.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserAdapter.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserFragment.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserFullscreenActivity.java`
- Added: `app/src/main/java/de/ub0r/android/choosebrowser/EdgeToEdge.java`
- Added: `app/src/main/java/de/ub0r/android/choosebrowser/HiddenAppsActivity.java`
- Added: `app/src/main/java/de/ub0r/android/choosebrowser/HiddenAppsAdapter.java`
- Added: `app/src/main/java/de/ub0r/android/choosebrowser/HiddenAppsStore.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/IntentParser.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/PreferredAppsActivity.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/PreferredAppsAdapter.java`
- Modified: `app/src/main/java/de/ub0r/android/choosebrowser/SettingsActivity.java`
- Added: `app/src/main/res/layout/activity_hidden_apps.xml`
- Modified: `app/src/main/res/layout/fragment_chooser.xml`
- Added: `app/src/main/res/layout/item_hidden_app.xml`
- Modified: `app/src/main/res/menu/activity_chooser.xml`
- Modified: `app/src/main/res/values-de/strings.xml`
- Added: `app/src/main/res/values-ko/strings.xml`
- Modified: `app/src/main/res/values/strings.xml`
- Modified: `app/src/main/res/xml/pref_general.xml`
- Modified: `app/src/test/java/de/ub0r/android/choosebrowser/IntentParserTest.java`
- Modified: `build.gradle`
- Modified: `gradle.properties`
- Modified: `gradle/wrapper/gradle-wrapper.jar`
- Modified: `gradle/wrapper/gradle-wrapper.properties`
- Modified: `gradlew`
- Modified: `gradlew.bat`
