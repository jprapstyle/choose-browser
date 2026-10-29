[English](build-and-validation_EN.md)

# 빌드 및 검증

## 이번 작업 결과 (2026-10-29)

- 새 저장소의 Git 원격은 `https://github.com/jprapstyle/choose-browser.git`입니다.
- GitHub 기준 파일과 작업본의 추가 소스·리소스·테스트·wrapper를 이식했습니다. 33개 파일이 변경 또는 추가되었습니다.
- 이식 범위의 모든 파일을 바이트 단위로 비교하여 기존 `ChooseBrowser_fork`와 일치함을 확인했습니다.
- `assembleAlpha`: 성공
- `assembleAlphaAndroidTest`: 성공 (테스트 APK 컴파일 및 패키징 확인)
- `lintAlpha`: 성공 — 0 errors, 17 warnings
- 이 새 복제본으로 기기 테스트와 JVM 단위 테스트를 실행하지는 않았습니다. 테스트 APK 빌드 성공은 테스트 실행 성공을 의미하지 않습니다.
- 이전 작업에서 기기 테스트 23개를 통과한 이력이 있지만 이번 복제본의 재실행 결과로 취급하지 않습니다.
- 기존 Robolectric 단위 테스트는 Java 25 환경의 호환성 문제가 알려져 있어 별도 실행 환경 검토가 필요합니다.
- APK 설치 및 GitHub push는 이번 작업 범위에 포함하지 않았습니다.

## 환경과 재현 명령

- Windows PowerShell
- JDK: `C:/Program Files/Android/Android Studio/jbr` (Java 25 런타임, 소스 호환 Java 17)
- Android SDK: `C:/Users/enLioN/AppData/Local/Android/Sdk`
- Gradle wrapper 9.5.0 / AGP 9.3.2 / compile SDK 36
- `local.properties`는 로컬 환경 파일이며 `.gitignore`로 제외됩니다. 다른 PC에서는 Android SDK 위치를 설정해야 합니다.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat --offline assembleAlpha assembleAlphaAndroidTest lintAlpha --console=plain
```

`--offline`은 이 PC에 캐시된 의존성을 사용합니다. 최초 환경에서는 옵션을 빼고 의존성을 내려받아야 합니다.

## 결과 위치

- 앱 APK: `app/build/outputs/apk/alpha/app-alpha.apk`
- 기기 테스트 APK: `app/build/outputs/apk/androidTest/alpha/app-alpha-androidTest.apk`
- Lint 상세: `app/build/reports/lint-results-alpha.html`
- 이번 실행 로그: `build-validation.log` (로컬 전용, Git 추적 제외)

기기 테스트를 실행할 경우 별도 테스트 기기에서 `connectedAlphaAndroidTest`를 사용합니다. 기존 설치 앱과 테스트 대상의 패키지·서명을 먼저 확인해야 합니다.

후속 배포 및 설치·GitHub 반영 기록은 [배포 기록](releases_KR.md)을 참조합니다. 로컬 빌드 로그 링크는 이 PC에서만 유효하며 로그 파일은 Git 추적 대상이 아닙니다.
