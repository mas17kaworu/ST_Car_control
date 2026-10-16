# ST_Car_control

Android app for controlling a model car via UDP/Bluetooth.

## Chassis control

Open **VCU > CHASSIS**, after **X in 1**. Uses real communication; initialize the
existing service through the normal app entry flow.

- Controls start locked. **Vehicle**, **Steering**, and **Brake pedal** can be enabled together; click again to lock only that group.
- Vehicle enables speed, Steering enables angle, and Brake pedal enables EHB/EMB. Release a slider to send only that field.
- Sliders quantize directly to their configured step instead of generating Material tick marks; EHB/EMB retain 1 N precision, including accessibility adjustments.
- Speed commands remain -20 to 20 km/h. Speed feedback is nonnegative, so the gauge displays 0 to 20 km/h; mock speed reports use the same nonnegative range.
- Each input sets the exact target in the same range and step as its slider. Press keyboard **Done/Enter** to send; typing or leaving the input never sends. Invalid input is rejected.
- Speed is at the upper left and Steering Angle below it. EHB/EMB share the full-height right card, with fixed feedback readings above the plot. EHB is cyan/dashed; EMB is amber/solid and drawn on top. The braking chart shows the latest 1 second (`-1s`, `now`); steering shows the latest 10 seconds (`-10s`, `-5s`, `now`).
- History is retained by monotonic receive timestamps, not a fixed sample count, so changing the report frequency does not shorten the visible time span. One sample before the window is kept to connect the curve at the left edge. Charts advance with new reports and pause when reports stop.
- History maintenance and chart-point preparation run off the UI thread. Every received sample is processed before display updates are coalesced to at most approximately 30 Hz (34 ms minimum interval). Telemetry updates only the feedback sections, not input/control state; braking points are prepared from the 1-second window only. Static chart grids and labels are cached separately from the moving curves.
- EHB and EMB retain separate sliders and inputs. **Set EHB + EMB** edits both targets together and sends them in one frame on **Done/Enter**, with Brake pedal enabled. It shows `--` when the two targets differ.
- After a combined-input submission, reports received during the next 2 seconds are recorded independently of the live chart. **EMB / EHB Response** then opens with only a title, EMB amber/solid and EHB cyan/dashed legends, and a fixed 0–2000 ms plot (N). Time zero is command submission, not the first report; samples are included from 0 ms up to, but not including, 2000 ms. Only actual reports are plotted, without synthetic endpoints or a fixed sample count; the force axis expands if feedback exceeds the normal range.
- Each valid combined submission replaces the previous recording. Individual sliders/inputs do not start a recording. Failed submissions do not open a response dialog; an empty window explicitly shows that no reports were received. Closing the dialog sends nothing, and leaving the page cancels recording and clears the result. Compare remains independent; if it is open when recording finishes, the response appears after Compare closes.
- All three cards stay on one screen without vertical scrolling. Input targets never replace measured feedback; current offset remains independently available beside the combined input.
- **Compare**, to the right of Current offset, opens the EMB offset comparison dialog and sends one `0x40` request. The first 50 returned values are cyan/dashed (without offset); the last 50 are amber/solid (with offset), evenly placed over 0-500 ms. The N axis expands when returned forces exceed the normal control range.
- Comparison waits up to 5 seconds and displays a failure if the reply is missing or invalid; no partial or synthetic data is plotted. Close and reopen to request again. Closing the dialog or leaving the page unregisters the request without sending a reset or changing controls; normal chassis reports continue independently.
- Locking does not reset the vehicle or cancel submitted commands. Enabling does not resend old targets.
- Reports are received independently of control enablement, from page entry until its view is destroyed.
- The upper-right title bar shows `ramp: 12.34%`, then `voltage: 48.30 V` to its right. Both are read-only and displayed with exactly two decimal places. Ramp is divided by 100 from its raw value and is a percentage, not an angle; voltage is divided by 10 and is in volts. Before the first report they show `ramp: --%` and `voltage: -- V`.
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
The comparison dialog requires an actual `0x40` reply; the periodic chassis mock does not fabricate comparison results.

## Code

`VCUChassisFragment` hosts Compose + ViewModel in `compose/ui/chassis/`.
`compose/data/chassis/` contains one Repository layer and models; adjust ranges in
`ChassisControlConfig`. The repository calls the existing `ServiceManager`.

Protocol: `communication/commandList/CMDChassisList/` contains
`CMDChassisControl` (`0x3D`) and `CMDChassisReport` (`0x3E`).
The report is 25 bytes with length `0x16`: speed, steering, EHB and EMB retain
their existing offsets; Ramp is a little-endian uint16 at byte offsets 20-21
(after EMB), followed by voltage as a little-endian uint16 at offsets 22-23,
then the checksum at offset 24. Ramp uses 100 raw units per percent (0.01% per
raw unit); voltage uses 10 raw units per volt (0.1 V per raw unit). The old 21-byte and 23-byte
reports are no longer accepted.
`CMDChassisCompare` (`0x40`) sends the parameter-free request `5A 3C 02 40 BD`.
Its UDP reply is one 205-byte frame: `5A 3C CA 40`, then 100 little-endian uint16
values (1 raw = 1 N), then the checksum at offset 204. Offsets 4-103 contain
the 50 values without offset; offsets 104-203 contain the 50 values with offset.
Comparison force samples use 2 bytes; the force fields in `0x3D` and `0x3E`
remain 4 bytes. `ProtocolMessageDispatch` is unchanged. This single-frame comparison protocol targets UDP; the existing
128-byte Bluetooth frame assembler cannot receive it.
Individual controls remain single-field commands. **Set EHB + EMB** sets validity
bits `0x0C` and both force fields to the same value in a single `0x3D` frame.
`CMDChassisEmergencyStop` (`0x3F`, length `0x06`) sends a 4-byte value: `0x55` on, `0x00` off.
Integers are little-endian. `CheckSumBit` excludes the header on send and includes it
on receive. There is no control acknowledgment; submission does not confirm execution.
