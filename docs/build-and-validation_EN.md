[한국어](build-and-validation_KR.md)

# Build and validation

## Import validation (2026-10-29)

- Git remote: `https://github.com/jprapstyle/choose-browser.git`.
- Imported baseline files and added source, resources, tests and wrapper files: 33 changed or added files.
- Byte-for-byte comparison confirmed that all imported files matched the existing local improvement source.
- `assembleAlpha`: passed.
- `assembleAlphaAndroidTest`: passed; this confirms test APK compilation and packaging.
- `lintAlpha`: passed, with 0 errors and 17 warnings.
- Device tests and JVM unit tests were not executed against this fresh clone. Building the test APK does not mean tests passed.
- An earlier work session passed 23 device tests; that historical result is not a new run against this clone.
- Existing Robolectric unit tests have a known compatibility issue with the Java 25 environment and require separate runtime review.
- Installation and GitHub publication were outside the initial import validation. See [Release history](releases_EN.md) for subsequent actions.

## Environment and reproduction

- Windows PowerShell.
- JDK: `C:/Program Files/Android/Android Studio/jbr` (Java 25 runtime, Java 17 source compatibility).
- Android SDK: `C:/Users/enLioN/AppData/Local/Android/Sdk`.
- Gradle wrapper 9.5.0 / AGP 9.3.2 / compile SDK 36.
- `local.properties` is ignored by Git. Configure the Android SDK location on other machines.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat --offline assembleAlpha assembleAlphaAndroidTest lintAlpha --console=plain
```

`--offline` uses locally cached dependencies. Omit it when setting up a machine that must download dependencies.

## Output locations

- App APK: `app/build/outputs/apk/alpha/app-alpha.apk`.
- Device-test APK: `app/build/outputs/apk/androidTest/alpha/app-alpha-androidTest.apk`.
- Lint report: `app/build/reports/lint-results-alpha.html`.
- Local build log: `docs/build-validation.log` (not tracked or available in GitHub).

Use `connectedAlphaAndroidTest` on a separate test device to execute device tests. Check package identities and signing compatibility with installed apps first.

Subsequent installation and publication are recorded in [Release history](releases_EN.md). Build logs remain local and are excluded from Git.
