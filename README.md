# ST_Car_control

Android app for controlling a model car via UDP/Bluetooth.

## Chassis control

Open **VCU > CHASSIS**, after **X in 1**. Uses real communication; initialize the
existing service through the normal app entry flow.

- Controls start locked. **Vehicle**, **Steering**, and **Brake pedal** are mutually exclusive; click again to lock.
- Vehicle enables speed, Steering enables angle, and Brake pedal enables EHB/EMB. Release a slider to send only that field.
- Locking does not reset the vehicle or cancel submitted commands. Enabling does not resend old targets.
- Reports are received independently of control enablement, from page entry until its view is destroyed.
- EPB and current offset are disabled.

| Signal | Control range | Step | Wire units |
| --- | --- | --- | --- |
| Speed | 0 to 20 km/h | 1 km/h | 1 raw = 1 km/h |
| Steering | -540 to +540 degrees | 1 degree | 1 raw = 0.01 degree |
| EHB / EMB force | 0 to 20000 N | 1 N | 1 raw = 1 N |

## Code

`VCUChassisFragment` hosts Compose + ViewModel in `compose/ui/chassis/`.
`compose/data/chassis/` contains one Repository layer and models; adjust ranges in
`ChassisControlConfig`. The repository calls the existing `ServiceManager`.

Protocol: `communication/commandList/CMDChassisList/` contains
`CMDChassisControl` (`0x3D`, single-field commands) and `CMDChassisReport` (`0x3E`).
Integers are little-endian. `CheckSumBit` excludes the header on send and includes it
on receive. There is no control acknowledgment; submission does not confirm execution.
