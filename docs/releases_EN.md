[한국어](releases_KR.md)

# Release history

## 1.3-enlion.1-alpha.1 — 2026-10-29

- Repository tag: `v1.3-enlion.1`.
- versionCode: `230013001`.
- Installed package: `de.ub0r.android.choosebrowser.alpha`.
- First Enlion version integrating the existing local improvements into the user's GitHub fork.
- Includes asynchronous URL resolution, app metadata caching, hidden-app management, Korean translations, UI/lifecycle improvements and SDK/build updates. See [Changes](local-changes_EN.md).
- `assembleAlpha assembleRelease lintAlpha --offline` passed. Alpha Lint: 0 errors, 17 warnings.
- Updated Galaxy Z Fold5 (SM-F946N) using `adb install -r`, preserving app data. Verified the installed versionName and versionCode.
- Feature-specific device regression tests were not rerun for this deployment.
- Release `1.3-enlion.1` was built but remains unsigned because release signing is not configured. The device received the Alpha APK.
- Source and documentation were committed and pushed to the user's GitHub repository. Git history records the final commit and tag state.
- Local logs under `docs/*.log` are excluded from Git. Earlier validation descriptions reflect the work performed at those times.

Installed APK: `app/build/outputs/apk/alpha/app-alpha.apk`.

## Documentation update — 2026-10-30

Added `_KR` Korean and `_EN` English documents and removed the two local import path entries. The previous Alpha tag was deleted. The existing `v1.3-enlion.1` tag is reassigned to this documentation commit at the user's request. App code and APK versions are unchanged.
