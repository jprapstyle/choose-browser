# 로컬 개선 사항 적용 내역

## 기준과 적용 범위

- 적용일: 2026-10-29 (한국 시간)
- 저장소: https://github.com/jprapstyle/choose-browser
- 원본: https://github.com/felixb/choose-browser
- 복제 기준: `fa01bd9e3e5dd254562eddaaa576868b3bd53fc0` (`prepare release 1.3`, 2022-02-04)
- 이식한 작업본: `C:/Users/enLioN/Personal_Project/ChooseBrowser_fork`
- 대상: `C:/Users/enLioN/Personal_Project/choose-browser`
- 비교 당시 두 GitHub master와 기존 로컬 원본의 HEAD는 동일합니다. 이후 원본 업데이트를 병합한 작업은 아닙니다.
- 기존 파일과 추가된 소스·리소스·테스트 및 Gradle wrapper를 반영했습니다. 빌드 산출물, `.gradle`, `work`의 임시 스크립트·기기 백업·로그는 이식하지 않았습니다.
- `local.properties`는 이 PC의 빌드용으로만 복사했으며 Git 추적 대상이 아닙니다.

## 기능 및 화면

- 숨긴 앱 목록과 설정 화면, SharedPreferences 저장 기능을 추가했습니다.
- 목록에서 자기 자신, 비활성화되었거나 외부 실행 불가한 앱을 제외합니다. 나머지 앱이 모두 숨김 상태이면 기존의 전체 목록 복귀 동작을 유지합니다.
- 한국어 설정·안내와 로딩 중/결과 없음/실패 안내를 추가했습니다.
- 선택 화면이 최근 앱 목록에 남지 않도록 태스크 및 종료 동작을 정리했습니다.
- 중복 Fragment 생성 방지, 화면 여백 및 세로 방향 대응을 포함합니다.
- 기존 도메인별 선택 기억하기 기능을 유지하고, 리디렉션 해석 후 도착 URL의 기억된 앱을 확인합니다.

## 성능 및 안정성

- URL의 로컬 파싱과 단축 URL 네트워크 해석을 분리했습니다. 메인 스레드의 네트워크 해석을 금지합니다.
- 단축 URL 요청에 3초 call timeout, 취소, 응답 닫기, 상대 Location 해석 및 실패 시 입력 URL 유지 처리를 적용했습니다. 네트워크 해석은 한 단계 리디렉션입니다.
- HTTP/HTTPS와 호스트를 검증하고 Google 리디렉션의 호스트 경계를 확인합니다. null 텍스트와 ClipData 대체 입력을 처리합니다.
- 앱 조회·이름·아이콘 로딩을 작업 스레드로 이동하고 48개 메타데이터 캐시를 추가했습니다. 앱 설치·삭제·변경 시 캐시를 비웁니다.
- 화면 종료 시 작업을 취소하고 세대 번호로 오래된 결과의 UI 반영을 차단합니다.
- 연속 실행 방지, 실행 불가·권한 예외 처리 및 기억된 앱 실패 후 복구 처리를 보강했습니다.
- `ChooserPerf` 로그로 목록 준비 소요 시간을 기록합니다. 실제 속도는 기기와 URL 유형에 따라 다릅니다.

## 빌드 및 테스트

- compile/target SDK: 31 → 36, min SDK: 23 유지
- Java 17, Android Gradle Plugin 9.3.2, Gradle 9.5.0
- alpha applicationId: `de.ub0r.android.choosebrowser.alpha`
- alpha 표시 버전: `1.3-enlion.1-alpha.1` (`versionCode` 230013001)
- 정식 버전: `1.3-enlion.1`. [버전 규칙](versioning.md)을 따릅니다. 이전 이식 당시에는 `1.3-ALPHA-perf1` / 230013000이었습니다.
- 기존 debug/release applicationId는 `de.ub0r.android.choosebrowser`입니다.
- URL 파싱·리디렉션·취소·필터 관련 회귀 테스트와 기존 기기 테스트 수정을 반영했습니다.
- 누락 번역 Lint 검사는 기존 작업본처럼 비활성화되어 있으며 모든 언어의 완전 번역을 의미하지 않습니다.

## 변경 파일

- 수정: `app/build.gradle`
- 추가: `app/src/androidTest/java/de/ub0r/android/choosebrowser/ChooserRegressionTest.java`
- 수정: `app/src/androidTest/java/de/ub0r/android/choosebrowser/IntentParserInstrumentedTest.java`
- 수정: `app/src/androidTest/java/de/ub0r/android/choosebrowser/PreferredAppsStoreInstrumentedTest.java`
- 수정: `app/src/main/AndroidManifest.xml`
- 추가: `app/src/main/java/de/ub0r/android/choosebrowser/BrowserCatalog.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserActivity.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserAdapter.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserFragment.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/ChooserFullscreenActivity.java`
- 추가: `app/src/main/java/de/ub0r/android/choosebrowser/EdgeToEdge.java`
- 추가: `app/src/main/java/de/ub0r/android/choosebrowser/HiddenAppsActivity.java`
- 추가: `app/src/main/java/de/ub0r/android/choosebrowser/HiddenAppsAdapter.java`
- 추가: `app/src/main/java/de/ub0r/android/choosebrowser/HiddenAppsStore.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/IntentParser.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/PreferredAppsActivity.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/PreferredAppsAdapter.java`
- 수정: `app/src/main/java/de/ub0r/android/choosebrowser/SettingsActivity.java`
- 추가: `app/src/main/res/layout/activity_hidden_apps.xml`
- 수정: `app/src/main/res/layout/fragment_chooser.xml`
- 추가: `app/src/main/res/layout/item_hidden_app.xml`
- 수정: `app/src/main/res/menu/activity_chooser.xml`
- 수정: `app/src/main/res/values-de/strings.xml`
- 추가: `app/src/main/res/values-ko/strings.xml`
- 수정: `app/src/main/res/values/strings.xml`
- 수정: `app/src/main/res/xml/pref_general.xml`
- 수정: `app/src/test/java/de/ub0r/android/choosebrowser/IntentParserTest.java`
- 수정: `build.gradle`
- 수정: `gradle.properties`
- 수정: `gradle/wrapper/gradle-wrapper.jar`
- 수정: `gradle/wrapper/gradle-wrapper.properties`
- 수정: `gradlew`
- 수정: `gradlew.bat`
