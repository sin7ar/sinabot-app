# SINABOT Android shell

A tiny Android app: a full-screen WebView that loads the SINABOT dashboard
(`http://76.13.78.123:5000`). Built in the cloud by GitHub Actions.

Phase 1: dashboard only (no volume button yet).

The APK is produced automatically on every push to `main`:
- Actions tab -> latest run -> artifact `sinabot-apk`, or
- Releases -> latest -> `app-debug.apk`
