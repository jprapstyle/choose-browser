# 배포 기록

## 1.3-enlion.1-alpha.1 — 2026-10-29

- Git 태그: `v1.3-enlion.1-alpha.1`
- versionCode: `230013001`
- 패키지: `de.ub0r.android.choosebrowser.alpha`
- 기존 로컬 개선 사항을 사용자 GitHub 포크에 통합한 첫 Enlion 버전입니다.
- URL 비동기 해석, 앱 메타데이터 캐시, 숨긴 앱 관리, 한국어, 화면·생명주기 안정화 및 SDK/빌드 환경 갱신을 포함합니다. 상세 내용은 [적용 내역](local-changes.md)을 참조합니다.
- `assembleAlpha assembleRelease lintAlpha --offline` 성공. Alpha Lint 오류 0건, 경고 17건.
- Galaxy Z Fold5 (SM-F946N)에 `adb install -r`로 업데이트 설치 성공. 기존 앱 데이터 유지. 설치된 버전명과 versionCode를 확인했습니다.
- 이번 배포에서는 기능별 기기 회귀 테스트를 재실행하지 않았습니다.
- Release `1.3-enlion.1`은 빌드만 확인했으며 서명 설정이 없어 미서명 APK입니다. 기기에 설치한 것은 Alpha입니다.
- 소스·문서 커밋과 위 태그를 사용자 GitHub 저장소에 반영하는 배포입니다. 최종 커밋과 태그 상태는 Git 기록에서 확인합니다.
- 로컬 빌드 로그는 `docs/*.log`에 보관하고 Git 추적에서 제외합니다. 문서의 과거 검증 설명은 당시 작업의 기록입니다.

설치 APK: `app/build/outputs/apk/alpha/app-alpha.apk`
