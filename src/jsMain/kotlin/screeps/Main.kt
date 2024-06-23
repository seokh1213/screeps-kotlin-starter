package screeps

import screeps.game.GameEngine


@OptIn(ExperimentalJsExport::class)
@JsExport
fun loop() { // screeps entry
    GameEngine.run()
}

