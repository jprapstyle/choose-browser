[한국어](versioning_KR.md)

# Versioning

Established on 2026-10-29 using the Enlion fork convention adopted in the URLCheck conversation.

## Names and increment rules

- Release: `upstreamVersion-enlion.revision`.
- Alpha: `upstreamVersion-enlion.revision-alpha.testNumber`.
- Debug: `upstreamVersion-enlion.revision-debug`.
- The upstream version reflects the version actually integrated, currently 1.3.
- Increment the Enlion revision for each custom release. Do not use feature names as version suffixes.
- Increment the Alpha test number for each test deployment of the same custom version.
- Restart the revision at 1 when integrating a new upstream version, for example `1.4-enlion.1`.
- Android `versionCode` increases for every new APK deployment to the same package. Never reset it when the upstream version, revision or test number changes.
- Rebuilding alone does not automatically increment versions. Set them explicitly in the source intended for deployment.
- Different packages can share a versionCode. An update to the same package and signature must use a higher code than the previous deployment.

## Current configuration

| Build | versionName | versionCode | applicationId |
|---|---|---|---|
| release | `1.3-enlion.1` | 230013001 | `de.ub0r.android.choosebrowser` |
| alpha | `1.3-enlion.1-alpha.1` | 230013001 | `de.ub0r.android.choosebrowser.alpha` |
| debug | `1.3-enlion.1-debug` | 230013001 | `de.ub0r.android.choosebrowser` |

The code was incremented from the previous Alpha `1.3-ALPHA-perf1` / 230013000. Package names were retained.
Debug and Release share a package and cannot overwrite each other if their signatures differ. Release signing is managed separately.

## Next deployment examples

- Next Alpha: `1.3-enlion.1-alpha.2`, code 230013002.
- Next custom release: `1.3-enlion.2`, with a code greater than the package's latest deployment.
- After upstream 1.4 integration: `1.4-enlion.1`, continuing versionCode above the previous maximum.

Tag naming conventions are `v1.3-enlion.1` for release and `v1.3-enlion.1-alpha.1` for Alpha. The current repository tag is `v1.3-enlion.1`; the previous Alpha tag was removed. At the user's request, the release tag is reassigned to the 2026-10-30 documentation commit.

Configuration lives in `app/build.gradle`: `defaultConfig.versionName`, `versionCode` and `buildTypes.alpha.versionNameSuffix`. Android determines update order from versionCode, not lexicographic versionName ordering.

## Verification

`assembleAlpha assembleRelease --offline` passed. APK output metadata confirmed the names and codes above. Release signing is not configured, so the release output is `app-release-unsigned.apk`. These initial versioning checks did not themselves install or publish a build. Local log: `docs/version-build.log` (excluded from Git).

See [Release history](releases_EN.md) for subsequent installation and publication.
