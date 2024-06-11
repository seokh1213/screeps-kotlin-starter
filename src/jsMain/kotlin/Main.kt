import starter.gameLoop

/**
 * Entry point
 * is called by screeps
 *
 * must not be removed by DCE
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun loop() {
    gameLoop()
}
