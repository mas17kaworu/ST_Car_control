# ST_Car_control

android apps for control a module car

## Activity startup permissions

`BaseActivity` requests missing startup permissions in one batch on first creation.
Subclasses extend `getStartupPermissions()` instead of starting a second request.
`MainActivity` adds microphone access; `MODIFY_AUDIO_SETTINGS` is a manifest-only
permission. Legacy storage write access is requested only before Android 11,
and legacy storage read access only before Android 13. This does not grant
all-files access or replace scoped-storage/file-picker handling.

Cancelled or incomplete permission results are logged without accessing empty
arrays. Denied permissions are logged and shown in a toast; grant-only setup
runs only after a successful result or when no permissions are missing.

Run the activity permission regressions with:

```sh
./gradlew :app:testDebugUnitTest --tests '*StartupPermissionsTest'
```

问题：
tbox 表格形式
充电枪 fragment
demo/Actual difference

笑笑：
电流dashboard

todo
bms J
vcu J
bcm
plgm
mcu J
tbox J
发动机音效 J
