# iForward modernized Android project

This is a new Android Studio / Gradle project based on the extracted ADT source. It retains the existing main screens and SQLite database classes, removes the first-launch tutorial, and replaces the hidden `ITelephony.endCall()` receiver with `CallScreeningService`.

## Behavior

- Requires Android 10 (API 29) or newer. The app requests the `ROLE_CALL_SCREENING` role and `READ_CONTACTS` permission on launch.
- For an incoming call, the service compares the incoming number to the existing forwarding list with `PhoneNumberUtils.compare()`.
- A matching number is rejected through Telecom (`setDisallowCall(true)` and `setRejectCall(true)`). No second call is placed by the app. Whether that rejection triggers the carrier's configured busy forwarding must be verified on the intended phone and carrier.
- Numbers saved in contacts can be screened only while the app has contacts access. If the permission is denied, Telecom may not deliver those calls to the screening service.

Guide screenshots were converted from NinePatch filenames to regular PNG resources because they are full-screen images, not stretchable UI controls. The old `BlockCallReceiver` implementation was deliberately excluded from this project because it reflects into the private `ITelephony` API. The application ID stays `cn.xm.hanley.iforward.activity` so Android can retain the old app's private data when installing an upgrade signed with the same key.

Open this directory in Android Studio. The Gradle Android plugin is declared in the root `build.gradle`; Android SDK 35 and a compatible JDK are required. The legacy project depends on the included support-v4 JAR and keeps its original UI implementation; a later UI refresh can replace that dependency with AndroidX separately.

