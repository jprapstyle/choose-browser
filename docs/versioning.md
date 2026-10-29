# 버전 규칙

2026-10-29 지정. URLCheck 채팅에서 채택한 Enlion 포크 버전 규칙을 동일하게 적용합니다.

## 표기 및 증가 기준

- 정식: `원본버전-enlion.수정번호`
- Alpha: `원본버전-enlion.수정번호-alpha.테스트번호`
- Debug: `원본버전-enlion.수정번호-debug`
- 원본버전은 실제로 통합한 원본의 버전을 따릅니다. 현재 기준은 원본 1.3입니다.
- 수정번호는 자체 정식 배포마다 증가시킵니다. 기능명을 버전에 붙이지 않습니다.
- 같은 자체 버전의 Alpha 배포마다 테스트번호를 증가시킵니다.
- 원본 새 버전을 통합하면 수정번호는 1부터 시작합니다. 예: `1.4-enlion.1`.
- Android `versionCode`는 같은 패키지에 새 APK를 배포할 때마다 계속 증가시킵니다. 원본버전·수정번호·테스트번호가 바뀌어도 초기화하지 않습니다.
- 단순 재빌드만으로 번호를 자동 증가시키지 않습니다. 배포할 소스에서 명시적으로 변경합니다.
- 서로 다른 패키지는 같은 versionCode를 사용할 수 있습니다. 동일 패키지·동일 서명으로 업데이트하려면 이전 배포보다 큰 코드를 사용합니다.

## 현재 설정

| 빌드 | versionName | versionCode | applicationId |
|---|---|---|---|
| release | `1.3-enlion.1` | 230013001 | `de.ub0r.android.choosebrowser` |
| alpha | `1.3-enlion.1-alpha.1` | 230013001 | `de.ub0r.android.choosebrowser.alpha` |
| debug | `1.3-enlion.1-debug` | 230013001 | `de.ub0r.android.choosebrowser` |

기존 Alpha `1.3-ALPHA-perf1` / 230013000에서 코드 1을 올렸습니다. 패키지명은 유지했습니다.
Debug와 Release는 같은 패키지이며 서명이 다르면 서로 덮어쓸 수 없습니다. 정식 서명 설정은 별도 관리합니다.

## 다음 배포 예시

- 다음 Alpha: `1.3-enlion.1-alpha.2`, 코드 230013002
- 이후 자체 정식 배포: `1.3-enlion.2`, 그 시점에 해당 패키지의 마지막 배포보다 큰 코드
- 원본 1.4 통합: `1.4-enlion.1`, versionCode는 기존 최댓값 이후로 계속 증가

Git 태그는 정식 `v1.3-enlion.1`, Alpha `v1.3-enlion.1-alpha.1` 형식을 사용합니다. 태그는 실제 배포 소스 커밋에 부여하며 이번 규칙 지정만으로 생성·푸시하지 않습니다.

설정 위치는 `app/build.gradle`의 `defaultConfig.versionName`, `versionCode`와 `buildTypes.alpha.versionNameSuffix`입니다. APK와 앱 정보에서 확인할 수 있으며 버전 이름의 정렬 대신 versionCode로 Android 업데이트 순서가 결정됩니다.

## 적용 확인

`assembleAlpha assembleRelease --offline` 빌드 성공. 두 APK의 output metadata에서 위 버전명·코드를 확인했습니다. Release는 서명 설정이 없어 `app-release-unsigned.apk`로 생성됩니다. 설치·커밋·태그 생성·푸시는 수행하지 않았습니다. 빌드 로그는 [version-build.log](version-build.log)에 기록했습니다.

후속 배포 및 설치·GitHub 반영 기록은 [배포 기록](releases.md)을 참조합니다. 로컬 빌드 로그 링크는 이 PC에서만 유효하며 로그 파일은 Git 추적 대상이 아닙니다.
