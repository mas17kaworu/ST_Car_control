# ST_Car_control

android apps for control a module car

## Chassis UI

Open **VCU > CHASSIS** to use the Compose chassis page. It is hosted by
`VCUChassisFragment`, with a screen-scoped ViewModel and a repository injected
through `AppContainer`.

This first version is explicitly **demo-only**. The chassis repository uses its
own fake device source, independently of `inUIDebugMode`, and never calls
`ServiceManager` or sends vehicle commands. Slider movement edits a local target;
release commits that target to the simulator. The three charts each display one
timestamped feedback signal. The four control shortcuts only change selection;
they do not implement vehicle linkage or EPB commands.

The demo uses configurable signed steps for speed/angle and normalized positive
levels for EHB/EMB. These are not hardware protocol values. Real integration belongs
behind `ChassisDeviceDataSource` after command IDs, units, limits, acknowledgements,
send rates, release behavior, and current-offset semantics are specified. Do not
persist or replay old control commands on reconnect.

Feature code is in `compose/ui/chassis/` and `compose/data/chassis/`; protocol
encoding should stay in `communication/commandList/` when it is introduced.

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
