# ST_Car_control

Android app for controlling a model car via UDP/Bluetooth.

## Chassis control

Open **VCU > CHASSIS**, after **X in 1**. Uses real communication; initialize the
existing service through the normal app entry flow.

- Controls start locked. **Vehicle**, **Steering**, and **Brake pedal** can be enabled together; click again to lock only that group.
- Vehicle enables speed, Steering enables angle, and Brake pedal enables EHB/EMB. Release a slider to send only that field.
- Speed commands remain -20 to 20 km/h. Speed feedback is nonnegative, so the gauge displays 0 to 20 km/h; mock speed reports use the same nonnegative range.
- Each input sets the exact target in the same range and step as its slider. Press keyboard **Done/Enter** to send; typing or leaving the input never sends. Invalid input is rejected.
- Speed is at the upper left and Steering Angle below it. EHB/EMB share the full-height right card, with fixed feedback readings above the plot. EHB is cyan/dashed; EMB is amber/solid and drawn on top. The braking chart shows the latest 3 seconds (`-3s`, `-2s`, `-1s`, `now`); steering retains its 30-second window.
- EHB and EMB retain separate sliders and inputs. **Set EHB + EMB** edits both targets together and sends them in one frame on **Done/Enter**, with Brake pedal enabled. It shows `--` when the two targets differ.
- All three cards stay on one screen without vertical scrolling. Input targets never replace measured feedback; current offset remains independently available beside the combined input.
- Locking does not reset the vehicle or cancel submitted commands. Enabling does not resend old targets.
- Reports are received independently of control enablement, from page entry until its view is destroyed.
- EPB and current offset are independent switches; each toggle sends its own command. They start off without sending and are not reset on page exit. Displayed states are local targets, not device feedback.
- STOP toggles emergency stop on/off independently, with press feedback and a local-state highlight (not device confirmation). It sends nothing on page entry/exit.

| Signal | Control range | Step | Wire units |
| --- | --- | --- | --- |
| Speed | -20 to 20 km/h (0 centered) | 1 km/h | signed, 1 raw = 1 km/h |
| Steering | -32 to +32 degrees | 1 degree | 1 raw = 0.01 degree |
| EHB / EMB force | 0 to 20000 N | 1 N | 1 raw = 1 N |

Mock: set `STCarApplication.inUIDebugMode = true` and rebuild. Chassis injects
reports every 100 ms while visible; values cycle independently of slider input.
Disconnect the vehicle: command sending remains real. Set the flag back to `false` for real reports.

## Code

`VCUChassisFragment` hosts Compose + ViewModel in `compose/ui/chassis/`.
`compose/data/chassis/` contains one Repository layer and models; adjust ranges in
`ChassisControlConfig`. The repository calls the existing `ServiceManager`.

Protocol: `communication/commandList/CMDChassisList/` contains
`CMDChassisControl` (`0x3D`) and `CMDChassisReport` (`0x3E`).
Individual controls remain single-field commands. **Set EHB + EMB** sets validity
bits `0x0C` and both force fields to the same value in a single `0x3D` frame.
`CMDChassisEmergencyStop` (`0x3F`, length `0x06`) sends a 4-byte value: `0x55` on, `0x00` off.
Integers are little-endian. `CheckSumBit` excludes the header on send and includes it
on receive. There is no control acknowledgment; submission does not confirm execution.
