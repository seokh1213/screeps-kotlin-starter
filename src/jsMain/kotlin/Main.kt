import starter.gameLoop
import starter.garbageCollect

@OptIn(ExperimentalJsExport::class)
@JsExport
fun loop() {
    gameLoop()
    garbageCollect()
}
