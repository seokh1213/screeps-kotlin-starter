import starter.GameEngine

@OptIn(ExperimentalJsExport::class)
@JsExport
fun loop() { // screeps entry
    GameEngine.loop()
}
